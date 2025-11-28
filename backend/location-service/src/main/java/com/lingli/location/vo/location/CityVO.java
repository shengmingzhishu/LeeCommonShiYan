package com.lingli.location.vo.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 城市VO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityVO {

    /**
     * 城市ID
     */
    private Long cityId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市代码
     */
    private String cityCode;

    /**
     * 公司列表
     */
    private List<CompanyVO> companies;

    /**
     * 距离用户位置的距离（米）
     */
    private Integer distance;

    /**
     * 是否为用户所在城市
     */
    private Boolean isUserCity;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 公司信息
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CompanyVO {
        
        private Long companyId;
        private String companyName;
        private String companyCode;
        private String contactPhone;
        private String contactEmail;
        private String address;
        private Boolean isDefault; // 是否为默认公司
    }
}