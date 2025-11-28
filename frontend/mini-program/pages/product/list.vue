<template>
  <view class="product-list-page">
    <!-- 搜索栏 -->
    <view class="search-section">
      <view class="search-bar">
        <input 
          class="search-input" 
          placeholder="搜索体检套餐..." 
          v-model="searchKeyword"
          @input="onSearchInput"
        />
        <button class="search-btn" @click="searchPackages">搜索</button>
      </view>
    </view>

    <!-- 分类筛选 -->
    <view class="category-filter">
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

    <!-- 排序选项 -->
    <view class="sort-section">
      <picker class="sort-picker" :value="currentSortIndex" :range="sortOptions" 
              @change="changeSort">
        <view class="sort-display">
          <text class="sort-label">{{ sortOptions[currentSortIndex] }}</text>
          <text class="sort-arrow">▼</text>
        </view>
      </picker>
    </view>

    <!-- 套餐列表 -->
    <view class="package-list" v-if="packages.length > 0">
      <view 
        class="package-item" 
        v-for="package in packages" 
        :key="package.id"
        @click="goToDetail(package.id)"
      >
        <view class="package-image">
          <image :src="package.coverImage || '/static/images/default-package.jpg'" mode="aspectFill" />
          <view class="package-tags">
            <text class="tag hot" v-if="package.isHot">热门</text>
            <text class="tag new" v-if="package.isNew">新品</text>
            <text class="tag discount" v-if="package.discountPercent > 0">
              {{ package.discountPercent }}%OFF
            </text>
          </view>
        </view>
        
        <view class="package-info">
          <text class="package-name">{{ package.name }}</text>
          <text class="package-desc">{{ package.description }}</text>
          
          <!-- 检测项目 -->
          <view class="test-items">
            <text class="test-item" v-for="(item, index) in package.testItems.slice(0, 3)" :key="index">
              {{ item }}
            </text>
            <text class="test-more" v-if="package.testItems.length > 3">
              +{{ package.testItems.length - 3 }}
            </text>
          </view>
          
          <!-- 价格和按钮 -->
          <view class="package-footer">
            <view class="price-section">
              <text class="current-price">¥{{ package.price }}</text>
              <text class="original-price" v-if="package.originalPrice > package.price">
                ¥{{ package.originalPrice }}
              </text>
            </view>
            
            <view class="action-buttons">
              <button class="add-cart-btn" @click.stop="addToCart(package)">
                <text class="icon">🛒</text>
                加购物车
              </button>
              <button class="buy-now-btn" @click.stop="buyNow(package)">
                立即购买
              </button>
            </view>
          </view>
          
          <!-- 采样方式提示 -->
          <view class="sampling-method" v-if="package.shippingMethods">
            <text class="method-icon">📦</text>
            <text class="method-text">{{ getSamplingMethodText(package.shippingMethods) }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-else-if="!loading">
      <image class="empty-image" src="/static/images/empty-packages.png" mode="aspectFit" />
      <text class="empty-text">
        {{ searchKeyword ? '未找到相关套餐' : '暂无套餐数据' }}
      </text>
      <button class="reset-btn" @click="resetFilter" v-if="searchKeyword || currentCategoryId > 0">
        重置筛选
      </button>
    </view>

    <!-- 加载状态 -->
    <view class="loading" v-if="loading">
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 分页加载 -->
    <view class="load-more" v-if="hasMore && !loading" @click="loadMore">
      <text class="load-text">加载更多</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ProductListPage',
  
  data() {
    return {
      searchKeyword: '',
      categories: [],
      packages: [],
      currentCategoryId: 0,
      currentSortIndex: 0,
      sortOptions: ['默认排序', '价格从低到高', '价格从高到低', '销量排序'],
      page: 1,
      size: 10,
      hasMore: true,
      loading: false
    }
  },
  
  onLoad(options) {
    this.loadCategories()
    
    // 如果有分类参数，设置选中分类
    if (options.categoryCode) {
      // TODO: 根据分类代码查找分类ID
    }
    
    this.loadPackages()
  },
  
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMorePackages()
    }
  },
  
  methods: {
    // 加载分类数据
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
    
    // 加载套餐列表
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
        
        if (this.searchKeyword) {
          params.keyword = this.searchKeyword
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
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        this.loading = false
      }
    },
    
    // 搜索套餐
    searchPackages() {
      this.page = 1
      this.hasMore = true
      this.packages = []
      this.loadPackages()
    },
    
    // 搜索输入事件
    onSearchInput() {
      // 可以实现防抖搜索
      if (this.searchTimeout) {
        clearTimeout(this.searchTimeout)
      }
      
      this.searchTimeout = setTimeout(() => {
        this.page = 1
        this.hasMore = true
        this.packages = []
        this.loadPackages()
      }, 500)
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
    
    // 改变排序
    changeSort(e) {
      const index = e.detail.value
      if (this.currentSortIndex === index) return
      
      this.currentSortIndex = index
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
    
    // 加载更多
    loadMorePackages() {
      this.loadPackages()
    },
    
    // 添加到购物车
    async addToCart(packageInfo) {
      try {
        // 检查登录状态
        const token = uni.getStorageSync('access_token')
        if (!token) {
          uni.showModal({
            title: '提示',
            content: '加入购物车需要登录，是否立即登录？',
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
        
        const cartData = {
          packageId: packageInfo.id,
          quantity: 1,
          samplingMethod: 1 // 默认自邮寄
        }
        
        await this.$api.cart.addToCart(cartData)
        
        uni.showToast({
          title: '已加入购物车',
          icon: 'success'
        })
        
        // 触发购物车数量更新
        uni.$emit('cart_updated')
        
      } catch (error) {
        console.error('添加购物车失败:', error)
        uni.showToast({
          title: error.message || '添加失败',
          icon: 'none'
        })
      }
    },
    
    // 立即购买
    buyNow(packageInfo) {
      // 跳转到订单确认页面，带上套餐信息
      const packageData = encodeURIComponent(JSON.stringify([packageInfo]))
      uni.navigateTo({
        url: `/pages/order/confirm?packages=${packageData}`
      })
    },
    
    // 跳转到套餐详情
    goToDetail(packageId) {
      uni.navigateTo({
        url: `/pages/product/detail?id=${packageId}`
      })
    },
    
    // 获取采样方式文本
    getSamplingMethodText(shippingMethods) {
      const methods = {
        'mailing': '支持自邮寄',
        'appointment': '支持上门预约'
      }
      
      const text = shippingMethods.map(method => methods[method]).join('、')
      return text || '默认方式'
    },
    
    // 重置筛选
    resetFilter() {
      this.searchKeyword = ''
      this.currentCategoryId = 0
      this.currentSortIndex = 0
      this.page = 1
      this.hasMore = true
      this.packages = []
      this.loadPackages()
    }
  }
}
</script>

<style lang="scss" scoped>
.product-list-page {
  min-height: 100vh;
  background-color: #f8f8f8;

  .search-section {
    background: #ffffff;
    padding: 20rpx;
    border-bottom: 1rpx solid #eee;

    .search-bar {
      display: flex;
      align-items: center;
      gap: 20rpx;

      .search-input {
        flex: 1;
        height: 70rpx;
        background-color: #f5f5f5;
        border-radius: 35rpx;
        padding: 0 30rpx;
        font-size: 28rpx;
        color: #333;

        &::placeholder {
          color: #999;
        }
      }

      .search-btn {
        background-color: #4a90e2;
        color: #ffffff;
        border: none;
        border-radius: 8rpx;
        padding: 20rpx 40rpx;
        font-size: 28rpx;
      }
    }
  }

  .category-filter {
    background: #ffffff;
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

  .sort-section {
    background: #ffffff;
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid #eee;

    .sort-picker {
      .sort-display {
        display: flex;
        align-items: center;
        font-size: 28rpx;
        color: #666;

        .sort-label {
          margin-right: 10rpx;
        }

        .sort-arrow {
          font-size: 20rpx;
          color: #999;
        }
      }
    }
  }

  .package-list {
    padding: 20rpx;

    .package-item {
      background: #ffffff;
      border-radius: 16rpx;
      margin-bottom: 20rpx;
      overflow: hidden;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);

      .package-image {
        position: relative;
        height: 300rpx;
        overflow: hidden;

        image {
          width: 100%;
          height: 100%;
        }

        .package-tags {
          position: absolute;
          top: 20rpx;
          left: 20rpx;
          display: flex;
          gap: 10rpx;

          .tag {
            padding: 8rpx 16rpx;
            border-radius: 20rpx;
            font-size: 20rpx;
            color: #ffffff;

            &.hot {
              background: linear-gradient(45deg, #ff6b35, #f7931e);
            }

            &.new {
              background: linear-gradient(45deg, #6c5ce7, #a29bfe);
            }

            &.discount {
              background: linear-gradient(45deg, #fd79a8, #fdcb6e);
            }
          }
        }
      }

      .package-info {
        padding: 30rpx;

        .package-name {
          display: block;
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 15rpx;
          line-height: 1.4;
        }

        .package-desc {
          display: block;
          font-size: 26rpx;
          color: #666;
          margin-bottom: 20rpx;
          line-height: 1.5;
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 2;
          overflow: hidden;
        }

        .test-items {
          display: flex;
          flex-wrap: wrap;
          gap: 15rpx;
          margin-bottom: 25rpx;

          .test-item {
            background-color: #f0f8ff;
            color: #4a90e2;
            padding: 8rpx 16rpx;
            border-radius: 20rpx;
            font-size: 22rpx;
            border: 1rpx solid #e3f2fd;
          }

          .test-more {
            color: #999;
            font-size: 22rpx;
            padding: 8rpx 16rpx;
          }
        }

        .package-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20rpx;

          .price-section {
            .current-price {
              font-size: 36rpx;
              font-weight: bold;
              color: #ff4757;
              margin-right: 15rpx;
            }

            .original-price {
              font-size: 24rpx;
              color: #999;
              text-decoration: line-through;
            }
          }

          .action-buttons {
            display: flex;
            gap: 15rpx;

            .add-cart-btn {
              background-color: #f8f9fa;
              color: #4a90e2;
              border: 1rpx solid #4a90e2;
              border-radius: 8rpx;
              padding: 15rpx 25rpx;
              font-size: 24rpx;
              display: flex;
              align-items: center;
              gap: 8rpx;

              .icon {
                font-size: 24rpx;
              }
            }

            .buy-now-btn {
              background-color: #4a90e2;
              color: #ffffff;
              border: none;
              border-radius: 8rpx;
              padding: 15rpx 25rpx;
              font-size: 24rpx;
            }
          }
        }

        .sampling-method {
          display: flex;
          align-items: center;
          padding: 15rpx 0;
          border-top: 1rpx solid #f0f0f0;

          .method-icon {
            font-size: 24rpx;
            margin-right: 10rpx;
          }

          .method-text {
            font-size: 24rpx;
            color: #666;
          }
        }
      }
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 120rpx 40rpx;

    .empty-image {
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

    .reset-btn {
      background-color: #4a90e2;
      color: #ffffff;
      border: none;
      border-radius: 8rpx;
      padding: 20rpx 60rpx;
      font-size: 28rpx;
    }
  }

  .loading, .load-more {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 60rpx;
    color: #666;

    .loading-text, .load-text {
      font-size: 28rpx;
    }
  }

  .load-more {
    cursor: pointer;

    .load-text {
      color: #4a90e2;
    }
  }
}
</style>