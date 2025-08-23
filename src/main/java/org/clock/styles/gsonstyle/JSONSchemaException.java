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
