package com.example.demo.common;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class MetadataFactory {

    private static final Map<Class<?>, List<ColumnMeta>> CACHE = new HashMap<>();

    public static <T> List<ColumnMeta> getMetadata(Class<T> clazz) {
        return CACHE.computeIfAbsent(clazz, MetadataFactory::build);
    }

    private static List<ColumnMeta> build(Class<?> clazz) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            List<ColumnMeta> list = new ArrayList<>();
            Field[] fields = clazz.getDeclaredFields();

            int index = 0;

            for (Field f : fields) {
                if (Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers()))
                    continue;

                f.setAccessible(true);
                MethodHandle getter = lookup.unreflectGetter(f);
                list.add(new ColumnMeta(index++, getter, resolveType(f.getType())));
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static ColumnType resolveType(Class<?> type) {

        if (type == String.class) return ColumnType.STRING;
        if (type == Long.class || type == long.class) return ColumnType.LONG;
        if (type == Integer.class || type == int.class) return ColumnType.INTEGER;
        if (type == Double.class || type == double.class) return ColumnType.DOUBLE;
        if (type == Boolean.class || type == boolean.class) return ColumnType.BOOLEAN;
        if (type == Date.class) return ColumnType.DATE;
        if (type == BigDecimal.class) return ColumnType.BIG_DECIMAL;
        if (type == LocalDate.class) return ColumnType.LOCAL_DATE;
        if (type == LocalDateTime.class) return ColumnType.LOCAL_DATE_TIME;
        if (type == Short.class || type == short.class) return ColumnType.SHORT;
        if (type == Byte.class || type == byte.class) return ColumnType.BYTE;
        if (type == Float.class || type == float.class) return ColumnType.FLOAT;
        if (type == BigInteger.class) return ColumnType.BIG_INTEGER;
        if (type == Character.class || type == char.class) return ColumnType.CHARACTER;
        if (type == LocalTime.class) return ColumnType.LOCAL_TIME;
        if (type.isEnum()) return ColumnType.ENUM;

        return ColumnType.OBJECT;
    }
}
