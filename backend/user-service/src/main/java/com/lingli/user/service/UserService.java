package com.lingli.user.service;

import com.lingli.user.dto.user.LoginDTO;
import com.lingli.user.dto.user.RegisterDTO;
import com.lingli.user.entity.User;
import com.lingli.user.vo.UserVO;
import com.lingli.common.core.PageResult;

/**
 * 用户服务接口
 *
 * @author lingli
 * @since 2023-11-28
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserVO register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    UserVO login(LoginDTO loginDTO);

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 根据ID查找用户
     */
    User findById(Long id);

    /**
     * 刷新令牌
     */
    UserVO refreshToken(String refreshToken);

    /**
     * 获取当前登录用户信息
     */
    UserVO getCurrentUser(Long userId);

    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(Long userId);

    /**
     * 检查用户名是否可用
     */
    boolean isUsernameAvailable(String username);

    /**
     * 检查手机号是否可用
     */
    boolean isPhoneAvailable(String phone);

    /**
     * 检查邮箱是否可用
     */
    boolean isEmailAvailable(String email);
}