package com.lingli.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.lingli.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 体检套餐实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("health_packages")
public class HealthPackage extends BaseEntity {

    /**
     * 分类ID
     */
    private Long categoryId;

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
    @TableField(exist = false)
    private List<String> detailImages;

    /**
     * 检测项目列表
     */
    @TableField(exist = false)
    private List<String> testItems;

    /**
     * 采样方式：1-自采样，2-上门采样，3-两种方式
     */
    private Integer samplingMethod;

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
     * 状态：0-下架，1-上架
     */
    private Integer status;
}