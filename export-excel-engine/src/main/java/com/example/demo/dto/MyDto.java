package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyDto {
    private Long id;
    private String username;
    private Integer age;
    private Long balance;
    private BigDecimal amount;
    private Boolean active;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private Double score;
    private String note;
    private String phoneNumber;
}
