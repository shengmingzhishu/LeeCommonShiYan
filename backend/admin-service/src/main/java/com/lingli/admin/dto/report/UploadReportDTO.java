package com.lingli.admin.dto.report;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 检测报告上传DTO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
public class UploadReportDTO {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 订单项ID
     */
    @NotNull(message = "订单项ID不能为空")
    private Long orderItemId;

    /**
     * 采样人ID
     */
    @NotNull(message = "采样人ID不能为空")
    private Long samplerId;

    /**
     * 报告类型：1-检验报告，2-健康建议
     */
    @NotNull(message = "报告类型不能为空")
    private Integer reportType;

    /**
     * 报告标题
     */
    @NotBlank(message = "报告标题不能为空")
    private String title;

    /**
     * 报告描述
     */
    private String description;

    /**
     * 检验结果概要
     */
    private String summary;

    /**
     * 异常指标
     */
    private String abnormalIndicators;

    /**
     * 健康建议
     */
    private String healthAdvice;

    /**
     * 复检建议
     */
    private String retestAdvice;

    /**
     * 报告状态：1-草稿，2-已发布
     */
    @NotNull(message = "报告状态不能为空")
    private Integer status;

    /**
     * 检验日期
     */
    private String testDate;

    /**
     * 报告日期
     */
    private String reportDate;

    /**
     * 检验师
     */
    private String tester;

    /**
     * 审核师
     */
    private String reviewer;

    /**
     * 审核日期
     */
    private String reviewDate;

    /**
     * 审核备注
     */
    private String reviewRemark;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 是否紧急
     */
    private Boolean isUrgent;

    /**
     * 紧急程度：1-普通，2-重要，3-紧急
     */
    private Integer urgencyLevel;

    /**
     * 备注信息
     */
    private String remark;
}