package com.rascal.aud.service;

import com.rascal.aud.dao.UserLoginLogDao;
import com.rascal.aud.entity.UserLoginLog;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserLoginLogService extends BaseService<UserLoginLog, Long> {

    @Autowired
    private UserLoginLogDao userLoginLogDao;


    @Override
    protected BaseDao<UserLoginLog, Long> getEntityDao() {
        return userLoginLogDao;
    }

    @Transactional(readOnly = true)
    public UserLoginLog findBySessionId(String httpSessionId) {
        return userLoginLogDao.findByHttpSessionId(httpSessionId);
    }
}
