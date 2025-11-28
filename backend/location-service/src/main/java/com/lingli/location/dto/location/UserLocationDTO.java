package com.lingli.location.dto.location;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户地理位置DTO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
public class UserLocationDTO {

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
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
     * 位置来源：ip-IP地址定位，user_select-用户选择，gps-GPS定位
     */
    @NotNull(message = "位置来源不能为空")
    private String locationSource;
}