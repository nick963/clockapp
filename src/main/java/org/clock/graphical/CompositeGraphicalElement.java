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
