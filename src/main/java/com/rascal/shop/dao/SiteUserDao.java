package com.rascal.shop.dao;


import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.auth.entity.User;
import com.rascal.shop.entity.SiteUser;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteUserDao extends BaseDao<SiteUser, Long> {

    SiteUser findByUser(User user);
}
