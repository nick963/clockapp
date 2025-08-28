package org.clock;

import org.clock.styles.gsonstyle.StyleGroups;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;

public class StyleUtils {
    static BufferedImage createImage(Style style, int dimension) {
        ClockComponent clockPanel = new ClockComponent(new StyleGroups("", new ArrayList<>()), Calendar::getInstance);
        clockPanel.setSize(dimension,dimension);
        clockPanel.setStyle(style);
        BufferedImage image = new BufferedImage(dimension, dimension, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, dimension, dimension); // Fill the entire image with transparent pixels
        graphics.setComposite(AlphaComposite.Src);
        Ellipse2D ellipse = new Ellipse2D.Double(0, 0, dimension, dimension);
        graphics.setClip(ellipse);
        clockPanel.paint(graphics);
        return image;
    }
}
