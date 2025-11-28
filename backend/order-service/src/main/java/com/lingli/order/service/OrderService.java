package com.lingli.order.service;

import com.lingli.common.core.PageResult;
import com.lingli.order.dto.cart.AddCartDTO;
import com.lingli.order.dto.cart.UpdateCartDTO;
import com.lingli.order.dto.order.CreateOrderDTO;
import com.lingli.order.dto.order.SamplingInfoDTO;
import com.lingli.order.entity.Order;
import com.lingli.order.vo.cart.CartItemVO;
import com.lingli.order.vo.order.OrderDetailVO;
import com.lingli.order.vo.order.OrderListVO;
import com.lingli.order.vo.order.OrderVO;

import java.util.List;

/**
 * 订单服务接口
 *
 * @author lingli
 * @since 2023-11-28
 */
public interface OrderService {

    // ========== 购物车相关 ==========

    /**
     * 加入购物车（登录检查）
     */
    void addToCart(Long userId, AddCartDTO cartDTO);

    /**
     * 获取购物车列表
     */
    List<CartItemVO> getCartList(Long userId);

    /**
     * 更新购物车
     */
    void updateCart(Long userId, Long cartId, UpdateCartDTO updateDTO);

    /**
     * 删除购物车商品
     */
    void removeFromCart(Long userId, Long cartId);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);

    /**
     * 批量删除购物车商品
     */
    void batchRemoveFromCart(Long userId, List<Long> cartIds);

    /**
     * 合并未登录购物车到登录用户
     */
    void mergeCartFromGuest(String guestCartToken, Long userId);

    // ========== 订单相关 ==========

    /**
     * 创建订单
     */
    OrderVO createOrder(Long userId, CreateOrderDTO orderDTO);

    /**
     * 获取订单列表
     */
    PageResult<OrderListVO> getOrderList(Long userId, Integer status, Integer page, Integer size);

    /**
     * 获取订单详情
     */
    OrderDetailVO getOrderDetail(Long userId, Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId, String reason);

    /**
     * 确认收货
     */
    void confirmReceipt(Long userId, Long orderId);

    /**
     * 根据订单号获取订单（用于支付回调）
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * 支付成功处理
     */
    void handlePaymentSuccess(String paymentNo, String tradeNo);

    /**
     * 支付失败处理
     */
    void handlePaymentFailure(String paymentNo, String reason);

    // ========== 采样相关 ==========

    /**
     * 添加采样信息
     */
    void addSamplingInfo(Long userId, Long orderId, SamplingInfoDTO samplingInfoDTO);

    /**
     * 更新采样信息
     */
    void updateSamplingInfo(Long userId, Long orderId, SamplingInfoDTO samplingInfoDTO);

    /**
     * 获取订单的采样信息
     */
    SamplingInfoDTO getSamplingInfo(Long userId, Long orderId);

    /**
     * 提交采样信息
     */
    void submitSamplingInfo(Long userId, Long orderId);

    // ========== 订单状态管理 ==========

    /**
     * 更新订单状态
     */
    void updateOrderStatus(Long orderId, Integer status);

    /**
     * 更新支付状态
     */
    void updatePayStatus(Long orderId, Integer payStatus);

    /**
     * 更新采样状态
     */
    void updateSamplingStatus(Long orderId, Integer samplingStatus);

    // ========== 统计相关 ==========

    /**
     * 获取用户订单统计
     */
    OrderStatisticsVO getOrderStatistics(Long userId);

    /**
     * 获取购物车商品数量
     */
    Integer getCartItemCount(Long userId);

    /**
     * 检查用户是否需要设置地理位置
     */
    boolean needSetLocation(Long userId);

    /**
     * 获取用户购物车状态
     */
    CartStatusVO getCartStatus(Long userId);

    /**
     * 订单统计VO
     */
    class OrderStatisticsVO {
        private Integer totalOrders;
        private Integer pendingOrders;
        private Integer paidOrders;
        private Integer shippedOrders;
        private Integer completedOrders;
        private Integer cancelledOrders;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;

        // getters and setters
    }

    /**
     * 购物车状态VO
     */
    class CartStatusVO {
        private Boolean needLogin;
        private Boolean needSetLocation;
        private Integer itemCount;
        private String message;
        private String redirectUrl;

        // getters and setters
    }
}