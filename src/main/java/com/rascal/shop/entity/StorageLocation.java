package com.rascal.shop.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.entity.BaseUuidEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "shop_stock_storage_location")
@MetaData(value = "库存地")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StorageLocation extends BaseUuidEntity {

    @MetaData(value = "编码")
    @Column(length = 32, unique = true, nullable = false)
    @JsonProperty
    @Size(min = 3, max = 10)
    private String code;

    @MetaData(value = "名称")
    @Column(length = 128, nullable = false)
    @JsonProperty
    private String title;

    @MetaData(value = "地址")
    @Column(length = 512)
    @JsonProperty
    private String addr;

    @Transient
    @JsonProperty
    public String getDisplay() {
        return code + " " + title;
    }
}
