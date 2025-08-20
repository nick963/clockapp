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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.awt.geom.Point2D;

public class OffsetRadiusTest {
    @Test
    public void adjustPoint() {
        OffsetRadius offsetRadius = new OffsetRadius(50,50, 1);
        Point2D point = new Point2D.Double(20, 30);
        var newPoint = offsetRadius.adjust(point);
        assertEquals(70d, newPoint.getX(), 0d);
        assertEquals(80d, newPoint.getY(), 0d);
    }
}
