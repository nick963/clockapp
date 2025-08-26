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
package org.clock;

import com.google.gson.Gson;
import org.clock.styles.gsonstyle.GsonStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.function.Supplier;

public class ClockPreviewAccessory extends ClockPanel implements PropertyChangeListener {

    public ClockPreviewAccessory(Supplier< Calendar > calendarSupplier) {
        super(calendarSupplier);
        final int gap = 10;
        final int thumbnailSize = 150;
        setPreferredSize(new Dimension(thumbnailSize + gap, thumbnailSize + gap));
        setBorder(new EmptyBorder(gap, gap, gap, gap));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
            File newFile = (File) evt.getNewValue();
            if (newFile != null && newFile.isFile()) {
                try {
                    setStyle(new GsonStyle(new Gson(), new FileInputStream(newFile)));
                    return;
                } catch (Exception ignored) {}
            }
            setStyle(null);
        }
    }
}
