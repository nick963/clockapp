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
package org.clock.styles.colorful;

import org.clock.ClockUtils;
import org.clock.HoursMinutesSeconds;
import org.clock.graphical.OffsetRadius;
import org.clock.Style;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import static org.clock.graphical.LineTransforms.adjust;
import static org.clock.graphical.LineTransforms.rotate;
import static org.clock.styles.colorful.ColorfulConstants.FACE_BACKGROUND_COLORS;
import static org.clock.styles.colorful.ColorfulConstants.FACE_BACKGROUND_COLOR_FRACTIONS;

public class ColorfulStyle implements Style {

    private static final Font BASE_FONT = new Font("Academy Engraved LET", Font.PLAIN, 30);
    private static final Paint TEXT_FILL_PAINT = new Color(255, 255, 255, 50);
    private static final Paint TEXT_DRAW_PAINT = Color.DARK_GRAY;

    @Override
    public String getName() {
        return "Colorful Clock";
    }

    public boolean staticClockFace() {
        return false;
    }

    @Override
    public void paintClockFace(Graphics2D graphics2D, OffsetRadius offsetRadius, HoursMinutesSeconds hoursMinutesSeconds) {
        double gradientTheta = ClockUtils.toRadians(hoursMinutesSeconds.seconds()/60d);
        double gradientOffset = hoursMinutesSeconds.seconds()/2;
        Line2D line = adjust(rotate(new Line2D.Double(0d, 1d - gradientOffset, 0d, -1d - gradientOffset), gradientTheta), offsetRadius);
        Paint paint = new LinearGradientPaint(
                line.getP1(),
                line.getP2(),
                FACE_BACKGROUND_COLOR_FRACTIONS,
                FACE_BACKGROUND_COLORS,
                MultipleGradientPaint.CycleMethod.REPEAT);
        graphics2D.setPaint(paint);
        graphics2D.fill(offsetRadius.getBounds());
        printTime(graphics2D, offsetRadius, hoursMinutesSeconds);
    }

    private void printTime(Graphics2D graphics2D, OffsetRadius offsetRadius, HoursMinutesSeconds hoursMinutesSeconds) {
        // set font
        double radius = offsetRadius.radius();
        Font font = BASE_FONT.deriveFont((float) (radius * 0.88f));
        graphics2D.setFont(font);

        // calculate string
        long hours = Math.round(hoursMinutesSeconds.hours());
        String string = String.format("%d:%02d", hours == 0 ? 12 : hours, Math.round(hoursMinutesSeconds.minutes()));

        // derive text shape
        TextLayout textLayout = new TextLayout(string, font, graphics2D.getFontRenderContext());
        Rectangle2D bounds = textLayout.getBounds();
        AffineTransform transform = AffineTransform.getTranslateInstance(
                offsetRadius.offsetX() - bounds.getWidth() / 2d,
                offsetRadius.offsetY() + bounds.getHeight() / 2d);
        Shape textShape = textLayout.getOutline(transform);

        // fill text
        graphics2D.setPaint(TEXT_FILL_PAINT);
        graphics2D.fill(textShape);

        // draw text
        graphics2D.setStroke(new BasicStroke((float) (radius * .010f)));
        graphics2D.setPaint(TEXT_DRAW_PAINT);
        graphics2D.draw(textShape);
    }

    private void printTimeOld(Graphics2D graphics2D, OffsetRadius offsetRadius, HoursMinutesSeconds hoursMinutesSeconds) {
        // set font
        double radius = offsetRadius.radius();
        Font font = BASE_FONT.deriveFont((float) (radius * 0.88f));
        graphics2D.setFont(font);

        // calculate string dimensions
        long hours = Math.round(hoursMinutesSeconds.hours());
        String string = String.format("%d:%02d", hours == 0 ? 12 : hours, Math.round(hoursMinutesSeconds.minutes()));
        FontMetrics fontMetrics = graphics2D.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(string);
        int stringAscent = fontMetrics.getAscent();

        // derive text shape
        TextLayout textLayout = new TextLayout(string, font, graphics2D.getFontRenderContext());
        double x = offsetRadius.offsetX();
        double y = offsetRadius.offsetY();
        AffineTransform transform = AffineTransform.getTranslateInstance(
                Math.round(x - stringWidth / 2f),
                Math.round(y + stringAscent / 2f));
        Shape outline = textLayout.getOutline(transform);

        // fill text
        graphics2D.setPaint(TEXT_FILL_PAINT);
        graphics2D.fill(outline);

        // draw text
        graphics2D.setStroke(new BasicStroke((float) (radius * .010f)));
        graphics2D.setPaint(TEXT_DRAW_PAINT);
        graphics2D.draw(outline);
    }

    @Override
    public void paintHourHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double hour) {
        // This clock style does not display a hour hand.
    }

    @Override
    public void paintMinuteHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double minute) {
        // This clock style does not display a minute hand.
    }

    @Override
    public void paintSecondHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double second) {
        // This clock style does not display a second hand.
    }
}
