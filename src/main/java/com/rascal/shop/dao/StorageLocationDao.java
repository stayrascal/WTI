package com.rascal.shop.dao;


import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.shop.entity.StorageLocation;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageLocationDao extends BaseDao<StorageLocation, String> {

}
