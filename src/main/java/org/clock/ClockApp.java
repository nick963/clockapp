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
import org.clock.styles.gsonstyle.JSONSchemaException;
import org.clock.styles.gsonstyle.StyleGroup;
import org.clock.styles.gsonstyle.StyleGroups;
import org.clock.styles.metro.MetroStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Calendar;

public class ClockApp {

    private static final StyleGroups CLOCK_STYLE_GROUPS;

    static {
        ArrayList<Style> clockStyles = new ArrayList<>();
        clockStyles.add(new MetroStyle());
        clockStyles.add(new ColorfulStyle());
        StyleGroup javaStyles = new StyleGroup("Custom Java Clocks", clockStyles);
        try {
            StyleGroups jsonStyleGroups = StyleGroups.loadFromResource("/json/styles");
            ArrayList<StyleGroup> allGroups = new ArrayList<>(jsonStyleGroups.groups());
            allGroups.add(javaStyles);
            CLOCK_STYLE_GROUPS = new StyleGroups("All Groups", allGroups);
        } catch (JSONSchemaException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        Window window = createAppWindow();
        ClockComponent clock = new ClockComponent(CLOCK_STYLE_GROUPS, Calendar::getInstance);
        window.add(clock,BorderLayout.CENTER);
        ClockMouseListener clockMouseListener = new ClockMouseListener(window, clock);
        clock.addMouseListener(clockMouseListener);
        clock.addMouseMotionListener(clockMouseListener);
        window.setVisible(true);
    }

    static Window createAppWindow() {
        JFrame window = new JFrame();
        window.setUndecorated(true);
        window.setLayout(new BorderLayout());
        double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int preferredAppDimension = 397;
        double appX = Math.round((screenWidth - preferredAppDimension)/2d);
        double appY = Math.round((screenHeight - preferredAppDimension)/2d);
        window.setBounds((int) appX, (int) appY, preferredAppDimension, preferredAppDimension);
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