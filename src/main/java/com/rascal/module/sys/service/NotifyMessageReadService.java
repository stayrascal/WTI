package com.rascal.module.sys.service;


import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import com.rascal.module.sys.dao.NotifyMessageReadDao;
import com.rascal.module.sys.entity.NotifyMessage;
import com.rascal.module.sys.entity.NotifyMessageRead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotifyMessageReadService extends BaseService<NotifyMessageRead, Long> {

    @Autowired
    private NotifyMessageReadDao notifyMessageReadDao;

    @Override
    protected BaseDao<NotifyMessageRead, Long> getEntityDao() {
        return notifyMessageReadDao;
    }

    public Integer countByNotifyMessage(NotifyMessage notifyMessage) {
        return notifyMessageReadDao.countByNotifyMessage(notifyMessage.getId());
    }

}
