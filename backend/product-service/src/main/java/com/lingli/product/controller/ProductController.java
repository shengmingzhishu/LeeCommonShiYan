package com.lingli.product.controller;

import com.lingli.common.core.PageResult;
import com.lingli.common.core.Result;
import com.lingli.product.dto.category.CategoryDTO;
import com.lingli.product.dto.packageinfo.PackageDTO;
import com.lingli.product.service.ProductService;
import com.lingli.product.vo.CategoryVO;
import com.lingli.product.vo.PackageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品控制器
 *
 * @author lingli
 * @since 2023-11-28
 */
@Slf4j
@RestController
@RequestMapping("/products")
@Tag(name = "商品管理", description = "商品和套餐相关接口")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    // ===== 分类接口 =====

    @GetMapping("/categories/tree")
    @Operation(summary = "获取分类树形结构")
    public Result<List<CategoryVO>> getCategoryTree() {
        List<CategoryVO> categories = productService.getCategoryTree();
        return Result.success(categories);
    }

    @GetMapping("/categories/top")
    @Operation(summary = "获取顶级分类列表")
    public Result<List<CategoryVO>> getTopCategories() {
        List<CategoryVO> categories = productService.getTopCategories();
        return Result.success(categories);
    }

    @GetMapping("/categories/{parentId}/children")
    @Operation(summary = "根据父分类ID获取子分类")
    public Result<List<CategoryVO>> getCategoriesByParentId(@PathVariable Long parentId) {
        List<CategoryVO> categories = productService.getCategoriesByParentId(parentId);
        return Result.success(categories);
    }

    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "获取分类详情")
    public Result<CategoryVO> getCategoryDetail(@PathVariable Long categoryId) {
        CategoryVO category = productService.getCategoryDetail(categoryId);
        return Result.success(category);
    }

    @PostMapping("/categories")
    @Operation(summary = "创建分类")
    public Result<CategoryVO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryVO category = productService.createCategory(categoryDTO);
        return Result.success("分类创建成功", category);
    }

    @PutMapping("/categories/{categoryId}")
    @Operation(summary = "更新分类")
    public Result<CategoryVO> updateCategory(@PathVariable Long categoryId, 
                                           @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryVO category = productService.updateCategory(categoryId, categoryDTO);
        return Result.success("分类更新成功", category);
    }

    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        productService.deleteCategory(categoryId);
        return Result.success("分类删除成功", null);
    }

    // ===== 套餐接口 =====

    @GetMapping("/packages")
    @Operation(summary = "获取套餐列表")
    public Result<PageResult<PackageVO>> getPackageList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        PageResult<PackageVO> packages = productService.getPackageList(categoryId, keyword, page, size);
        return Result.success(packages);
    }

    @GetMapping("/packages/{packageId}")
    @Operation(summary = "获取套餐详情")
    public Result<PackageVO> getPackageDetail(@PathVariable Long packageId) {
        PackageVO packageInfo = productService.getPackageDetail(packageId);
        return Result.success(packageInfo);
    }

    @GetMapping("/packages/hot")
    @Operation(summary = "获取热门套餐")
    public Result<List<PackageVO>> getHotPackages(@RequestParam(defaultValue = "10") Integer limit) {
        List<PackageVO> packages = productService.getHotPackages(limit);
        return Result.success(packages);
    }

    @GetMapping("/packages/recommended")
    @Operation(summary = "获取推荐套餐")
    public Result<List<PackageVO>> getRecommendedPackages(@RequestParam(defaultValue = "10") Integer limit) {
        List<PackageVO> packages = productService.getRecommendedPackages(limit);
        return Result.success(packages);
    }

    @GetMapping("/packages/search")
    @Operation(summary = "搜索套餐")
    public Result<PageResult<PackageVO>> searchPackages(
            @RequestParam @NotNull(message = "搜索关键词不能为空") String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        PageResult<PackageVO> packages = productService.searchPackages(keyword, page, size);
        return Result.success(packages);
    }

    @PostMapping("/packages")
    @Operation(summary = "创建套餐")
    public Result<PackageVO> createPackage(@Valid @RequestBody PackageDTO packageDTO) {
        PackageVO packageInfo = productService.createPackage(packageDTO);
        return Result.success("套餐创建成功", packageInfo);
    }

    @PutMapping("/packages/{packageId}")
    @Operation(summary = "更新套餐")
    public Result<PackageVO> updatePackage(@PathVariable Long packageId,
                                         @Valid @RequestBody PackageDTO packageDTO) {
        PackageVO packageInfo = productService.updatePackage(packageId, packageDTO);
        return Result.success("套餐更新成功", packageInfo);
    }

    @DeleteMapping("/packages/{packageId}")
    @Operation(summary = "删除套餐")
    public Result<Void> deletePackage(@PathVariable Long packageId) {
        productService.deletePackage(packageId);
        return Result.success("套餐删除成功", null);
    }

    @PutMapping("/packages/{packageId}/status")
    @Operation(summary = "上下架套餐")
    public Result<PackageVO> updatePackageStatus(@PathVariable Long packageId,
                                               @RequestParam Integer status) {
        PackageVO packageInfo = productService.updatePackageStatus(packageId, status);
        return Result.success("套餐状态更新成功", packageInfo);
    }

    @GetMapping("/packages/check-code")
    @Operation(summary = "检查套餐代码是否可用")
    public Result<Boolean> checkPackageCode(@RequestParam String code,
                                           @RequestParam(required = false) Long excludeId) {
        boolean available = productService.isPackageCodeAvailable(code, excludeId);
        return Result.success(available);
    }
}