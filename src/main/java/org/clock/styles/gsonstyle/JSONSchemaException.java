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

public class JSONSchemaException extends Exception {
    private String resource;
    private String shape;

    public JSONSchemaException(String message) {
        super(message);
    }

    public JSONSchemaException setResource(String resource) {
        this.resource = resource;
        return this;
    }

    public JSONSchemaException setShape(String shape) {
        this.shape = shape;
        return this;
    }

    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        if (resource != null) {
            builder.append("Resource: \"").append(resource).append("\"\n");
        }
        if (shape != null) {
            builder.append("Shape: \"").append(shape).append("\"\n");
        }
        builder.append(super.getMessage());
        return builder.toString();
    }
}
