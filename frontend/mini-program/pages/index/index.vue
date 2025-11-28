<template>
  <view class="index-page">
    <!-- 顶部导航 -->
    <view class="header">
      <view class="search-bar">
        <input 
          class="search-input" 
          placeholder="搜索体检套餐..." 
          @click="goToSearch"
          disabled
        />
        <image class="search-icon" src="/static/icons/search.png" mode="aspectFit" />
      </view>
    </view>

    <!-- 轮播图 -->
    <view class="banner-swiper" v-if="banners.length > 0">
      <swiper class="swiper" indicator-dots autoplay interval="3000" duration="500">
        <swiper-item v-for="banner in banners" :key="banner.id">
          <image class="banner-image" :src="banner.image" mode="aspectFill" />
        </swiper-item>
      </swiper>
    </view>

    <!-- 快捷入口 -->
    <view class="quick-entry">
      <view class="entry-item" @click="goToPackageList('BASIC')">
        <image class="entry-icon" src="/static/icons/basic-checkup.png" mode="aspectFit" />
        <text class="entry-text">基础体检</text>
      </view>
      <view class="entry-item" @click="goToPackageList('PREMIUM')">
        <image class="entry-icon" src="/static/icons/premium-checkup.png" mode="aspectFit" />
        <text class="entry-text">高端体检</text>
      </view>
      <view class="entry-item" @click="goToPackageList('SPECIALIZED')">
        <image class="entry-icon" src="/static/icons/specialized-checkup.png" mode="aspectFit" />
        <text class="entry-text">专项体检</text>
      </view>
      <view class="entry-item" @click="goToOrderList">
        <image class="entry-icon" src="/static/icons/order.png" mode="aspectFit" />
        <text class="entry-text">我的订单</text>
      </view>
    </view>

    <!-- 分类导航 -->
    <view class="category-nav">
      <scroll-view class="category-scroll" scroll-x>
        <view class="category-tabs">
          <view 
            class="category-tab" 
            :class="{ active: currentCategoryId === 0 }"
            @click="selectCategory(0)"
          >
            全部
          </view>
          <view 
            v-for="category in categories" 
            :key="category.id"
            class="category-tab" 
            :class="{ active: currentCategoryId === category.id }"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 套餐列表 -->
    <view class="package-section">
      <view class="section-header">
        <text class="section-title">推荐套餐</text>
        <text class="section-more" @click="goToPackageList()">更多 ></text>
      </view>
      
      <view class="package-grid" v-if="packages.length > 0">
        <view 
          class="package-card" 
          v-for="package in packages" 
          :key="package.id"
          @click="goToPackageDetail(package.id)"
        >
          <image class="package-image" :src="package.coverImage || '/static/images/default-package.jpg'" mode="aspectFill" />
          <view class="package-info">
            <text class="package-name">{{ package.name }}</text>
            <text class="package-desc">{{ package.description }}</text>
            <view class="package-price">
              <text class="current-price">¥{{ package.price }}</text>
              <text class="original-price" v-if="package.originalPrice && package.originalPrice > package.price">
                ¥{{ package.originalPrice }}
              </text>
              <text class="discount" v-if="package.discountPercent && package.discountPercent > 0">
                {{ package.discountPercent }}%OFF
              </text>
            </view>
            <view class="package-tags">
              <text class="tag" v-if="package.samplingMethod === 3">双采样</text>
              <text class="tag" v-if="package.tags && package.tags.includes('限时优惠')">优惠</text>
              <text class="tag" v-if="package.reportDeliveryDays <= 3">快速报告</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-state" v-else>
        <image class="empty-image" src="/static/images/empty-packages.png" mode="aspectFit" />
        <text class="empty-text">暂无套餐数据</text>
      </view>
    </view>

    <!-- 底部间距 -->
    <view class="bottom-space"></view>
  </view>
</template>

