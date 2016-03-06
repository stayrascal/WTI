package com.rascal.shop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseUuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "shop_commodity")
@MetaData(value = "商品")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Audited
public class Commodity extends BaseUuidEntity {

    @MetaData(value = "唯一编码")
    @Column(length = 64, unique = true, nullable = false)
    @JsonProperty
    @Size(min = 5, max = 10)
    private String sku;

    @MetaData(value = "实物条码")
    @JsonProperty
    private String barcode;

    @MetaData(value = "商品名称")
    @JsonProperty
    @Column(length = 256, nullable = false)
    private String title;

    @JsonProperty
    @MetaData(value = "成本价")
    private BigDecimal costPrice;

    @JsonProperty
    @MetaData(value = "销售价")
    private BigDecimal salePrice;

    @MetaData(value = "默认库存地", tooltips = "用于采购或销售时初始库存地")
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "default_storage_location_id")
    @NotAudited
    @JsonProperty
    private StorageLocation defaultStorageLocation;

    @MetaData(value = "不可买", tooltips = "显示商品信息，但处于不可购买状态")
    @Column(nullable = false)
    private Boolean soldOut = Boolean.FALSE;

    @MetaData(value = "已下架", tooltips = "不显示商品信息只提示商品已下架")
    @Column(nullable = false)
    private Boolean removed = Boolean.FALSE;

    @Transient
    @JsonProperty
    public String getDisplay() {
        return sku + " " + title;
    }

}
