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
package org.clock.styles.gsonstyle;

import com.google.gson.Gson;
import org.clock.*;
import org.clock.graphical.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.clock.ClockUtils.paintOnClock;

public class GsonStyle implements Style {

    private final String name;
    private final String description;
    private final List<GraphicalElement> clockFaceElements;
    private final List<GraphicalElement> secondHandElements;
    private final List<GraphicalElement> minuteHandElements;
    private final List<GraphicalElement> hourHandElements;

    public GsonStyle(Map<?, ?> jsonClockMap) throws JSONSchemaException {
        name = jsonClockMap.get("name").toString();
        description = jsonClockMap.containsKey("description") ? jsonClockMap.get("description").toString() : null;
        clockFaceElements= toGraphicalElements((List<?>)jsonClockMap.get("clock_face"));
        secondHandElements = toGraphicalElements((List<?>)jsonClockMap.get("second_hand"));
        minuteHandElements = toGraphicalElements((List<?>)jsonClockMap.get("minute_hand"));
        hourHandElements = toGraphicalElements((List<?>)jsonClockMap.get("hour_hand"));
    }

    public GsonStyle(Gson gson, InputStream styleStream) throws JSONSchemaException {
        this(gson.fromJson(readContents(styleStream), Map.class));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    static String readContents(InputStream styleStream) throws JSONSchemaException {
        try {
            return new String(styleStream.readAllBytes());
        } catch (IOException ex) {
            throw new JSONSchemaException(ex.getMessage());
        }
    }

    List<GraphicalElement> toGraphicalElements(List<?> shapeList) throws JSONSchemaException {
        ArrayList<GraphicalElement> graphicalElements = new ArrayList<>();
        if (shapeList != null) {
            for (Object object : shapeList) {
                var map = new JSONShapeMap((Map<?,?>)object);
                String shape = map.getShape();
                if (shape == null) {
                    throw new JSONSchemaException("shape is not specified for graphical element.");
                }
                try {
                    GraphicalElement element = switch (shape) {
                        case "polygon" -> new FilledPoly(map.getColor(),  map.getPoints().toArray(new Point2D[0]));
                        case "rectangle" ->
                                FilledPoly.rectangle(map.getColor(),  map.getPoint("fx", "fy"), map.getPoint("tx", "ty"));
                        case "circle" -> new CircleGraphicalElement(map.getColor(), map.getPoint(), map.getRadius());
                        case "text" -> new TextGraphicalElement(map.getColor(), map.getText(), map.getPoint(), map.getFont(), map.getSize(), map.getStyles());
                        default -> throw new JSONSchemaException("Invalid shape.");
                    };
                    Optional<Integer> ticks = map.getTicks();
                    graphicalElements.add(ticks.map(t -> (GraphicalElement) CompositeGraphicalElement.ticks(element, t)).orElse(element));
                } catch (JSONSchemaException ex) {
                    throw ex.setShape(shape);
                }
            }
        }
        return graphicalElements;
    }

    @Override
    public void paintClockFace(Graphics2D graphics2D, OffsetRadius offsetRadius, HoursMinutesSeconds hoursMinutesSeconds) {
        clockFaceElements.forEach(ge -> ge.adjust(offsetRadius).draw(graphics2D));
    }

    @Override
    public void paintMinuteHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double minutes) {
        var fraction = minutes/60d;
        minuteHandElements.forEach(ge -> paintOnClock(graphics2D, ge, offsetRadius, fraction));
    }

    @Override
    public void paintHourHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double hour) {
        var fraction = hour/12d;
        hourHandElements.forEach(ge -> paintOnClock(graphics2D, ge, offsetRadius, fraction));
    }

    @Override
    public void paintSecondHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double seconds) {
        var fraction = seconds/60d;
        secondHandElements.forEach(ge -> paintOnClock(graphics2D, ge, offsetRadius, fraction));
    }
}
