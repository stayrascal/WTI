package com.rascal.aud.service;

import com.rascal.aud.dao.UserLogonLogDao;
import com.rascal.aud.entity.UserLogonLog;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.dao.mybatis.MyBatisDao;
import com.rascal.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserLogonLogService extends BaseService<UserLogonLog, Long> {

    @Autowired
    private UserLogonLogDao userLogonLogDao;

    @Autowired
    private MyBatisDao myBatisDao;

    @Override
    protected BaseDao<UserLogonLog, Long> getEntityDao() {
        return userLogonLogDao;
    }

    @Transactional(readOnly = true)
    public UserLogonLog findBySessionId(String httpSessionId) {
        return userLogonLogDao.findByHttpSessionId(httpSessionId);
    }

    public List<Map<String, Object>> findGroupByLogonDay() {
        return myBatisDao.findList(UserLogonLog.class.getName(), "findGroupByLogonDay", null);
    }
}
