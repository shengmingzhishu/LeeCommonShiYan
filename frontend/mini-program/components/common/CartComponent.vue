<template>
  <view class="cart-container">
    <!-- 登录检查提示 -->
    <view class="login-check" v-if="cartStatus.needLogin">
      <view class="login-prompt">
        <image class="login-icon" src="/static/icons/login.png" mode="aspectFit" />
        <text class="login-text">{{ cartStatus.message }}</text>
        <button class="login-btn" @click="goToLogin">去登录</button>
      </view>
    </view>

    <!-- 地理位置检查提示 -->
    <view class="location-check" v-else-if="cartStatus.needSetLocation">
      <view class="location-prompt">
        <image class="location-icon" src="/static/icons/location.png" mode="aspectFit" />
        <text class="location-text">{{ cartStatus.message }}</text>
        <button class="location-btn" @click="goToSetLocation">设置位置</button>
      </view>
    </view>

    <!-- 购物车内容 -->
    <view class="cart-content" v-else>
      <!-- 购物车为空 -->
      <view class="empty-cart" v-if="cartItems.length === 0">
        <image class="empty-icon" src="/static/icons/empty-cart.png" mode="aspectFit" />
        <text class="empty-text">购物车空空如也</text>
        <button class="go-shopping-btn" @click="goShopping">去逛逛</button>
      </view>

      <!-- 购物车商品列表 -->
      <view class="cart-list" v-else>
        <!-- 商品项 -->
        <view class="cart-item" v-for="item in cartItems" :key="item.id">
          <view class="item-image">
            <image :src="item.packageImage || '/static/images/default-package.jpg'" mode="aspectFill" />
          </view>
          
          <view class="item-info">
            <text class="item-name">{{ item.packageName }}</text>
            <text class="item-desc">{{ item.packageDesc }}</text>
            
            <!-- 采样方式选择 -->
            <view class="sampling-method">
              <text class="method-label">采样方式：</text>
              <picker class="method-picker" :value="item.samplingMethod - 1" :range="samplingMethods" 
                      @change="changeSamplingMethod($event, item)">
                <text class="method-value">{{ samplingMethods[item.samplingMethod - 1] }}</text>
              </picker>
            </view>

            <!-- 采样人选择（当选择上门服务时显示） -->
            <view class="sampler-select" v-if="item.samplingMethod === 2">
              <text class="sampler-label">检测人：</text>
              <picker class="sampler-picker" :value="getSamplerIndex(item.samplerId)" :range="samplers" 
                      range-key="name" @change="changeSampler($event, item)">
                <text class="sampler-value">{{ getCurrentSamplerName(item.samplerId) }}</text>
              </picker>
              <button class="add-sampler-btn" @click="addSampler">添加检测人</button>
            </view>
          </view>

          <view class="item-actions">
            <!-- 数量控制 -->
            <view class="quantity-control">
              <button class="quantity-btn" @click="decreaseQuantity(item)">-</button>
              <text class="quantity-value">{{ item.quantity }}</text>
              <button class="quantity-btn" @click="increaseQuantity(item)">+</button>
            </view>

            <!-- 价格信息 -->
            <view class="item-price">
              <text class="current-price">¥{{ item.packagePrice }}</text>
            </view>

            <!-- 删除按钮 -->
            <button class="delete-btn" @click="removeItem(item.id)">删除</button>
          </view>
        </view>
      </view>

      <!-- 底部结算栏 -->
      <view class="cart-footer" v-if="cartItems.length > 0">
        <view class="footer-left">
          <text class="total-text">共 {{ totalCount }} 件商品</text>
          <text class="total-price">合计：¥{{ totalAmount }}</text>
        </view>
        
        <view class="footer-right">
          <button class="settle-btn" @click="settleOrder" :disabled="totalCount === 0">
            结算
          </button>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view class="loading" v-if="loading">
      <text>加载中...</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CartComponent',
  
  data() {
    return {
      cartItems: [],
      cartStatus: {
        needLogin: false,
        needSetLocation: false,
        message: '',
        itemCount: 0
      },
      samplingMethods: ['自邮寄', '上门预约'],
      samplers: [], // 检测人列表
      loading: false,
      totalCount: 0,
      totalAmount: 0
    }
  },
  
  onLoad() {
    this.checkLoginStatus()
    this.loadSamplers()
  },
  
  onShow() {
    // 页面显示时刷新数据
    this.refreshCartData()
  },
  
  methods: {
    // 检查登录状态和购物车状态
    async checkLoginStatus() {
      try {
        this.loading = true
        
        // 检查用户登录状态
        const token = uni.getStorageSync('access_token')
        if (!token) {
          this.cartStatus = {
            needLogin: true,
            needSetLocation: false,
            message: '登录后即可查看购物车并下单',
            itemCount: 0
          }
          return
        }

        // 已登录用户，获取购物车状态
        const response = await this.$api.cart.getCartStatus()
        if (response.success) {
          this.cartStatus = response.data
        }

        // 如果需要设置地理位置，提示用户
        if (this.cartStatus.needSetLocation) {
          uni.showModal({
            title: '位置信息',
            content: this.cartStatus.message,
            confirmText: '去设置',
            success: (res) => {
              if (res.confirm) {
                this.goToSetLocation()
              }
            }
          })
        }

        // 加载购物车数据
        if (!this.cartStatus.needLogin && !this.cartStatus.needSetLocation) {
          await this.loadCartItems()
        }
        
      } catch (error) {
        console.error('检查登录状态失败:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    // 刷新购物车数据
    async refreshCartData() {
      if (!this.cartStatus.needLogin && !this.cartStatus.needSetLocation) {
        await this.loadCartItems()
      }
    },
    
    // 加载购物车商品
    async loadCartItems() {
      try {
        const response = await this.$api.cart.getCartList()
        if (response.success) {
          this.cartItems = response.data || []
          this.calculateTotals()
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
      }
    },
    
    // 加载检测人列表
    async loadSamplers() {
      try {
        const token = uni.getStorageSync('access_token')
        if (token) {
          const response = await this.$api.user.getSamplers()
          if (response.success) {
            this.samplers = response.data || []
          }
        }
      } catch (error) {
        console.error('加载检测人列表失败:', error)
      }
    },
    
    // 计算总计
    calculateTotals() {
      this.totalCount = this.cartItems.reduce((sum, item) => sum + item.quantity, 0)
      this.totalAmount = this.cartItems.reduce((sum, item) => 
        sum + (item.packagePrice * item.quantity), 0).toFixed(2)
    },
    
    // 改变采样方式
    changeSamplingMethod(e, item) {
      const index = e.detail.value
      item.samplingMethod = parseInt(index) + 1
      
      // 如果切换到上门预约，需要选择检测人
      if (item.samplingMethod === 2 && !item.samplerId) {
        this.selectSampler(item)
      }
      
      // 更新服务器端数据
      this.updateCartItem(item)
    },
    
    // 改变检测人
    changeSampler(e, item) {
      const index = e.detail.value
      const sampler = this.samplers[index]
      item.samplerId = sampler.id
      this.updateCartItem(item)
    },
    
    // 选择检测人
    selectSampler(item) {
      if (this.samplers.length === 0) {
        uni.showModal({
          title: '提示',
          content: '暂未添加检测人，是否立即添加？',
          success: (res) => {
            if (res.confirm) {
              this.addSampler()
            }
          }
        })
        return
      }
      
      // 打开检测人选择器
      this.$nextTick(() => {
        const picker = this.$refs.samplerPicker
        if (picker) {
          picker.showPicker()
        }
      })
    },
    
    // 添加检测人
    addSampler() {
      uni.navigateTo({
        url: '/pages/user/sampler/add'
      })
    },
    
    // 减少数量
    decreaseQuantity(item) {
      if (item.quantity > 1) {
        item.quantity--
        this.updateCartItem(item)
      } else {
        this.removeItem(item.id)
      }
    },
    
    // 增加数量
    increaseQuantity(item) {
      item.quantity++
      this.updateCartItem(item)
    },
    
    // 更新购物车商品
    async updateCartItem(item) {
      try {
        const updateData = {
          quantity: item.quantity,
          samplerId: item.samplerId,
          samplingMethod: item.samplingMethod
        }
        
        await this.$api.cart.updateCart(item.id, updateData)
        this.calculateTotals()
      } catch (error) {
        console.error('更新购物车失败:', error)
        uni.showToast({
          title: '更新失败',
          icon: 'none'
        })
      }
    },
    
    // 删除商品
    removeItem(cartId) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这个商品吗？',
        success: (res) => {
          if (res.confirm) {
            this.deleteCartItem(cartId)
          }
        }
      })
    },
    
    // 删除购物车商品
    async deleteCartItem(cartId) {
      try {
        await this.$api.cart.removeFromCart(cartId)
        
        // 从本地列表中移除
        const index = this.cartItems.findIndex(item => item.id === cartId)
        if (index > -1) {
          this.cartItems.splice(index, 1)
          this.calculateTotals()
        }
        
        uni.showToast({
          title: '删除成功',
          icon: 'success'
        })
      } catch (error) {
        console.error('删除购物车商品失败:', error)
        uni.showToast({
          title: '删除失败',
          icon: 'none'
        })
      }
    },
    
    // 结算订单
    settleOrder() {
      if (this.totalCount === 0) {
        uni.showToast({
          title: '购物车为空',
          icon: 'none'
        })
        return
      }

      // 检查是否有上门预约的商品需要选择检测人
      const needSamplers = this.cartItems.some(item => 
        item.samplingMethod === 2 && !item.samplerId
      )
      
      if (needSamplers) {
        uni.showToast({
          title: '请选择上门预约的检测人',
          icon: 'none'
        })
        return
      }

      // 跳转到订单确认页面
      uni.navigateTo({
        url: '/pages/order/confirm'
      })
    },
    
    // 获取检测人索引
    getSamplerIndex(samplerId) {
      return this.samplers.findIndex(s => s.id === samplerId)
    },
    
    // 获取当前检测人名称
    getCurrentSamplerName(samplerId) {
      const sampler = this.samplers.find(s => s.id === samplerId)
      return sampler ? sampler.name : '请选择检测人'
    },
    
    // 页面跳转
    goToLogin() {
      // 保存当前页面信息，登录后返回
      const currentPage = getCurrentPages().pop()
      const pages = getPages()
      const loginPageIndex = pages.findIndex(p => p.route === 'pages/auth/login')
      
      uni.navigateTo({
        url: '/pages/auth/login',
        success: () => {
          if (loginPageIndex > -1) {
            // 设置登录成功后的回调
            uni.$once('login_success', () => {
              this.refreshCartData()
            })
          }
        }
      })
    },
    
    goToSetLocation() {
      uni.navigateTo({
        url: '/pages/user/location'
      })
    },
    
    goShopping() {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.cart-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  padding-bottom: 100rpx;

  .login-check, .location-check {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 60vh;
    padding: 40rpx;

    .login-prompt, .location-prompt {
      text-align: center;
      background: #ffffff;
      padding: 60rpx 40rpx;
      border-radius: 16rpx;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);

      .login-icon, .location-icon {
        width: 120rpx;
        height: 120rpx;
        margin-bottom: 30rpx;
        opacity: 0.6;
      }

      .login-text, .location-text {
        display: block;
        font-size: 28rpx;
        color: #666;
        margin-bottom: 40rpx;
        line-height: 1.5;
      }

      .login-btn, .location-btn {
        background-color: #4a90e2;
        color: #ffffff;
        border: none;
        border-radius: 8rpx;
        padding: 20rpx 60rpx;
        font-size: 28rpx;
      }
    }
  }

  .cart-content {
    .empty-cart {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 120rpx 40rpx;
      background: #ffffff;
      margin: 20rpx;
      border-radius: 16rpx;

      .empty-icon {
        width: 200rpx;
        height: 200rpx;
        opacity: 0.3;
        margin-bottom: 30rpx;
      }

      .empty-text {
        font-size: 28rpx;
        color: #999;
        margin-bottom: 40rpx;
      }

      .go-shopping-btn {
        background-color: #4a90e2;
        color: #ffffff;
        border: none;
        border-radius: 8rpx;
        padding: 20rpx 60rpx;
        font-size: 28rpx;
      }
    }

    .cart-list {
      .cart-item {
        display: flex;
        background: #ffffff;
        margin: 10rpx 20rpx;
        padding: 30rpx;
        border-radius: 12rpx;
        box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);

        .item-image {
          width: 140rpx;
          height: 140rpx;
          border-radius: 8rpx;
          overflow: hidden;
          margin-right: 20rpx;

          image {
            width: 100%;
            height: 100%;
          }
        }

        .item-info {
          flex: 1;
          margin-right: 20rpx;

          .item-name {
            display: block;
            font-size: 30rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 10rpx;
          }

          .item-desc {
            display: block;
            font-size: 24rpx;
            color: #666;
            margin-bottom: 15rpx;
          }

          .sampling-method {
            margin-bottom: 15rpx;

            .method-label {
              font-size: 24rpx;
              color: #666;
              margin-right: 15rpx;
            }

            .method-picker {
              display: inline-block;
              background-color: #f0f8ff;
              padding: 8rpx 20rpx;
              border-radius: 20rpx;
              font-size: 24rpx;
              color: #4a90e2;

              .method-value {
                margin-right: 10rpx;
              }
            }
          }

          .sampler-select {
            display: flex;
            align-items: center;
            margin-bottom: 15rpx;

            .sampler-label {
              font-size: 24rpx;
              color: #666;
              margin-right: 15rpx;
            }

            .sampler-picker {
              flex: 1;
              background-color: #f5f5f5;
              padding: 8rpx 15rpx;
              border-radius: 8rpx;
              font-size: 24rpx;
              margin-right: 15rpx;

              .sampler-value {
                color: #333;
              }
            }

            .add-sampler-btn {
              background-color: #ff6b35;
              color: #ffffff;
              border: none;
              border-radius: 8rpx;
              padding: 8rpx 20rpx;
              font-size: 22rpx;
            }
          }
        }

        .item-actions {
          width: 180rpx;
          display: flex;
          flex-direction: column;
          align-items: flex-end;

          .quantity-control {
            display: flex;
            align-items: center;
            margin-bottom: 20rpx;

            .quantity-btn {
              width: 50rpx;
              height: 50rpx;
              background-color: #f5f5f5;
              border: none;
              border-radius: 50%;
              color: #666;
              font-size: 24rpx;
              display: flex;
              align-items: center;
              justify-content: center;
            }

            .quantity-value {
              margin: 0 20rpx;
              font-size: 28rpx;
              font-weight: bold;
              color: #333;
              min-width: 40rpx;
              text-align: center;
            }
          }

          .item-price {
            margin-bottom: 20rpx;

            .current-price {
              font-size: 32rpx;
              font-weight: bold;
              color: #ff4757;
            }
          }

          .delete-btn {
            background-color: transparent;
            color: #999;
            border: 1rpx solid #ddd;
            border-radius: 8rpx;
            padding: 8rpx 20rpx;
            font-size: 22rpx;
          }
        }
      }
    }
  }

  .cart-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #ffffff;
    padding: 20rpx 30rpx;
    border-top: 1rpx solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .footer-left {
      .total-text {
        display: block;
        font-size: 24rpx;
        color: #666;
        margin-bottom: 5rpx;
      }

      .total-price {
        font-size: 32rpx;
        font-weight: bold;
        color: #ff4757;
      }
    }

    .footer-right {
      .settle-btn {
        background-color: #4a90e2;
        color: #ffffff;
        border: none;
        border-radius: 8rpx;
        padding: 20rpx 60rpx;
        font-size: 28rpx;
        font-weight: bold;

        &:disabled {
          background-color: #ccc;
        }
      }
    }
  }

  .loading {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200rpx;
    color: #666;
  }
}
</style>