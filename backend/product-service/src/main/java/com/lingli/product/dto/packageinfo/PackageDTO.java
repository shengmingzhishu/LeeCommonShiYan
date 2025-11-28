package com.lingli.product.dto.packageinfo;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

/**
 * 体检套餐DTO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
public class PackageDTO {

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 套餐名称
     */
    @NotBlank(message = "套餐名称不能为空")
    private String name;

    /**
     * 套餐代码
     */
    @NotBlank(message = "套餐代码不能为空")
    @Pattern(regexp = "^[A-Z_]{2,30}$", message = "套餐代码只能包含大写字母和下划线，长度2-30位")
    private String code;

    /**
     * 套餐描述
     */
    private String description;

    /**
     * 套餐价格
     */
    @NotNull(message = "套餐价格不能为空")
    @DecimalMin(value = "0.01", message = "套餐价格必须大于0")
    private BigDecimal price;

    /**
     * 原价
     */
    @DecimalMin(value = "0.00", message = "原价不能为负数")
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
    @NotNull(message = "采样方式不能为空")
    private Integer samplingMethod;

    /**
     * 报告交付天数
     */
    @NotNull(message = "报告交付天数不能为空")
    private Integer reportDeliveryDays;

    /**
     * 库存数量
     */
    @NotNull(message = "库存数量不能为空")
    private Integer stock;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态：0-下架，1-上架
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}