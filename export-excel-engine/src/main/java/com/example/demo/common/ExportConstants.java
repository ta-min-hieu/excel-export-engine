package com.example.demo.common;

import java.util.regex.Pattern;

public class ExportConstants {
    public static final Pattern GLOBAL_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    public static final Pattern DATA_PATTERN = Pattern.compile("\\*\\{([^}]+)}");

    public static final Pattern LIST_PATTERN = Pattern.compile("([a-zA-Z0-9_]+)\\.(.+)");
}
