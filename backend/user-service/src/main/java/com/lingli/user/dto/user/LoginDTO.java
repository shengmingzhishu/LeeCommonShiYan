package com.lingli.user.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 用户登录DTO
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
public class LoginDTO {

    /**
     * 用户名/手机号/邮箱
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}