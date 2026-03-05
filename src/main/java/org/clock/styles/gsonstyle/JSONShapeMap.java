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
import java.util.*;
import java.util.List;

record JSONShapeMap(Map<?, ?> map) {
    String getShape() throws JSONSchemaException {
        try {
            return getString("shape");
        } catch (ClassCastException classCastException) {
            throw new JSONSchemaException("Value for 'shape' is expected to be a string.");
        }
    }

    Color getColor() throws JSONSchemaException {
        return readColor(map.get("color"));
    }

    boolean getBoolean(String key) throws JSONSchemaException {
        if (!map.containsKey(key)) {
            return false;
        }
        try {
            return (Boolean) map.get(key);
        } catch (ClassCastException|NullPointerException classCastException) {
            throw new JSONSchemaException("Value for '"+key+"' is expected to be a boolean.");
        }
    }

    java.util.List<Point2D> getPoints() throws JSONSchemaException {
        try {
            java.util.List<?> pointList = (java.util.List<?>) map.get("polygon_points");
            List<Point2D> points = new ArrayList<>();
            for (Object o : pointList) {
                JSONShapeMap m = new JSONShapeMap((Map<?, ?>) o);
                points.add(m.readPoint("x", "y"));
            }
            return points;
        } catch (ClassCastException|NullPointerException classCastException) {
            throw new JSONSchemaException("Value for 'polygon_points' is expected to be a list of objects.");
        }
    }

    List<String> getStringArray(String key) throws JSONSchemaException {
        try {
            List<?> list = (List<?>) map.get(key);
            return list.stream().map(v -> (String) v).toList();
        } catch (ClassCastException|NullPointerException classCastException) {
            throw new JSONSchemaException("Value for '"+key+"' is expected to be a list of strings.");
        }
    }

    Point2D getPoint() throws JSONSchemaException {
        return getPoint("x", "y");
    }

    Point2D getPoint(String xKey, String yKey) throws JSONSchemaException {
        return readPoint(xKey, yKey);
    }

    double getRadius() throws JSONSchemaException {
        try {
            return getDouble("circle_radius");
        } catch (JSONSchemaException ex) {
            ex.setProperty("circleRadius");
            throw ex;
        }
    }

    double getSize() throws JSONSchemaException {
        return getDouble("text_size");
    }

    String getText() throws JSONSchemaException {
        return getString("text");
    }

    String getFont() throws JSONSchemaException {
        return getString("text_font");
    }

    String getString(String key) throws JSONSchemaException {
        try {
            return (String) map.get(key);
        } catch (ClassCastException classCastException) {
            throw new JSONSchemaException(String.format("Value for '%s' is expected to be a string.", key));
        }
    }

    double getDouble(String key) throws JSONSchemaException {
        try {
            return (double) map.get(key);
        } catch (ClassCastException|NullPointerException classCastException) {
            throw new JSONSchemaException(String.format("Value for '%s' is expected to be a double.", key));
        }
    }

    double getDouble(String key, double defaultValue) throws JSONSchemaException {
        try {
            return map.containsKey(key) ? (double) map.get(key) : defaultValue;
        } catch (ClassCastException|NullPointerException classCastException) {
            throw new JSONSchemaException(String.format("Value for '%s' is expected to be a double.", key));
        }
    }

    int getStyles() throws JSONSchemaException {
        String stylesString = getString("test_styles");
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

    Optional<Double> getTextRotation() throws JSONSchemaException {
        if (map.containsKey("text_rotation")) {
            return Optional.of(getDouble("text_rotation"));
        }
        return Optional.empty();
    }

    Optional<Integer> getTicks() throws JSONSchemaException {
        Object object = map.get("ticks");
        if (object != null) {
            if (object instanceof Double d && d == (int) (double) d && d >= 1d && d < 61d) {
                return Optional.of((int) (double) d);
            }
            throw new JSONSchemaException("Key value \"ticks\" must be a whole number between 1 and 60.");
        }
        return Optional.empty();
    }

    private Point2D readPoint(String xKey, String yKey) throws JSONSchemaException {
        return new Point2D.Double(getDouble(xKey, 0d), getDouble(yKey, 0d));
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
        throw new JSONSchemaException("Invalid color format, expecting string value in \"#FFFFFF\" format.");
    }
}
