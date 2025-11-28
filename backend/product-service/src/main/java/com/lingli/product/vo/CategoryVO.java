package com.lingli.product.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 套餐分类VO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类代码
     */
    private String code;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 子分类列表
     */
    private List<CategoryVO> children;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 图标
     */
    private String icon;
}