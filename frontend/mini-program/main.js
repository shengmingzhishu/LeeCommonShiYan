import { createSSRApp } from 'vue'
import App from './App.vue'

// 引入API
import api from './api/http.js'

// 创建应用实例
export function createApp() {
  const app = createSSRApp(App)
  
  // 全局配置API
  app.config.globalProperties.$api = api
  
  return {
    app
  }
}