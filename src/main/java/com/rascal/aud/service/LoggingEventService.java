package com.rascal.aud.service;

import com.rascal.aud.dao.LoggingEventDao;
import com.rascal.aud.entity.LoggingEvent;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoggingEventService extends BaseService<LoggingEvent, Long> {

    @Autowired
    private LoggingEventDao loggingEventDao;

    @Override
    protected BaseDao<LoggingEvent, Long> getEntityDao() {
        return loggingEventDao;
    }
}
