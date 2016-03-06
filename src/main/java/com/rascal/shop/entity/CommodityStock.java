package com.rascal.shop.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseNativeEntity;
import com.rascal.core.web.json.DateJsonSerializer;
import com.rascal.core.web.json.EntityIdDisplaySerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "shop_stock_commodity", uniqueConstraints = @UniqueConstraint(columnNames = {"Storage_Location_SID", "COMMODITY_SID", "batchNo"}))
@MetaData(value = "商品库存数据")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class CommodityStock extends BaseNativeEntity {

    @MetaData(value = "库存商品")
    @ManyToOne
    @JoinColumn(name = "commodity_sid", nullable = false, updatable = false)
    @JsonProperty
    private Commodity commodity;

    @MetaData(value = "库存地")
    @OneToOne
    @JoinColumn(name = "storage_location_sid", nullable = false, updatable = false)
    @JsonProperty
    @JsonSerialize(using = EntityIdDisplaySerializer.class)
    @NotAudited
    private StorageLocation storageLocation;


    @MetaData(value = "实物库存数量 ", comments = "采购单创建时加权更新计算")
    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    private BigDecimal curStockQuantity = BigDecimal.ZERO;

    @MetaData(value = "销售锁定数量", comments = "采购单提交时累加此属性，拣货出库时扣减此属性值")
    @Column(precision = 18, scale = 2)
    @JsonProperty
    private BigDecimal salingTotalQuantity = BigDecimal.ZERO;

    @MetaData(value = "采购在途数量", comments = "采购订单审核通过时累加此属性，采购订单收货入库时扣减此属性值")
    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    private BigDecimal purchasingTotalQuantity = BigDecimal.ZERO;

    @MetaData(value = "库存预留值")
    @Column(precision = 18, scale = 2)
    @JsonProperty
    private BigDecimal stockThresholdQuantity = BigDecimal.ZERO;


    @MetaData(value = "期初数量", comments = "期末结转更新")
    @Column(precision = 18, scale = 2)
    @JsonProperty
    private BigDecimal quantity00 = BigDecimal.ZERO;

    @MetaData(value = "期初成本金额", comments = "期末结转更新")
    @Column(precision = 18, scale = 2)
    @JsonProperty
    private BigDecimal costAmount00 = BigDecimal.ZERO;

    @MetaData(value = "期初成本价", comments = "期末结转更新：costAmount00/quantity00")
    @Column(precision = 18, scale = 2)
    @JsonProperty
    private BigDecimal costPrice00 = BigDecimal.ZERO;

    @MetaData(value = "当前成本价", comments = "采购单创建时加权更新计算成本价=(curStockAmount+入库金额)/(curStockQuantity+入库数量)")
    @Column(precision = 18, scale = 2)
    @JsonProperty
    private BigDecimal costPrice = BigDecimal.ZERO;

    @MetaData(value = "当前成本金额", comments = "采购单创建时加权更新计算")
    @Column(precision = 18, scale = 2, nullable = false)
    @JsonProperty
    private BigDecimal curStockAmount = BigDecimal.ZERO;

    @MetaData("批次号")
    @Column(length = 128, updatable = false)
    @JsonProperty
    private String batchNo;

    @MetaData("生产日期")
    @JsonProperty
    @JsonSerialize(using = DateJsonSerializer.class)
    private Date productDate;

    @MetaData("到期日期")
    @JsonProperty
    @JsonSerialize(using = DateJsonSerializer.class)
    private Date expireDate;

    @Formula("(cur_Stock_Quantity + purchasing_Total_Quantity - saling_Total_Quantity - stock_Threshold_Quantity)")
    @JsonProperty
    @NotAudited
    private BigDecimal availableQuantity;
}
