package com.example.demo.common;

import com.example.demo.shs.DateConstant;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CellWriter {
    private static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern(DateConstant.STR_PLAN_DD_MM_YYYY_STROKE);
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(DateConstant.STR_PLAN_DD_MM_YYYY_HH_MM_SS_STROKE);
    private static final DateTimeFormatter LOCAL_TIME_FORMAT = DateTimeFormatter.ofPattern(DateConstant.ISO_8601_EXTENDED_TIME_FORMAT);

    public static void write(Cell cell, Object value, ColumnType type) {
        if (value == null) return;

        switch (type) {

            case STRING -> cell.setCellValue((String) value);
            case INTEGER, SHORT, BYTE -> cell.setCellValue(((Number) value).intValue());
            case LONG -> cell.setCellValue(((Number) value).longValue());
            case FLOAT, DOUBLE -> cell.setCellValue(((Number) value).doubleValue());
            case BIG_DECIMAL -> cell.setCellValue(((BigDecimal) value).doubleValue());
            case BIG_INTEGER -> cell.setCellValue(((BigInteger) value).doubleValue());
            case BOOLEAN -> cell.setCellValue((Boolean) value);
            case CHARACTER -> cell.setCellValue(value.toString());
            case LOCAL_DATE -> cell.setCellValue(((LocalDate) value).format(LOCAL_DATE_FORMAT));
            case LOCAL_DATE_TIME -> cell.setCellValue(((LocalDateTime) value).format(LOCAL_DATE_TIME_FORMAT));
            case LOCAL_TIME -> cell.setCellValue(((LocalTime) value).format(LOCAL_TIME_FORMAT));
            case DATE -> cell.setCellValue((Date) value);
            case ENUM -> cell.setCellValue(((Enum<?>) value).name());

            default -> cell.setCellValue(value.toString());
        }
    }
}
