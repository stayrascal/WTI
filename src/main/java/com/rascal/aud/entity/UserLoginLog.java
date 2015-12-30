package com.rascal.aud.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.PersistableEntity;
import com.rascal.core.util.DateUtils;
import com.rascal.core.web.json.DateTimeJsonSerializer;
import com.rascal.module.auth.entity.User.AuthTypeEnum;
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
@Table(name = "auth_user_login_log")
//@Cache(usage = CacheConcurrencyStrategy.NONE)
@MetaData(value = "用户登录登出历史纪录")
public class UserLoginLog extends PersistableEntity<Long> {

    private static final long serialVersionUID = 172470816616580694L;

    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "native")
    private Long id;

    @MetaData(value = "账号类型对应的唯一标识")
    @Column(length = 64, nullable = false)
    private String authUid;

    @MetaData(value = "账号登陆")
    @Column(length = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthTypeEnum authType = AuthTypeEnum.SYS;

    @MetaData(value = "便于按日汇总统计的冗余属性")
    @Column(nullable = true)
    private String loginYearMonthDay;

    @MetaData(value = "登陆时间")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    private Date loginTime;

    @MetaData(value = "登出时间")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    private Date loginOutTime;

    @MetaData(value = "登陆时长")
    private Long loginTimeLength;

    @MetaData(value = "登陆次数")
    private Integer loginTimes;

    @MetaData(value = "userAgent")
    private String userAgent;

    @MetaData(value = "xforwardFor")
    private String xforwardFor;

    @MetaData(value = "localAddr")
    private String lcoalAddr;

    @MetaData(value = "localName")
    private String localName;

    @MetaData(value = "localPort")
    private Integer localPort;

    @MetaData(value = "remoteAddr")
    private String remoteAddr;

    @MetaData(value = "remoteHost")
    private String remoteHost;

    @MetaData(value = "remotePort")
    private Integer remotePort;

    @MetaData(value = "serverIP")
    private Integer serverIP;

    @MetaData(value = "Session编号")
    @Column(length = 128, nullable = false, unique = true)
    private String httpSessionId;

    @Override
    @Transient
    public String getDisplay() {
        return authType + ":" + authUid + ":" + httpSessionId;
    }

    @Transient
    public String getLoginTimeLengthFriendLy() {
        return DateUtils.getHumanDisplayForTimediff(loginTimeLength);
    }

}
