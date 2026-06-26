package com.example.demo.common;

public class SheetExport<T> {

    private final int sheetIndex;

    private final String sheetName;

    private final T data;

    public SheetExport(int sheetIndex, String sheetName, T data) {
        this.sheetIndex = sheetIndex;
        this.sheetName = sheetName;
        this.data = data;
    }

    public int getSheetIndex() {
        return sheetIndex;
    }

    public String getSheetName() {
        return sheetName;
    }

    public T getData() {
        return data;
    }
}
