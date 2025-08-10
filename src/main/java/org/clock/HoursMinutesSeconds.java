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

import java.util.Calendar;

public record HoursMinutesSeconds(double hours, double minutes, double seconds) {

    public static HoursMinutesSeconds getHoursMinutesSeconds(Calendar calendar, boolean discreteTimeIntervals) {
        double millis = calendar.get(Calendar.MILLISECOND);
        double seconds = calendar.get(Calendar.SECOND);
        double minutes = calendar.get(Calendar.MINUTE);
        double hours = calendar.get(Calendar.HOUR_OF_DAY);
        if (hours > 11) {
            hours -= 12;
        }
        if (!discreteTimeIntervals) {
            seconds += millis / 1000d;
            minutes += seconds / 60d;
            hours += minutes / 60d;
        }
        return new HoursMinutesSeconds(hours, minutes, seconds);
    }
}
