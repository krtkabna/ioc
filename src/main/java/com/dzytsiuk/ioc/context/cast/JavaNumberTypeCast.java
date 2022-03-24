package com.dzytsiuk.ioc.context.cast;


public class JavaNumberTypeCast {

    private static final String SHORT = "short";
    private static final String LONG = "long";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String BYTE = "byte";
    private static final String BOOLEAN = "boolean";
    private static final String INT = "int";

    public static Object castPrimitive(String value, Class<?> clazz) {
        switch (clazz.getName()) {
            case BYTE:
                return Byte.valueOf(value);
            case SHORT:
                return Short.valueOf(value);
            case INT:
                return Integer.valueOf(value);
            case LONG:
                return Long.valueOf(value);
            case FLOAT:
                return Float.valueOf(value);
            case DOUBLE:
                return Double.valueOf(value);
            case BOOLEAN:
                return Boolean.valueOf(value);
            default:
                return null;
        }
    }
}
