package com.rascal.aud.dao;

import com.rascal.aud.entity.JobRunHist;
import com.rascal.core.dao.jpa.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRunHistDao extends BaseDao<JobRunHist, Long> {

}