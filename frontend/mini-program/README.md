# 灵力检测小程序前端

## 项目介绍

这是一个基于UniApp开发的灵力检测实验室小程序前端项目，提供美观的用户界面和完整的业务功能。

## 功能特性

### 🏠 首页设计
- **精美轮播图**: 展示品牌形象和重要信息
- **快捷入口**: 基础体检、高端体检、专项体检快速导航
- **分类展示**: 支持分类筛选，美观的分类导航
- **套餐网格**: 2列网格布局，展示套餐卡片
- **实时数据**: 与后端API实时同步

### 🎨 设计特点
- **现代化UI**: 采用Material Design设计语言
- **响应式布局**: 适配不同屏幕尺寸
- **流畅动画**: 平滑的过渡效果和交互反馈
- **品牌色彩**: 统一的蓝色主题 (#4a90e2)
- **优雅图标**: 统一的图标设计风格

### 📱 页面结构
- **首页**: 商城主页，分类展示，套餐浏览
- **登录注册**: 用户认证界面
- **套餐列表**: 按分类查看套餐
- **套餐详情**: 套餐详细信息展示
- **购物车**: 购物车管理
- **订单管理**: 订单列表和详情
- **个人中心**: 用户信息和设置
- **采样人管理**: 采样人信息维护
- **检验报告**: 报告查看和下载

## 技术栈

### 框架
- **UniApp**: 跨平台开发框架
- **Vue 3**: 组合式API
- **SCSS**: CSS预处理器

### 工具
- **HBuilderX**: 推荐开发工具
- **Vite**: 构建工具
- **Vue Devtools**: 调试工具

## 项目结构

```
frontend/mini-program/
├── pages/                    # 页面目录
│   ├── index/               # 首页
│   ├── auth/                # 认证页面
│   ├── product/             # 商品页面
│   ├── cart/                # 购物车
│   ├── order/               # 订单页面
│   ├── user/                # 用户中心
│   └── report/              # 报告页面
├── components/              # 组件目录
│   ├── common/              # 通用组件
│   ├── product/             # 商品相关组件
│   └── order/               # 订单相关组件
├── static/                  # 静态资源
│   ├── images/              # 图片资源
│   └── icons/               # 图标资源
├── store/                   # 状态管理
│   └── modules/             # Vuex模块
├── utils/                   # 工具类
├── api/                     # API接口
├── App.vue                  # 应用入口
├── main.js                  # 主入口文件
├── pages.json               # 页面配置
└── manifest.json            # 应用配置
```

## 开发指南

### 环境要求
- Node.js 14+
- HBuilderX 3.8+
- 微信开发者工具（用于微信小程序调试）

### 安装依赖
```bash
# 使用npm安装依赖（如果需要）
npm install
```

### 运行项目

#### 方法一：HBuilderX运行
1. 用HBuilderX打开项目目录
2. 点击"运行"菜单
3. 选择要运行的平台：
   - 浏览器运行（H5）
   - 运行到小程序模拟器
   - 运行到手机或模拟器

#### 方法二：命令行运行
```bash
# 安装UniApp CLI
npm install -g @dcloudio/uni-cli

# 开发模式运行
npm run dev:h5      # H5开发
npm run dev:mp-weixin  # 微信小程序开发
npm run dev:app    # APP开发
```

### 构建项目
```bash
# 构建H5
npm run build:h5

# 构建小程序
npm run build:mp-weixin

# 构建APP
npm run build:app
```

## API接口配置

### 配置API地址
在 `api/http.js` 中配置后端API地址：

```javascript
const config = {
  baseURL: 'http://localhost:8081/api/v1', // 开发环境
  // baseURL: 'https://your-api-domain.com/api/v1', // 生产环境
  timeout: 10000
}
```

### 主要接口
- **认证接口**: `/auth/login`, `/auth/register`, `/auth/profile`
- **商品接口**: `/products/categories/*`, `/products/packages/*`
- **购物车接口**: `/cart/*`
- **订单接口**: `/orders/*`
- **报告接口**: `/reports/*`

## 自定义样式

### 全局样式
在 `App.vue` 中定义了全局样式类：

```scss
// 通用布局
.flex, .flex-center, .flex-between, .flex-column

// 文字对齐
.text-center, .text-left, .text-right

// 颜色类
.text-primary, .text-success, .text-warning, .text-danger
.bg-primary, .bg-success, .bg-warning, .bg-danger

// 间距类
.margin-small, .margin, .margin-large
.padding-small, .padding, .padding-large

// 圆角类
.radius-small, .radius, .radius-large

// 阴影类
.shadow-small, .shadow, .shadow-large
```

### 主题颜色
- **主色**: #4a90e2 (蓝色)
- **成功色**: #67c23a (绿色)
- **警告色**: #e6a23c (橙色)
- **危险色**: #f56c6c (红色)
- **中性色**: #909399, #c0c4cc (灰色)

## 页面开发规范

### 页面结构
每个页面应包含：
1. `<template>`: 页面结构
2. `<script>`: 页面逻辑
3. `<style>`: 页面样式

### 命名规范
- 页面文件: 小写字母，使用短横线连接
- 组件名: PascalCase
- 样式类: 小写字母，使用短横线连接
- 变量名: 小驼峰命名法

### 状态管理
使用Vuex进行状态管理：
- 用户状态: `store/modules/user.js`
- 商品状态: `store/modules/product.js`
- 购物车状态: `store/modules/cart.js`

## 部署说明

### H5部署
1. 构建项目: `npm run build:h5`
2. 将 `dist/build/h5` 目录上传到服务器
3. 配置Nginx或Apache进行部署

### 小程序部署
1. 构建小程序: `npm run build:mp-weixin`
2. 用微信开发者工具打开 `dist/build/mp-weixin` 目录
3. 提交代码审核并发布

### APP部署
1. 构建APP: `npm run build:app`
2. 使用HBuilderX进行云打包或本地打包
3. 发布到应用商店

## 常见问题

### Q: 如何调试API接口？
A: 检查后端服务是否启动，确认API地址配置正确，查看浏览器控制台错误信息。

### Q: 如何添加新的页面？
A: 在 `pages/` 目录下创建页面文件夹和对应的vue文件，在 `pages.json` 中注册页面。

### Q: 如何自定义主题颜色？
A: 修改 `App.vue` 中的全局样式变量，或使用CSS自定义属性。

### Q: 如何集成第三方组件？
A: 通过npm安装组件库，或在 `components/` 目录下创建自定义组件。

## 技术支持

如有问题，请联系开发团队或查看项目文档。