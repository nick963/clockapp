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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.clock.ClockUtils.paintOnClock;

public class GsonStyle implements Style {

    public static final String DEFAULT_JSON_RESOURCE = "/graphical-artifacts.json";

    private final String name;
    private final List<GraphicalElement> clockFaceElements;
    private final List<GraphicalElement> secondHandElements;
    private final List<GraphicalElement> minuteHandElements;
    private final List<GraphicalElement> hourHandElements;
    @Override
    public String getName() {
        return name;
    }

    public static Collection<GsonStyle> toGsonStyles() {
        return toGsonStyles(DEFAULT_JSON_RESOURCE);
    }

    public static Collection<GsonStyle> toGsonStyles(String resource) {
        List<Map<?, ?>> maps = toMaps(resource);
        ArrayList<GsonStyle> styles = new ArrayList<>();
        for (Map<?,?> map : maps) {
            styles.add(new GsonStyle(map));
        }
        return styles;
    }

    private static List<Map<?,?>> toMaps(String jsonResource) {
        try {
            try (InputStream stream = GsonStyle.class.getResourceAsStream(jsonResource)) {
                if (stream == null) {
                    throw new NullPointerException("unable to read resource: " + jsonResource);
                }
                return toMaps(stream);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static List<Map<?,?>> toMaps(InputStream stream) {
        try {
            String jsonString = new String(stream.readAllBytes());
            Gson gson = new Gson();
            List<?> list = gson.fromJson(jsonString, List.class);
            ArrayList<Map<?,?>> mapList = new ArrayList<>();
            list.forEach(o -> mapList.add((Map<?, ?>) o));
            return mapList;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public GsonStyle(Map<?, ?> josonClockMap) {
        name = josonClockMap.get("name").toString();

        clockFaceElements= toGraphicalElements((List<?>)josonClockMap.get("clock_face"));
        secondHandElements = toGraphicalElements((List<?>)josonClockMap.get("second_hand"));
        minuteHandElements = toGraphicalElements((List<?>)josonClockMap.get("minute_hand"));
        hourHandElements = toGraphicalElements((List<?>)josonClockMap.get("hour_hand"));
    }

    public GsonStyle(Gson gson, InputStream styleStream) throws IOException {
        this(gson.fromJson(new String(styleStream.readAllBytes()), Map.class));
    }

    List<GraphicalElement> toGraphicalElements(List<?> shapeList) {
        ArrayList<GraphicalElement> graphicalElements = new ArrayList<>();
        if (shapeList != null) {
            for (Object object : shapeList) {
                var map = (Map<?,?>)object;
                String shape = (String) map.get("shape");
                if (shape.equals("polygon")) {
                    Color color = readColor(map.get("color"));
                    List<?> pointList = (List<?>) map.get("points");
                    List<Point2D> points = new ArrayList<>();
                    for (Object o : pointList) {
                        Map<?, ?> m = (Map<?, ?>) o;
                        points.add(readPoint(m, "x","y"));
                    }
                    graphicalElements.add(new FilledPoly(color, points.toArray(new Point2D[0])));
                } else if (shape.equals("rectangle")) {
                    Color color = readColor(map.get("color"));
                    Point2D fp = readPoint(map, "fx", "fy");
                    Point2D tp = readPoint(map, "tx", "ty");
                    graphicalElements.add(new FilledPoly(color, fp, new Point2D.Double(fp.getX(), tp.getY()), tp, new Point2D.Double(tp.getX(), fp.getY())));
                } else if (shape.equals("circle")) {
                    Color color = readColor(map.get("color"));
                    double radius = (double) map.get("radius");
                    graphicalElements.add(new CircleGraphicalElement(color, readPoint(map, "cx", "cy"), radius));
                } else if (shape.equals("text")) {
                    Color color = readColor(map.get("color"));
                    Point2D center = readPoint(map, "cx", "cy");
                    String text = (String) map.get("text");
                    String font = (String) map.get("font");
                    double size = (double) map.get("size");
                    String stylesString = (String) map.get("styles");
                    int styles = 0;
                    if (stylesString != null) {
                        String[] array = stylesString.split(",");
                        for (String token : array) {
                            styles |= switch (token) {
                                case "bold" -> Font.BOLD;
                                case "italic" -> Font.ITALIC;
                                case "plain" -> Font.PLAIN;
                                default -> throw new RuntimeException("unknown style:" + token);
                            };
                        }

                    }
                    graphicalElements.add(new TextGraphicalElement(color, text, center, font, size, styles));
                } else {
                    throw new RuntimeException("Unknown shape:" + shape);
                }
            }
        }
        return graphicalElements;
    }

    static Point2D readPoint(Map<?,?> map, String xKey, String yKey) {
        return new Point2D.Double((double) map.get(xKey), (double) map.get(yKey));
    }

    private static Color readColor(Object object) {
        if (object == null) {
            return Color.BLACK;
        }
        if (object instanceof String) {
            return Color.decode((String) object);
        }
        Map<?, ?> colorMap = (Map<?, ?>)object;
        return new Color(
                Float.parseFloat(colorMap.get("r").toString()) / 255f,
                Float.parseFloat(colorMap.get("g").toString()) / 255f,
                Float.parseFloat(colorMap.get("b").toString()) / 255f);
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
