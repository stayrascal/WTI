package com.rascal.shop.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.shop.entity.Commodity;
import com.rascal.shop.entity.CommodityStock;
import com.rascal.shop.entity.StorageLocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommodityStockDao extends BaseDao<CommodityStock, Long> {

    @Query("from CommodityStock where commodity=:commodity and storageLocation=:storageLocation and (batchNo is null or batchNo='')")
    CommodityStock findByCommodityAndStorageLocation(@Param("commodity") Commodity commodity,
                                                     @Param("storageLocation") StorageLocation storageLocation);

    CommodityStock findByCommodityAndStorageLocationAndBatchNo(Commodity commodity, StorageLocation storageLocation,
                                                               String batchNo);

    List<CommodityStock> findByCommodity(Commodity commodity);
}
