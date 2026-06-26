package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class MyExport2Dto {

    private String name;

    private List<MyDto> d;
}
