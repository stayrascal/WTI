package com.rascal.core.entity.def;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public interface OperationAuditable {

    /**
     * 转换操作状态数据值为字面显示字符串
     */
    String convertStateToDisplay(String rawState);

}
