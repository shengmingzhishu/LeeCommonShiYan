package com.lingli.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingli.common.core.PageResult;
import com.lingli.common.exception.BusinessException;
import com.lingli.common.utils.JsonUtils;
import com.lingli.product.entity.PackageCategory;
import com.lingli.product.entity.HealthPackage;
import com.lingli.product.mapper.PackageCategoryMapper;
import com.lingli.product.mapper.HealthPackageMapper;
import com.lingli.product.dto.category.CategoryDTO;
import com.lingli.product.dto.packageinfo.PackageDTO;
import com.lingli.product.service.ProductService;
import com.lingli.product.vo.CategoryVO;
import com.lingli.product.vo.PackageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 *
 * @author lingli
 * @since 2023-11-28
 */
@Slf4j
@Service
@Transactional
public class ProductServiceImpl extends ServiceImpl<PackageCategoryMapper, PackageCategory> implements ProductService {

    @Autowired
    private PackageCategoryMapper categoryMapper;

    @Autowired
    private HealthPackageMapper packageMapper;

    @Override
    public List<CategoryVO> getCategoryTree() {
        // 获取所有启用的分类
        LambdaQueryWrapper<PackageCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PackageCategory::getStatus, 1)
               .orderByAsc(PackageCategory::getSortOrder);
        
        List<PackageCategory> categories = categoryMapper.selectList(wrapper);
        
