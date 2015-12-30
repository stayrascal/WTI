package com.rascal.aud.service;

import com.rascal.aud.dao.SendMessageLogDao;
import com.rascal.aud.entity.SendMessageLog;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SendMessageLogService extends BaseService<SendMessageLog, Long> {

    @Autowired
    private SendMessageLogDao sendMessageLogDao;

    @Override
    protected BaseDao<SendMessageLog, Long> getEntityDao() {
        return sendMessageLogDao;
    }

    public void asynSave(SendMessageLog entity) {
        sendMessageLogDao.save(entity);
    }
}
