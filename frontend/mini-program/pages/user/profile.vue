<template>
  <view class="profile-page">
    <!-- 用户信息头部 -->
    <view class="profile-header">
      <view class="header-bg"></view>
      <view class="user-info">
        <view class="avatar-section">
          <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.jpg'" mode="aspectFill" />
          <view class="edit-avatar-btn" @click="changeAvatar">
            <text class="edit-icon">📷</text>
          </view>
        </view>
        
        <view class="user-details">
          <text class="username">{{ userInfo.username || '未登录' }}</text>
          <text class="user-phone">{{ userInfo.phone || '未设置手机号' }}</text>
          <view class="location-info" v-if="locationInfo.city">
            <text class="location-icon">📍</text>
            <text class="location-text">{{ locationInfo.city }}</text>
            <button class="change-location-btn" @click="changeLocation">更换</button>
          </view>
          <view class="location-warning" v-else @click="setLocation">
            <text class="warning-icon">⚠️</text>
            <text class="warning-text">请设置您的位置信息</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 订单快捷入口 -->
    <view class="order-section">
      <view class="section-header">
        <text class="section-title">我的订单</text>
        <button class="view-all-btn" @click="goToOrderList">查看全部 ></button>
      </view>
      
      <view class="order-stats">
        <view class="stat-item" @click="goToOrderList(1)">
          <text class="stat-icon">⏳</text>
          <text class="stat-number">{{ orderStats.pending || 0 }}</text>
          <text class="stat-label">待付款</text>
        </view>
        <view class="stat-item" @click="goToOrderList(2)">
          <text class="stat-icon">📦</text>
          <text class="stat-number">{{ orderStats.paid || 0 }}</text>
          <text class="stat-label">已付款</text>
        </view>
        <view class="stat-item" @click="goToOrderList(3)">
          <text class="stat-icon">🚚</text>
          <text class="stat-number">{{ orderStats.shipped || 0 }}</text>
          <text class="stat-label">待收货</text>
        </view>
        <view class="stat-item" @click="goToOrderList(5)">
          <text class="stat-icon">✅</text>
          <text class="stat-number">{{ orderStats.completed || 0 }}</text>
          <text class="stat-label">已完成</text>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-group">
        <view class="menu-item" @click="goToSamplers">
          <view class="menu-left">
            <text class="menu-icon">👥</text>
            <text class="menu-title">检测人管理</text>
            <text class="menu-desc">管理采样人信息</text>
          </view>
          <view class="menu-right">
            <text class="menu-count" v-if="samplerCount > 0">{{ samplerCount }}</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
        
        <view class="menu-item" @click="goToReports">
          <view class="menu-left">
            <text class="menu-icon">📄</text>
            <text class="menu-title">检验报告</text>
            <text class="menu-desc">查看检测报告</text>
          </view>
          <view class="menu-right">
            <text class="menu-arrow">></text>
          </view>
        </view>
        
        <view class="menu-item" @click="goToAddresses">
          <view class="menu-left">
            <text class="menu-icon">📍</text>
            <text class="menu-title">收货地址</text>
            <text class="menu-desc">管理收货地址</text>
          </view>
          <view class="menu-right">
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>
      
      <view class="menu-group">
        <view class="menu-item" @click="goToCoupons">
          <view class="menu-left">
            <text class="menu-icon">🎫</text>
            <text class="menu-title">优惠券</text>
            <text class="menu-desc">查看可用优惠券</text>
          </view>
          <view class="menu-right">
            <text class="menu-count" v-if="couponCount > 0">{{ couponCount }}</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
        
        <view class="menu-item" @click="goToFavorites">
          <view class="menu-left">
            <text class="menu-icon">❤️</text>
            <text class="menu-title">我的收藏</text>
            <text class="menu-desc">收藏的套餐</text>
          </view>
          <view class="menu-right">
            <text class="menu-count" v-if="favoriteCount > 0">{{ favoriteCount }}</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
        
        <view class="menu-item" @click="goToWallet">
          <view class="menu-left">
            <text class="menu-icon">💰</text>
            <text class="menu-title">我的钱包</text>
            <text class="menu-desc">余额和交易记录</text>
          </view>
          <view class="menu-right">
            <text class="menu-balance" v-if="walletBalance > 0">¥{{ walletBalance }}</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>
      
      <view class="menu-group">
        <view class="menu-item" @click="goToSettings">
          <view class="menu-left">
            <text class="menu-icon">⚙️</text>
            <text class="menu-title">设置</text>
            <text class="menu-desc">账号和安全设置</text>
          </view>
          <view class="menu-right">
            <text class="menu-arrow">></text>
          </view>
        </view>
        
        <view class="menu-item" @click="goToHelp">
          <view class="menu-left">
            <text class="menu-icon">❓</text>
            <text class="menu-title">帮助与反馈</text>
            <text class="menu-desc">常见问题和意见反馈</text>
          </view>
          <view class="menu-right">
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>
    </view>

    <!-- 客服联系 -->
    <view class="service-section">
      <view class="service-header">
        <text class="service-title">在线客服</text>
        <text class="service-time">服务时间：9:00-21:00</text>
      </view>
      
      <view class="service-options">
        <button class="service-btn" @click="callService">
          <text class="service-icon">📞</text>
          <text class="service-text">电话客服</text>
        </button>
        
        <button class="service-btn" @click="chatService">
          <text class="service-icon">💬</text>
          <text class="service-text">在线客服</text>
        </button>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <button class="logout-btn" @click="logout" v-if="isLoggedIn">
        退出登录
      </button>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ProfilePage',
  
  data() {
    return {
      userInfo: {},
      locationInfo: {},
      orderStats: {},
      samplerCount: 0,
      couponCount: 0,
      favoriteCount: 0,
      walletBalance: 0,
      isLoggedIn: false,
      loading: false
    }
  },
  
  onLoad() {
    this.checkLoginStatus()
  },
  
  onShow() {
    if (this.isLoggedIn) {
      this.loadUserData()
    }
  },
  
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const token = uni.getStorageSync('access_token')
      this.isLoggedIn = !!token
      
      if (this.isLoggedIn) {
        this.loadUserData()
      } else {
        // 未登录，显示登录提示
        this.showLoginPrompt()
      }
    },
    
    // 加载用户数据
    async loadUserData() {
      try {
        this.loading = true
        
        // 并行加载各种数据
        const [userResponse, locationResponse, statsResponse] = await Promise.allSettled([
          this.$api.auth.getProfile(),
          this.$api.location.getUserLocation(),
          this.$api.user.getOrderStats()
        ])
        
        // 处理用户信息
        if (userResponse.status === 'fulfilled' && userResponse.value.success) {
          this.userInfo = userResponse.value.data
        }
        
        // 处理地理位置信息
        if (locationResponse.status === 'fulfilled' && locationResponse.value.success) {
          this.locationInfo = locationResponse.value.data
        }
        
        // 处理订单统计
        if (statsResponse.status === 'fulfilled' && statsResponse.value.success) {
          this.orderStats = statsResponse.value.data || {}
        }
        
        // 加载其他统计数据
        await this.loadOtherStats()
        
      } catch (error) {
        console.error('加载用户数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    // 加载其他统计数据
    async loadOtherStats() {
      try {
        const [samplerResponse, couponResponse, favoriteResponse] = await Promise.allSettled([
          this.$api.user.getSamplers(),
          this.$api.coupon.getAvailableCoupons(),
          this.$api.user.getFavorites()
        ])
        
        if (samplerResponse.status === 'fulfilled' && samplerResponse.value.success) {
          this.samplerCount = samplerResponse.value.data.length || 0
        }
        
        if (couponResponse.status === 'fulfilled' && couponResponse.value.success) {
          this.couponCount = couponResponse.value.data.length || 0
        }
        
        if (favoriteResponse.status === 'fulfilled' && favoriteResponse.value.success) {
          this.favoriteCount = favoriteResponse.value.data.length || 0
        }
        
        // TODO: 加载钱包余额
        
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },
    
    // 显示登录提示
    showLoginPrompt() {
      uni.showModal({
        title: '提示',
        content: '登录后可查看个人信息和订单',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            uni.navigateTo({
              url: '/pages/auth/login'
            })
          }
        }
      })
    },
    
    // 页面跳转方法
    goToOrderList(status) {
      const query = status ? `?status=${status}` : ''
      uni.navigateTo({
        url: `/pages/order/list${query}`
      })
    },
    
    goToSamplers() {
      uni.navigateTo({
        url: '/pages/user/samplers'
      })
    },
    
    goToReports() {
      uni.navigateTo({
        url: '/pages/report/list'
      })
    },
    
    goToAddresses() {
      uni.navigateTo({
        url: '/pages/user/addresses'
      })
    },
    
    goToCoupons() {
      uni.navigateTo({
        url: '/pages/user/coupons'
      })
    },
    
    goToFavorites() {
      uni.navigateTo({
        url: '/pages/user/favorites'
      })
    },
    
    goToWallet() {
      uni.navigateTo({
        url: '/pages/user/wallet'
      })
    },
    
    goToSettings() {
      uni.navigateTo({
        url: '/pages/user/settings'
      })
    },
    
    goToHelp() {
      uni.navigateTo({
        url: '/pages/user/help'
      })
    },
    
    // 功能方法
    changeAvatar() {
      uni.showActionSheet({
        itemList: ['从相册选择', '拍照'],
        success: (res) => {
          const sourceType = res.tapIndex === 0 ? ['album'] : ['camera']
          uni.chooseImage({
            count: 1,
            sourceType,
            success: (result) => {
              // TODO: 上传头像
              console.log('选择头像:', result)
            }
          })
        }
      })
    },
    
    changeLocation() {
      uni.navigateTo({
        url: '/pages/user/location'
      })
    },
    
    setLocation() {
      uni.navigateTo({
        url: '/pages/user/location'
      })
    },
    
    callService() {
      uni.makePhoneCall({
        phoneNumber: '400-123-4567'
      })
    },
    
    chatService() {
      // TODO: 打开在线客服
      uni.showToast({
        title: '客服功能开发中',
        icon: 'none'
      })
    },
    
    // 退出登录
    logout() {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            this.performLogout()
          }
        }
      })
    },
    
    async performLogout() {
      try {
        // 清除本地存储
        uni.removeStorageSync('access_token')
        uni.removeStorageSync('refresh_token')
        uni.removeStorageSync('user_info')
        
        // 重置页面数据
        this.userInfo = {}
        this.locationInfo = {}
        this.orderStats = {}
        this.samplerCount = 0
        this.couponCount = 0
        this.favoriteCount = 0
        this.walletBalance = 0
        this.isLoggedIn = false
        
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
        
        // 跳转到首页
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          })
        }, 1500)
        
      } catch (error) {
        console.error('退出登录失败:', error)
        uni.showToast({
          title: '退出失败',
          icon: 'none'
        })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f8f8f8;

  .profile-header {
    position: relative;
    background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
    padding: 60rpx 30rpx 40rpx;
    margin-bottom: 20rpx;

    .header-bg {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: url('/static/images/profile-bg.png') no-repeat center center;
      background-size: cover;
      opacity: 0.1;
    }

    .user-info {
      position: relative;
      z-index: 1;
      display: flex;
      align-items: center;

      .avatar-section {
        position: relative;
        margin-right: 30rpx;

        .avatar {
          width: 120rpx;
          height: 120rpx;
          border-radius: 60rpx;
          border: 4rpx solid rgba(255, 255, 255, 0.3);
        }

        .edit-avatar-btn {
          position: absolute;
          bottom: 0;
          right: 0;
          width: 40rpx;
          height: 40rpx;
          background-color: rgba(255, 255, 255, 0.9);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;

          .edit-icon {
            font-size: 20rpx;
          }
        }
      }

      .user-details {
        flex: 1;
        color: #ffffff;

        .username {
          display: block;
          font-size: 36rpx;
          font-weight: bold;
          margin-bottom: 10rpx;
        }

        .user-phone {
          display: block;
          font-size: 28rpx;
          opacity: 0.8;
          margin-bottom: 15rpx;
        }

        .location-info {
          display: flex;
          align-items: center;
          background-color: rgba(255, 255, 255, 0.2);
          padding: 10rpx 20rpx;
          border-radius: 20rpx;

          .location-icon {
            margin-right: 10rpx;
          }

          .location-text {
            flex: 1;
            font-size: 24rpx;
          }

          .change-location-btn {
            background-color: rgba(255, 255, 255, 0.3);
            color: #ffffff;
            border: none;
            border-radius: 15rpx;
            padding: 5rpx 15rpx;
            font-size: 20rpx;
          }
        }

        .location-warning {
          display: flex;
          align-items: center;
          background-color: rgba(255, 193, 7, 0.2);
          padding: 10rpx 20rpx;
          border-radius: 20rpx;
          cursor: pointer;

          .warning-icon {
            margin-right: 10rpx;
          }

          .warning-text {
            flex: 1;
            font-size: 24rpx;
            color: #ffc107;
          }
        }
      }
    }
  }

  .order-section {
    background: #ffffff;
    margin-bottom: 20rpx;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 30rpx 30rpx 20rpx;
      border-bottom: 1rpx solid #f0f0f0;

      .section-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }

      .view-all-btn {
        background-color: transparent;
        color: #4a90e2;
        border: none;
        font-size: 26rpx;
      }
    }

    .order-stats {
      display: flex;
      padding: 30rpx;

      .stat-item {
        flex: 1;
        text-align: center;

        .stat-icon {
          display: block;
          font-size: 48rpx;
          margin-bottom: 15rpx;
        }

        .stat-number {
          display: block;
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 10rpx;
        }

        .stat-label {
          display: block;
          font-size: 24rpx;
          color: #666;
        }
      }
    }
  }

  .menu-section {
    background: #ffffff;
    margin-bottom: 20rpx;

    .menu-group {
      border-bottom: 1rpx solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .menu-item {
        display: flex;
        align-items: center;
        padding: 30rpx;
        cursor: pointer;
        transition: background-color 0.3s ease;

        &:active {
          background-color: #f8f9fa;
        }

        .menu-left {
          flex: 1;
          display: flex;
          flex-direction: column;

          .menu-icon {
            font-size: 40rpx;
            margin-bottom: 10rpx;
          }

          .menu-title {
            font-size: 30rpx;
            color: #333;
            margin-bottom: 5rpx;
          }

          .menu-desc {
            font-size: 24rpx;
            color: #999;
          }
        }

        .menu-right {
          display: flex;
          align-items: center;
          gap: 15rpx;

          .menu-count, .menu-balance {
            background-color: #ff4757;
            color: #ffffff;
            padding: 5rpx 12rpx;
            border-radius: 20rpx;
            font-size: 20rpx;
            min-width: 40rpx;
            text-align: center;
          }

          .menu-balance {
            background-color: #4a90e2;
          }

          .menu-arrow {
            font-size: 24rpx;
            color: #ccc;
          }
        }
      }
    }
  }

  .service-section {
    background: #ffffff;
    padding: 30rpx;
    margin-bottom: 20rpx;

    .service-header {
      text-align: center;
      margin-bottom: 30rpx;

      .service-title {
        display: block;
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 10rpx;
      }

      .service-time {
        font-size: 24rpx;
        color: #666;
      }
    }

    .service-options {
      display: flex;
      gap: 30rpx;

      .service-btn {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        background-color: #f8f9fa;
        border: none;
        border-radius: 12rpx;
        padding: 30rpx 20rpx;

        .service-icon {
          font-size: 60rpx;
          margin-bottom: 15rpx;
        }

        .service-text {
          font-size: 28rpx;
          color: #333;
        }
      }
    }
  }

  .logout-section {
    padding: 40rpx 30rpx;

    .logout-btn {
      width: 100%;
      background-color: #ffffff;
      color: #ff4757;
      border: 1rpx solid #ff4757;
      border-radius: 12rpx;
      padding: 25rpx;
      font-size: 30rpx;
    }
  }
}
</style>