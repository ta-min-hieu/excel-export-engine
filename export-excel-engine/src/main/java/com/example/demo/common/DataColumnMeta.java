package com.example.demo.common;

import org.apache.poi.ss.usermodel.CellStyle;

public record DataColumnMeta(
        int columnIndex,
        String fieldName,
        CellStyle style,
        ColumnType type
) {}
