package com.example.demo.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetContext {

    private int templateRowIndex = -1;

    private final Map<String, List<ListColumnMeta>> listColumns =
            new HashMap<>();

    private final Map<String, List<?>> listDataCache =
            new HashMap<>();

    public int getTemplateRowIndex() {
        return templateRowIndex;
    }

    public void setTemplateRowIndex(int templateRowIndex) {
        this.templateRowIndex = templateRowIndex;
    }

    public Map<String, List<ListColumnMeta>> getListColumns() {
        return listColumns;
    }

    public Map<String, List<?>> getListDataCache() {
        return listDataCache;
    }
}
