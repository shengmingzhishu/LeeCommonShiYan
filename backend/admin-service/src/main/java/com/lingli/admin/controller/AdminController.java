package com.lingli.admin.controller;

import com.lingli.admin.dto.packageinfo.AdminPackageDTO;
import com.lingli.admin.dto.report.UploadReportDTO;
import com.lingli.admin.service.AdminService;
import com.lingli.admin.vo.packageinfo.AdminPackageVO;
import com.lingli.admin.vo.report.AdminReportVO;
import com.lingli.common.core.PageResult;
import com.lingli.common.core.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 管理后台控制器
 *
 * @author lingli
 * @since 2023-11-28
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Tag(name = "管理后台", description = "管理后台相关接口")
@Validated
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ========== 商品管理接口 ==========

    @GetMapping("/packages")
    @Operation(summary = "获取商品列表")
    public Result<PageResult<AdminPackageVO>> getPackages(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        PageResult<AdminPackageVO> packages = adminService.getPackages(categoryId, keyword, status, page, size);
        return Result.success(packages);
    }

    @GetMapping("/packages/{packageId}")
    @Operation(summary = "获取套餐详情")
    public Result<AdminPackageVO> getPackageDetail(@PathVariable Long packageId) {
        AdminPackageVO packageInfo = adminService.getPackageDetail(packageId);
        return Result.success(packageInfo);
    }

    @PostMapping("/packages")
    @Operation(summary = "创建套餐")
    public Result<AdminPackageVO> createPackage(@Valid @RequestBody AdminPackageDTO packageDTO) {
        AdminPackageVO packageInfo = adminService.createPackage(packageDTO);
        return Result.success("套餐创建成功", packageInfo);
    }

    @PutMapping("/packages/{packageId}")
    @Operation(summary = "更新套餐")
    public Result<AdminPackageVO> updatePackage(@PathVariable Long packageId, 
                                             @Valid @RequestBody AdminPackageDTO packageDTO) {
        AdminPackageVO packageInfo = adminService.updatePackage(packageId, packageDTO);
        return Result.success("套餐更新成功", packageInfo);
    }

    @DeleteMapping("/packages/{packageId}")
    @Operation(summary = "删除套餐")
    public Result<Void> deletePackage(@PathVariable Long packageId) {
        adminService.deletePackage(packageId);
        return Result.success("套餐删除成功", null);
    }

    @PutMapping("/packages/{packageId}/status")
    @Operation(summary = "上下架套餐")
    public Result<AdminPackageVO> updatePackageStatus(@PathVariable Long packageId, 
                                                   @RequestParam Integer status) {
        AdminPackageVO packageInfo = adminService.updatePackageStatus(packageId, status);
        return Result.success("套餐状态更新成功", packageInfo);
    }

    @PutMapping("/packages/batch/status")
    @Operation(summary = "批量操作套餐状态")
    public Result<Void> batchUpdatePackageStatus(@RequestParam @NotBlank String packageIds, 
                                              @RequestParam @NotNull Integer status) {
        adminService.batchUpdatePackageStatus(packageIds, status);
        return Result.success("批量操作成功", null);
    }

    @PutMapping("/packages/batch/delete")
    @Operation(summary = "批量删除套餐")
    public Result<Void> batchDeletePackages(@RequestParam @NotBlank String packageIds) {
        adminService.batchDeletePackages(packageIds);
        return Result.success("批量删除成功", null);
    }

    @PutMapping("/packages/{packageId}/recommended")
    @Operation(summary = "设置推荐套餐")
    public Result<AdminPackageVO> setRecommended(@PathVariable Long packageId, 
                                              @RequestParam Boolean recommended) {
        AdminPackageVO packageInfo = adminService.setRecommended(packageId, recommended);
        return Result.success("推荐状态更新成功", packageInfo);
    }

    @PutMapping("/packages/{packageId}/hot")
    @Operation(summary = "设置热门套餐")
    public Result<AdminPackageVO> setHot(@PathVariable Long packageId, 
                                      @RequestParam Boolean hot) {
        AdminPackageVO packageInfo = adminService.setHot(packageId, hot);
        return Result.success("热门状态更新成功", packageInfo);
    }

    // ========== 报告管理接口 ==========

    @GetMapping("/reports")
    @Operation(summary = "获取报告列表")
    public Result<PageResult<AdminReportVO>> getReports(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String samplerName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        PageResult<AdminReportVO> reports = adminService.getReports(orderId, samplerName, status, page, size);
        return Result.success(reports);
    }

    @PostMapping("/reports/upload")
    @Operation(summary = "上传检测报告")
    public Result<AdminReportVO> uploadReport(
            @Valid @RequestPart UploadReportDTO reportDTO,
            @RequestPart MultipartFile file) {
        
        // 这里应该保存文件并获取文件URL
        String fileUrl = "https://cdn.example.com/reports/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        
        AdminReportVO reportInfo = adminService.uploadReport(reportDTO, fileUrl);
        return Result.success("报告上传成功", reportInfo);
    }

    @PutMapping("/reports/{reportId}")
    @Operation(summary = "更新报告")
    public Result<AdminReportVO> updateReport(@PathVariable Long reportId, 
                                            @Valid @RequestBody UploadReportDTO reportDTO) {
        AdminReportVO reportInfo = adminService.updateReport(reportId, reportDTO);
        return Result.success("报告更新成功", reportInfo);
    }

    @DeleteMapping("/reports/{reportId}")
    @Operation(summary = "删除报告")
    public Result<Void> deleteReport(@PathVariable Long reportId) {
        adminService.deleteReport(reportId);
        return Result.success("报告删除成功", null);
    }

    @PutMapping("/reports/{reportId}/review")
    @Operation(summary = "审核报告")
    public Result<AdminReportVO> reviewReport(@PathVariable Long reportId, 
                                          @RequestParam @NotNull Integer status,
                                          @RequestParam(required = false) String reviewRemark) {
        AdminReportVO reportInfo = adminService.reviewReport(reportId, status, reviewRemark);
        return Result.success("报告审核成功", reportInfo);
    }

    @PutMapping("/reports/batch/review")
    @Operation(summary = "批量审核报告")
    public Result<Void> batchReviewReports(@RequestParam @NotBlank String reportIds, 
                                        @RequestParam @NotNull Integer status,
                                        @RequestParam(required = false) String reviewRemark) {
        adminService.batchReviewReports(reportIds, status, reviewRemark);
        return Result.success("批量审核成功", null);
    }

    @GetMapping("/reports/{reportId}")
    @Operation(summary = "获取报告详情")
    public Result<AdminReportVO> getReportDetail(@PathVariable Long reportId) {
        AdminReportVO reportInfo = adminService.getReportDetail(reportId);
        return Result.success(reportInfo);
    }

    @GetMapping("/reports/{reportId}/download")
    @Operation(summary = "下载报告文件")
    public Result<Void> downloadReportFile(@PathVariable Long reportId) {
        adminService.downloadReportFile(reportId);
        return Result.success(null);
    }
}