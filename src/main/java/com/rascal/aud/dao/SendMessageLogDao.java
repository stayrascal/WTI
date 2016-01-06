package com.rascal.aud.dao;


import com.rascal.aud.entity.SendMessageLog;
import com.rascal.core.dao.jpa.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface SendMessageLogDao extends BaseDao<SendMessageLog, Long> {

}