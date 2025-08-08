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

import org.clock.FilledPoly;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class MetroConstants {

    public static Color[] FACE_BACKGROUND_COLORS = {Color.LIGHT_GRAY.darker(), Color.LIGHT_GRAY, Color.LIGHT_GRAY.brighter()};

    // Define the fractions for the color stops (where each color appears)
    public static float[] FACE_BACKGROUND_COLOR_FRACTIONS = {0.0f, 0.93f, 1.0f}; // Example fractions

    static double HALF_HOUR_MINUTE_HAND_WIDTH = 0.016;

    public static FilledPoly SECOND_HAND_POLYGON = new FilledPoly(
            Color.RED,
            new Point2D.Double(-0.01, 0.99),
            new Point2D.Double(0.01, 0.99),
            new Point2D.Double(0.01, -0.1),
            new Point2D.Double(-0.01, -0.1));


    public static Line2D MINUTE_HAND_LINE = new Line2D.Double(0,1.0,0,0.9375);

    public static FilledPoly MINUTE_HAND_POLYGON = new FilledPoly(
            Color.BLACK,
            new Point2D.Double(-HALF_HOUR_MINUTE_HAND_WIDTH, 0.99),
            new Point2D.Double(HALF_HOUR_MINUTE_HAND_WIDTH, 0.99),
            new Point2D.Double(HALF_HOUR_MINUTE_HAND_WIDTH, -0.1),
            new Point2D.Double(-HALF_HOUR_MINUTE_HAND_WIDTH, -0.1));

    public static FilledPoly HOUR_HAND_POLYGON = new FilledPoly(
            Color.BLACK,
            new Point2D.Double(-HALF_HOUR_MINUTE_HAND_WIDTH, 0.69),
            new Point2D.Double(HALF_HOUR_MINUTE_HAND_WIDTH, 0.69),
            new Point2D.Double(HALF_HOUR_MINUTE_HAND_WIDTH, -0.1),
            new Point2D.Double(-HALF_HOUR_MINUTE_HAND_WIDTH, -0.1));

    public static FilledPoly HOUR_HAND_MARKER = new FilledPoly(
            Color.BLACK,
            new Point2D.Double(-0.005, 0.72),
            new Point2D.Double(0.005, 0.72),
            new Point2D.Double(0.005, 0.99),
            new Point2D.Double(-0.005, 0.99));

    public static FilledPoly MINUTE_HAND_MARKER = new FilledPoly(
            Color.BLACK,
            new Point2D.Double(-0.003, 0.89),
            new Point2D.Double(0.003, 0.89),
            new Point2D.Double(0.003, 0.99),
            new Point2D.Double(-0.003, 0.99));

    public static final FilledPoly LOGO;

    static {
        var p = new FilledPoly(
                Color.BLACK,
                new Point2D.Double(0.0, 0.00), // 1: bottom left of rectangle
                new Point2D.Double(0.0, -0.25), // 2: top left of rectangle
                new Point2D.Double(0.25, -0.25), // 3: bottom left of M
                new Point2D.Double(0.28, -0.75), // 4: top left of M
                new Point2D.Double(0.38, -0.75), // 5: top second left of M
                new Point2D.Double(0.50, -0.48), // 6: center top of M
                new Point2D.Double(0.50, -0.25), // 7: center top of rectangle
                new Point2D.Double(0.45, -0.25), // 8: bottom right hole of M
                new Point2D.Double(0.34, -0.65), // 9: top hole of M
                new Point2D.Double(0.33, -0.25), // 8: bottom right hole of M
                new Point2D.Double(0.50, -0.25), // 8: center top of rectangle
                new Point2D.Double(0.50, 0.00)); // 9: center bottom of rectangle
        var p2 = p.join(p.scale(-1, 1).shift(1,0));
        LOGO = p2.shift(-.5, -2.5).scale(.15);
    }
}
