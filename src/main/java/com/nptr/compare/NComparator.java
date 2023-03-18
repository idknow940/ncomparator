package com.nptr.compare;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public interface NComparator<T> {
    static Optional<Integer> defaultCompare(Object f, Object s) throws NumberFormatException {
        Class<?> type = f.getClass();
        if (type == Long.class || type == Integer.class || type == Short.class || type == Byte.class) {
            return Optional.of(Long.compare(Long.parseLong(f.toString()), Long.parseLong(s.toString())));
        } else if (type == Double.class || type == Float.class) {
            return Optional.of(Double.compare(Double.parseDouble(f.toString()), Double.parseDouble(s.toString())));
        } else if (type == String.class || type == Character.class) {
            return Optional.of(f.toString().compareTo(s.toString()));
        }
        return Optional.empty();
    }

    default Optional<Integer> compare(T f, T s) {
        Class<?> cls = f.getClass();
        try {
            return defaultCompare(f, s);
        } catch (Exception _e) {
            Field[] declaredFields = cls.getDeclaredFields();
            for (Field field : declaredFields) {
                String name = field.getName();
                Object fV;
                Object sV;
                try {
                    Field classDeclaredField = cls.getDeclaredField(name);
                    fV = classDeclaredField.get(f);
                    sV = classDeclaredField.get(s);
                    return defaultCompare(fV, sV);
                } catch (IllegalAccessException e) {
                    Method[] declaredMethods = cls.getDeclaredMethods();
                    for (Method dm :
                            declaredMethods) {
                        if (dm.getName().startsWith("get" + StringUtils.capitalize(name))) {
                            try {
                                Object fM = dm.invoke(f);
                                Object sM = dm.invoke(s);
                                return defaultCompare(fM, sM);
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            return Optional.empty();
        }
    }
}
