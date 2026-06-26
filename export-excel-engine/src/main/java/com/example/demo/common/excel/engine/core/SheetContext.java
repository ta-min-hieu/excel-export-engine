package com.example.demo.common.excel.engine.core;

import com.example.demo.common.excel.engine.metadata.ListColumnMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SheetContext {
    private int templateRowIndex = -1;

    private final Map<String, List<ListColumnMeta>> listColumns = new HashMap<>();

    private final Map<String, List<?>> listDataCache = new HashMap<>();
}
