package com.lingli.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.lingli.common.core.Result;
import com.lingli.common.enums.UserStatus;
import com.lingli.common.exception.BusinessException;
import com.lingli.common.utils.JwtUtils;
import com.lingli.common.utils.PasswordUtils;
import com.lingli.common.utils.ValidationUtils;
import com.lingli.user.dto.user.LoginDTO;
import com.lingli.user.dto.user.RegisterDTO;
import com.lingli.user.entity.User;
import com.lingli.user.mapper.UserMapper;
import com.lingli.user.service.UserService;
import com.lingli.user.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现
 *
 * @author lingli
 * @since 2023-11-28
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordUtils passwordUtils;

    @Override
    public UserVO register(RegisterDTO registerDTO) {
        // 验证用户名唯一性
        if (!isUsernameAvailable(registerDTO.getUsername())) {
            throw new BusinessException(10002, "用户名已存在");
        }

        // 验证手机号唯一性
        if (registerDTO.getPhone() != null && !isPhoneAvailable(registerDTO.getPhone())) {
            throw new BusinessException(10002, "手机号已存在");
        }

        // 验证邮箱唯一性
        if (registerDTO.getEmail() != null && !isEmailAvailable(registerDTO.getEmail())) {
            throw new BusinessException(10002, "邮箱已存在");
        }

        // 创建用户
        User user = new User();
        BeanUtil.copyProperties(registerDTO, user);
        user.setPassword(passwordUtils.encode(registerDTO.getPassword()));
        user.setStatus(UserStatus.ENABLED);

        userMapper.insert(user);

        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), "USER");
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        // 构建返回VO
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setToken(token);
        userVO.setRefreshToken(refreshToken);
        userVO.setExpiresIn(jwtUtils.getExpirationDateFromToken(token).getTime() / 1000);

        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());
        return userVO;
    }

    @Override
    public UserVO login(LoginDTO loginDTO) {
        User user = null;

        // 根据不同登录方式查找用户
        if (ValidationUtils.isValidPhone(loginDTO.getUsername())) {
            user = userMapper.findByPhone(loginDTO.getUsername());
        } else if (ValidationUtils.isValidEmail(loginDTO.getUsername())) {
            user = userMapper.findByEmail(loginDTO.getUsername());
        } else {
            user = userMapper.findByUsername(loginDTO.getUsername());
        }

        // 验证用户存在
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        // 验证用户状态
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(10003, "用户已被禁用");
        }

        // 验证密码
        if (!passwordUtils.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(10004, "密码错误");
        }

        // 更新最后登录时间
        updateLastLoginTime(user.getId());

        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), "USER");
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        // 构建返回VO
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setToken(token);
        userVO.setRefreshToken(refreshToken);
        userVO.setExpiresIn(jwtUtils.getExpirationDateFromToken(token).getTime() / 1000);

        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        return userVO;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public UserVO refreshToken(String refreshToken) {
        // 验证刷新令牌
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BusinessException(10005, "刷新令牌无效");
        }

        // 从刷新令牌中获取用户ID
        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new BusinessException(10005, "刷新令牌无效");
        }

        // 查找用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new BusinessException(10003, "用户已被禁用");
        }

        // 生成新的访问令牌
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), "USER");
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getId());

        // 构建返回VO
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        userVO.setToken(token);
        userVO.setRefreshToken(newRefreshToken);
        userVO.setExpiresIn(jwtUtils.getExpirationDateFromToken(token).getTime() / 1000);

        log.info("用户刷新令牌成功: userId={}", user.getId());
        return userVO;
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(10001, "用户不存在");
        }

        return BeanUtil.copyProperties(user, UserVO.class);
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        userMapper.updateLastLoginTime(userId);
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return userMapper.findByUsername(username) == null;
    }

    @Override
    public boolean isPhoneAvailable(String phone) {
        return phone == null || userMapper.findByPhone(phone) == null;
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return email == null || userMapper.findByEmail(email) == null;
    }
}