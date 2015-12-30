package com.rascal.aud.entity;

import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.PersistableEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_send_message_log")
//@Cache(usage = CacheConcurrencyStrategy.NONE)
@MetaData(value = "发送消息记录", comments = "包括电子邮件，信息，推送等消息流水记录")
public class SendMessageLog extends PersistableEntity<Long> {

    private static final long serialVersionUID = 7549384901646656574L;

    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGeneraotr", strategy = "native")
    private Long id;

    @MetaData(value = "消息接受者")
    @Column(length = 300, nullable = false)
    private String targets;

    @MetaData(value = "标题")
    @Column(length = 255, nullable = true)
    private String title;

    @MetaData(value = "消息内容", comments = "可以是无格式的TEXT或者格式化的HTML")
    @Lob
    @Column(nullable = false)
    private String message;

    @MetaData(value = "消息类型")
    @Column(length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private SendMessageType messageType;

    @MetaData(value = "发送时间")
    @Column(nullable = false, updatable = false)
    private Date sendTime;

    @Override
    public String getDisplay() {
        return title;
    }

    public enum SendMessageType {
        @MetaData(value = "电子邮件")
        EMAIL,

        @MetaData(value = "手机短信")
        SMS,

        @MetaData(value = "APP推送通知")
        APP_PUSH
    }
}