<script>
export default {
  name: 'IndexPage',
  
  data() {
    return {
      banners: [
        {
          id: 1,
          image: '/static/images/banner1.jpg',
          title: '专业体检，守护健康'
        },
        {
          id: 2,
          image: '/static/images/banner2.jpg',
          title: '精准检测，权威报告'
        }
      ],
      categories: [],
      packages: [],
      currentCategoryId: 0,
      loading: false,
      page: 1,
      size: 10,
      hasMore: true
    }
  },
  
  onLoad() {
    this.loadData()
  },
  
  onPullDownRefresh() {
    this.refreshData()
  },
  
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMorePackages()
    }
  },
  
  methods: {
    // 加载数据
    async loadData() {
      try {
        await Promise.all([
          this.loadCategories(),
          this.loadPackages()
        ])
      } catch (error) {
        console.error('加载数据失败:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'error'
        })
      }
    },
    
    // 刷新数据
    async refreshData() {
      this.page = 1
      this.hasMore = true
      await this.loadData()
      uni.stopPullDownRefresh()
    },
    
    // 加载分类
    async loadCategories() {
      try {
        const response = await this.$api.product.getTopCategories()
        if (response.success) {
          this.categories = response.data
        }
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    },
    
    // 加载套餐
    async loadPackages() {
      if (this.loading) return
      
      this.loading = true
      try {
        const params = {
          page: this.page,
          size: this.size
        }
        
        if (this.currentCategoryId > 0) {
          params.categoryId = this.currentCategoryId
        }
        
        const response = await this.$api.product.getPackageList(params)
        if (response.success) {
          const newPackages = response.data.records || []
          
          if (this.page === 1) {
            this.packages = newPackages
          } else {
            this.packages = this.packages.concat(newPackages)
          }
          
          this.hasMore = newPackages.length === this.size
          this.page++
        }
      } catch (error) {
        console.error('加载套餐失败:', error)
        uni.showToast({
          title: '加载套餐失败',
          icon: 'error'
        })
      } finally {
        this.loading = false
      }
    },
    
    // 加载更多套餐
    loadMorePackages() {
      this.loadPackages()
    },
    
    // 选择分类
    selectCategory(categoryId) {
      if (this.currentCategoryId === categoryId) return
      
      this.currentCategoryId = categoryId
      this.page = 1
      this.hasMore = true
      this.packages = []
      
      uni.showLoading({
        title: '加载中...'
      })
      
      this.loadPackages().finally(() => {
        uni.hideLoading()
      })
    },
    
    // 页面跳转
    goToSearch() {
      uni.navigateTo({
        url: '/pages/product/search'
      })
    },
    
    goToPackageList(categoryCode) {
      if (categoryCode) {
        uni.navigateTo({
          url: `/pages/product/list?categoryCode=${categoryCode}`
        })
      } else {
        uni.navigateTo({
          url: '/pages/product/list'
        })
      }
    },
    
    goToPackageDetail(packageId) {
      uni.navigateTo({
        url: `/pages/product/detail?id=${packageId}`
      })
    },
    
    goToOrderList() {
      // 检查登录状态
      const token = uni.getStorageSync('access_token')
      if (!token) {
        uni.showModal({
          title: '提示',
          content: '请先登录',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              uni.navigateTo({
                url: '/pages/auth/login'
              })
            }
          }
        })
        return
      }
      
      uni.navigateTo({
        url: '/pages/order/list'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.index-page {
  min-height: 100vh;
  background-color: #f8f8f8;
  
  .header {
    background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
    padding: 20rpx 30rpx;
    
    .search-bar {
      position: relative;
      
      .search-input {
        width: 100%;
        height: 70rpx;
        background-color: rgba(255, 255, 255, 0.9);
        border-radius: 35rpx;
        padding: 0 80rpx 0 30rpx;
        font-size: 28rpx;
        color: #333;
        
        &::placeholder {
          color: #999;
        }
      }
      
      .search-icon {
        position: absolute;
        right: 30rpx;
        top: 50%;
        transform: translateY(-50%);
        width: 40rpx;
        height: 40rpx;
        opacity: 0.6;
      }
    }
  }
  
  .banner-swiper {
    height: 300rpx;
    
    .swiper {
      height: 100%;
      
      .banner-image {
        width: 100%;
        height: 100%;
      }
    }
  }
  
  .quick-entry {
    background-color: #ffffff;
    padding: 30rpx 0;
    display: flex;
    
    .entry-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .entry-icon {
        width: 80rpx;
        height: 80rpx;
        margin-bottom: 15rpx;
      }
      
      .entry-text {
        font-size: 24rpx;
        color: #333;
      }
    }
  }
  
  .category-nav {
    background-color: #ffffff;
    border-bottom: 1rpx solid #eee;
    
    .category-scroll {
      white-space: nowrap;
      
      .category-tabs {
        display: inline-flex;
        padding: 0 30rpx;
        
        .category-tab {
          display: inline-block;
          padding: 25rpx 30rpx;
          margin-right: 20rpx;
          font-size: 28rpx;
          color: #666;
          border-bottom: 4rpx solid transparent;
          transition: all 0.3s ease;
          white-space: nowrap;
          
          &.active {
            color: #4a90e2;
            border-bottom-color: #4a90e2;
            font-weight: bold;
          }
        }
      }
    }
  }
  
  .package-section {
    background-color: #ffffff;
    margin-top: 20rpx;
    
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 30rpx;
      border-bottom: 1rpx solid #f0f0f0;
      
      .section-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }
      
      .section-more {
        font-size: 26rpx;
        color: #999;
      }
    }
    
    .package-grid {
      padding: 20rpx 30rpx;
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20rpx;
      
      .package-card {
        background-color: #ffffff;
        border-radius: 16rpx;
        overflow: hidden;
        box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
        transition: transform 0.3s ease;
        
        &:active {
          transform: scale(0.98);
        }
        
        .package-image {
          width: 100%;
          height: 200rpx;
          background-color: #f5f5f5;
        }
        
        .package-info {
          padding: 20rpx;
          
          .package-name {
            display: block;
            font-size: 28rpx;
            font-weight: bold;
            color: #333;
            margin-bottom: 10rpx;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
          
          .package-desc {
            display: block;
            font-size: 24rpx;
            color: #666;
            margin-bottom: 15rpx;
            height: 40rpx;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
          
          .package-price {
            display: flex;
            align-items: baseline;
            margin-bottom: 15rpx;
            
            .current-price {
              font-size: 32rpx;
              font-weight: bold;
              color: #ff4757;
              margin-right: 10rpx;
            }
            
            .original-price {
              font-size: 24rpx;
              color: #999;
              text-decoration: line-through;
              margin-right: 10rpx;
            }
            
            .discount {
              font-size: 20rpx;
              color: #ff4757;
              background-color: rgba(255, 71, 87, 0.1);
              padding: 4rpx 8rpx;
              border-radius: 8rpx;
            }
          }
          
          .package-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 8rpx;
            
            .tag {
              font-size: 20rpx;
              color: #4a90e2;
              background-color: rgba(74, 144, 226, 0.1);
              padding: 4rpx 8rpx;
              border-radius: 8rpx;
            }
          }
        }
      }
    }
    
    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 100rpx 30rpx;
      
      .empty-image {
        width: 200rpx;
        height: 200rpx;
        opacity: 0.5;
        margin-bottom: 30rpx;
      }
      
      .empty-text {
        font-size: 28rpx;
        color: #999;
      }
    }
  }
  
  .bottom-space {
    height: 100rpx;
  }
}
</style>