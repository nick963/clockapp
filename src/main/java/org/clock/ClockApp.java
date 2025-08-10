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

import org.clock.styles.colorful.ColorfulStyle;
import org.clock.styles.metro.MetroStyle;
import org.clock.styles.quartz.QuartzStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Calendar;

public class ClockApp {

    private static final Style[] CLOCK_STYLES = {
            new MetroStyle(),
            new QuartzStyle(),
            new ColorfulStyle()
    };

    public static void main(String[] args) {
        JWindow window = createAppWindow();
        ClockPanel clock = new ClockPanel(CLOCK_STYLES);
        window.add(clock,BorderLayout.CENTER);
        ClockMouseListener clockMouseListener = new ClockMouseListener(window, clock);
        clock.addMouseListener(clockMouseListener);
        clock.addMouseMotionListener(clockMouseListener);

        window.setVisible(true);
        Timer timer = new Timer(10, e -> {
            Calendar now = Calendar.getInstance();
            clock.setCalendar(now);
            clock.repaint();
        });
        timer.setInitialDelay(5);
        timer.start();
    }

    static JWindow createAppWindow() {
        JWindow window = new JWindow();
        window.setLayout(new BorderLayout());
        window.setBounds(300, 300, 800, 800);
        window.setShape(new RoundRectangle2D.Double(0, 0, window.getWidth(), window.getHeight(), window.getWidth(), window.getHeight()));
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        return window;
    }
}