package com.lingli.product.dto.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 套餐分类DTO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
public class CategoryDTO {

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 分类代码
     */
    @NotBlank(message = "分类代码不能为空")
    @Pattern(regexp = "^[A-Z_]{2,20}$", message = "分类代码只能包含大写字母和下划线，长度2-20位")
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
     * 状态：0-禁用，1-启用
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}