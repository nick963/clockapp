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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import static org.clock.graphical.FilledPoly.rotatePoint;

public record CircleGraphicalElement(Color color, Point2D center, double radius) implements GraphicalElement {
    @Override
    public GraphicalElement rotate(double theta) {
        Point2D newCenter = rotatePoint(theta, center);
        return new CircleGraphicalElement(color, newCenter, radius);
    }

    @Override
    public GraphicalElement adjust(OffsetRadius offsetRadius) {
        return new CircleGraphicalElement(
                color,
                offsetRadius.adjust(center),
                offsetRadius.adjustDimension(radius));
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        double diameter = radius * 2;
        graphics2D.setColor(color);
        graphics2D.fill(new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, diameter, diameter));
    }
}
