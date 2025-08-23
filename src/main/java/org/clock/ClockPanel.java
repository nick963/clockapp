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

import org.clock.graphical.OffsetRadius;
import org.clock.styles.gsonstyle.GsonStyle;
import org.clock.styles.gsonstyle.StyleGroup;
import org.clock.styles.gsonstyle.StyleGroups;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/** Displays a clock. */
public class ClockPanel extends JPanel {
    private static final boolean BUFFER_CLOCK_FACE = true;

    private final List<Style> clockStyles;
    private final StyleGroups clockStyleGroups;
    private GroupAndStyle currentGroupAndStyle;
    private Calendar calendar;
    private OffsetRadius lastOffsetRadius;
    private BufferedImage lastBufferedImage;

    public ClockPanel(List<Style> clockStyles, StyleGroups clockStyleGroups) {
        this.clockStyles = new ArrayList<>(clockStyles);
        this.clockStyleGroups = clockStyleGroups;
        this.calendar = Calendar.getInstance();
        lastOffsetRadius = new OffsetRadius(0,0,0);
        lastBufferedImage = null;
        if (!clockStyles.isEmpty()) {
            setGroupAndStyle(new GroupAndStyle(null, clockStyles.get(0)));
        }
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle2D.Double r = centerSquare();
        OffsetRadius offsetRadius = new OffsetRadius(r.x + r.width/2, r.y + r.height/2, r.width/2);
        HoursMinutesSeconds hoursMinutesSeconds =
                HoursMinutesSeconds.getHoursMinutesSeconds(calendar, currentGroupAndStyle.style.discreteTimeIntervals());

        Style style = currentGroupAndStyle.style;
        if (BUFFER_CLOCK_FACE && style.staticClockFace()) {
            if (lastOffsetRadius.equals(offsetRadius)) {
                graphics2D.drawImage(lastBufferedImage, 0, 0, null);
            } else {
                BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D bufferedImageGraphics = (Graphics2D) bufferedImage.getGraphics();
                bufferedImageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                bufferedImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                style.paintClockFace(bufferedImageGraphics, offsetRadius, hoursMinutesSeconds);
                graphics2D.drawImage(bufferedImage, 0, 0, null);
                lastBufferedImage = bufferedImage;
                lastOffsetRadius = offsetRadius;
            }
        } else {
            style.paintClockFace(graphics2D, offsetRadius, hoursMinutesSeconds);
        }
        style.paintMinuteHand(graphics2D, offsetRadius, hoursMinutesSeconds.minutes());
        style.paintHourHand(graphics2D, offsetRadius, hoursMinutesSeconds.hours());
        style.paintSecondHand(graphics2D, offsetRadius, hoursMinutesSeconds.seconds());
    }

    Rectangle2D.Double centerSquare() {
        double w = getWidth();
        double h = getHeight();
        double centerX = w/2d;
        double centerY = h/2d;
        double radius = Math.min(w, h)/2d;
        double diameter = radius * 2d;
        return new Rectangle2D.Double(centerX - radius, centerY - radius, diameter, diameter);
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setStyle(Style style) {
        setGroupAndStyle(new GroupAndStyle(null, style));
    }

    public record GroupAndStyle(StyleGroup group, Style style) {
        public boolean isSame(GroupAndStyle other) {
            if (group == null && other.group == null) {
                return style.getName().equals(other.style.getName());
            }
            if (group == null || other.group == null) {
                return false;
            }
            return group.getName().equals(other.group.getName())
                    && style.getName().equals(other.style.getName());
        }
    }

    public java.util.List<GroupAndStyle> getGroupsAndStyles() {
        ArrayList<GroupAndStyle> list = new ArrayList<>();
        clockStyles.forEach(s -> list.add(new GroupAndStyle(null, s)));
        clockStyleGroups.groups().forEach(g -> g.getStyles().forEach(s -> list.add(new GroupAndStyle(g, s))));
        return list;
    }

    public GroupAndStyle getCurrentGroupAndStyle() {
        return currentGroupAndStyle;
    }

    public void setGroupAndStyle(GroupAndStyle groupAndStyle) {
        currentGroupAndStyle = groupAndStyle;
        lastOffsetRadius = new OffsetRadius();
        setToolTipText(groupAndStyle.style.getName());
    }
}
