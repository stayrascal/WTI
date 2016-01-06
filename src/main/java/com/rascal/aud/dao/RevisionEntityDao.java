package com.rascal.aud.dao;

import com.rascal.core.audit.envers.ExtDefaultRevisionEntity;
import com.rascal.core.dao.jpa.BaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface RevisionEntityDao extends BaseDao<ExtDefaultRevisionEntity, Long> {

}
