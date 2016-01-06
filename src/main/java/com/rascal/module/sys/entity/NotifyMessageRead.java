package com.rascal.module.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseNativeEntity;
import com.rascal.core.web.json.DateTimeJsonSerializer;
import com.rascal.module.auth.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_notify_message_read", uniqueConstraints = @UniqueConstraint(columnNames = { "notifyMessage_id", "readUser_id" }))
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@MetaData(value = "公告阅读记录")
public class NotifyMessageRead extends BaseNativeEntity {

    private static final long serialVersionUID = -2680515888038751963L;

    @MetaData(value = "公告")
    @ManyToOne
    @JoinColumn(name = "notifyMessage_id", nullable = false)
    @JsonIgnore
    private NotifyMessage notifyMessage;

    @MetaData(value = "阅读用户")
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "readUser_id", nullable = false)
    @JsonIgnore
    private User readUser;

    @MetaData(value = "首次阅读时间")
    @Column(nullable = false, updatable = false)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    private Date firstReadTime;

    @MetaData(value = "最后阅读时间")
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    private Date lastReadTime;

    @MetaData(value = "总计阅读次数")
    @Column(nullable = false)
    private Integer readTotalCount = 1;

    @Override
    @Transient
    public String getDisplay() {
        return null;
    }

    @Transient
    public String getReadUserLabel() {
        return readUser.getDisplay();
    }
}
