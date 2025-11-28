package com.lingli.order.dto.cart;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 加入购物车DTO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
public class AddCartDTO {

    /**
     * 套餐ID
     */
    @NotNull(message = "套餐ID不能为空")
    private Long packageId;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    private Integer quantity;

    /**
     * 采样人ID
     */
    private Long samplerId;

    /**
     * 采样方式：1-自邮寄，2-上门预约
     */
    @NotNull(message = "采样方式不能为空")
    private Integer samplingMethod;
}