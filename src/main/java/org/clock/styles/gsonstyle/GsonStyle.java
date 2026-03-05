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
import static org.clock.ClockUtils.toRadians;

public class GsonStyle implements Style {

    private final String name;
    private final String description;
    private final List<GraphicalElement> clockFaceElements;
    private final List<GraphicalElement> secondHandElements;
    private final List<GraphicalElement> minuteHandElements;
    private final List<GraphicalElement> hourHandElements;
    private String sourceCode = null;

    private GsonStyle(Map<?, ?> jsonClockMap) throws JSONSchemaException {
        name = jsonClockMap.get("name").toString();
        description = jsonClockMap.containsKey("description") ? jsonClockMap.get("description").toString() : null;
        clockFaceElements = toGraphicalElements(jsonClockMap, "clock_face");
        secondHandElements = toGraphicalElements(jsonClockMap, "second_hand");
        minuteHandElements = toGraphicalElements(jsonClockMap, "minute_hand");
        hourHandElements = toGraphicalElements(jsonClockMap, "hour_hand");
    }

    public GsonStyle(Gson gson, String json) throws JSONSchemaException {
        this(gson.fromJson(json, Map.class));
        sourceCode = json;
    }

    public GsonStyle(Gson gson, InputStream styleStream) throws JSONSchemaException {
        this(gson, readContents(styleStream));
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
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

    List<GraphicalElement> toGraphicalElements(Map<?,?> jsonClockMap, String key) throws JSONSchemaException {
        List<?> shapeList;
        try {
            shapeList = (List<?>) jsonClockMap.get(key);
        } catch (ClassCastException classCastException) {
            throw new JSONSchemaException(String.format("The value for '%s' is expected to be a JSON array.", key));
        }
        ArrayList<GraphicalElement> graphicalElements = new ArrayList<>();
        if (shapeList != null) {
                for (Object object : shapeList) {
                    JSONShapeMap map;
                    try {
                        map = new JSONShapeMap((Map<?, ?>) object);
                    } catch (ClassCastException exception) {
                        throw new JSONSchemaException(String.format("The elements in '%s' are expected to be JSON objects.", key));
                    }
                    String shape = map.getShape();
                    if (shape == null) {
                        throw new JSONSchemaException(String.format("'%s' element is missing a value for 'shape'.", key));
                    }
                    try {
                        Optional<Integer> ticks = map.getTicks();
                        GraphicalElement element = switch (shape) {
                            case "polygon" -> new FilledPoly(map.getColor(), map.getPoints().toArray(new Point2D[0]));
                            case "rectangle" ->
                                    FilledPoly.rectangle(map.getColor(), map.getPoint("rectangle_fx", "rectangle_fy"), map.getPoint("rectangle_tx", "rectangle_ty"));
                            case "circle" ->
                                    new CircleGraphicalElement(map.getColor(), map.getPoint(), map.getRadius());
                            case "text" ->
                                    new TextGraphicalElement(map.getColor(), map.getText(), map.getPoint(), map.getFont(), map.getSize(), map.getStyles(), map.getTextRotation());
                            case "text_ticks" -> textTicks(map);
                            default -> throw new JSONSchemaException(String.format("Unknown value for shape '%s'", shape));
                        };
                        graphicalElements.add(ticks.map(t -> (GraphicalElement) CompositeGraphicalElement.ticks(element, t)).orElse(element));
                    } catch (JSONSchemaException ex) {
                        throw ex.setShape(shape);
                    }
                }
        }
        return graphicalElements;
    }

    static CompositeGraphicalElement textTicks(JSONShapeMap map) throws JSONSchemaException{
        ArrayList<GraphicalElement> elements = new ArrayList<>();
        Color color = map.getColor();
        Point2D point = map.getPoint();
        String font = map.getFont();
        double size = map.getSize();
        int styles = map.getStyles();
        Optional<Double> textRotation = map.getTextRotation();
        List<String> ticksElements = map.getStringArray("text_ticks_elements");
        boolean rotateText = map.getBoolean("text_ticks_rotate_text");
        for (int i = 0; i < ticksElements.size(); i++) {
            String label = ticksElements.get(i);
            Optional<Double> totalRotation = textRotation;
            if (rotateText) {
                totalRotation = Optional.of(textRotation.orElse(0d) + (double) i / (double) ticksElements.size());
            }
            GraphicalElement element = new TextGraphicalElement(color, label, point, font, size, styles, totalRotation);
            element = element.rotate(toRadians(i/ (double) ticksElements.size()));
            elements.add(element);
        }
        return new CompositeGraphicalElement(elements);
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
