package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MyExportDto {
    private LocalDate startDate;
    private LocalDate endDate;

    private List<MyDto> datas;
}
