package com.rascal.module.auth.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.auth.entity.Department;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

@Repository
public interface DepartmentDao extends BaseDao<Department, Long> {

    @Query("from Department")
    @QueryHints({ @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true") })
    public Iterable<Department> findAllCached();

}
