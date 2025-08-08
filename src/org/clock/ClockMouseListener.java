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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

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

    ClockMouseListener(JWindow windowContainingClock, ClockPanel clock) {
        this.windowContainingClock = windowContainingClock;
        this.clock = clock;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            JPopupMenu popupMenu = new JPopupMenu();
            ButtonGroup buttonGroup = new ButtonGroup();
            String currentStyleName = clock.getCurrentStyleName();

            for (String styleName : clock.getStyleNames()) {
                JRadioButton radioButton = new JRadioButton(styleName);
                if (styleName.equals(currentStyleName)) {
                    radioButton.setSelected(true);
                }
                radioButton.addActionListener(ev -> clock.setStyle(styleName));
                buttonGroup.add(radioButton);
                popupMenu.add(radioButton);
            }
            popupMenu.add(new JSeparator());
            JMenuItem quitMenuItem = new JMenuItem("Quit");
            quitMenuItem.addActionListener(ev -> {
                windowContainingClock.setVisible(false);
                System.exit(0);
            });
            popupMenu.add(quitMenuItem);
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
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
