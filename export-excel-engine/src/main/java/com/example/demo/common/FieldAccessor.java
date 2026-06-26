package com.example.demo.common;

import java.lang.reflect.Field;

import static com.example.demo.common.MetadataFactory.resolveType;

public class FieldAccessor {

    private final Field field;

    public FieldAccessor(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public Object get(Object target) {
        try {
            return field.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ColumnType getFieldType() {
        return resolveType(field.getType());
    }
}
