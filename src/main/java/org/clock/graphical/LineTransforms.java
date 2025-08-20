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

import java.awt.geom.Line2D;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class LineTransforms {
    static public Line2D adjust(Line2D line, OffsetRadius offsetRadius) {
        return shift(scale(line, offsetRadius.radius()), offsetRadius.offsetX(), offsetRadius.offsetY());
    }

    public static Line2D rotate(Line2D line, double theta) {
        return new Line2D.Double(
                line.getX1() * cos(theta) - line.getY1() * sin(theta),
                line.getX1() * sin(theta) + line.getY1() * cos(theta),
                line.getX2() * cos(theta) - line.getY2() * sin(theta),
                line.getX2() * sin(theta) + line.getY2() * cos(theta));
    }

    public static Line2D scale(Line2D line, double xScale, double yScale) {
        return new Line2D.Double(
                line.getX1() * xScale, line.getY1() * yScale,
                line.getX2() * xScale, line.getY2() * yScale);
    }

    public static Line2D scale(Line2D line, double scale) {
        return scale(line, scale, scale);
    }

    public static Line2D shift(Line2D line, double xOffset, double yOffset) {
        return new Line2D.Double(
                line.getX1() + xOffset,
                line.getY1() + yOffset,
                line.getX2() + xOffset,
                line.getY2() + yOffset);
    }
}
