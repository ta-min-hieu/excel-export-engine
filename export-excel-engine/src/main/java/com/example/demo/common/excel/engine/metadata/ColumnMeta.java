package com.example.demo.common.excel.engine.metadata;

import java.lang.invoke.MethodHandle;

public record ColumnMeta(
        int index,
        MethodHandle getter,
        ColumnType type
) {}
