/*
 * Copyright 2025 Nick Lerissa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.clock;

import com.google.gson.Gson;
import org.clock.styles.gsonstyle.GsonStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;

import static org.clock.StyleUtils.createImage;

/**
 * MouseListener/MouseMotionListener for ClockPanel.
 * - Mouse drag with shift resizes clock
 * - Mouse draw wo shift moves clock
 * - Adds popup menu to quit of change clock style
 */
class ClockMouseListener extends MouseAdapter {

    private final Window windowContainingClock;
    private final ClockComponent clock;

    // "initial" fields are set at the start of moving or resizing the clock.
    private Rectangle initialWindowBounds = null;
    private int initialXOnScreen;
    private int initialYOnScreen;

    // clockIsResizing is set at the start of resizing the clock.
    private boolean clockIsResizing = false;

    private File lastLoadedJSONFile = null;
    private JMenuItem loadJSONFile = null;

    private final JFileChooser openJsonFileDialog = new JFileChooser();
    private final JFileChooser savePngDialog = new JFileChooser();

    ClockMouseListener(Window windowContainingClock, ClockComponent clock) {
        this.windowContainingClock = windowContainingClock;
        this.clock = clock;
        ClockPreviewAccessory preview = new ClockPreviewAccessory(Calendar::getInstance);
        openJsonFileDialog.setAccessory(preview);
        openJsonFileDialog.addPropertyChangeListener(preview);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popup = createPopup();
            SwingUtilities.invokeLater(popup::requestFocus);
            popup.show(clock, e.getX(), e.getY());
        }
    }

    private JPopupMenu createPopup() {
        JPopupMenu popupMenu = new JPopupMenu();
        ButtonGroup buttonGroup = new ButtonGroup();
        ClockComponent.GroupAndStyle currentStyleName = clock.getCurrentGroupAndStyle();
        HashMap<String, JMenu> submenus = new HashMap<>();
        for (ClockComponent.GroupAndStyle groupAndStyle : clock.getGroupsAndStyles()) {
            JRadioButton radioButton = new JRadioButton(groupAndStyle.style().getName());
            if (currentStyleName.isSame(groupAndStyle)) {
                radioButton.setSelected(true);
            }
            radioButton.addActionListener(ev -> { clock.setGroupAndStyle(groupAndStyle); popupMenu.setVisible(false); } );
            buttonGroup.add(radioButton);
            if (groupAndStyle.group() == null) {
                popupMenu.add(radioButton);
            } else {
                JMenu submenu = submenus.get(groupAndStyle.group().getName());
                if (submenu == null) {
                    submenu = new JMenu(groupAndStyle.group().getName());
                    submenus.put(groupAndStyle.group().getName(), submenu);
                }
                popupMenu.add(submenu);
                submenu.add(radioButton);
            }
        }
        popupMenu.add(new JSeparator());
        JMenuItem copyrightMenuItem = new JMenuItem("Copyright License...");
        copyrightMenuItem.addActionListener(ev -> {
            try {
                String string = new String(ClockMouseListener.class.getClassLoader().getResourceAsStream("LICENSE").readAllBytes());
                showViewTextDialog(windowContainingClock, "Copyright License", null, string);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        JMenuItem noticeMenuItem = new JMenuItem("Copyright Notice...");
        noticeMenuItem.addActionListener(ev -> {
            try {
                String string = new String(ClockMouseListener.class.getClassLoader().getResourceAsStream("NOTICE").readAllBytes());
                showViewTextDialog(windowContainingClock, "Copyright Notice", null, string);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.addActionListener(ev -> {
            windowContainingClock.setVisible(false);
            System.exit(0);
        });
        popupMenu.add(openJsonFileMenuItem(popupMenu));
        reloadJSSONFileMenuItem().ifPresent(popupMenu::add);
        jsonSourceViewerMenuItem().ifPresent((popupMenu::add));
        popupMenu.add(new JSeparator());
        JMenuItem saveImage = new JMenuItem("Save PNG of Clock Image...");
        saveImage.addActionListener(ev -> saveImageAction());
        popupMenu.add(saveImage);
        popupMenu.add(new JSeparator());
        popupMenu.add(copyrightMenuItem);
        popupMenu.add(noticeMenuItem);
        popupMenu.add(new JSeparator());
        popupMenu.add(quitMenuItem);
        return popupMenu;
    }

    void saveImageAction() {
        showSavePngDialog(clock.getCurrentGroupAndStyle().style().getName());
    }

    private Optional<JMenuItem> reloadJSSONFileMenuItem() {
        if (lastLoadedJSONFile == null) {
            return Optional.empty();
        }
        JMenuItem menuItem = new JMenuItem(
                String.format(
                        "Reload file: \".../%s\"",
                        lastLoadedJSONFile.getName()));
        menuItem.addActionListener(ev -> {
            try {
                openJsonFileMenuItem(lastLoadedJSONFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return Optional.of(menuItem);
    }

    private JMenuItem openJsonFileMenuItem(JPopupMenu popup) {
        if (loadJSONFile != null) {
            return loadJSONFile;
        }
        loadJSONFile = new JMenuItem("Open JSON Clock File...");
        loadJSONFile.addActionListener(ev -> showJsonFileSelector(popup));
        return loadJSONFile;
    }

    private void showJsonFileSelector(JPopupMenu popup) {
        if (popup != null) {
            openJsonFileDialog.setComponentPopupMenu(popup);
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files (*.json)", "json");
        openJsonFileDialog.addChoosableFileFilter(filter);
        if (openJsonFileDialog.showOpenDialog(windowContainingClock) == JFileChooser.APPROVE_OPTION) {
            try {
                openJsonFileMenuItem(openJsonFileDialog.getSelectedFile());
                GsonStyle style = new GsonStyle(new Gson(), new FileInputStream(lastLoadedJSONFile));
                clock.setStyle(style);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showSavePngDialog(String baseName) {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Files (*.png)", "png");
        savePngDialog.addChoosableFileFilter(filter);
        savePngDialog.setDialogType(JFileChooser.SAVE_DIALOG);
        savePngDialog.setSelectedFile(new File(String.format("%s %dx%d.png", clock.getCurrentGroupAndStyle().style().getName(), clock.getWidth(), clock.getHeight())));
        if (savePngDialog.showSaveDialog(windowContainingClock) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = savePngDialog.getSelectedFile();
            if (selectedFile != null) {
                BufferedImage image = createImage(clock.getCurrentGroupAndStyle().style(), clock.getWidth());
                try {
                    ImageIO.write(image, "png", selectedFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private Optional<JMenuItem> jsonSourceViewerMenuItem() {
        ClockComponent.GroupAndStyle groupAndStyle = clock.getCurrentGroupAndStyle();
        if (groupAndStyle.style().getSourceCode() == null) {
            return Optional.empty();
        }
        JMenuItem menuItem = new JMenuItem(
                String.format(
                        "View JSON: '%s'",
                        groupAndStyle.style().getName()));
        menuItem.addActionListener(ev -> showJsonDialog());
        return Optional.of(menuItem);
    }

    private void showJsonDialog() {
        ClockComponent.GroupAndStyle groupAndStyle = clock.getCurrentGroupAndStyle();
        if (groupAndStyle.style().getSourceCode() == null) {
            return;
        }
        try {
            Style style = groupAndStyle.style();
            showViewTextDialog(
                    windowContainingClock,
                    "JSON for " + style.getName(),
                    createImage(style, 128),
                    style.getSourceCode());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void openJsonFileMenuItem(File file) {
        lastLoadedJSONFile = file;
        try {
            GsonStyle style = new GsonStyle(new Gson(), new FileInputStream(file));
            clock.setStyle(style);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    windowContainingClock,
                    ex.getMessage(),
                    String.format("Error Loading: %s", file.getName()),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    static void showViewTextDialog(Window parent, String title, Image image, String text) {
        try {
            JTextArea textArea = new JTextArea(text);
            textArea.setEditable(false); // Make it non-editable but copyable
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(512, 256));
            // Create a custom button
            JButton copyButton = new JButton("Copy to Clipboard");

            // Create the JOptionPane with custom options
            Object[] options = {copyButton, "Close"};
            JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.DEFAULT_OPTION,
                    null, // Icon is null here, can be added later
                    options,
                    options[0]);
            if (image != null) {
                optionPane.setIcon(new ImageIcon(image));
            } else {
                optionPane.setIcon(new ImageIcon());
            }

            // Use the JOptionPane to create a JDialog
            JDialog dialog = optionPane.createDialog(title);
            // Add action listener to the copy button
            copyButton.addActionListener(e -> {
                StringSelection stringSelection = new StringSelection(textArea.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            });

            // Make the dialog resizable
            dialog.setResizable(true);

            // Show the dialog
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    parent,
                    ex.getMessage(),
                    "Error Viewing JSON Source",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            showJsonDialog();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        initialWindowBounds = null;
        clockIsResizing = false;
        clock.setDisplaySize(false);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (initialWindowBounds == null) {
            initialXOnScreen = e.getXOnScreen();
            initialYOnScreen = e.getYOnScreen();
            initialWindowBounds = windowContainingClock.getBounds();
            this.clockIsResizing = e.isShiftDown();
            clock.setDisplaySize(this.clockIsResizing);
        } else {
            if (clockIsResizing) {
                int newWidth = Math.max(100, initialWindowBounds.width + e.getXOnScreen() - initialXOnScreen);
                int newHeight = Math.max(100, initialWindowBounds.height + e.getYOnScreen() - initialYOnScreen);
                int size = Math.min(newWidth, newHeight);
                size = Math.min(size, 1024);
                windowContainingClock.setSize(size, size);
                windowContainingClock.setShape(new RoundRectangle2D.Double(0, 0, size, size, size, size));

            } else {
                Point newLocation = new Point(
                        initialWindowBounds.x + e.getXOnScreen() - initialXOnScreen,
                        initialWindowBounds.y + e.getYOnScreen() - initialYOnScreen);
                windowContainingClock.setLocation(newLocation);
            }
        }
    }
}
