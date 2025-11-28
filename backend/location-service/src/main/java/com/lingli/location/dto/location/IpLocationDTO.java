package com.lingli.location.dto.location;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * IP地理位置DTO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
public class IpLocationDTO {

    /**
     * IP地址
     */
    private String ip;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份/州
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
     * 运营商
     */
    private String isp;

    /**
     * 定位精度（米）
     */
    private Integer accuracy;

    /**
     * 是否为中国大陆
     */
    private Boolean isChina;
}