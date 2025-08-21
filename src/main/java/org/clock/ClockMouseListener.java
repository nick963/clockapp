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

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private final JWindow windowContainingClock;
    private final ClockPanel clock;

    // "initial" fields are set at the start of moving or resizing the clock.
    private Rectangle initialWindowBounds = null;
    private int initialXOnScreen;
    private int initialYOnScreen;

    // clockIsResizing is set at the start of resizing the clock.
    private boolean clockIsResizing = false;

    private File lastLoadedJSONFile = null;
    private JMenuItem loadJSONFile = null;

    ClockMouseListener(JWindow windowContainingClock, ClockPanel clock) {
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
        popupMenu.add(quitMenuItem);
        return popupMenu;
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return Optional.of(menuItem);
    }

    JFrame fileChooser = null;

    JMenuItem loadJSONFile(JPopupMenu popup) {
        if (loadJSONFile != null) {
            return loadJSONFile;
        }
        loadJSONFile = new JMenuItem("Load JSON Clock File...");
        loadJSONFile.addActionListener(ev -> {
            if (fileChooser == null) {
                fileChooser = newJFrameFileChooser();
            }
            fileChooser.setVisible(true);
        });
        return loadJSONFile;
    }

    private JFrame newJFrameFileChooser() {

        JFrame frame = new JFrame("Embedded JFileChooser");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 600;
        int height = 400;
        frame.setLocation((screenSize.width - width)/2, (screenSize.height - height)/2);
        frame.setSize(width, height);
        frame.setLayout(new BorderLayout());

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files (*.json)", "json");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.addActionListener(ae -> {
            if (JFileChooser.APPROVE_SELECTION.equals(ae.getActionCommand())) {
                try {
                    loadJSONFile(fileChooser.getSelectedFile());
                    GsonStyle style = new GsonStyle(new Gson(), new FileInputStream(lastLoadedJSONFile));
                    clock.setStyle(style);
                    frame.setVisible(false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (JFileChooser.CANCEL_SELECTION.equals(ae.getActionCommand())) {
                frame.setVisible(false);
            }
        });
        frame.add(fileChooser);
        return frame;
    }

    void loadJSONFile(File file) throws IOException {
        lastLoadedJSONFile = file;
        GsonStyle style = new GsonStyle(new Gson(), new FileInputStream(file));
        clock.setStyle(style);
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
