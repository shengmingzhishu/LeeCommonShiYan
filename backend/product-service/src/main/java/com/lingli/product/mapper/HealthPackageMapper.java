package com.lingli.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lingli.product.entity.HealthPackage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 体检套餐Mapper接口
 *
 * @author lingli
 * @since 2023-11-28
 */
@Mapper
public interface HealthPackageMapper extends BaseMapper<HealthPackage> {

    /**
     * 根据分类ID分页查询启用的套餐
     */
    IPage<HealthPackage> selectByCategoryId(Page<HealthPackage> page, @Param("categoryId") Long categoryId);

    /**
     * 搜索套餐（按名称和描述）
     */
    IPage<HealthPackage> searchPackages(Page<HealthPackage> page, @Param("keyword") String keyword);

    /**
     * 获取热门套餐（按销量或排序）
     */
    IPage<HealthPackage> selectHotPackages(Page<HealthPackage> page);

    /**
     * 获取推荐套餐
     */
    IPage<HealthPackage> selectRecommendedPackages(Page<HealthPackage> page);
}