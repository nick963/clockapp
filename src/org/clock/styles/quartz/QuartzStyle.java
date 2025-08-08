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
package org.clock.styles.quartz;

import org.clock.HoursMinutesSeconds;
import org.clock.OffsetRadius;
import org.clock.ClockUtils;
import org.clock.Style;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import static org.clock.ClockUtils.paintOnClock;
import static org.clock.FilledPoly.rotatePoint;
import static org.clock.LineTransforms.adjust;
import static org.clock.LineTransforms.rotate;
import static org.clock.styles.metro.MetroConstants.MINUTE_HAND_LINE;
import static org.clock.styles.quartz.QuartzConstants.*;
import static java.awt.Color.*;

public class QuartzStyle implements Style {

    @Override
    public String getName() {
        return "Quartz Clock";
    }

    @Override
    public void paintClockFace(Graphics2D graphics2D, OffsetRadius offsetRadius, HoursMinutesSeconds hoursMinutesSeconds) {
        graphics2D.setPaint(LIGHT_GRAY);
        graphics2D.fill(offsetRadius.getBounds());

        graphics2D.setPaint(BLACK);
        double radius = offsetRadius.radius();
        Stroke fatLineStroke = new BasicStroke((float) (radius * .018f));
        Ellipse2D ellipse1 = new Ellipse2D.Double(
                offsetRadius.offsetX() - radius,
                offsetRadius.offsetY() - radius,
                radius * 2,
                radius * 2);
        graphics2D.setStroke(fatLineStroke);
        graphics2D.draw(ellipse1);

        double radius2 = radius * 0.93;
        Ellipse2D ellipse2 = new Ellipse2D.Double(
                offsetRadius.offsetX() - radius2,
                offsetRadius.offsetY() - radius2,
                radius2 * 2,
                radius2 * 2);
        Stroke innerLineStroke = new BasicStroke((float) (radius * .005f));
        graphics2D.setStroke(innerLineStroke);
        graphics2D.draw(ellipse2);

        Font baseFont = new Font("Gill Sans", Font.PLAIN, 30); // graphics2D.getFont();
        graphics2D.setFont(baseFont.deriveFont((float) (radius * .229007f)));

        FontMetrics fontMetrics;
        for (int minute = 0; minute < 60; minute++) {
            double theta = ClockUtils.toRadians(0.5d + minute / 60d);
            Line2D line = adjust(rotate(MINUTE_HAND_LINE, theta), offsetRadius);
            if (minute % 5 == 0) {
                graphics2D.setStroke(fatLineStroke);
                Point2D pt = rotatePoint(theta, new Point2D.Double(0, 0.8 * radius));
                int hour = minute / 5;
                if (hour == 0) {
                    hour = 12;
                }
                fontMetrics = graphics2D.getFontMetrics();
                String hourString = String.valueOf(hour);
                double width = fontMetrics.stringWidth(hourString);
                LineMetrics lineMetrics = fontMetrics.getLineMetrics(hourString, graphics2D);
                double ascent = lineMetrics.getAscent();
                double x = offsetRadius.offsetX() + pt.getX();
                double y = offsetRadius.offsetY() + pt.getY();
                graphics2D.drawString(hourString, Math.round(x - width / 2d), Math.round(y + ascent / 2d));
            } else {
                graphics2D.setStroke(innerLineStroke);
            }
            graphics2D.draw(line);
        }

        graphics2D.setFont(baseFont.deriveFont((float) (radius * .06f)));
        fontMetrics = graphics2D.getFontMetrics();
        int width = fontMetrics.stringWidth("quartz");
        int height = fontMetrics.getAscent();
        Point2D pt = new Point2D.Double(0, 0.45 * radius);
        double x = offsetRadius.offsetX() + pt.getX();
        double y = offsetRadius.offsetY() + pt.getY();
        graphics2D.drawString("quartz", Math.round(x - width / 2d), Math.round(y + height / 2d));
        double size = 0.1 * offsetRadius.radius();
        Ellipse2D.Double oval = new Ellipse2D.Double(
                offsetRadius.offsetX() - size/2d,
                offsetRadius.offsetY() - size/2d,
                size, size);
        graphics2D.fill(oval);
    }

    @Override
    public void paintMinuteHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double minutes) {
        paintOnClock(graphics2D, MINUTE_HAND_POLYGON, offsetRadius, minutes/60d);
    }

    @Override
    public void paintHourHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double hours) {
        paintOnClock(graphics2D, HOUR_HAND_POLYGON, offsetRadius, hours/12d);
    }

    @Override
    public void paintSecondHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double seconds) {
        // This clock style does not display a second hand.
    }
}
