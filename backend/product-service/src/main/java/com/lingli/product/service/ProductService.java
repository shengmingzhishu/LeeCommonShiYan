package com.lingli.product.service;

import com.lingli.common.core.PageResult;
import com.lingli.product.dto.category.CategoryDTO;
import com.lingli.product.dto.packageinfo.PackageDTO;
import com.lingli.product.entity.PackageCategory;
import com.lingli.product.entity.HealthPackage;
import com.lingli.product.vo.CategoryVO;
import com.lingli.product.vo.PackageVO;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author lingli
 * @since 2023-11-28
 */
public interface ProductService {

    // ===== 分类相关接口 =====

    /**
     * 获取分类列表（树形结构）
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取顶级分类列表
     */
    List<CategoryVO> getTopCategories();

    /**
     * 根据父分类ID获取子分类
     */
    List<CategoryVO> getCategoriesByParentId(Long parentId);

    /**
     * 获取分类详情
     */
    CategoryVO getCategoryDetail(Long categoryId);

    /**
     * 创建分类
     */
    CategoryVO createCategory(CategoryDTO categoryDTO);

    /**
     * 更新分类
     */
    CategoryVO updateCategory(Long categoryId, CategoryDTO categoryDTO);

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);

    // ===== 套餐相关接口 =====

    /**
     * 获取套餐列表
     */
    PageResult<PackageVO> getPackageList(Long categoryId, String keyword, Integer page, Integer size);

    /**
     * 获取套餐详情
     */
    PackageVO getPackageDetail(Long packageId);

    /**
     * 获取热门套餐
     */
    List<PackageVO> getHotPackages(Integer limit);

    /**
     * 获取推荐套餐
     */
    List<PackageVO> getRecommendedPackages(Integer limit);

    /**
     * 搜索套餐
     */
    PageResult<PackageVO> searchPackages(String keyword, Integer page, Integer size);

    /**
     * 创建套餐
     */
    PackageVO createPackage(PackageDTO packageDTO);

    /**
     * 更新套餐
     */
    PackageVO updatePackage(Long packageId, PackageDTO packageDTO);

    /**
     * 删除套餐
     */
    void deletePackage(Long packageId);

    /**
     * 上下架套餐
     */
    PackageVO updatePackageStatus(Long packageId, Integer status);

    /**
     * 检查套餐代码是否可用
     */
    boolean isPackageCodeAvailable(String code, Long excludeId);
}