package com.lingli.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lingli.user.entity.Sampler;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 采样人信息Mapper接口
 *
 * @author lingli
 * @since 2023-11-28
 */
@Mapper
public interface SamplerMapper extends BaseMapper<Sampler> {

    /**
     * 根据用户ID分页查询采样人列表
     */
    IPage<Sampler> selectByUserId(Page<Sampler> page, @Param("userId") Long userId);
}