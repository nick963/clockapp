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

import org.clock.FilledPoly;

import java.awt.*;
import java.awt.geom.Point2D;

public class QuartzConstants {

    static double HALF_HOUR_MINUTE_HAND_WIDTH = 0.028;

    public static FilledPoly MINUTE_HAND_POLYGON = new FilledPoly(
            Color.BLACK,
            new Point2D.Double(-HALF_HOUR_MINUTE_HAND_WIDTH, 0.92),
            new Point2D.Double(HALF_HOUR_MINUTE_HAND_WIDTH, 0.92),
            new Point2D.Double(HALF_HOUR_MINUTE_HAND_WIDTH, -0.15),
            new Point2D.Double(-HALF_HOUR_MINUTE_HAND_WIDTH, -0.15));

    public static FilledPoly HOUR_HAND_POLYGON = new FilledPoly(
            Color.BLACK,
            new Point2D.Double(-HALF_HOUR_MINUTE_HAND_WIDTH, 0.69),
            new Point2D.Double(HALF_HOUR_MINUTE_HAND_WIDTH, 0.69),
            new Point2D.Double(HALF_HOUR_MINUTE_HAND_WIDTH, -0.15),
            new Point2D.Double(-HALF_HOUR_MINUTE_HAND_WIDTH, -0.15));
}
