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

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

record JSONShapeMap(Map<?, ?> map) {
    String getShape() {
        return (String) map.get("shape");
    }

    Color getColor() throws JSONSchemaException {
        return readColor(map.get("color"));
    }

    java.util.List<Point2D> getPoints() throws JSONSchemaException {
        java.util.List<?> pointList = (java.util.List<?>) map.get("points");
        List<Point2D> points = new ArrayList<>();
        for (Object o : pointList) {
            JSONShapeMap m = new JSONShapeMap((Map<?, ?>) o);
            points.add(m.readPoint("x", "y"));
        }
        return points;
    }

    Point2D getPoint() throws JSONSchemaException {
        return getPoint("x", "y");
    }

    Point2D getPoint(String xKey, String yKey) throws JSONSchemaException {
        return readPoint(xKey, yKey);
    }

    double getRadius() {
        return (double) map.get("radius");
    }

    double getSize() {
        return (double) map.get("size");
    }

    String getText() {
        return (String) map.get("text");
    }

    String getFont() {
        return (String) map.get("font");
    }

    int getStyles() {
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
        return styles;
    }

    Optional<Integer> getTicks() throws JSONSchemaException {
        Object object = map.get("ticks");
        if (object != null) {
            if (object instanceof Double d && d == (int) (double) d && d >= 2d && d < 61d) {
                return Optional.of((int) (double) d);
            }
            throw new JSONSchemaException("Key value \"ticks\" must be a whole number between 2 and 60.");
        }
        return Optional.empty();
    }

    private Point2D readPoint(String xKey, String yKey) throws JSONSchemaException {
        if (!map.containsKey(xKey) || !map.containsKey(yKey)) {
            throw new JSONSchemaException(String.format("Point specified incorrectly (expecting: %s = <double>, %s = <double>).", xKey, yKey));
        }
        return new Point2D.Double((double) map.get(xKey), (double) map.get(yKey));
    }

    private static Optional<Color> parseColorWithAlpha(String hexString) {
        if (hexString.length() != 9 || hexString.charAt(0) != '#') {
            return Optional.empty();
        }
        // Remove the '#' prefix
        String hexWithoutPrefix = hexString.substring(1);
        // Parse the hexadecimal string to an integer
        int colorInt = Integer.parseUnsignedInt(hexWithoutPrefix, 16);
        // Create the Color object
        return Optional.of(new Color(colorInt, true));
    }

    private static Color readColor(Object object) throws JSONSchemaException {
        if (object == null) {
            return Color.BLACK;
        }
        if (object instanceof String string) {
            try {
                return parseColorWithAlpha(string).orElseGet(() -> Color.decode(string));
            } catch (NumberFormatException ignored) {
                // no-op, will throw exception below.
            }
        }
        throw new JSONSchemaException("Invalid color format, expecting \"#FFFFFF\" format.");
    }
}
