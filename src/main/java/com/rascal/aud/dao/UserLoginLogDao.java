package com.rascal.aud.dao;

import com.rascal.aud.entity.UserLoginLog;
import com.rascal.core.dao.jpa.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginLogDao extends BaseDao<UserLoginLog, Long> {

    UserLoginLog findByHttpSessionId(String httpSessionId);
}
