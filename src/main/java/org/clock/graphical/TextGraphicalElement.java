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
package org.clock.graphical;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public record TextGraphicalElement(Color color, String text, Point2D center, String fontName, double size, int styles) implements GraphicalElement {

    private static final boolean SHOW_TEXT_DIMENSIONS = false;
    @Override
    public GraphicalElement rotate(double theta) {
        return new TextGraphicalElement(color, text, FilledPoly.rotatePoint(theta, center), fontName, size, styles);
    }

    @Override
    public GraphicalElement adjust(OffsetRadius offsetRadius) {
        return new TextGraphicalElement(
                color,
                text,
                offsetRadius.adjust(center),
                fontName,
                offsetRadius.adjustDimension(size),
                styles);
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        Font font = new Font(fontName, styles, 10).deriveFont((float) size);
        // derive text shape
        TextLayout textLayout = new TextLayout(text, font, graphics2D.getFontRenderContext());
        Rectangle2D bounds = textLayout.getBounds();

        AffineTransform transform = AffineTransform.getTranslateInstance(
                center.getX() - bounds.getX() - bounds.getWidth() / 2d,
                center.getY() + bounds.getHeight() / 2d);
        Shape textShape = textLayout.getOutline(transform);

        // fill text
        graphics2D.setPaint(color);
        graphics2D.fill(textShape);
        if (SHOW_TEXT_DIMENSIONS) {
            Rectangle2D newBounds = new Rectangle2D.Double(
                    center.getX() - bounds.getWidth() / 2d,
                    center.getY() - bounds.getHeight() / 2d,
                    bounds.getWidth(), bounds.getHeight());
            graphics2D.setColor(Color.RED);
            graphics2D.draw(newBounds);
            graphics2D.fill(new Rectangle2D.Double(center.getX() - 4, center.getY() - 4, 8, 8));
        }
    }
}
