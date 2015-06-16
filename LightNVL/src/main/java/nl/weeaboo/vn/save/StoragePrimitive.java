package nl.weeaboo.vn.save;

import nl.weeaboo.common.StringUtil;

public final class StoragePrimitive {

    private static final String[] stringEscapeFrom = { "\\", "\n", "\r", "\f", "\t", "\"", "\'" };
    private static final String[] stringEscapeTo = { "\\\\", "\\n", "\\r", "\\f", "\\t", "\\\"", "\\'" };

    private final Object value;

    private StoragePrimitive(Object value) {
        this.value = value;
    }

    protected static String escapeString(String string) {
        return StringUtil.replaceAll(string, stringEscapeFrom, stringEscapeTo);
    }

    protected static String unescapeString(String string) {
        return StringUtil.replaceAll(string, stringEscapeTo, stringEscapeFrom);
    }

    /**
     * @return The JSON value converted to a {@link StoragePrimitive} object, or {@code null} if the JSON
     *         value couldn't be parsed.
     */
    public static StoragePrimitive fromJson(String json) {
        // null
        if (json.equals("null")) {
            return new StoragePrimitive(null);
        }

        // boolean
        if (json.equals("true")) {
            return new StoragePrimitive(Boolean.TRUE);
        } else if (json.equals("false")) {
            return new StoragePrimitive(Boolean.FALSE);
        }

        // number
        try {
            return new StoragePrimitive(Double.parseDouble(json));
        } catch (NumberFormatException nfe) {
            // Not a number
        }

        // string
        if (json.startsWith("\"") && json.endsWith("\"")) {
            String str = unescapeString(json.substring(1, json.length() - 1));
            return new StoragePrimitive(str);
        }

        return new StoragePrimitive(json);
    }

    public String toJson() {
        if (value instanceof String) {
            // string
            return "\"" + escapeString(value.toString()) + "\"";
        }

        if (value instanceof Number) {
            // number
            double doubleValue = ((Number)value).doubleValue();
            int intValue = (int)doubleValue;
            if (doubleValue == intValue) {
                return Integer.toString(intValue);
            } else {
                return Double.toString(doubleValue);
            }
        }

        // boolean, null
        return String.valueOf(value);
    }

    @Override
    public int hashCode() {
        return (value != null ? value.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StoragePrimitive)) {
            return false;
        }
        StoragePrimitive p = (StoragePrimitive) obj;
        return value == p.value || (value != null && value.equals(p.value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static StoragePrimitive fromBoolean(boolean value) {
        return new StoragePrimitive(value);
    }

    public boolean toBoolean(boolean defaultValue) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else if (value instanceof String) {
            return ((String) value).equalsIgnoreCase("true");
        }
        return defaultValue;
    }

    public static StoragePrimitive fromDouble(double value) {
        return new StoragePrimitive(value);
    }

    public double toDouble(double defaultValue) {
        if (value instanceof Number) {
            Number number = (Number) value;
            return number.doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble(String.valueOf(value));
            } catch (NumberFormatException nfe) {
                // Value is not convertible to double
            }
        }
        return defaultValue;
    }

    public static StoragePrimitive fromString(String value) {
        return new StoragePrimitive(value);
    }

    public String toString(String defaultValue) {
        if (value != null) {
            return value.toString();
        }
        return defaultValue;
    }

}
