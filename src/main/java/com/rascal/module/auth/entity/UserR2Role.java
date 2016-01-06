package com.rascal.module.auth.entity;

import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseNativeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "auth_user_r2_role", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "登录账号与角色关联")
@Audited
public class UserR2Role extends BaseNativeEntity {

    private static final long serialVersionUID = -1727859177925448384L;

    @MetaData(value = "登录账号对象")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MetaData(value = "关联角色对象")
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Transient
    @Override
    public String getDisplay() {
        return user.getDisplay() + "_" + role.getDisplay();
    }
}
