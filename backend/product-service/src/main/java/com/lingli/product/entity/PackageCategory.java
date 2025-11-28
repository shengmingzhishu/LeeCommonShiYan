package com.lingli.product.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingli.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 套餐分类实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("package_categories")
public class PackageCategory extends BaseEntity {

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类代码
     */
    private String code;

    /**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}