package com.example.demo.service;

import com.example.demo.common.*;
import com.example.demo.config.ExcelExportConfig;
import com.example.demo.dto.MyExport2Dto;
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
        try (
                InputStream template = getClass().getResourceAsStream(TemplateConstants.TEMPLATE_PATH_TEST_V4);
                OutputStream os = new FileOutputStream(FILE_PATH)
        ) {
            FakeMyDtoRepository repo = new FakeMyDtoRepository();
            FakeMyDto2Repository repo2 = new FakeMyDto2Repository();

            MyExportDto dto1 = new MyExportDto();
            dto1.setStartDate(LocalDate.now().minusDays(1));
            dto1.setEndDate(LocalDate.now());
            dto1.setDatas(repo.fetchBatch(0, exportConfig.getBatchSize()));

            MyExport2Dto dto2 = new MyExport2Dto();
            dto2.setName("hieu.tm");
            dto2.setD(repo2.fetchBatch(0, exportConfig.getBatchSize()));

            // =========================
            // BUILD WORKBOOK EXPORT
            // =========================
            WorkbookExport workbook = WorkbookExport.builder()
                    .sheet(0, dto1)
                    .sheet(1, dto2)
                    .build();

            ExcelExportEngine engine = new ExcelExportEngine(exportConfig, template);

            engine.write(workbook);
            engine.finish(os);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
