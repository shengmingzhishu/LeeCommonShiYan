package com.lingli.location.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingli.common.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约上门取样实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sampling_appointments")
public class SamplingAppointment extends BaseEntity {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单项ID
     */
    private Long orderItemId;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 预约时间段：09:00-12:00，14:00-17:00，18:00-21:00
     */
    private String appointmentTimeSlot;

    /**
     * 预约地址
     */
    private String appointmentAddress;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 预约状态：1-待确认，2-已确认，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 确认人
     */
    private String confirmedBy;

    /**
     * 确认时间
     */
    private LocalDateTime confirmedAt;

    /**
     * 采样人员
     */
    private String sampledBy;

    /**
     * 采样完成时间
     */
    private LocalDateTime sampledAt;
}