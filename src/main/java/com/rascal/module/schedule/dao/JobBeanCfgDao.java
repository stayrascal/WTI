package com.rascal.module.schedule.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.schedule.entity.JobBeanCfg;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface JobBeanCfgDao extends BaseDao<JobBeanCfg, Long> {

    @Query("from JobBeanCfg")
    List<JobBeanCfg> findAll();

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    JobBeanCfg findByJobClass(String jobClass);
}