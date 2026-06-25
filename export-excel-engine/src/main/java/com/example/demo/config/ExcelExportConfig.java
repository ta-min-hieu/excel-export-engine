package com.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ExcelExportConfig {

    @Value("${export-excel.batch-size}")
    private int batchSize;

    @Value("${export-excel.window-size}")
    private int windowSize;
}
