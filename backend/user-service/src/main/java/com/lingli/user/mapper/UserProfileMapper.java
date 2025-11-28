package com.lingli.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingli.user.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户资料Mapper接口
 *
 * @author lingli
 * @since 2023-11-28
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}