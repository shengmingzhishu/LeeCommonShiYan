package com.lingli.common.enums;

/**
 * 订单状态枚举
 *
 * @author lingli
 * @since 2023-11-28
 */
public enum OrderStatus {
    
    PENDING_PAYMENT(1, "待支付"),
    PAID(2, "已支付"),
    PENDING_SHIPMENT(3, "待发货"),
    SHIPPED(4, "已发货"),
    COMPLETED(5, "已完成"),
    CANCELLED(6, "已取消");
    
    private final Integer code;
    private final String description;
    
    OrderStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static OrderStatus getByCode(Integer code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}