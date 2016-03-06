package com.rascal.shop.dao;

import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.shop.entity.StockInOut;
import com.rascal.shop.entity.StockInOut.VoucherTypeEnum;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInOutDao extends BaseDao<StockInOut, Long> {

    List<StockInOut> findByVoucherAndVoucherType(String voucher, VoucherTypeEnum voucherType);
}
