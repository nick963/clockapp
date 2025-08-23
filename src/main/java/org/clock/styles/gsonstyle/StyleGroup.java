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

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class StyleGroup {
    private final String name;
    private final List<GsonStyle> styles;

    public StyleGroup(String name, List<GsonStyle> styles) {
        this.name = name;
        this.styles = new ArrayList<>(styles);
    }

    public String getName() {
        return name;
    }

    public List<GsonStyle> getStyles() {
        return styles;
    }

    static StyleGroup loadFromResource(String directoryResource) throws JSONSchemaException {
        Gson gson = new Gson();
        String propertiesResource = directoryResource + "/group.properties";
        try (InputStream groupPropertiesStream = StyleGroup.class.getResourceAsStream(propertiesResource)) {
            Properties groupProperties = new Properties();
            groupProperties.load(groupPropertiesStream);
            String name = groupProperties.getProperty("name");
            String stylesString = groupProperties.getProperty("styles");
            String[] styleIds = stylesString.split(",");
            ArrayList<GsonStyle> gsonStyleList = new ArrayList<>();
            for (String styleId : styleIds) {
                InputStream styleStream;
                String resource = directoryResource + "/" + styleId + ".json";
                styleStream = StyleGroup.class.getResourceAsStream(resource);

                if (styleStream == null) {
                    throw new RuntimeException("Resource InputStream is null: " + resource);
                }
                try {
                    gsonStyleList.add(new GsonStyle(gson, styleStream));
                } catch (JSONSchemaException exception) {
                    throw exception.setResource(resource);
                }
            }
            return new StyleGroup(name, gsonStyleList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
