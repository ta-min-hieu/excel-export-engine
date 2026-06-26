package com.example.demo.common;

public record SheetExport<T>(int sheetIndex, String sheetName, T data) {
}
