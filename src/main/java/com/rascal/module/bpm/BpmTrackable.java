package com.rascal.module.bpm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Transient;
import java.io.Serializable;

/**
 * 标识实体会进行BPM流程处理过程回调，写入当前业务对象所处的工作流节点
 * 一般在实体定义private String activeTaskName属性，然后生成对应的setter和getter方式即可
 */
public interface BpmTrackable {

    @Transient
    @JsonIgnore
    String getBpmBusinessKey();

    @JsonProperty
    String getActiveTaskName();

    BpmTrackable setActiveTaskName(String activeTaskName);

    @JsonProperty
    Serializable getId();
}
