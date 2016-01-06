package com.rascal.module.sys.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.module.sys.entity.Menu;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface MenuDao extends BaseDao<Menu, Long> {

    @Query("from Menu order by inheritLevel asc, orderRank desc")
    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    List<Menu> findAllCached();
}
