package com.example.demo.common;

import java.lang.invoke.MethodHandle;

public record ColumnMeta(
        int index,
        MethodHandle getter,
        ColumnType type
) {}
