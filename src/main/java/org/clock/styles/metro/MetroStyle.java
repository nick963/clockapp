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
package org.clock.styles.metro;

import org.clock.HoursMinutesSeconds;
import org.clock.graphical.OffsetRadius;
import org.clock.Style;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import static org.clock.ClockUtils.paintOnClock;
import static org.clock.styles.metro.MetroConstants.*;

public class MetroStyle implements Style {

    @Override
    public String getName() {
        return "Copenhagen Metro Clock";
    }

    @Override
    public void paintClockFace(Graphics2D graphics2D, OffsetRadius offsetRadius, HoursMinutesSeconds hoursMinutesSeconds) {
        RadialGradientPaint paint = new RadialGradientPaint(
                new Point2D.Double(offsetRadius.offsetX(), offsetRadius.offsetY()),
                (float) offsetRadius.radius(),
                FACE_BACKGROUND_COLOR_FRACTIONS,
                FACE_BACKGROUND_COLORS
        );
        graphics2D.setPaint(paint);
        graphics2D.fill(offsetRadius.getBounds());

        for (int hour = 0; hour <= 11; hour++) {
            paintOnClock(graphics2D, HOUR_HAND_MARKER, offsetRadius, hour/12d);
        }
        for (int minute = 0; minute < 60; minute++) {
            paintOnClock(graphics2D, MINUTE_HAND_MARKER, offsetRadius, minute/60d);
        }
        LOGO.adjust(offsetRadius).draw(graphics2D);
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
        paintOnClock(graphics2D, SECOND_HAND_POLYGON, offsetRadius, seconds/60d);
        double size = 0.06 * offsetRadius.radius();
        Ellipse2D.Double oval = new Ellipse2D.Double(
                offsetRadius.offsetX() - 0.03d * offsetRadius.radius(),
                offsetRadius.offsetY() - 0.03d * offsetRadius.radius(),
                size,
                size);
        graphics2D.fill(oval);
    }
}
