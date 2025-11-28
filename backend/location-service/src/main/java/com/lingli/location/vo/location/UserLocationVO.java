package com.lingli.location.vo.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户地理位置VO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLocationVO {

    /**
     * 地理位置ID
     */
    private Long id;

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
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 位置来源
     */
    private String locationSource;

    /**
     * 位置来源描述
     */
    private String locationSourceDesc;

    /**
     * 是否已设置位置
     */
    private Boolean hasLocation;

    /**
     * 所在城市信息
     */
    private CityVO cityInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}