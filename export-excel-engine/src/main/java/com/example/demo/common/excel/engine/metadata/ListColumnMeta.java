package com.example.demo.common.excel.engine.metadata;

import org.apache.poi.ss.usermodel.CellStyle;

public record ListColumnMeta(
        int columnIndex,
        String listName,
        String fieldName,
        CellStyle style
) {}
