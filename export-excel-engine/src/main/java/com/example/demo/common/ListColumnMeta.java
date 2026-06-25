package com.example.demo.common;

import org.apache.poi.ss.usermodel.CellStyle;

public record ListColumnMeta(
        int columnIndex,
        String listName,
        String fieldName,
        CellStyle style
) {}
