package com.rascal.shop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseNativeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "shop_stock_in_out")
@MetaData(value = "库存变动明细记录")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StockInOut extends BaseNativeEntity {

    @MetaData("单据凭证号")
    @JsonProperty
    private String voucher;

    @MetaData(value = "子凭证号")
    @Column(length = 128, nullable = true)
    @JsonProperty
    private String subVoucher;

    @MetaData("单据类型")
    @Enumerated(EnumType.STRING)
    @Column(length = 8, nullable = true)
    @JsonProperty
    private VoucherTypeEnum voucherType;

    @MetaData("商品库存信息")
    @ManyToOne
    @JoinColumn(name = "shop_stock_commodity", nullable = false)
    @JsonProperty
    private CommodityStock commodityStock;

    @MetaData("原始数量")
    @JsonProperty
    private BigDecimal originalQuantity = BigDecimal.ZERO;

    @MetaData("原始锁定量")
    @JsonProperty
    private BigDecimal originalSalingQuantity = BigDecimal.ZERO;

    @MetaData("原始在途量")
    @JsonProperty
    private BigDecimal originalPurchasingQuantity = BigDecimal.ZERO;

    @MetaData(value = "变更后数量", tooltips = "本次结余量(之前结余量-本次变更量)")
    @JsonProperty
    private BigDecimal quantity = BigDecimal.ZERO;

    @MetaData(value = "变更后锁定量", tooltips = "本次结余量(之前结余量-本次变更量)")
    @JsonProperty
    private BigDecimal salingQuantity = BigDecimal.ZERO;

    @MetaData(value = "变更后在途量", tooltips = "本次结余量(之前结余量-本次变更量)")
    @JsonProperty
    private BigDecimal purchasingQuantity = BigDecimal.ZERO;

    @MetaData("数量变化")
    @JsonProperty
    private BigDecimal diffQuantity = BigDecimal.ZERO;

    @MetaData("锁定量变化")
    @JsonProperty
    private BigDecimal diffSalingQuantity = BigDecimal.ZERO;

    @MetaData("在途量变化")
    @JsonProperty
    private BigDecimal diffPurchasingQuantity = BigDecimal.ZERO;

    @MetaData("原始成本价")
    @JsonProperty
    private BigDecimal originalCostPrice = BigDecimal.ZERO;

    @MetaData("原始库存价值")
    @JsonProperty
    private BigDecimal originalStockAmount = BigDecimal.ZERO;

    @MetaData("变更后成本价")
    @JsonProperty
    private BigDecimal costPrice = BigDecimal.ZERO;

    @MetaData("变更后库存价值")
    @JsonProperty
    private BigDecimal stockAmount = BigDecimal.ZERO;

    @MetaData(value = "操作摘要")
    @JsonProperty
    private String operationSummary;

    @MetaData(value = "标识已红冲")
    @JsonProperty
    private Boolean redword;

    public StockInOut() {
        super();
    }

    public StockInOut(String voucher, String subVoucher, VoucherTypeEnum voucherType, CommodityStock commodityStock) {
        BigDecimal zero = BigDecimal.ZERO;
        this.voucher = voucher;
        this.subVoucher = subVoucher;
        this.voucherType = voucherType;
        this.commodityStock = commodityStock;
        this.originalQuantity = commodityStock.getCurStockQuantity();
        this.originalSalingQuantity = commodityStock.getSalingTotalQuantity() == null ? zero : commodityStock
                .getSalingTotalQuantity();
        this.originalPurchasingQuantity = commodityStock.getPurchasingTotalQuantity() == null ? zero : commodityStock
                .getPurchasingTotalQuantity();
        this.originalCostPrice = commodityStock.getCostPrice();
        this.originalStockAmount = commodityStock.getCurStockAmount();
    }

    public void addOperationSummary(String operationSummary) {
        if (StringUtils.isBlank(this.operationSummary)) {
            this.operationSummary = operationSummary;
        } else {
            this.operationSummary += ";" + operationSummary;
        }
    }

    @MetaData("单据/凭证类型")
    public enum VoucherTypeEnum {

        @MetaData(value = "采购订单")
        JHD,

        @MetaData(value = "采购(入库)单")
        JH,

        @MetaData(value = "销售订单")
        XSD,

        @MetaData(value = "销售单")
        XS,

        @MetaData(value = "调拨单")
        DH,

        @MetaData(value = "付款单")
        FKD,

        @MetaData(value = "盘存")
        PC,

        @MetaData(value = "退货")
        TH,

        @MetaData(value = "收款单")
        SKD
    }
}
