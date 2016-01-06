package com.rascal.module.auth.entity;

import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseNativeEntity;
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
@Table(name = "auth_mgmt_user")
@MetaData(value = "管理端用户信息")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class MgmtUser extends BaseNativeEntity {

    private static final long serialVersionUID = 512335968914828057L;

    @MetaData(value = "登录账号对象")
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private User user;

    @MetaData(value = "所属部门")
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "department_id")
    private Department department;

    @Override
    @Transient
    public String getDisplay() {
        return user.getDisplay();
    }
}
