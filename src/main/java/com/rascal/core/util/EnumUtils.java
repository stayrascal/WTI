package com.rascal.core.util;

import com.google.common.collect.Maps;
import com.rascal.core.annotation.MetaData;

import java.lang.reflect.Field;
import java.util.Map;

public class EnumUtils {

    private static Map<Class<?>, Map<Enum<?>, String>> enumDatasContainer = Maps.newHashMap();

    /**
     * 基于Enum类返回对应的key-value Map构建对象
     */
    public static Map<Enum<?>, String> getEnumDataMap(Class<? extends Enum> enumClass) {
        Map<Enum<?>, String> enumDataMap = enumDatasContainer.get(enumClass);
        if (enumDataMap != null) {
            return enumDataMap;
        }
        enumDataMap = Maps.newLinkedHashMap();
        enumDatasContainer.put(enumClass, enumDataMap);
        Field[] fields = enumClass.getFields();
        for (Field field : fields) {
            String name = field.getName();
            String label = name;
            MetaData entityComment = field.getAnnotation(MetaData.class);
            if (entityComment != null) {
                label = entityComment.value();
            }
            enumDataMap.put(Enum.valueOf(enumClass, name), label);
        }
        return enumDataMap;
    }

}
