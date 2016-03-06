package com.rascal.shop.service;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import com.rascal.shop.dao.CommodityDao;
import com.rascal.shop.entity.Commodity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommodityService extends BaseService<Commodity, String> {

    @Qualifier("commodityDao")
    @Autowired
    private CommodityDao commodityDao;

    @Override
    protected BaseDao<Commodity, String> getEntityDao() {
        return commodityDao;
    }

    public Commodity findByBarcode(String barcode) {
        return this.findByProperty("barcode", barcode);
    }
}
