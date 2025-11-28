package com.lingli.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lingli.common.core.BaseEntity;
import com.lingli.common.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：0-禁用，1-启用
     */
    private UserStatus status;

    /**
     * 最后登录时间
     */
    private java.time.LocalDateTime lastLoginAt;
}