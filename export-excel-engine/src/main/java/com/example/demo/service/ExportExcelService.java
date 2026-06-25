package com.example.demo.service;

public interface ExportExcelService {
    void handleExport() throws Exception;
    void handleExport(int sheetAt) throws Exception;
}
