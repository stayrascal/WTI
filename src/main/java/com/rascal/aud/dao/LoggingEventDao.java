package com.rascal.aud.dao;

import com.rascal.aud.entity.LoggingEvent;
import com.rascal.core.dao.jpa.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggingEventDao extends BaseDao<LoggingEvent, Long> {

}