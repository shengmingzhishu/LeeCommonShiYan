package com.lingli.product.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 体检套餐VO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackageVO {

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
     * 报告交付天数
     */
    private Integer reportDeliveryDays;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 是否上架
     */
    private Boolean onShelf;

    /**
     * 折扣百分比
     */
    private Integer discountPercent;

    /**
     * 套餐特点标签
     */
    private List<String> tags;

    /**
     * 销售数量（可选）
     */
    private Integer salesCount;
}