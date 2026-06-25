package com.example.demo.service;

import com.example.demo.common.ExcelExportEngine;
import com.example.demo.common.ExportConfig;
import com.example.demo.common.FakeMyDtoRepository;
import com.example.demo.common.TemplateConstants;
import com.example.demo.config.ExcelExportConfig;
import com.example.demo.dto.MyExportDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;

@Log4j2
@Service
@RequiredArgsConstructor
public class ExportExcelServiceImpl implements ExportExcelService {
    private static final String FILE_PATH = "D:/excel-export-file/report.xlsx";

    private final ExcelExportConfig excelExportConfig;
    private ExportConfig exportConfig;

    @PostConstruct
    public void init() {
        exportConfig = new ExportConfig();
        exportConfig.setBatchSize(excelExportConfig.getBatchSize());
        exportConfig.setWindowSize(excelExportConfig.getWindowSize());
    }

    @Override
    public void handleExport() throws Exception {
        handleExport(0);
    }

    @Override
    public void handleExport(int sheetAt) throws Exception {
        try (
                InputStream template = getClass().getResourceAsStream(TemplateConstants.TEMPLATE_PATH_TEST_V3);
                OutputStream os = new FileOutputStream(FILE_PATH)
        ) {
            MyExportDto dto = new MyExportDto();

            ExcelExportEngine engine = new ExcelExportEngine(exportConfig, template, sheetAt);

            FakeMyDtoRepository repo = new FakeMyDtoRepository();

            dto.setStartDate(LocalDate.now().minusDays(1));
            dto.setEndDate(LocalDate.now());
            dto.setDatas(repo.fetchBatch(0, exportConfig.getBatchSize()));

            engine.write(dto);   // 👈 CORE CALL

            engine.finish(os);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
