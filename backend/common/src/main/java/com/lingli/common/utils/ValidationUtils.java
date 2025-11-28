package com.lingli.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 *
 * @author lingli
 * @since 2023-11-28
 */
public class ValidationUtils {

    /**
     * 手机号正则表达式
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 邮箱正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    /**
     * 身份证号正则表达式
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
            "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

    /**
     * 用户名正则表达式（3-20位字母数字下划线）
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    /**
     * 验证手机号
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        return matcher.matches();
    }

    /**
     * 验证邮箱
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * 验证身份证号
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null) {
            return false;
        }
        Matcher matcher = ID_CARD_PATTERN.matcher(idCard);
        return matcher.matches();
    }

    /**
     * 验证用户名
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        Matcher matcher = USERNAME_PATTERN.matcher(username);
        return matcher.matches();
    }

    /**
     * 验证密码强度
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // 检查是否包含数字
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // 检查是否包含字母
        if (!password.matches(".*[a-zA-Z].*")) {
            return false;
        }

        return true;
    }

    /**
     * 验证是否为空或空白
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 验证字符串长度
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * 验证是否为正整数
     */
    public static boolean isPositiveInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            int value = Integer.parseInt(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证是否为非负整数
     */
    public static boolean isNonNegativeInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            int value = Integer.parseInt(str);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证是否为正数（金额）
     */
    public static boolean isPositiveAmount(String str) {
        if (str == null) {
            return false;
        }
        try {
            double value = Double.parseDouble(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}