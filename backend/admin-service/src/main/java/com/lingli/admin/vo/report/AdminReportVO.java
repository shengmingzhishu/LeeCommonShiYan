package com.lingli.admin.vo.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员报告VO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminReportVO {

    /**
     * 报告ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 订单项ID
     */
    private Long orderItemId;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 采样人ID
     */
    private Long samplerId;

    /**
     * 采样人姓名
     */
    private String samplerName;

    /**
     * 采样人身份证
     */
    private String samplerIdCard;

    /**
     * 报告标题
     */
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
     * 报告类型：1-检验报告，2-健康建议
     */
    private Integer reportType;

    /**
     * 报告类型描述
     */
    private String reportTypeDesc;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 报告状态：1-草稿，2-已发布，3-已审核
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

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
     * 紧急程度描述
     */
    private String urgencyLevelDesc;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 城市
     */
    private String city;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 更新人
     */
    private String updatedBy;
}