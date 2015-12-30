package com.rascal.core.cons;

import com.google.common.collect.Maps;
import com.rascal.core.annotation.MetaData;

import java.util.Map;

/**
 * Date: 2015/11/22
 * Time: 23:02
 *
 * @author Rascal
 */
public class GlobalConstant {

    public static final Map<Boolean, String> booleanLabelMap = Maps.newLinkedHashMap();
    public final static String GlobalForeignKeyName = "none";

    static {
        booleanLabelMap.put(Boolean.TRUE, "是");
        booleanLabelMap.put(Boolean.FALSE, "否");
    }

    public enum GenderEnum {
        @MetaData(value = "男")
        M,

        @MetaData(value = "女")
        F,

        @MetaData(value = "保密")
        U
    }
}