        // 转换为VO
        List<CategoryVO> categoryVOs = categories.stream()
                .map(this::convertToCategoryVO)
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildCategoryTree(categoryVOs, 0L);
    }

    @Override
    public List<CategoryVO> getTopCategories() {
        LambdaQueryWrapper<PackageCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PackageCategory::getStatus, 1)
               .eq(PackageCategory::getParentId, 0)
               .orderByAsc(PackageCategory::getSortOrder);
        
        List<PackageCategory> categories = categoryMapper.selectList(wrapper);
        
        return categories.stream()
                .map(this::convertToCategoryVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getCategoriesByParentId(Long parentId) {
        LambdaQueryWrapper<PackageCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PackageCategory::getStatus, 1)
               .eq(PackageCategory::getParentId, parentId)
               .orderByAsc(PackageCategory::getSortOrder);
        
        List<PackageCategory> categories = categoryMapper.selectList(wrapper);
        
        return categories.stream()
                .map(this::convertToCategoryVO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryVO getCategoryDetail(Long categoryId) {
        PackageCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(20001, "分类不存在");
        }
        
        return convertToCategoryVO(category);
    }

    @Override
    public CategoryVO createCategory(CategoryDTO categoryDTO) {
        // 检查分类代码是否重复
        LambdaQueryWrapper<PackageCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PackageCategory::getCode, categoryDTO.getCode());
        
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(20002, "分类代码已存在");
        }
        
        // 创建分类
        PackageCategory category = BeanUtil.copyProperties(categoryDTO, PackageCategory.class);
        categoryMapper.insert(category);
        
        log.info("创建分类成功: categoryId={}, name={}", category.getId(), category.getName());
        return convertToCategoryVO(category);
    }

    @Override
    public CategoryVO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        PackageCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(20001, "分类不存在");
        }
        
        // 检查分类代码是否重复（排除自己）
        LambdaQueryWrapper<PackageCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PackageCategory::getCode, categoryDTO.getCode())
               .ne(PackageCategory::getId, categoryId);
        
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(20002, "分类代码已存在");
        }
        
        // 更新分类
        BeanUtil.copyProperties(categoryDTO, category);
        category.setId(categoryId);
        categoryMapper.updateById(category);
        
        log.info("更新分类成功: categoryId={}, name={}", categoryId, category.getName());
        return convertToCategoryVO(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        PackageCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(20001, "分类不存在");
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<PackageCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PackageCategory::getParentId, categoryId);
        
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(20003, "该分类下有子分类，无法删除");
        }
        
        // 检查是否有套餐使用此分类
        LambdaQueryWrapper<HealthPackage> packageWrapper = new LambdaQueryWrapper<>();
        packageWrapper.eq(HealthPackage::getCategoryId, categoryId);
        
        if (packageMapper.selectCount(packageWrapper) > 0) {
            throw new BusinessException(20004, "该分类下有套餐，无法删除");
        }
        
        categoryMapper.deleteById(categoryId);
        log.info("删除分类成功: categoryId={}, name={}", categoryId, category.getName());
    }

    @Override
    public PageResult<PackageVO> getPackageList(Long categoryId, String keyword, Integer page, Integer size) {
        Page<HealthPackage> pageRequest = new Page<>(page, size);
        
        IPage<HealthPackage> result;
        if (categoryId != null && categoryId > 0) {
            result = packageMapper.selectByCategoryId(pageRequest, categoryId);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            result = packageMapper.searchPackages(pageRequest, keyword.trim());
        } else {
            // 获取所有启用的套餐
            LambdaQueryWrapper<HealthPackage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(HealthPackage::getStatus, 1)
                   .orderByAsc(HealthPackage::getSortOrder);
            result = packageMapper.selectPage(pageRequest, wrapper);
        }
        
        List<PackageVO> packageVOs = result.getRecords().stream()
                .map(this::convertToPackageVO)
                .collect(Collectors.toList());
        
        return PageResult.of(packageVOs, result.getTotal(), page, size);
    }

    @Override
    public PackageVO getPackageDetail(Long packageId) {
        HealthPackage healthPackage = packageMapper.selectById(packageId);
        if (healthPackage == null) {
            throw new BusinessException(20011, "套餐不存在");
        }
        
        if (healthPackage.getStatus() == 0) {
            throw new BusinessException(20012, "套餐已下架");
        }
        
        return convertToPackageVO(healthPackage);
    }

    @Override
    public List<PackageVO> getHotPackages(Integer limit) {
        Page<HealthPackage> pageRequest = new Page<>(1, limit);
        IPage<HealthPackage> result = packageMapper.selectHotPackages(pageRequest);
        
        return result.getRecords().stream()
                .map(this::convertToPackageVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PackageVO> getRecommendedPackages(Integer limit) {
        Page<HealthPackage> pageRequest = new Page<>(1, limit);
        IPage<HealthPackage> result = packageMapper.selectRecommendedPackages(pageRequest);
        
        return result.getRecords().stream()
                .map(this::convertToPackageVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<PackageVO> searchPackages(String keyword, Integer page, Integer size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException(40001, "搜索关键词不能为空");
        }
        
        Page<HealthPackage> pageRequest = new Page<>(page, size);
        IPage<HealthPackage> result = packageMapper.searchPackages(pageRequest, keyword.trim());
        
        List<PackageVO> packageVOs = result.getRecords().stream()
                .map(this::convertToPackageVO)
                .collect(Collectors.toList());
        
        return PageResult.of(packageVOs, result.getTotal(), page, size);
    }

    @Override
    public PackageVO createPackage(PackageDTO packageDTO) {
        // 检查套餐代码是否重复
        if (!isPackageCodeAvailable(packageDTO.getCode(), null)) {
            throw new BusinessException(20021, "套餐代码已存在");
        }
        
        // 检查分类是否存在
        PackageCategory category = categoryMapper.selectById(packageDTO.getCategoryId());
        if (category == null || category.getStatus() == 0) {
            throw new BusinessException(20022, "分类不存在或已禁用");
        }
        
        // 创建套餐
        HealthPackage healthPackage = BeanUtil.copyProperties(packageDTO, HealthPackage.class);
        
        // 处理JSON字段
        if (packageDTO.getDetailImages() != null) {
            healthPackage.setDetailImages(JsonUtils.toJson(packageDTO.getDetailImages()));
        }
        if (packageDTO.getTestItems() != null) {
            healthPackage.setTestItems(JsonUtils.toJson(packageDTO.getTestItems()));
        }
        
        packageMapper.insert(healthPackage);
        
        log.info("创建套餐成功: packageId={}, name={}", healthPackage.getId(), healthPackage.getName());
        return convertToPackageVO(healthPackage);
    }

    @Override
    public PackageVO updatePackage(Long packageId, PackageDTO packageDTO) {
        HealthPackage healthPackage = packageMapper.selectById(packageId);
        if (healthPackage == null) {
            throw new BusinessException(20011, "套餐不存在");
        }
        
        // 检查套餐代码是否重复（排除自己）
        if (!isPackageCodeAvailable(packageDTO.getCode(), packageId)) {
            throw new BusinessException(20021, "套餐代码已存在");
        }
        
        // 检查分类是否存在
        PackageCategory category = categoryMapper.selectById(packageDTO.getCategoryId());
        if (category == null || category.getStatus() == 0) {
            throw new BusinessException(20022, "分类不存在或已禁用");
        }
        
        // 更新套餐
        BeanUtil.copyProperties(packageDTO, healthPackage);
        healthPackage.setId(packageId);
        
        // 处理JSON字段
        if (packageDTO.getDetailImages() != null) {
            healthPackage.setDetailImages(JsonUtils.toJson(packageDTO.getDetailImages()));
        }
        if (packageDTO.getTestItems() != null) {
            healthPackage.setTestItems(JsonUtils.toJson(packageDTO.getTestItems()));
        }
        
        packageMapper.updateById(healthPackage);
        
        log.info("更新套餐成功: packageId={}, name={}", packageId, healthPackage.getName());
        return convertToPackageVO(healthPackage);
    }

    @Override
    public void deletePackage(Long packageId) {
        HealthPackage healthPackage = packageMapper.selectById(packageId);
        if (healthPackage == null) {
            throw new BusinessException(20011, "套餐不存在");
        }
        
        packageMapper.deleteById(packageId);
        log.info("删除套餐成功: packageId={}, name={}", packageId, healthPackage.getName());
    }

    @Override
    public PackageVO updatePackageStatus(Long packageId, Integer status) {
        HealthPackage healthPackage = packageMapper.selectById(packageId);
        if (healthPackage == null) {
            throw new BusinessException(20011, "套餐不存在");
        }
        
        healthPackage.setStatus(status);
        packageMapper.updateById(healthPackage);
        
        log.info("更新套餐状态成功: packageId={}, status={}", packageId, status);
        return convertToPackageVO(healthPackage);
    }

    @Override
    public boolean isPackageCodeAvailable(String code, Long excludeId) {
        LambdaQueryWrapper<HealthPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPackage::getCode, code);
        
        if (excludeId != null) {
            wrapper.ne(HealthPackage::getId, excludeId);
        }
        
        return packageMapper.selectCount(wrapper) == 0;
    }

    // ===== 私有方法 =====

    /**
     * 转换为分类VO
     */
    private CategoryVO convertToCategoryVO(PackageCategory category) {
        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        categoryVO.setEnabled(category.getStatus() == 1);
        
        // 添加默认图标
        categoryVO.setIcon(getDefaultIcon(category.getCode()));
        
        return categoryVO;
    }

    /**
     * 转换为套餐VO
     */
    private PackageVO convertToPackageVO(HealthPackage healthPackage) {
        PackageVO packageVO = BeanUtil.copyProperties(healthPackage, PackageVO.class);
        
        // 设置分类名称
        if (healthPackage.getCategoryId() != null) {
            PackageCategory category = categoryMapper.selectById(healthPackage.getCategoryId());
            if (category != null) {
                packageVO.setCategoryName(category.getName());
            }
        }
        
        // 处理JSON字段
        if (healthPackage.getDetailImages() != null) {
            packageVO.setDetailImages(JsonUtils.fromJson(healthPackage.getDetailImages(), List.class));
        }
        if (healthPackage.getTestItems() != null) {
            packageVO.setTestItems(JsonUtils.fromJson(healthPackage.getTestItems(), List.class));
        }
        
        // 计算折扣百分比
        if (healthPackage.getOriginalPrice() != null && healthPackage.getOriginalPrice().compareTo(healthPackage.getPrice()) > 0) {
            BigDecimal discount = healthPackage.getOriginalPrice().subtract(healthPackage.getPrice())
                    .divide(healthPackage.getOriginalPrice(), 2, BigDecimal.ROUND_HALF_UP);
            packageVO.setDiscountPercent(discount.multiply(new java.math.BigDecimal("100")).intValue());
        }
        
        // 设置采样方式描述
        packageVO.setSamplingMethodDesc(getSamplingMethodDesc(healthPackage.getSamplingMethod()));
        
        packageVO.setOnShelf(healthPackage.getStatus() == 1);
        
        // 设置默认标签
        packageVO.setTags(getDefaultTags(healthPackage));
        
        return packageVO;
    }

    /**
     * 构建分类树形结构
     */
    private List<CategoryVO> buildCategoryTree(List<CategoryVO> categories, Long parentId) {
        return categories.stream()
                .filter(category -> ObjectUtil.equal(category.getParentId(), parentId))
                .peek(category -> {
                    List<CategoryVO> children = buildCategoryTree(categories, category.getId());
                    if (!children.isEmpty()) {
                        category.setChildren(children);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取默认图标
     */
    private String getDefaultIcon(String code) {
        Map<String, String> iconMap = Map.of(
                "BASIC", "🩺",
                "PRE_EMPLOYMENT", "💼",
                "ANNUAL_CHECKUP", "📅",
                "OCCUPATIONAL_HEALTH", "🏭",
                "PREMIUM", "⭐",
                "ELITE", "👑",
                "VIP", "💎",
                "SPECIALIZED", "🔬",
                "CARDIOVASCULAR", "❤️",
                "CANCER_SCREENING", "🎯"
        );
        return iconMap.getOrDefault(code, "📋");
    }

    /**
     * 获取采样方式描述
     */
    private String getSamplingMethodDesc(Integer method) {
        switch (method != null ? method : 0) {
            case 1: return "自采样";
            case 2: return "上门采样";
            case 3: return "自采样 + 上门采样";
            default: return "未知";
        }
    }

    /**
     * 获取默认标签
     */
    private List<String> getDefaultTags(HealthPackage packageInfo) {
        List<String> tags = new ArrayList<>();
        
        if (packageInfo.getDiscountPercent() != null && packageInfo.getDiscountPercent() > 0) {
            tags.add("限时优惠");
        }
        
        if (packageInfo.getSamplingMethod() != null && packageInfo.getSamplingMethod() == 3) {
            tags.add("双采样方式");
        }
        
        if (packageInfo.getStock() != null && packageInfo.getStock() < 100) {
            tags.add("库存紧张");
        }
        
        return tags;
    }
}