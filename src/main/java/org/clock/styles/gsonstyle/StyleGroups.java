package org.clock.styles.gsonstyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public record StyleGroups(String name, List<StyleGroup> groups) {
    public static StyleGroups loadFromResource(String directoryResource) throws JSONSchemaException {
        String propertyResource = directoryResource + "/groups.properties";
        try (InputStream groupsPropertiesStream = StyleGroups.class.getResourceAsStream(propertyResource)) {
            Properties groupsProperties = new Properties();
            groupsProperties.load(groupsPropertiesStream);
            String name = groupsProperties.getProperty("name");
            String groupsString = groupsProperties.getProperty("groups");
            String[] groupDirectoryNames = groupsString.split(",");
            ArrayList<StyleGroup> groupList = new ArrayList<>();
            for (String groupDirectoryName : groupDirectoryNames) {
                StyleGroup styleGroup = StyleGroup.loadFromResource(directoryResource + "/" + groupDirectoryName);
                groupList.add(styleGroup);
            }
            return new StyleGroups(name, groupList);
        } catch (IOException ex) {
            throw new JSONSchemaException("IOException when reading property resource: " + propertyResource);
        }
    }
}
