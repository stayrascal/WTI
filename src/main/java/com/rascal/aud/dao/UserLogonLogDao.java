package com.rascal.aud.dao;

import com.rascal.aud.entity.UserLogonLog;
import com.rascal.core.dao.jpa.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogonLogDao extends BaseDao<UserLogonLog, Long> {

    UserLogonLog findByHttpSessionId(String httpSessionId);

}