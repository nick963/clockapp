package org.clock.styles.gsonstyle;

import org.clock.styles.colorful.ColorfulStyle;
import org.clock.styles.metro.MetroStyle;
import org.clock.styles.quartz.QuartzStyle;
import org.junit.Test;

public class StylesValidTest {
    @Test
    public void validate() throws Exception {
        new MetroStyle();
        new QuartzStyle();
        new ColorfulStyle();
        GsonStyle.toGsonStyles();
        StyleGroups.loadFromResource("/json/styles");
    }
}
