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

import org.clock.graphical.OffsetRadius;

import java.awt.*;

/**
 * Style of clock.
 */
public interface Style {
    default boolean discreteTimeIntervals() {
        return false;
    }
    String getName();
    void paintClockFace(Graphics2D graphics2D, OffsetRadius offsetRadius, HoursMinutesSeconds hoursMinutesSeconds);
    void paintMinuteHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double minute);
    void paintHourHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double hour);
    void paintSecondHand(Graphics2D graphics2D, OffsetRadius offsetRadius, double second);

    default boolean staticClockFace() {
        return true;
    }
}
