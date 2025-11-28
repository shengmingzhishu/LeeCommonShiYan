package com.lingli.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.lingli.common.core.BaseEntity;
import com.lingli.common.enums.OrderStatus;
import com.lingli.common.enums.PayStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Order extends BaseEntity {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 所属公司ID
     */
    private Long companyId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 已支付金额
     */
    private BigDecimal paidAmount;

    /**
     * 订单状态
     */
    private OrderStatus status;

    /**
     * 支付状态
     */
    private PayStatus payStatus;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 配送方式：1-自邮寄，2-上门取样
     */
    private Integer shippingType;

    /**
     * 配送地址
     */
    private String shippingAddress;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 支付记录ID
     */
    private Long paymentId;

    /**
     * 采样状态：1-待采样，2-已预约，3-已采样，4-已送检
     */
    private Integer samplingStatus;

    /**
     * 预约ID
     */
    private Long appointmentId;
}