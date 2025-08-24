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
import java.util.ArrayList;

import static org.clock.ClockUtils.toRadians;

public class CompositeGraphicalElement implements GraphicalElement {
    final java.util.List<GraphicalElement> elements;

    public static CompositeGraphicalElement ticks(GraphicalElement base, int ticks) {
        java.util.List<GraphicalElement> elements = new ArrayList<>();
        for (double i = 0; i < ticks; i++) {
            double theta = toRadians(0.5d + i/ticks);
            elements.add(base.rotate(theta));
        }
        return new CompositeGraphicalElement(elements);
    }

    private CompositeGraphicalElement(java.util.List<GraphicalElement> elements) {
        this.elements = elements;
    }
    @Override
    public GraphicalElement rotate(double theta) {
        return new CompositeGraphicalElement(elements.stream().map(e -> e.rotate(theta)).toList());
    }

    @Override
    public GraphicalElement adjust(OffsetRadius offsetRadius) {
        return new CompositeGraphicalElement(elements.stream().map(e -> e.adjust(offsetRadius)).toList());
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        elements.forEach(e -> e.draw(graphics2D));
    }
}
