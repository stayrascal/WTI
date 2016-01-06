package com.rascal.module.sys.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseNativeEntity;
import com.rascal.core.web.json.EntityIdDisplaySerializer;
import com.rascal.module.auth.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_user_profile_data", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "code" }))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "用户配置数据")
public class UserProfileData extends BaseNativeEntity {

    private static final long serialVersionUID = -3203959719354074416L;

    @MetaData(value = "用户")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonSerialize(using = EntityIdDisplaySerializer.class)
    private User user;

    @MetaData(value = "代码")
    @Column(length = 128, nullable = false)
    private String code;

    @MetaData(value = "参数值")
    @Column(length = 128, nullable = true)
    private String value;
}
