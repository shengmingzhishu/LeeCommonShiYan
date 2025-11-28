package com.lingli.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingli.common.core.BaseEntity;
import com.lingli.common.enums.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采样人信息实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("samplers")
public class Sampler extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 采样人姓名
     */
    private String name;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Gender gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 病史信息
     */
    private String medicalHistory;
}