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
