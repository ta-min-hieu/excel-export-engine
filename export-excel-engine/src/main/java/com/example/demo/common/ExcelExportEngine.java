package com.example.demo.common;

import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;

import static com.example.demo.common.ExportConstants.*;
import static com.example.demo.common.MetadataFactory.resolveType;

@Log4j2
public class ExcelExportEngine {
    private SXSSFWorkbook sxssfWorkbook;
    private Sheet sxssfSheet;
    private final XSSFWorkbook xssfWorkbook;
    private final Sheet xssfSheet;
    private final ExportConfig config;
    private final int sheetAt;

    // Dòng chứa *{}
    private int templateRowIndex = -1;
    // Metadata của các cột data
    private final Map<String, List<ListColumnMeta>> listColumns = new HashMap<>();
    private final Map<String, List<?>> listDataCache = new HashMap<>();

    public ExcelExportEngine(ExportConfig config, InputStream templateStream) throws Exception {
        this(config, templateStream, 0);
    }

    public ExcelExportEngine(ExportConfig config, InputStream templateStream, int sheetAt) throws Exception {
        xssfWorkbook = new XSSFWorkbook(templateStream);

        if (xssfWorkbook.getNumberOfSheets() == 0)
            throw new IllegalArgumentException("Workbook has no sheet");

        this.xssfSheet = xssfWorkbook.getSheetAt(sheetAt);
        this.config = config;
        this.sheetAt = sheetAt;
    }

    public void write(Object exportObj) {
        scanTemplate();
        replaceVariables(exportObj);
        cacheLists(exportObj);

        // ghi dòng đầu bằng XSSF
        writeFirstDataRow();

        sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, config.getWindowSize());
        sxssfSheet = sxssfWorkbook.getSheetAt(sheetAt);

        // bắt đầu từ phần tử thứ 2
        writeRemainingRows();
    }

    private void writeRemainingRows() {
        int rowIndex = templateRowIndex + 1;

        for (Map.Entry<String, List<?>> entry : listDataCache.entrySet()) {

            List<?> datas = entry.getValue();

            if (datas == null || datas.size() <= 1)
                continue;

            for (int i = 1; i < datas.size(); i++) {
                Object item = datas.get(i);
                Row row = sxssfSheet.createRow(rowIndex++);
                writeDataObject(row, item, entry.getKey());
            }
        }

        templateRowIndex = rowIndex;
    }

    private void writeFirstDataRow() {
        Row row = xssfSheet.getRow(templateRowIndex);

        if (row == null)
            row = xssfSheet.createRow(templateRowIndex);

        for (Map.Entry<String, List<?>> entry : listDataCache.entrySet()) {
            List<?> datas = entry.getValue();

            if (datas == null || datas.isEmpty())
                continue;

            Object firstItem = datas.get(0);

            writeDataObject(row, firstItem, entry.getKey());
        }
    }

    private void writeDataObject(Row row, Object item, String listName) {
        List<ListColumnMeta> columns = listColumns.get(listName);

        if (columns == null)
            return;

        for (ListColumnMeta meta : columns) {
            Cell cell = row.getCell(meta.columnIndex());

            if (cell == null)
                cell = row.createCell(meta.columnIndex());

            cell.setCellStyle(meta.style());

            try {
                Field field = item.getClass().getDeclaredField(meta.fieldName());
                field.setAccessible(true);
                Object value = field.get(item);
                CellWriter.write(cell, value, resolveType(field.getType()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // =========================
    // FINISH
    // =========================
    public void finish(OutputStream os) {
        try {
            sxssfWorkbook.write(os);
            sxssfWorkbook.dispose();
            sxssfWorkbook.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void scanTemplate() {
        for (Row row : xssfSheet) {
            for (Cell cell : row) {
                if (cell.getCellType() != CellType.STRING)
                    continue;

                String text = cell.getStringCellValue();
                Matcher matcher = DATA_PATTERN.matcher(text);

                if (!matcher.matches())
                    continue;

                String expr = matcher.group(1);

                Matcher listMatcher = LIST_PATTERN.matcher(expr);

                if (!listMatcher.matches())
                    throw new RuntimeException("Invalid list expression: " + expr);

                String listName = listMatcher.group(1);
                String fieldName = listMatcher.group(2);

                if (templateRowIndex == -1)
                    templateRowIndex = row.getRowNum();

                listColumns
                        .computeIfAbsent(listName, k -> new ArrayList<>())
                        .add(new ListColumnMeta(cell.getColumnIndex(), listName, fieldName, cell.getCellStyle()));
            }
        }

        if (templateRowIndex == -1)
            throw new RuntimeException("No *{list.field} row found");
    }

    public void replaceVariables(Object exportDto) {
        replaceVariables(extractVariables(exportDto));
    }

    public void replaceVariables(Map<String, VariableValue> variables) {
        for (Sheet sheet : xssfWorkbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() != CellType.STRING)
                        continue;

                    String text = cell.getStringCellValue().trim();

                    Matcher matcher = GLOBAL_PATTERN.matcher(text);

                    if (!matcher.matches())
                        continue;

                    String key = matcher.group(1);

                    VariableValue variable = variables.get(key);

                    if (variable == null)
                        continue;

                    CellWriter.write(cell, variable.value(), variable.type());
                }
            }
        }
    }

    private Map<String, VariableValue> extractVariables(Object obj) {
        Map<String, VariableValue> variables = new HashMap<>();

        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                Object value = field.get(obj);

                if (value == null)
                    continue;

                // bỏ qua List, Set...
                if (Collection.class.isAssignableFrom(field.getType()))
                    continue;

                variables.put(field.getName(), new VariableValue(value, MetadataFactory.resolveType(field.getType())));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return variables;
    }

    private void cacheLists(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);

                if (value instanceof List<?> list)
                    listDataCache.put(field.getName(), list);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
