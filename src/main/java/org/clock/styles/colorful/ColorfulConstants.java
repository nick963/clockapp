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
package org.clock.styles.colorful;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ColorfulConstants {

    public static List<Color> COLORS = Arrays.asList(
            new Color(56, 102, 65),
            new Color(249, 122, 0),
            new Color(254, 209, 106),
            new Color(255, 244, 164),
            new Color(56, 102, 65));

    public static Color[] FACE_BACKGROUND_COLORS;
    // Define the fractions for the color stops (where each color appears)
    public static float[] FACE_BACKGROUND_COLOR_FRACTIONS;

    static {
        int size = COLORS.size();
        FACE_BACKGROUND_COLORS = new Color[size];
        FACE_BACKGROUND_COLOR_FRACTIONS = new float[size];
        for (int i = 0; i < size; i++) {
            FACE_BACKGROUND_COLORS[i] = COLORS.get(i);
            FACE_BACKGROUND_COLOR_FRACTIONS[i] = i/(float) (FACE_BACKGROUND_COLOR_FRACTIONS.length - 1);
        }
        System.out.println(Arrays.toString(FACE_BACKGROUND_COLOR_FRACTIONS));
    }

}
