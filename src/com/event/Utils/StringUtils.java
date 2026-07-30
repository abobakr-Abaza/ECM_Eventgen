package com.event.Utils;

public class StringUtils {
    
    /**
     * Returns the input string or empty string if null
     */
    public static String safeString(String s) {
        return s == null ? "" : s;
    }
    
    /**
     * Escapes backslashes and quotes for Java string literals
     */
    public static String escapeForJava(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    /**
     * Converts string to valid Java variable name (ASCII only)
     * Replaces invalid characters with underscores
     */
    public static String safeVar(String s) {
        if (s == null) return "var";
        return s.replaceAll("[^A-Za-z0-9_]", "_");
    }
    
    /**
     * Converts string to valid Java variable name (supports Unicode)
     * Replaces invalid characters with underscores
     */
    public static String safeVarUnicode(String s) {
        if (s == null) return "var";
        return s.replaceAll("[^\\p{L}\\p{N}_]", "_");
    }
    public static String sanitizeIdentifier(String name) {
        if (name == null || name.isEmpty()) return "unnamed";
        
        // Replace spaces with underscore
        String sanitized = name.replaceAll("\\s+", "_");
        
        // Remove any character that is not letter, digit or underscore
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9_\\u0600-\\u06FF]", "_");
        
        // Ensure it doesn't start with a digit
        if (Character.isDigit(sanitized.charAt(0)))
            sanitized = "_" + sanitized;
        //        System.out.println(sanitized);
        return sanitized;
        
    }
}