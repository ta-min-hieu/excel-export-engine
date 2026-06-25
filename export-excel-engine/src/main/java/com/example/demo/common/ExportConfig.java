package com.example.demo.common;

import lombok.Data;

@Data
public class ExportConfig {
    private int batchSize;   // 5k - 10k - 50k tùy hệ thống
    private int windowSize;   // SXSSF window
}
