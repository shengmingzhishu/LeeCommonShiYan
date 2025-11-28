package com.lingli.admin.service;

import com.lingli.admin.dto.packageinfo.AdminPackageDTO;
import com.lingli.admin.dto.report.UploadReportDTO;
import com.lingli.admin.vo.packageinfo.AdminPackageVO;
import com.lingli.admin.vo.report.AdminReportVO;
import com.lingli.common.core.PageResult;

/**
 * 管理后台服务接口
 *
 * @author lingli
 * @since 2023-11-28
 */
public interface AdminService {

    // ========== 商品管理相关 ==========

    /**
     * 获取商品列表（管理员）
     */
    PageResult<AdminPackageVO> getPackages(Long categoryId, String keyword, Integer status, Integer page, Integer size);

    /**
     * 获取套餐详情（管理员）
     */
    AdminPackageVO getPackageDetail(Long packageId);

    /**
     * 创建套餐
     */
    AdminPackageVO createPackage(AdminPackageDTO packageDTO);

    /**
     * 更新套餐
     */
    AdminPackageVO updatePackage(Long packageId, AdminPackageDTO packageDTO);

    /**
     * 删除套餐
     */
    void deletePackage(Long packageId);

    /**
     * 上下架套餐
     */
    AdminPackageVO updatePackageStatus(Long packageId, Integer status);

    /**
     * 批量操作套餐
     */
    void batchUpdatePackageStatus(String packageIds, Integer status);

    /**
     * 批量删除套餐
     */
    void batchDeletePackages(String packageIds);

    /**
     * 设置推荐套餐
     */
    AdminPackageVO setRecommended(Long packageId, Boolean recommended);

    /**
     * 设置热门套餐
     */
    AdminPackageVO setHot(Long packageId, Boolean hot);

    /**
     * 调整套餐排序
     */
    void adjustPackageSort(Long packageId, Integer sortOrder);

    // ========== 分类管理相关 ==========

    /**
     * 获取分类列表（管理员）
     */
    AdminCategoryVO getCategories();

    /**
     * 创建分类
     */
    AdminCategoryVO createCategory(AdminCategoryDTO categoryDTO);

    /**
     * 更新分类
     */
    AdminCategoryVO updateCategory(Long categoryId, AdminCategoryDTO categoryDTO);

    /**
     * 删除分类
     */
    void deleteCategory(Long categoryId);

    /**
     * 调整分类排序
     */
    void adjustCategorySort(Long categoryId, Integer sortOrder);

    // ========== 报告管理相关 ==========

    /**
     * 获取报告列表（管理员）
     */
    PageResult<AdminReportVO> getReports(Long orderId, String samplerName, Integer status, Integer page, Integer size);

    /**
     * 上传检测报告
     */
    AdminReportVO uploadReport(UploadReportDTO reportDTO, String fileUrl);

    /**
     * 更新报告
     */
    AdminReportVO updateReport(Long reportId, UploadReportDTO reportDTO);

    /**
     * 删除报告
     */
    void deleteReport(Long reportId);

    /**
     * 审核报告
     */
    AdminReportVO reviewReport(Long reportId, Integer status, String reviewRemark);

    /**
     * 批量审核报告
     */
    void batchReviewReports(String reportIds, Integer status, String reviewRemark);

    /**
     * 设置报告公开状态
     */
    AdminReportVO setReportPublic(Long reportId, Boolean isPublic);

    /**
     * 获取报告详情（管理员）
     */
    AdminReportVO getReportDetail(Long reportId);

    /**
     * 下载报告文件
     */
    void downloadReportFile(Long reportId);

    // ========== 订单管理相关 ==========

    /**
     * 获取订单列表（管理员）
     */
    PageResult<AdminOrderVO> getOrders(Long companyId, Long userId, Integer status, String startDate, String endDate, Integer page, Integer size);

    /**
     * 获取订单详情（管理员）
     */
    AdminOrderVO getOrderDetail(Long orderId);

    /**
     * 更新订单状态
     */
    AdminOrderVO updateOrderStatus(Long orderId, Integer status);

    /**
     * 订单发货
     */
    AdminOrderVO shipOrder(Long orderId, String logisticsCompany, String trackingNumber, String remark);

    /**
     * 确认完成订单
     */
    AdminOrderVO completeOrder(Long orderId);

    /**
     * 取消订单
     */
    AdminOrderVO cancelOrder(Long orderId, String reason);

    /**
     * 获取订单统计
     */
    AdminOrderStatisticsVO getOrderStatistics();

    // ========== 用户管理相关 ==========

    /**
     * 获取用户列表
     */
    PageResult<AdminUserVO> getUsers(String keyword, Integer status, Integer page, Integer size);

    /**
     * 获取用户详情
     */
    AdminUserVO getUserDetail(Long userId);

    /**
     * 更新用户状态
     */
    AdminUserVO updateUserStatus(Long userId, Integer status);

    /**
     * 重置用户密码
     */
    void resetUserPassword(Long userId);

    // ========== 系统统计相关 ==========

    /**
     * 获取系统概览
     */
    AdminDashboardVO getDashboard();

    /**
     * 获取销售统计
     */
    AdminSalesStatisticsVO getSalesStatistics(String startDate, String endDate);

    /**
     * 获取套餐统计
     */
    AdminPackageStatisticsVO getPackageStatistics();
}