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
@Table(name = "auth_role_r2_privilege", uniqueConstraints = @UniqueConstraint(columnNames = { "privilege_id", "role_id" }))
@MetaData(value = "角色与权限关联")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class RoleR2Privilege extends BaseNativeEntity {

    private static final long serialVersionUID = -4312077296555510354L;

    /** 关联权限对象 */
    @ManyToOne
    @JoinColumn(name = "privilege_id", nullable = false)
    private Privilege privilege;

    /** 关联角色对象 */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Transient
    @Override
    public String getDisplay() {
        return privilege.getDisplay() + "_" + role.getDisplay();
    }
}
