<template>
  <view id="app">
    <!-- 应用入口 -->
  </view>
</template>

<script>
export default {
  name: 'App',
  
  onLaunch() {
    console.log('App Launch')
    
    // 检查登录状态
    this.checkLoginStatus()
    
    // 初始化应用
    this.initApp()
  },
  
  onShow() {
    console.log('App Show')
  },
  
  onHide() {
    console.log('App Hide')
  },
  
  onError(err) {
    console.error('App Error:', err)
  },
  
  methods: {
    // 检查登录状态
    async checkLoginStatus() {
      try {
        const token = uni.getStorageSync('access_token')
        if (token) {
          // 验证token有效性
          const response = await this.$api.auth.getProfile()
          if (response.success) {
            this.$store.commit('user/setUserInfo', response.data)
          }
        }
      } catch (error) {
        console.error('检查登录状态失败:', error)
        // 清除无效token
        uni.removeStorageSync('access_token')
        uni.removeStorageSync('refresh_token')
      }
    },
    
    // 初始化应用
    initApp() {
      // 设置全局配置
      this.setGlobalConfig()
      
      // 预加载数据
      this.preloadData()
    },
    
    // 设置全局配置
    setGlobalConfig() {
      // 设置导航栏颜色
      uni.setNavigationBarColor({
        frontColor: '#ffffff',
        backgroundColor: '#4a90e2'
      })
      
      // 设置页面标题
      uni.setNavigationBarTitle({
        title: '灵力检测'
      })
    },
    
    // 预加载数据
    async preloadData() {
      try {
        // 预加载套餐分类
        const categories = await this.$api.product.getTopCategories()
        if (categories.success) {
          this.$store.commit('product/setCategories', categories.data)
        }
        
        // 预加载热门套餐
        const hotPackages = await this.$api.product.getHotPackages(10)
        if (hotPackages.success) {
          this.$store.commit('product/setHotPackages', hotPackages.data)
        }
      } catch (error) {
        console.error('预加载数据失败:', error)
      }
    }
  }
}
</script>

<style lang="scss">
// 全局样式
page {
  background-color: #f8f8f8;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Helvetica Neue', sans-serif;
}

// 通用样式类
.flex {
  display: flex;
}

.flex-center {
  display: flex;
  justify-content: center;
  align-items: center;
}

.flex-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.text-center {
  text-align: center;
}

.text-left {
  text-align: left;
}

.text-right {
  text-align: right;
}

// 颜色类
.text-primary {
  color: #4a90e2;
}

.text-success {
  color: #67c23a;
}

.text-warning {
  color: #e6a23c;
}

.text-danger {
  color: #f56c6c;
}

.text-gray {
  color: #909399;
}

.text-light-gray {
  color: #c0c4cc;
}

.text-white {
  color: #ffffff;
}

// 背景颜色类
.bg-primary {
  background-color: #4a90e2;
}

.bg-success {
  background-color: #67c23a;
}

.bg-warning {
  background-color: #e6a23c;
}

.bg-danger {
  background-color: #f56c6c;
}

.bg-white {
  background-color: #ffffff;
}

.bg-light {
  background-color: #f8f8f8;
}

// 间距类
.margin-small {
  margin: 10rpx;
}

.margin {
  margin: 20rpx;
}

.margin-large {
  margin: 30rpx;
}

.margin-top-small {
  margin-top: 10rpx;
}

.margin-top {
  margin-top: 20rpx;
}

.margin-top-large {
  margin-top: 30rpx;
}

.margin-bottom-small {
  margin-bottom: 10rpx;
}

.margin-bottom {
  margin-bottom: 20rpx;
}

.margin-bottom-large {
  margin-bottom: 30rpx;
}

.margin-left-small {
  margin-left: 10rpx;
}

.margin-left {
  margin-left: 20rpx;
}

.margin-right-small {
  margin-right: 10rpx;
}

.margin-right {
  margin-right: 20rpx;
}

.padding-small {
  padding: 10rpx;
}

.padding {
  padding: 20rpx;
}

.padding-large {
  padding: 30rpx;
}

// 圆角类
.radius-small {
  border-radius: 8rpx;
}

.radius {
  border-radius: 12rpx;
}

.radius-large {
  border-radius: 16rpx;
}

// 阴影类
.shadow-small {
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.shadow {
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.shadow-large {
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
}
</style>