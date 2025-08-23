package org.clock.styles.gsonstyle;

import org.clock.ClockPanel;
import org.clock.styles.colorful.ColorfulStyle;
import org.clock.styles.metro.MetroStyle;
import org.junit.Assert;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;

public class StylesValidTest {
    @Test
    public void validate() throws Exception {
        new MetroStyle();
        new ColorfulStyle();
        StyleGroups groups = StyleGroups.loadFromResource("/json/styles");
        ClockPanel clockPanel = new ClockPanel(new ArrayList<>(), groups, Calendar::getInstance);
        clockPanel.setSize(300, 300);
        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_4BYTE_ABGR);
        for (StyleGroup group : groups.groups()) {
            for (GsonStyle style : group.getStyles()) {
                clockPanel.setGroupAndStyle(new ClockPanel.GroupAndStyle(group, style));
                long time = System.currentTimeMillis();
                clockPanel.paint(image.getGraphics());
                long time2 = System.currentTimeMillis();
                clockPanel.paint(image.getGraphics());
                long time3 = System.currentTimeMillis();
                Assert.assertTrue(style.getName(),time2 - time < 5000 );
                Assert.assertTrue(style.getName(),time3 - time2 < 10);
            }
        }
    }
}
