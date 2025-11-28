package com.lingli.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lingli.product.entity.PackageCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 套餐分类Mapper接口
 *
 * @author lingli
 * @since 2023-11-28
 */
@Mapper
public interface PackageCategoryMapper extends BaseMapper<PackageCategory> {

    /**
     * 获取启用的分类列表，按排序字段排序
     */
    IPage<PackageCategory> selectEnabledCategories(Page<PackageCategory> page);

    /**
     * 根据父分类ID获取子分类
     */
    IPage<PackageCategory> selectByParentId(Page<PackageCategory> page, @Param("parentId") Long parentId);
}