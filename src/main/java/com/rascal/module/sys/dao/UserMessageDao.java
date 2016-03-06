package com.rascal.module.sys.dao;


import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.sys.entity.UserMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMessageDao extends BaseDao<UserMessage, Long> {
}