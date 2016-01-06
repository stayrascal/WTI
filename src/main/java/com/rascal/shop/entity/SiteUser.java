package com.rascal.shop.entity;

import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseNativeEntity;
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
@Table(name = "shop_site_user")
@MetaData(value = "前端用户信息")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SiteUser extends BaseNativeEntity {

    private static final long serialVersionUID = 2686339300612095738L;

    @MetaData(value = "登录账号对象")
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    @Transient
    public String getDisplay() {
        return user.getDisplay();
    }
}
