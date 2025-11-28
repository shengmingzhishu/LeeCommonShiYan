package com.lingli.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingli.common.core.PageResult;
import com.lingli.common.exception.BusinessException;
import com.lingli.location.service.LocationService;
import com.lingli.location.vo.location.UserLocationVO;
import com.lingli.order.dto.cart.AddCartDTO;
import com.lingli.order.dto.cart.UpdateCartDTO;
import com.lingli.order.dto.order.CreateOrderDTO;
import com.lingli.order.dto.order.SamplingInfoDTO;
import com.lingli.order.entity.Order;
import com.lingli.order.entity.ShoppingCart;
import com.lingli.order.mapper.OrderMapper;
import com.lingli.order.mapper.ShoppingCartMapper;
import com.lingli.order.service.OrderService;
import com.lingli.order.vo.cart.CartItemVO;
import com.lingli.order.vo.order.OrderDetailVO;
import com.lingli.order.vo.order.OrderListVO;
import com.lingli.order.vo.order.OrderVO;
import com.lingli.user.entity.Sampler;
import com.lingli.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 订单服务实现
 *
 * @author lingli
 * @since 2023-11-28
 */
@Slf4j
@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis键前缀
    private static final String GUEST_CART_KEY = "cart:guest:";
    private static final String ORDER_NO_PREFIX = "ORD";

    // ========== 购物车核心功能 ==========

    @Override
    public void addToCart(Long userId, AddCartDTO cartDTO) {
        // 关键逻辑：检查用户登录状态
        if (userId == null || userId <= 0) {
            // 用户未登录，检查是否为首次加入购物车
            CartStatusVO cartStatus = getCartStatus(null);
            if (cartStatus.getNeedLogin()) {
                throw new BusinessException(40001, "加入购物车需要登录，请先登录");
            }
            
            // 对于未登录用户，保存到Redis中作为游客购物车
            saveGuestCart(cartDTO);
            return;
        }

        // 用户已登录，执行正常的购物车添加逻辑
        addToLoggedInUserCart(userId, cartDTO);
    }

    @Override
    public List<CartItemVO> getCartList(Long userId) {
        if (userId == null || userId <= 0) {
            // 未登录用户，获取游客购物车
            return getGuestCart();
        }
        
        // 已登录用户，获取数据库中的购物车
        return getLoggedInUserCart(userId);
    }

    @Override
    public void updateCart(Long userId, Long cartId, UpdateCartDTO updateDTO) {
        // 检查登录状态
        if (userId == null || userId <= 0) {
            throw new BusinessException(40001, "更新购物车需要登录");
        }

        ShoppingCart shoppingCart = shoppingCartMapper.selectById(cartId);
        if (shoppingCart == null || !shoppingCart.getUserId().equals(userId)) {
            throw new BusinessException(40002, "购物车商品不存在");
        }

        if (updateDTO.getQuantity() != null) {
            if (updateDTO.getQuantity() <= 0) {
                shoppingCartMapper.deleteById(cartId);
            } else {
                shoppingCart.setQuantity(updateDTO.getQuantity());
                shoppingCartMapper.updateById(shoppingCart);
            }
        }

        if (updateDTO.getSamplerId() != null) {
            shoppingCart.setSamplerId(updateDTO.getSamplerId());
            shoppingCartMapper.updateById(shoppingCart);
        }

        if (updateDTO.getSamplingMethod() != null) {
            shoppingCart.setSamplingMethod(updateDTO.getSamplingMethod());
            shoppingCartMapper.updateById(shoppingCart);
        }

        log.info("更新购物车: userId={}, cartId={}, quantity={}", userId, cartId, updateDTO.getQuantity());
    }

    @Override
    public void removeFromCart(Long userId, Long cartId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(40001, "删除购物车商品需要登录");
        }

        ShoppingCart shoppingCart = shoppingCartMapper.selectById(cartId);
        if (shoppingCart == null || !shoppingCart.getUserId().equals(userId)) {
            throw new BusinessException(40002, "购物车商品不存在");
        }

        shoppingCartMapper.deleteById(cartId);
        log.info("删除购物车商品: userId={}, cartId={}", userId, cartId);
    }

    @Override
    public void clearCart(Long userId) {
        if (userId == null || userId <= 0) {
            // 清空游客购物车
            clearGuestCart();
            return;
        }

        // 清空已登录用户的购物车
        shoppingCartMapper.delete(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId));
        log.info("清空购物车: userId={}", userId);
    }

    @Override
    public void batchRemoveFromCart(Long userId, List<Long> cartIds) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(40001, "批量删除购物车商品需要登录");
        }

        if (cartIds == null || cartIds.isEmpty()) {
            return;
        }

        shoppingCartMapper.delete(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId)
                .in(ShoppingCart::getId, cartIds));
        log.info("批量删除购物车商品: userId={}, count={}", userId, cartIds.size());
    }

    @Override
    public void mergeCartFromGuest(String guestCartToken, Long userId) {
        if (StrUtil.isBlank(guestCartToken) || userId == null || userId <= 0) {
            return;
        }

        // 获取游客购物车数据
        List<CartItemVO> guestCartItems = getGuestCartItems(guestCartToken);
        if (guestCartItems.isEmpty()) {
            return;
        }

        // 合并到已登录用户的购物车
        for (CartItemVO item : guestCartItems) {
            AddCartDTO addCartDTO = new AddCartDTO();
            addCartDTO.setPackageId(item.getPackageId());
            addCartDTO.setQuantity(item.getQuantity());
            addCartDTO.setSamplerId(item.getSamplerId());
            addCartDTO.setSamplingMethod(item.getSamplingMethod());
            
            addToLoggedInUserCart(userId, addCartDTO);
        }

        // 清除游客购物车
        clearGuestCartItems(guestCartToken);
        
        log.info("合并游客购物车到用户: userId={}, items={}", userId, guestCartItems.size());
    }

    // ========== 订单核心功能 ==========

    @Override
    public OrderVO createOrder(Long userId, CreateOrderDTO orderDTO) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(40001, "创建订单需要登录");
        }

        // 检查是否需要设置地理位置
        if (needSetLocation(userId)) {
            throw new BusinessException(40003, "请先设置您的地理位置信息");
        }

        // 验证购物车商品
        List<CartItemVO> cartItems = getLoggedInUserCart(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException(40004, "购物车为空");
        }

        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 计算订单总金额
        BigDecimal totalAmount = calculateTotalAmount(cartItems);
        
        // 获取用户地理位置和对应公司
        UserLocationVO userLocation = locationService.getUserLocation(userId);
        Long companyId = getCompanyIdByCity(userLocation.getCity());

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setCompanyId(companyId);
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setStatus(com.lingli.common.enums.OrderStatus.PENDING_PAYMENT);
        order.setPayStatus(com.lingli.common.enums.PayStatus.PENDING);
        order.setShippingType(orderDTO.getShippingType());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setContactName(orderDTO.getContactName());
        order.setContactPhone(orderDTO.getContactPhone());
        order.setRemark(orderDTO.getRemark());
        order.setProvince(userLocation.getProvince());
        order.setCity(userLocation.getCity());
        order.setDistrict(userLocation.getDistrict());
        order.setSamplingStatus(1); // 待采样

        orderMapper.insert(order);

        // 创建订单详情
        createOrderItems(order.getId(), cartItems);

        // 清空购物车
        shoppingCartMapper.delete(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId));

        log.info("创建订单成功: orderNo={}, userId={}, amount={}", orderNo, userId, totalAmount);
        
        return convertToOrderVO(order, cartItems);
    }

    @Override
    public PageResult<OrderListVO> getOrderList(Long userId, Integer status, Integer page, Integer size) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(40001, "获取订单列表需要登录");
        }

        Page<Order> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        
        wrapper.orderByDesc(Order::getCreatedAt);
        
        IPage<Order> result = orderMapper.selectPage(pageRequest, wrapper);
        
        List<OrderListVO> orderListVOs = result.getRecords().stream()
                .map(this::convertToOrderListVO)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        return PageResult.of(orderListVOs, result.getTotal(), page, size);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long orderId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(40001, "获取订单详情需要登录");
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(40005, "订单不存在");
        }

        // TODO: 获取订单详情，包括订单项、物流信息、支付信息等
        return convertToOrderDetailVO(order);
    }

    // ========== 统计功能 ==========

    @Override
    public Integer getCartItemCount(Long userId) {
        if (userId == null || userId <= 0) {
            // 未登录用户，获取游客购物车数量
            return getGuestCartItemCount();
        }
        
        // 已登录用户
        return shoppingCartMapper.selectCount(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId));
    }

    @Override
    public boolean needSetLocation(Long userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        
        return locationService.needSetLocation(userId);
    }

    @Override
    public CartStatusVO getCartStatus(Long userId) {
        CartStatusVO statusVO = new CartStatusVO();
        
        if (userId == null || userId <= 0) {
            // 未登录用户
            statusVO.setNeedLogin(true);
            statusVO.setNeedSetLocation(false);
            statusVO.setItemCount(getGuestCartItemCount());
            statusVO.setMessage("登录后即可查看购物车并下单");
            statusVO.setRedirectUrl("/pages/auth/login");
            return statusVO;
        }

        // 已登录用户
        statusVO.setNeedLogin(false);
        statusVO.setNeedSetLocation(needSetLocation(userId));
        statusVO.setItemCount(getCartItemCount(userId));
        
        if (statusVO.getNeedSetLocation()) {
            statusVO.setMessage("请先设置您的地理位置信息，以便为您匹配就近的检测机构");
            statusVO.setRedirectUrl("/pages/user/location");
        } else {
            statusVO.setMessage("购物车状态正常");
            statusVO.setRedirectUrl(null);
        }
        
        return statusVO;
    }

    // ========== 私有方法 ==========

    /**
     * 保存游客购物车
     */
    private void saveGuestCart(AddCartDTO cartDTO) {
        // 这里简化实现，实际中应该生成唯一的游客token
        String guestToken = getCurrentGuestToken();
        String key = GUEST_CART_KEY + guestToken;
        
        List<CartItemVO> guestCart = getGuestCartItems(guestToken);
        
        // 检查是否已存在相同商品
        boolean found = false;
        for (CartItemVO item : guestCart) {
            if (item.getPackageId().equals(cartDTO.getPackageId())) {
                item.setQuantity(item.getQuantity() + cartDTO.getQuantity());
                found = true;
                break;
            }
        }
        
        if (!found) {
            CartItemVO newItem = convertToCartItemVO(cartDTO);
            guestCart.add(newItem);
        }
        
        redisTemplate.opsForValue().set(key, guestCart, 7, TimeUnit.DAYS);
    }

    /**
     * 为已登录用户添加购物车
     */
    private void addToLoggedInUserCart(Long userId, AddCartDTO cartDTO) {
        // 检查是否已存在相同商品
        ShoppingCart existing = shoppingCartMapper.selectOne(
            new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId)
                .eq(ShoppingCart::getPackageId, cartDTO.getPackageId())
        );

        if (existing != null) {
            // 更新数量
            existing.setQuantity(existing.getQuantity() + cartDTO.getQuantity());
            shoppingCartMapper.updateById(existing);
        } else {
            // 创建新记录
            ShoppingCart shoppingCart = convertToShoppingCart(userId, cartDTO);
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    /**
     * 获取游客购物车列表
     */
    private List<CartItemVO> getGuestCart() {
        String guestToken = getCurrentGuestToken();
        return getGuestCartItems(guestToken);
    }

    /**
     * 获取已登录用户购物车列表
     */
    private List<CartItemVO> getLoggedInUserCart(Long userId) {
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(
            new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId)
        );

        return shoppingCarts.stream()
                .map(this::convertToCartItemVO)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * 获取游客购物车数据
     */
    @SuppressWarnings("unchecked")
    private List<CartItemVO> getGuestCartItems(String guestToken) {
        String key = GUEST_CART_KEY + guestToken;
        List<CartItemVO> guestCart = (List<CartItemVO>) redisTemplate.opsForValue().get(key);
        return guestCart != null ? guestCart : new ArrayList<>();
    }

    /**
     * 清除游客购物车数据
     */
    private void clearGuestCartItems(String guestToken) {
        String key = GUEST_CART_KEY + guestToken;
        redisTemplate.delete(key);
    }

    /**
     * 获取游客购物车数量
     */
    private Integer getGuestCartItemCount() {
        List<CartItemVO> guestCart = getGuestCart();
        return guestCart.size();
    }

    /**
     * 清除游客购物车
     */
    private void clearGuestCart() {
        String guestToken = getCurrentGuestToken();
        String key = GUEST_CART_KEY + guestToken;
        redisTemplate.delete(key);
    }

    /**
     * 获取当前游客token（简化实现）
     */
    private String getCurrentGuestToken() {
        // 这里简化处理，实际中应该从请求中获取或生成
        return "guest_" + System.currentTimeMillis();
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().toString().replaceAll("[-:]", "").substring(0, 14);
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return ORDER_NO_PREFIX + timestamp + random;
    }

    /**
     * 计算订单总金额
     */
    private BigDecimal calculateTotalAmount(List<CartItemVO> cartItems) {
        return cartItems.stream()
                .map(item -> item.getPackagePrice().multiply(new java.math.BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 根据城市获取公司ID（简化实现）
     */
    private Long getCompanyIdByCity(String city) {
        // TODO: 实际项目中应该查询数据库获取对应城市的公司
        return 1L; // 默认公司ID
    }

    // ========== 转换方法 ==========

    private CartItemVO convertToCartItemVO(AddCartDTO cartDTO) {
        CartItemVO cartItemVO = new CartItemVO();
        // TODO: 从套餐服务获取套餐详情
        return cartItemVO;
    }

    private ShoppingCart convertToShoppingCart(Long userId, AddCartDTO cartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCart.setPackageId(cartDTO.getPackageId());
        shoppingCart.setQuantity(cartDTO.getQuantity());
        shoppingCart.setSamplerId(cartDTO.getSamplerId());
        shoppingCart.setSamplingMethod(cartDTO.getSamplingMethod());
        return shoppingCart;
    }

    private CartItemVO convertToCartItemVO(ShoppingCart shoppingCart) {
        CartItemVO cartItemVO = new CartItemVO();
        cartItemVO.setId(shoppingCart.getId());
        cartItemVO.setUserId(shoppingCart.getUserId());
        cartItemVO.setPackageId(shoppingCart.getPackageId());
        cartItemVO.setQuantity(shoppingCart.getQuantity());
        cartItemVO.setSamplerId(shoppingCart.getSamplerId());
        cartItemVO.setSamplingMethod(shoppingCart.getSamplingMethod());
        // TODO: 填充套餐信息
        return cartItemVO;
    }

    private OrderVO convertToOrderVO(Order order, List<CartItemVO> cartItems) {
        OrderVO orderVO = new OrderVO();
        orderVO.setId(order.getId());
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setTotalAmount(order.getTotalAmount());
        orderVO.setStatus(order.getStatus());
        orderVO.setPayStatus(order.getPayStatus());
        orderVO.setItems(cartItems);
        return orderVO;
    }

    private OrderListVO convertToOrderListVO(Order order) {
        OrderListVO orderListVO = new OrderListVO();
        orderListVO.setId(order.getId());
        orderListVO.setOrderNo(order.getOrderNo());
        orderListVO.setTotalAmount(order.getTotalAmount());
        orderListVO.setStatus(order.getStatus());
        orderListVO.setCreatedAt(order.getCreatedAt());
        return orderListVO;
    }

    private OrderDetailVO convertToOrderDetailVO(Order order) {
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        // TODO: 填充详细的订单信息
        return orderDetailVO;
    }

    private void createOrderItems(Long orderId, List<CartItemVO> cartItems) {
        // TODO: 创建订单详情记录
    }

    // ========== 接口实现 ==========

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
    }

    @Override
    public void handlePaymentSuccess(String paymentNo, String tradeNo) {
        // TODO: 实现支付成功处理逻辑
    }

    @Override
    public void handlePaymentFailure(String paymentNo, String reason) {
        // TODO: 实现支付失败处理逻辑
    }

    @Override
    public void addSamplingInfo(Long userId, Long orderId, SamplingInfoDTO samplingInfoDTO) {
        // TODO: 实现添加采样信息
    }

    @Override
    public void updateSamplingInfo(Long userId, Long orderId, SamplingInfoDTO samplingInfoDTO) {
        // TODO: 实现更新采样信息
    }

    @Override
    public SamplingInfoDTO getSamplingInfo(Long userId, Long orderId) {
        // TODO: 实现获取采样信息
        return null;
    }

    @Override
    public void submitSamplingInfo(Long userId, Long orderId) {
        // TODO: 实现提交采样信息
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setStatus(com.lingli.common.enums.OrderStatus.valueOf(status));
            orderMapper.updateById(order);
        }
    }

    @Override
    public void updatePayStatus(Long orderId, Integer payStatus) {
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setPayStatus(com.lingli.common.enums.PayStatus.valueOf(payStatus));
            orderMapper.updateById(order);
        }
    }

    @Override
    public void updateSamplingStatus(Long orderId, Integer samplingStatus) {
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            order.setSamplingStatus(samplingStatus);
            orderMapper.updateById(order);
        }
    }

    @Override
    public OrderStatisticsVO getOrderStatistics(Long userId) {
        // TODO: 实现订单统计
        return new OrderStatisticsVO();
    }

    @Override
    public void cancelOrder(Long userId, Long orderId, String reason) {
        // TODO: 实现取消订单
    }

    @Override
    public void confirmReceipt(Long userId, Long orderId) {
        // TODO: 实现确认收货
    }

    private void clearGuestCart() {
        clearGuestCart();
    }
}