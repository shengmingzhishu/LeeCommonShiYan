package com.lingli.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingli.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 *
 * @author lingli
 * @since 2023-11-28
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(@Param("username") String username);

    /**
     * 根据手机号查找用户
     */
    User findByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查找用户
     */
    User findByEmail(@Param("email") String email);
}