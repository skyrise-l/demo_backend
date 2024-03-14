package com.zju.QueryArtisan.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class otherUtils {

    public static boolean areAllFieldsNotNull(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true); // 可以访问私有字段
            try {
                if (field.get(object) == null) {
                    return false; // 如果任何字段为null，返回false
                }
            } catch (IllegalAccessException e) {
                log.info(String.valueOf(e));
                return false;
            }
        }
        return true; // 所有字段都不为null
    }
}
