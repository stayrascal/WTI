package com.rascal.module.sys.entity;

import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseNativeEntity;
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
@Table(name = "sys_sms_verify_code")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "短信校验码")
public class SmsVerifyCode extends BaseNativeEntity {

    private static final long serialVersionUID = 615208416034164816L;

    @Column(length = 32, nullable = false, unique = true)
    private String mobileNum;

    @Column(length = 32, nullable = false)
    private String code;

    @Column(nullable = false)
    private Date generateTime;

    @MetaData(value = "过期时间", comments = "定时任务定期清理过期的校验码")
    @Column(nullable = false)
    private Date expireTime;

    @MetaData(value = "首次验证通过时间", comments = "验证通过的手机号保留下来")
    @Column(nullable = true)
    private Date firstVerifiedTime;

    @MetaData(value = "最后验证通过时间", comments = "验证通过的手机号保留下来")
    @Column(nullable = true)
    private Date lastVerifiedTime;

    @MetaData(value = "总计验证通过次数")
    @Column(nullable = true)
    private Integer totalVerifiedCount = 0;
}
