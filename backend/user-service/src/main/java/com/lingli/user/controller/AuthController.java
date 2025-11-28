package com.lingli.user.controller;

import com.lingli.common.core.Result;
import com.lingli.common.utils.JwtUtils;
import com.lingli.user.dto.user.LoginDTO;
import com.lingli.user.dto.user.RegisterDTO;
import com.lingli.user.service.UserService;
import com.lingli.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 *
 * @author lingli
 * @since 2023-11-28
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "用户认证", description = "用户认证相关接口")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<UserVO> register(@Validated @RequestBody RegisterDTO registerDTO) {
        log.info("用户注册请求: username={}", registerDTO.getUsername());
        UserVO userVO = userService.register(registerDTO);
        return Result.success("注册成功", userVO);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<UserVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        log.info("用户登录请求: username={}", loginDTO.getUsername());
        UserVO userVO = userService.login(loginDTO);
        return Result.success("登录成功", userVO);
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌")
    public Result<UserVO> refresh(@RequestBody RefreshTokenRequest request) {
        log.info("用户刷新令牌请求");
        UserVO userVO = userService.refreshToken(request.getRefreshToken());
        return Result.success("令牌刷新成功", userVO);
    }

    @GetMapping("/profile")
    @Operation(summary = "获取用户信息")
    public Result<UserVO> getProfile(HttpServletRequest request) {
        String token = extractToken(request);
        Long userId = jwtUtils.getUserIdFromToken(token);
        UserVO userVO = userService.getCurrentUser(userId);
        return Result.success(userVO);
    }

    @GetMapping("/check/username")
    @Operation(summary = "检查用户名是否可用")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        return Result.success(available);
    }

    @GetMapping("/check/phone")
    @Operation(summary = "检查手机号是否可用")
    public Result<Boolean> checkPhone(@RequestParam String phone) {
        boolean available = userService.isPhoneAvailable(phone);
        return Result.success(available);
    }

    @GetMapping("/check/email")
    @Operation(summary = "检查邮箱是否可用")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean available = userService.isEmailAvailable(email);
        return Result.success(available);
    }

    /**
     * 从请求头中提取JWT令牌
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("未找到认证令牌");
    }

    /**
     * 刷新令牌请求
     */
    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}