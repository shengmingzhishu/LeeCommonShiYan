package com.lingli.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingli.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("shopping_cart")
public class ShoppingCart extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 套餐ID
     */
    private Long packageId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 采样人ID
     */
    private Long samplerId;

    /**
     * 采样方式：1-自邮寄，2-上门预约
     */
    private Integer samplingMethod;
}