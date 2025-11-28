package com.lingli.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingli.common.core.BaseEntity;
import com.lingli.common.enums.Gender;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户资料实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_profiles")
public class UserProfile extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Gender gender;

    /**
     * 出生日期
     */
    private java.time.LocalDate birthDate;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;
}