package com.rascal.shop.service;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import com.rascal.shop.dao.StorageLocationDao;
import com.rascal.shop.entity.StorageLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StorageLocationService extends BaseService<StorageLocation, String> {

    @Qualifier("storageLocationDao")
    @Autowired
    private StorageLocationDao storageLocationDao;

    @Override
    protected BaseDao<StorageLocation, String> getEntityDao() {
        return storageLocationDao;
    }
}
