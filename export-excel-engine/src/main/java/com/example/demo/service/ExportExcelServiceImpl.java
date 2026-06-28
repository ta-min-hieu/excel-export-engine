package com.example.demo.service;

import com.example.demo.common.excel.engine.config.ExportConfig;
import com.example.demo.common.excel.engine.core.ExcelExportEngine;
import com.example.demo.common.excel.engine.core.WorkbookExport;
import com.example.demo.config.ExcelExportConfig;
import com.example.demo.ultils.TemplateConstants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class ExportExcelServiceImpl implements ExportExcelService {
    private static final String FILE_PATH = "D:/excel-export-file/";

    private final ExcelExportConfig excelExportConfig;
    private ExportConfig exportConfig;

    @PostConstruct
    public void init() {
        exportConfig = new ExportConfig();
        exportConfig.setWindowSize(excelExportConfig.getWindowSize());
    }

    @Override
    public void handleExport(List<?> sheets) throws Exception {
        try {
            Path outputDir = Paths.get(FILE_PATH);
            Files.createDirectories(outputDir);

            try (
                    InputStream template = getClass().getResourceAsStream(TemplateConstants.TEMPLATE_PATH_TEST_V4);
                    OutputStream os = Files.newOutputStream(outputDir.resolve(UUID.randomUUID() + ".xlsx"))
            ) {
                WorkbookExport workbook = WorkbookExport.of(sheets);

                ExcelExportEngine engine = new ExcelExportEngine(exportConfig, template);

                engine.write(workbook);
                engine.finish(os);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
