package com.example.demo.service;

import java.util.List;

public interface ExportExcelService {
    void handleExport(List<?> sheets) throws Exception;
}
