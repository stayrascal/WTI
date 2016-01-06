package com.rascal.module.sys.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.sys.entity.DataDict;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface DataDictDao extends BaseDao<DataDict, Long> {

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    @Query("from DataDict where primaryKey=:primaryKey and parent is null")
    DataDict findByRootPrimaryKey(@Param("primaryKey") String primaryKey);

    @Query("from DataDict where parent.id=:parentId and disabled=false order by orderRank desc")
    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<DataDict> findEnabledChildrenByParentId(@Param("parentId") Long parentId);

    @Query("from DataDict order by parent asc,orderRank desc")
    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<DataDict> findAllCached();
}