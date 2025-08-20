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

import org.clock.graphical.GraphicalElement;
import org.clock.graphical.OffsetRadius;

import java.awt.*;

public final class ClockUtils {
    private static final double CIRCLE_RADIANS = 2d * Math.PI;

    public static double toRadians(double circleFraction) {
        return CIRCLE_RADIANS * circleFraction;
    }

    public static void paintOnClock(Graphics2D graphics2D, GraphicalElement poly, OffsetRadius offsetRadius, double fraction) {
        double theta = toRadians(0.5d + fraction);
        GraphicalElement transformedPoly = poly.rotate(theta).adjust(offsetRadius);
        transformedPoly.draw(graphics2D);
    }

    private ClockUtils() {}
}
