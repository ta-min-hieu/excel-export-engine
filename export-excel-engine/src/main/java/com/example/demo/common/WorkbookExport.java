package com.example.demo.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WorkbookExport {

    private final List<SheetExport<?>> sheets;

    private WorkbookExport(Builder builder) {
        this.sheets = builder.sheets;
    }

    public static Builder builder() {
        return new Builder();
    }

    // =========================
    // BUILDER
    // =========================
    public static class Builder {

        private final List<SheetExport<?>> sheets = new ArrayList<>();

        public Builder sheet(int index, Object data) {
            sheets.add(new SheetExport<>(index, null, data));
            return this;
        }

        public Builder sheet(String name, Object data) {
            sheets.add(new SheetExport<>(-1, name, data));
            return this;
        }

        public Builder sheet(int index, String name, Object data) {
            sheets.add(new SheetExport<>(index, name, data));
            return this;
        }

        public WorkbookExport build() {
            return new WorkbookExport(this);
        }
    }
}
