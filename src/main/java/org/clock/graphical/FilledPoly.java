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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.function.Function;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class FilledPoly implements GraphicalElement {
    protected final Point2D[] points;
    private final Paint paint;

    public FilledPoly(Paint paint, Point2D... points) {
        this.paint = paint;
        this.points = Arrays.copyOf(points, points.length);
    }

    public void draw(Graphics2D graphics2D) {
        Path2D path2D = new Path2D.Double();
        path2D.moveTo(points[0].getX(), points[0].getY());
        for (int i = 1; i < points.length; i++) {
            path2D.lineTo(points[i].getX(), points[i].getY());
        }
        path2D.closePath();
        graphics2D.setPaint(paint);
        graphics2D.fill(path2D);
    }

    public FilledPoly transform(Function<Point2D, Point2D> func) {
        Point2D[] newPoints = new Point2D[points.length];
        int index = 0;
        for (Point2D p : points) {
            newPoints[index] = func.apply(p);
            index++;
        }
        return new FilledPoly(paint, newPoints);
    }

    public FilledPoly join(FilledPoly another) {
        Point2D[] newPoints = new Point2D[points.length + another.points.length];
        System.arraycopy(points, 0, newPoints, 0, points.length);
        System.arraycopy(another.points, 0, newPoints, points.length, another.points.length);
        return new FilledPoly(paint, newPoints);
    }

    public FilledPoly shift(double xOffset, double yOffset) {
        return transform(p -> new Point2D.Double(
                p.getX() + xOffset,
                p.getY() + yOffset));
    }
    public FilledPoly scale(double scale) {
        return scale(scale, scale);
    }

    public FilledPoly scale(double xScale, double yScale) {
        return transform(p -> new Point2D.Double(
                p.getX() * xScale,
                p.getY() * yScale));
    }

    public FilledPoly rotate(double theta) {
        return transform(p -> rotatePoint(theta, p));
    }

    public FilledPoly adjust(OffsetRadius offsetRadius) {
        return transform(offsetRadius::adjust);
    }
    public static Point2D rotatePoint(double theta, Point2D point2D) {
        return new Point2D.Double(
                point2D.getX() * cos(theta) - point2D.getY() * sin(theta),
                point2D.getX() * sin(theta) + point2D.getY() * cos(theta));
    }

    public static FilledPoly rectangle(Color color, Point2D fp, Point2D tp) {
        return new FilledPoly(color, fp, new Point2D.Double(fp.getX(), tp.getY()), tp, new Point2D.Double(tp.getX(), fp.getY()));
    }
}
