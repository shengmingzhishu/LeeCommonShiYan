package com.lingli.admin.vo.packageinfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员套餐VO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminPackageVO {

    /**
     * 套餐ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 套餐代码
     */
    private String code;

    /**
     * 套餐描述
     */
    private String description;

    /**
     * 套餐价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 详情图片列表
     */
    private List<String> detailImages;

    /**
     * 检测项目列表
     */
    private List<String> testItems;

    /**
     * 采样方式：1-自采样，2-上门采样，3-两种方式
     */
    private Integer samplingMethod;

    /**
     * 采样方式描述
     */
    private String samplingMethodDesc;

    /**
     * 支持的邮寄方式
     */
    private List<String> shippingMethods;

    /**
     * 报告交付天数
     */
    private Integer reportDeliveryDays;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 是否推荐
     */
    private Boolean isRecommended;

    /**
     * 是否热门
     */
    private Boolean isHot;

    /**
     * 套餐标签
     */
    private List<String> tags;

    /**
     * 套餐特点
     */
    private List<String> features;

    /**
     * 注意事项
     */
    private String notice;

    /**
     * 适用人群
     */
    private String suitableFor;

    /**
     * 禁用人群
     */
    private String notSuitableFor;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 销售数量
     */
    private Integer salesCount;

    /**
     * 浏览数量
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;
}