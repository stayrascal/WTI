package com.rascal.module.sys.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.sys.entity.ConfigProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface ConfigPropertyDao extends BaseDao<ConfigProperty, Long> {

    @Query("from ConfigProperty")
    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<ConfigProperty> findAllCached();

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    ConfigProperty findByPropKey(String propKey);
}