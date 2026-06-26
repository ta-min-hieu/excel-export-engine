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

    private final XSSFWorkbook xssfWorkbook;

    private final ExportConfig config;
    private final Map<Class<?>, Map<String, Field>> fieldCache = new HashMap<>();

    public ExcelExportEngine(ExportConfig config, InputStream templateStream) throws Exception {

        this.config = config;
        this.xssfWorkbook = new XSSFWorkbook(templateStream);

        if (xssfWorkbook.getNumberOfSheets() == 0)
            throw new IllegalArgumentException("Workbook has no sheet");
    }

    /**
     * Export toàn bộ workbook.
     */
    public void write(WorkbookExport workbookExport) {

        Objects.requireNonNull(workbookExport, "WorkbookExport must not be null");

        for (SheetExport<?> sheetExport : workbookExport.getSheets())
            writeSheet(sheetExport.sheetIndex(), sheetExport.data());

        // Sau khi toàn bộ sheet đã được ghi bằng XSSF
        // mới chuyển sang SXSSF để giảm memory khi ghi file.
        sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook, config.getWindowSize());
    }

    /**
     * Export một sheet.
     */
    private void writeSheet(int sheetIndex, Object exportDto) {
        Sheet sheet = xssfWorkbook.getSheetAt(sheetIndex);

        if (sheet == null)
            throw new IllegalArgumentException("Sheet index " + sheetIndex + " not found.");

        SheetContext context = new SheetContext();

        //----------------------------------
        // 1. Scan template
        //----------------------------------
        scanTemplate(sheet, context);

        //----------------------------------
        // 2. Replace ${}
        //----------------------------------
        replaceVariables(sheet, exportDto);

        //----------------------------------
        // 3. Cache list
        //----------------------------------
        cacheLists(exportDto, context);

        //----------------------------------
        // 4. Write first row
        //----------------------------------
        writeFirstDataRow(sheet, context);

        //----------------------------------
        // 5. Remaining rows
        //----------------------------------
        writeRemainingRows(sheet, context);
    }

    /**
     * Ghi workbook ra OutputStream.
     */
    public void finish(OutputStream os) {

        if (sxssfWorkbook == null)
            throw new IllegalStateException("Please call write() before finish().");

        try {
            sxssfWorkbook.write(os);

            os.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {

            try {
                sxssfWorkbook.dispose();
            } catch (Exception ignore) {}

            try {
                sxssfWorkbook.close();
            } catch (Exception ignore) {}

            try {
                xssfWorkbook.close();
            } catch (Exception ignore) {}

            sxssfWorkbook = null;
        }
    }

    private void scanTemplate(Sheet sheet, SheetContext context) {
        for (Row row : sheet) {
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
                    throw new RuntimeException("Invalid list expression : " + expr);

                String listName = listMatcher.group(1);

                String fieldName = listMatcher.group(2);

                int rowNum = row.getRowNum();

                if (context.getTemplateRowIndex() == -1)
                    context.setTemplateRowIndex(rowNum);

                context.setTemplateRowIndex(Math.max(context.getTemplateRowIndex(), rowNum));

                context.getListColumns()
                        .computeIfAbsent(listName, k -> new ArrayList<>())
                        .add(new ListColumnMeta(cell.getColumnIndex(), listName, fieldName, cell.getCellStyle()));
            }
        }

        if (context.getTemplateRowIndex() == -1)
            throw new RuntimeException("No *{list.field} row found.");
    }

    private void replaceVariables(Sheet sheet, Object exportDto) {
        replaceVariables(sheet, extractVariables(exportDto));
    }

    private void replaceVariables(Sheet sheet, Map<String, VariableValue> variables) {
        int firstRow = 0;
        int lastRow = sheet.getLastRowNum();

        for (int i = firstRow; i <= lastRow; i++) {
            Row row = sheet.getRow(i);

            if (row == null) continue;

            for (Cell cell : row) {
                if (cell.getCellType() != CellType.STRING) continue;

                String text = cell.getStringCellValue().trim();
                Matcher matcher = GLOBAL_PATTERN.matcher(text);

                if (!matcher.matches()) continue;

                String key = matcher.group(1);
                VariableValue variable = variables.get(key);

                if (variable == null) continue;

                CellWriter.write(cell, variable.value(), variable.type());
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

                if (Collection.class.isAssignableFrom(field.getType()))
                    continue;

                variables.put(field.getName(), new VariableValue(value, MetadataFactory.resolveType(field.getType())));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return variables;
    }

    private void cacheLists(Object exportDto, SheetContext context) {
        try {
            for (Field field : exportDto.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(exportDto);

                if (!(value instanceof List<?> list))
                    continue;

                context.getListDataCache().put(field.getName(), list);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFirstDataRow(Sheet sheet, SheetContext context) {
        Row row = sheet.getRow(context.getTemplateRowIndex());

        if (row == null)
            row = sheet.createRow(context.getTemplateRowIndex());

        Map<String, List<?>> dataCache = context.getListDataCache();

        for (Map.Entry<String, List<?>> entry : dataCache.entrySet()) {
            List<?> datas = entry.getValue();

            if (datas == null || datas.isEmpty())
                continue;

            Object firstItem = datas.get(0);

            writeDataObject(row, firstItem, entry.getKey(), context);
        }
    }

    private void writeRemainingRows(Sheet sheet, SheetContext context) {
        int rowIndex = context.getTemplateRowIndex() + 1;

        Map<String, List<?>> dataCache = context.getListDataCache();

        for (Map.Entry<String, List<?>> entry : dataCache.entrySet()) {
            List<?> datas = entry.getValue();

            if (datas == null || datas.size() <= 1)
                continue;

            for (int i = 1; i < datas.size(); i++) {
                Object item = datas.get(i);
                Row row = sheet.createRow(rowIndex++);

                writeDataObject(row, item, entry.getKey(), context);
            }
        }

        context.setTemplateRowIndex(rowIndex);
    }

    private void writeDataObject(Row row, Object item, String listName, SheetContext context) {
        if (item == null)
            return;

        List<ListColumnMeta> columns = context.getListColumns().get(listName);

        if (columns == null || columns.isEmpty())
            return;

        Class<?> itemClass = item.getClass();

        for (ListColumnMeta meta : columns) {
            int col = meta.columnIndex();

            Cell cell = row.getCell(col);
            if (cell == null)
                cell = row.createCell(col);

            // giữ style từ template
            if (meta.style() != null)
                cell.setCellStyle(meta.style());

            try {
                Field field = getFieldCached(itemClass, meta.fieldName());

                if (field == null)
                    continue;

                field.setAccessible(true);
                Object value = field.get(item);

                CellWriter.write(cell, value, resolveType(field.getType()));
            } catch (Exception e) {
                throw new RuntimeException("Error writing cell at column " + meta.columnIndex() + " field " + meta.fieldName(), e);
            }
        }
    }

    private Field getFieldCached(Class<?> clazz, String fieldName) {

        return fieldCache
                .computeIfAbsent(clazz, c -> new HashMap<>())
                .computeIfAbsent(fieldName, f -> {
                    Class<?> current = clazz;

                    while (current != null && current != Object.class) {

                        try {
                            Field field = current.getDeclaredField(fieldName);
                            field.setAccessible(true);
                            return field;
                        } catch (NoSuchFieldException ignored) {
                            current = current.getSuperclass();
                        }
                    }

                    throw new RuntimeException("Field not found: " + fieldName + " in " + clazz.getName());
                });
    }
}
