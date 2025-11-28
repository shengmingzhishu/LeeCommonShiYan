<template>
  <view class="cart-page">
    <!-- 使用购物车组件 -->
    <CartComponent />
  </view>
</template>

<script>
import CartComponent from '@/components/common/CartComponent.vue'

export default {
  name: 'CartPage',
  
  components: {
    CartComponent
  },
  
  onLoad() {
    // 页面加载时的处理
  },
  
  onShow() {
    // 页面显示时的处理
  },
  
  onPullDownRefresh() {
    // 下拉刷新处理
    setTimeout(() => {
      uni.stopPullDownRefresh()
    }, 1000)
  }
}
</script>

<style lang="scss" scoped>
.cart-page {
  min-height: 100vh;
  background-color: #f8f8f8;
}
</style>