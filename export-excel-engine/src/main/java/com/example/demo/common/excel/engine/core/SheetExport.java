package com.example.demo.common.excel.engine.core;

public record SheetExport<T>(int sheetIndex, String sheetName, T data) {
}
