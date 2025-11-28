// API配置
const config = {
  baseURL: 'http://localhost:8081/api/v1', // 开发环境
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
}

// 请求拦截器
function request(options) {
  return new Promise((resolve, reject) => {
    // 添加认证token
    const token = uni.getStorageSync('access_token')
    if (token) {
      options.header = {
        ...options.header,
        'Authorization': `Bearer ${token}`
      }
    }
    
    // 显示加载状态
    if (options.loading !== false) {
      uni.showLoading({
        title: '加载中...',
        mask: true
      })
    }
    
    uni.request({
      url: config.baseURL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        ...config.headers,
        ...options.header
      },
      timeout: config.timeout,
      success: (res) => {
        if (options.loading !== false) {
          uni.hideLoading()
        }
        
        if (res.statusCode === 200) {
          resolve(res.data)
        } else if (res.statusCode === 401) {
          // token无效，清除并跳转到登录页
          uni.removeStorageSync('access_token')
          uni.removeStorageSync('refresh_token')
          uni.showToast({
            title: '登录已过期',
            icon: 'none'
          })
          setTimeout(() => {
            uni.navigateTo({
              url: '/pages/auth/login'
            })
          }, 1500)
          reject(new Error('未授权'))
        } else {
          uni.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          })
          reject(new Error(res.data.message || '请求失败'))
        }
      },
      fail: (err) => {
        if (options.loading !== false) {
          uni.hideLoading()
        }
        uni.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// 用户认证相关API
export const auth = {
  // 登录
  login(data) {
    return request({
      url: '/auth/login',
      method: 'POST',
      data
    })
  },
  
  // 注册
  register(data) {
    return request({
      url: '/auth/register',
      method: 'POST',
      data
    })
  },
  
  // 获取用户信息
  getProfile() {
    return request({
      url: '/auth/profile',
      method: 'GET'
    })
  },
  
  // 刷新token
  refreshToken(data) {
    return request({
      url: '/auth/refresh',
      method: 'POST',
      data
    })
  }
}

// 商品相关API
export const product = {
  // 获取分类树
  getCategoryTree() {
    return request({
      url: '/products/categories/tree',
      method: 'GET'
    })
  },
  
  // 获取顶级分类
  getTopCategories() {
    return request({
      url: '/products/categories/top',
      method: 'GET'
    })
  },
  
  // 获取分类详情
  getCategoryDetail(categoryId) {
    return request({
      url: `/products/categories/${categoryId}`,
      method: 'GET'
    })
  },
  
  // 获取套餐列表
  getPackageList(params) {
    const queryString = Object.keys(params)
      .map(key => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')
    
    return request({
      url: `/products/packages?${queryString}`,
      method: 'GET'
    })
  },
  
  // 获取套餐详情
  getPackageDetail(packageId) {
    return request({
      url: `/products/packages/${packageId}`,
      method: 'GET'
    })
  },
  
  // 获取热门套餐
  getHotPackages(limit = 10) {
    return request({
      url: `/products/packages/hot?limit=${limit}`,
      method: 'GET'
    })
  },
  
  // 获取推荐套餐
  getRecommendedPackages(limit = 10) {
    return request({
      url: `/products/packages/recommended?limit=${limit}`,
      method: 'GET'
    })
  },
  
  // 搜索套餐
  searchPackages(params) {
    const queryString = Object.keys(params)
      .map(key => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')
    
    return request({
      url: `/products/packages/search?${queryString}`,
      method: 'GET'
    })
  }
}

export default {
  request,
  auth,
  product
}