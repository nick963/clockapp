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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    ClockMouseListener(Window windowContainingClock, ClockPanel clock) {
        this.windowContainingClock = windowContainingClock;
        this.clock = clock;
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
        JFileChooser fileChooser = new JFileChooser();
        if (popup != null) {
            fileChooser.setComponentPopupMenu(popup);
        }
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files (*.json)", "json");
        fileChooser.addChoosableFileFilter(filter);
        if (fileChooser.showOpenDialog(windowContainingClock) == JFileChooser.APPROVE_OPTION) {
            try {
                loadJSONFile(fileChooser.getSelectedFile());
                GsonStyle style = new GsonStyle(new Gson(), new FileInputStream(lastLoadedJSONFile));
                clock.setStyle(style);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showSavePngDialog(String baseName) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Files (*.png)", "png");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setSelectedFile(new File(clock.getCurrentGroupAndStyle().style().getName() + ".png"));
        if (fileChooser.showSaveDialog(windowContainingClock) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                BufferedImage image = new BufferedImage(clock.getWidth(), clock.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                Graphics graphics = image.getGraphics();
                graphics.setColor(new Color(0f, 0f, 0f, 0f));
                graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
                clock.paint(graphics);
                try {
                    ImageIO.write(image, "png", selectedFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
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

    @Override
    public void mouseReleased(MouseEvent e) {
        initialWindowBounds = null;
        clockIsResizing = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (initialWindowBounds == null) {
            initialXOnScreen = e.getXOnScreen();
            initialYOnScreen = e.getYOnScreen();
            initialWindowBounds = windowContainingClock.getBounds();
            this.clockIsResizing = e.isShiftDown();
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
