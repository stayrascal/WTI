package com.rascal.module.auth.dao;


import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.auth.entity.UserExt;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExtDao extends BaseDao<UserExt, Long> {

    UserExt findByRandomCode(String randomCode);
}