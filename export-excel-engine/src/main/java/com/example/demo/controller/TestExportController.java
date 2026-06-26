package com.example.demo.controller;

import com.example.demo.service.ExportExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/test-export")
public class TestExportController {
    private final ExportExcelService exportExcelService;

    @GetMapping("/export")
    public ResponseEntity<?> export() throws Exception {
        long start = System.currentTimeMillis();

        exportExcelService.handleExport();

        long end = System.currentTimeMillis() - start;
        log.info("End: {} ms", end);

        return new ResponseEntity<>(end, HttpStatus.OK);
    }
}
