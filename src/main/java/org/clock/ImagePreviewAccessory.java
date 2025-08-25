package org.clock;

import com.google.gson.Gson;
import org.clock.styles.gsonstyle.GsonStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;

public class ImagePreviewAccessory extends JLabel implements PropertyChangeListener {

    private final int thumbnailSize = 150;

    public ImagePreviewAccessory() {
        int gap = 10;
        setPreferredSize(new Dimension(thumbnailSize + gap, thumbnailSize + gap));
        setBorder(new EmptyBorder(gap, gap, gap, gap));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
            File newFile = (File) evt.getNewValue();
            if (newFile != null && newFile.isFile()) {
                try {
                    GsonStyle style = new GsonStyle(new Gson(), new FileInputStream(newFile));
                    BufferedImage image = ClockMouseListener.createImage(style, thumbnailSize);
                    setIcon(new ImageIcon(image));
                    return;
                } catch (Exception ignored) {}
            }
            setIcon(null);
        }
    }
}
