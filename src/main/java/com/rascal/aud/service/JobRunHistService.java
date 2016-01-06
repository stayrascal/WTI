package com.rascal.aud.service;

import com.rascal.aud.dao.JobRunHistDao;
import com.rascal.aud.entity.JobRunHist;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobRunHistService extends BaseService<JobRunHist, Long> {

    @Autowired
    private JobRunHistDao jobRunHistDao;

    @Override
    protected BaseDao<JobRunHist, Long> getEntityDao() {
        return jobRunHistDao;
    }
}
