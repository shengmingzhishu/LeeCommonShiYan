package com.lingli.common.enums;

/**
 * 支付状态枚举
 *
 * @author lingli
 * @since 2023-11-28
 */
public enum PayStatus {
    
    PENDING(1, "待支付"),
    PAYING(2, "支付中"),
    PAID(3, "已支付"),
    PAYMENT_FAILED(4, "支付失败");
    
    private final Integer code;
    private final String description;
    
    PayStatus(Integer code, String description) {
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
    public static PayStatus getByCode(Integer code) {
        for (PayStatus status : PayStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}