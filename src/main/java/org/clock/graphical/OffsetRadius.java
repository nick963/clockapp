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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public record OffsetRadius(double offsetX, double offsetY, double radius) {

    public OffsetRadius() {
        this(0,0,0);
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(
                offsetX - radius,
                offsetY - radius,
                1 + radius * 2,
                1 + radius * 2);
    }

    public Point2D adjust(Point2D point) {
        double newx = point.getX() * radius();
        double newy = point.getY() * radius();
        newx += offsetX();
        newy += offsetY();
        return new Point2D.Double(newx, newy);
    }

    public double adjustDimension(double value) {
        return radius * value;
    }
}
