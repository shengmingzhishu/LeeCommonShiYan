package com.lingli.location.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingli.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户地理位置记录实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_locations")
public class UserLocation extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 经度
     */
    private java.math.BigDecimal longitude;

    /**
     * 纬度
     */
    private java.math.BigDecimal latitude;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 位置来源：ip-IP地址定位，user_select-用户选择，gps-GPS定位
     */
    private String locationSource;
}