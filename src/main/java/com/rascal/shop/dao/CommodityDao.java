package com.rascal.shop.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.shop.entity.Commodity;
import org.springframework.stereotype.Repository;

@Repository
public interface CommodityDao extends BaseDao<Commodity, String> {
}
