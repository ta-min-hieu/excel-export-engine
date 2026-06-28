package com.example.demo.controller;

import com.example.demo.dto.MyExport2Dto;
import com.example.demo.dto.MyExportDto;
import com.example.demo.repository.FakeMyDto2Repository;
import com.example.demo.repository.FakeMyDtoRepository;
import com.example.demo.service.ExportExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/test-export")
public class TestExportController {
    private final ExportExcelService exportExcelService;

    @GetMapping("/export")
    public ResponseEntity<?> export() throws Exception {
        long start = System.currentTimeMillis();

        FakeMyDtoRepository repo = new FakeMyDtoRepository();
        FakeMyDto2Repository repo2 = new FakeMyDto2Repository();

        MyExportDto dto1 = new MyExportDto();
        dto1.setStartDate(LocalDate.now().minusDays(1));
        dto1.setEndDate(LocalDate.now());
        dto1.setDatas(repo.fetchBatch(0, 10000));

        MyExport2Dto dto2 = new MyExport2Dto();
        dto2.setName("hieu.tm");
        dto2.setD(repo2.fetchBatch(0, 10000));

        List<?> sheets = List.of(dto1, dto2);

        exportExcelService.handleExport(sheets);

        long end = System.currentTimeMillis() - start;
        log.info("End: {} ms", end);

        return new ResponseEntity<>(end, HttpStatus.OK);
    }
}
