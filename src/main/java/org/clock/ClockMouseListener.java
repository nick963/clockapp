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
import org.clock.styles.gsonstyle.StyleGroups;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;

/**
 * MouseListener/MouseMotionListener for ClockPanel.
 * - Mouse drag with shift resizes clock
 * - Mouse draw wo shift moves clock
 * - Adds popup menu to quit of change clock style
 */
class ClockMouseListener extends MouseAdapter {

    private final Window windowContainingClock;
    private final ClockPanel clock;

    // "initial" fields are set at the start of moving or resizing the clock.
    private Rectangle initialWindowBounds = null;
    private int initialXOnScreen;
    private int initialYOnScreen;

    // clockIsResizing is set at the start of resizing the clock.
    private boolean clockIsResizing = false;

    private File lastLoadedJSONFile = null;
    private JMenuItem loadJSONFile = null;

    private final JFileChooser jsonFileSelector = new JFileChooser();
    private final JFileChooser savePngDialog = new JFileChooser();

    ClockMouseListener(Window windowContainingClock, ClockPanel clock) {
        this.windowContainingClock = windowContainingClock;
        this.clock = clock;
        ImagePreviewAccessory preview = new ImagePreviewAccessory();
        jsonFileSelector.setAccessory(preview);
        jsonFileSelector.addPropertyChangeListener(preview);
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
        ClockPanel.GroupAndStyle currentStyleName = clock.getCurrentGroupAndStyle();
        HashMap<String, JMenu> submenus = new HashMap<>();
        for (ClockPanel.GroupAndStyle groupAndStyle : clock.getGroupsAndStyles()) {
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
        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.addActionListener(ev -> {
            windowContainingClock.setVisible(false);
            System.exit(0);
        });
        popupMenu.add(loadJSONFile(popupMenu));
        reloadJSSONFile().ifPresent(popupMenu::add);
        jsonSourceViewerMenuItem().ifPresent((popupMenu::add));
        popupMenu.add(new JSeparator());
        JMenuItem saveImage = new JMenuItem("Save Clock Image...");
        saveImage.addActionListener(ev -> saveImageAction());
        popupMenu.add(saveImage);
        popupMenu.add(quitMenuItem);
        return popupMenu;
    }

    void saveImageAction() {
        showSavePngDialog(clock.getCurrentGroupAndStyle().style().getName());
    }

    Optional<JMenuItem> reloadJSSONFile() {
        if (lastLoadedJSONFile == null) {
            return Optional.empty();
        }
        JMenuItem menuItem = new JMenuItem(
                String.format(
                        "Reload file: \".../%s\"",
                        lastLoadedJSONFile.getName()));
        menuItem.addActionListener(ev -> {
            try {
                loadJSONFile(lastLoadedJSONFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return Optional.of(menuItem);
    }

    JMenuItem loadJSONFile(JPopupMenu popup) {
        if (loadJSONFile != null) {
            return loadJSONFile;
        }
        loadJSONFile = new JMenuItem("Load JSON Clock File...");
        loadJSONFile.addActionListener(ev -> showJsonFileSelector(popup));
        return loadJSONFile;
    }

    private void showJsonFileSelector(JPopupMenu popup) {
        if (popup != null) {
            jsonFileSelector.setComponentPopupMenu(popup);
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files (*.json)", "json");
        jsonFileSelector.addChoosableFileFilter(filter);
        if (jsonFileSelector.showOpenDialog(windowContainingClock) == JFileChooser.APPROVE_OPTION) {
            try {
                loadJSONFile(jsonFileSelector.getSelectedFile());
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
        savePngDialog.setSelectedFile(new File(clock.getCurrentGroupAndStyle().style().getName() + ".png"));
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
        ClockPanel.GroupAndStyle groupAndStyle = clock.getCurrentGroupAndStyle();
        if (groupAndStyle.style().getSourceCode() == null) {
            return Optional.empty();
        }
        JMenuItem menuItem = new JMenuItem(
                String.format(
                        "View JSON: '%s'",
                        groupAndStyle.style().getName()));
        menuItem.addActionListener(ev -> {
            try {
                viewJSONFile(groupAndStyle.style());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return Optional.of(menuItem);
    }

    void loadJSONFile(File file) {
        lastLoadedJSONFile = file;
        try {
            GsonStyle style = new GsonStyle(new Gson(), new FileInputStream(file));
            clock.setStyle(style);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    windowContainingClock,
                    ex.getMessage(),
                    "Error Loading JSON Clock",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    static BufferedImage createImage(Style style, int dimension) {
        ClockPanel clockPanel = new ClockPanel(new StyleGroups("", new ArrayList<>()), Calendar::getInstance);
        clockPanel.setSize(dimension,dimension);
        clockPanel.setStyle(style);
        BufferedImage image = new BufferedImage(dimension, dimension, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, dimension, dimension); // Fill the entire image with transparent pixels
        graphics.setComposite(AlphaComposite.Src);
        Ellipse2D ellipse = new Ellipse2D.Double(0, 0, dimension, dimension);
        graphics.setClip(ellipse);
        clockPanel.paint(graphics);
        return image;
    }

    void viewJSONFile(Style style) {
        try {
            String json = style.getSourceCode();
            JTextArea textArea = new JTextArea(json);
            textArea.setEditable(false); // Make it non-editable but copyable
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 200));
            JOptionPane optionPane = new JOptionPane(scrollPane, JOptionPane.INFORMATION_MESSAGE);
            optionPane.setIcon(new ImageIcon(createImage(style, 200)));

            // Use the JOptionPane to create a JDialog
            JDialog dialog = optionPane.createDialog("JSON for " + style.getName());


            // Make the dialog resizable
            dialog.setResizable(true);

            // Show the dialog
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    windowContainingClock,
                    ex.getMessage(),
                    "Error Viewing JSON Source",
                    JOptionPane.ERROR_MESSAGE);
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
