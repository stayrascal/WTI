package com.rascal.shop.service;


import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.service.BaseService;
import com.rascal.shop.dao.CommodityStockDao;
import com.rascal.shop.entity.Commodity;
import com.rascal.shop.entity.CommodityStock;
import com.rascal.shop.entity.StorageLocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommodityStockService extends BaseService<CommodityStock, Long> {

    @Qualifier("commodityStockDao")
    @Autowired
    private CommodityStockDao commodityStockDao;

    @Override
    protected BaseDao<CommodityStock, Long> getEntityDao() {
        return commodityStockDao;
    }

    public CommodityStock findBy(Commodity commodity, StorageLocation storageLocation) {
        return findBy(commodity, storageLocation, null);
    }

    public CommodityStock findBy(Commodity commodity, StorageLocation storageLocation, String batchNo) {
        CommodityStock commodityStock = null;
        if (StringUtils.isNotBlank(batchNo)) {
            commodityStock = commodityStockDao.findByCommodityAndStorageLocationAndBatchNo(commodity, storageLocation,
                    batchNo);
        } else {
            commodityStock = commodityStockDao.findByCommodityAndStorageLocation(commodity, storageLocation);
        }
        return commodityStock;
    }

    @Override
    public CommodityStock save(CommodityStock entity) {
        throw new UnsupportedOperationException("不允许直接修改库存量数据，需使用库存移动接口级联计算更新库存数据");
    }
}
