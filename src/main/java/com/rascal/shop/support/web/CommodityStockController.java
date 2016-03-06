package com.rascal.shop.support.web;

import com.rascal.core.service.BaseService;
import com.rascal.core.web.BaseController;
import com.rascal.shop.entity.CommodityStock;
import com.rascal.shop.service.CommodityService;
import com.rascal.shop.service.CommodityStockService;
import com.rascal.shop.service.StockInOutService;
import com.rascal.shop.service.StorageLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/admin/commodity/stock")
public class CommodityStockController extends BaseController<CommodityStock, Long> {

    @Autowired
    private CommodityStockService commodityStockService;
    @Autowired
    private CommodityService commodityService;
    @Autowired
    private StorageLocationService storageLocationService;
    @Autowired
    private StockInOutService stockInOutService;

    @Override
    protected BaseService<CommodityStock, Long> getEntityService() {
        return commodityStockService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "admin/shop/commodity-stock-index";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Page<CommodityStock> findByPage(HttpServletRequest request) {
        return super.findByPage(CommodityStock.class, request);
    }
}
