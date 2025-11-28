package com.lingli.location.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingli.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_records")
public class PaymentRecord extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付方式：wechat-微信支付，alipay-支付宝，unionpay-银联支付
     */
    private String paymentType;

    /**
     * 支付渠道：mp_weixin-微信小程序，h5-h5页面，pc-网页版
     */
    private String paymentChannel;

    /**
     * 支付状态：1-待支付，2-支付中，3-支付成功，4-支付失败，5-已退款
     */
    private Integer status;

    /**
     * 第三方交易号
     */
    private String tradeNo;

    /**
     * 支付回调数据
     */
    private String notifyData;

    /**
     * 支付成功时间
     */
    private LocalDateTime paidAt;
}