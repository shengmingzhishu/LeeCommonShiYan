<template>
  <div class="admin-container">
    <!-- 布局容器 -->
    <el-container class="admin-layout">
      <!-- 侧边栏 -->
      <el-aside :width="sidebarWidth" class="sidebar">
        <div class="sidebar-header">
          <img src="/images/logo.png" alt="Logo" class="logo" />
          <h2 class="system-name">灵力检测管理后台</h2>
        </div>
        
        <el-menu
          :default-active="currentRoute"
          :collapse="isCollapse"
          router
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <!-- 仪表盘 -->
          <el-menu-item index="/admin/dashboard">
            <el-icon><Monitor /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>

          <!-- 商品管理 -->
          <el-submenu index="products">
            <template #title>
              <el-icon><ShoppingBag /></el-icon>
              <span>商品管理</span>
            </template>
            <el-menu-item index="/admin/packages">套餐管理</el-menu-item>
            <el-menu-item index="/admin/categories">分类管理</el-menu-item>
          </el-submenu>

          <!-- 报告管理 -->
          <el-submenu index="reports">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>报告管理</span>
            </template>
            <el-menu-item index="/admin/reports/upload">上传报告</el-menu-item>
            <el-menu-item index="/admin/reports/list">报告列表</el-menu-item>
            <el-menu-item index="/admin/reports/audit">报告审核</el-menu-item>
          </el-submenu>

          <!-- 订单管理 -->
          <el-submenu index="orders">
            <template #title>
              <el-icon><Tickets /></el-icon>
              <span>订单管理</span>
            </template>
            <el-menu-item index="/admin/orders/list">订单列表</el-menu-item>
            <el-menu-item index="/admin/orders/ship">发货管理</el-menu-item>
            <el-menu-item index="/admin/orders/statistics">订单统计</el-menu-item>
          </el-submenu>

          <!-- 用户管理 -->
          <el-submenu index="users">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/users/list">用户列表</el-menu-item>
            <el-menu-item index="/admin/users/samplers">采样人管理</el-menu-item>
          </el-submenu>

          <!-- 系统管理 -->
          <el-submenu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin/system/cities">城市管理</el-menu-item>
            <el-menu-item index="/admin/system/companies">公司管理</el-menu-item>
            <el-menu-item index="/admin/system/logs">操作日志</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-container class="main-container">
        <!-- 顶部导航 -->
        <el-header class="main-header">
          <div class="header-left">
            <el-button
              :icon="isCollapse ? Expand : Fold"
              @click="toggleSidebar"
              text
              size="large"
            />
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-for="item in breadcrumb" :key="item.path" :to="item.path">
                {{ item.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>

          <div class="header-right">
            <el-badge :value="notificationCount" class="notification-badge">
              <el-button :icon="Bell" text size="large" />
            </el-badge>
            
            <el-dropdown @command="handleCommand" trigger="click">
              <span class="admin-user">
                <el-avatar :size="32" :src="adminUser.avatar || '/images/default-avatar.jpg'">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ adminUser.name || '管理员' }}</span>
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="settings">设置</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 页面内容 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>

    <!-- 上传报告对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传检测报告"
      width="800px"
      destroy-on-close
    >
      <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单号" prop="orderNo">
              <el-select v-model="uploadForm.orderNo" placeholder="选择订单" filterable>
                <el-option
                  v-for="order in orders"
                  :key="order.id"
                  :label="`${order.orderNo} - ${order.packageName}`"
                  :value="order.orderNo"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采样人" prop="samplerId">
              <el-select v-model="uploadForm.samplerId" placeholder="选择采样人">
                <el-option
                  v-for="sampler in samplers"
                  :key="sampler.id"
                  :label="`${sampler.name} (${sampler.phone})`"
                  :value="sampler.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报告类型" prop="reportType">
              <el-select v-model="uploadForm.reportType" placeholder="选择报告类型">
                <el-option label="检验报告" :value="1" />
                <el-option label="健康建议" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急程度" prop="urgencyLevel">
              <el-radio-group v-model="uploadForm.urgencyLevel">
                <el-radio :label="1">普通</el-radio>
                <el-radio :label="2">重要</el-radio>
                <el-radio :label="3">紧急</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="报告标题" prop="title">
          <el-input v-model="uploadForm.title" placeholder="请输入报告标题" />
        </el-form-item>

        <el-form-item label="报告文件" prop="file">
          <el-upload
            ref="uploadRef"
            :action="uploadAction"
            :headers="uploadHeaders"
            :data="uploadForm"
            :before-upload="beforeUpload"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-preview="handleFilePreview"
            accept=".pdf,.doc,.docx"
            drag
          >
            <el-icon class="el-icon--upload">
              <upload-filled />
            </el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持pdf、doc、docx格式，文件大小不超过20MB
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="检验结果概要">
          <el-input
            v-model="uploadForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入检验结果概要"
          />
        </el-form-item>

        <el-form-item label="健康建议">
          <el-input
            v-model="uploadForm.healthAdvice"
            type="textarea"
            :rows="3"
            placeholder="请输入健康建议"
          />
        </el-form-item>

        <el-form-item label="异常指标">
          <el-input
            v-model="uploadForm.abnormalIndicators"
            type="textarea"
            :rows="2"
            placeholder="如有异常指标请填写"
          />
        </el-form-item>

        <el-form-item label="复检建议">
          <el-input
            v-model="uploadForm.retestAdvice"
            type="textarea"
            :rows="2"
            placeholder="如需复检请填写复检建议"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="uploadForm.remark"
            type="textarea"
            :rows="2"
            placeholder="其他备注信息"
          />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="uploadForm.isPublic">公开报告</el-checkbox>
          <el-checkbox v-model="uploadForm.isUrgent">紧急处理</el-checkbox>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload" :loading="uploading">
            上传
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {
  Monitor,
  ShoppingBag,
  Document,
  Tickets,
  User,
  Setting,
  Bell,
  ArrowDown,
  Expand,
  Fold,
  UploadFilled
} from '@element-plus/icons-vue'

export default {
  name: 'AdminLayout',
  
  components: {
    Monitor,
    ShoppingBag,
    Document,
    Tickets,
    User,
    Setting,
    Bell,
    ArrowDown,
    Expand,
    Fold,
    UploadFilled
  },

  data() {
    return {
      isCollapse: false,
      sidebarWidth: '200px',
      notificationCount: 3,
      adminUser: {
        name: '管理员',
        avatar: null
      },
      uploadDialogVisible: false,
      uploading: false,
      uploadForm: {
        orderNo: '',
        samplerId: '',
        reportType: 1,
        urgencyLevel: 1,
        title: '',
        summary: '',
        healthAdvice: '',
        abnormalIndicators: '',
        retestAdvice: '',
        remark: '',
        isPublic: false,
        isUrgent: false,
        file: null
      },
      uploadRules: {
        orderNo: [{ required: true, message: '请选择订单', trigger: 'change' }],
        samplerId: [{ required: true, message: '请选择采样人', trigger: 'change' }],
        title: [{ required: true, message: '请输入报告标题', trigger: 'blur' }],
        file: [{ required: true, message: '请上传报告文件', trigger: 'change' }]
      },
      orders: [],
      samplers: []
    }
  },

  computed: {
    currentRoute() {
      return this.$route.path
    },
    
    breadcrumb() {
      const route = this.$route
      const matched = route.matched.filter(item => item.meta && item.meta.title)
      const breadcrumb = matched.map(item => ({
        title: item.meta.title,
        path: item.path
      }))
      return breadcrumb.slice(1)
    },

    uploadAction() {
      return `${this.$config.baseURL}/admin/reports/upload`
    },

    uploadHeaders() {
      return {
        'Authorization': `Bearer ${this.$store.getters.token}`
      }
    }
  },

  watch: {
    isCollapse(newVal) {
      this.sidebarWidth = newVal ? '64px' : '200px'
    }
  },

  methods: {
    toggleSidebar() {
      this.isCollapse = !this.isCollapse
    },

    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/admin/profile')
          break
        case 'settings':
          this.$router.push('/admin/settings')
          break
        case 'logout':
          this.handleLogout()
          break
      }
    },

    async handleLogout() {
      try {
        await this.$store.dispatch('logout')
        this.$router.push('/login')
        this.$message.success('退出成功')
      } catch (error) {
        this.$message.error('退出失败')
      }
    },

    // 上传报告相关方法
    openUploadDialog() {
      this.uploadDialogVisible = true
      this.loadOrders()
      this.loadSamplers()
    },

    async loadOrders() {
      try {
        const response = await this.$api.admin.getOrders({ page: 1, size: 100 })
        this.orders = response.data.records || []
      } catch (error) {
        console.error('加载订单失败:', error)
      }
    },

    async loadSamplers() {
      try {
        const response = await this.$api.admin.getSamplers()
        this.samplers = response.data || []
      } catch (error) {
        console.error('加载采样人失败:', error)
      }
    },

    beforeUpload(file) {
      const isValidType = ['application/pdf', 'application/msword', 
                        'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
      const isLt20M = file.size / 1024 / 1024 < 20

      if (!isValidType) {
        this.$message.error('只能上传 PDF、DOC、DOCX 格式的文件!')
      }
      if (!isLt20M) {
        this.$message.error('文件大小不能超过 20MB!')
      }
      
      if (isValidType && isLt20M) {
        this.uploadForm.file = file
      }
      
      return (isValidType && isLt20M)
    },

    handleUploadSuccess(response, file) {
      if (response.success) {
        this.$message.success('报告上传成功')
        this.uploadDialogVisible = false
        this.resetUploadForm()
        // 刷新报告列表
        this.$emit('upload-success')
      } else {
        this.$message.error(response.message || '上传失败')
      }
    },

    handleUploadError(error) {
      this.$message.error('上传失败: ' + error.message)
    },

    handleFilePreview(file) {
      // 预览文件
      window.open(file.url, '_blank')
    },

    submitUpload() {
      this.$refs.uploadFormRef.validate((valid) => {
        if (valid) {
          this.uploading = true
          // 实际上传通过Upload组件的action属性完成
          this.$nextTick(() => {
            this.$refs.uploadRef.submit()
          })
        } else {
          this.$message.error('请填写完整信息')
        }
      })
    },

    resetUploadForm() {
      this.uploadForm = {
        orderNo: '',
        samplerId: '',
        reportType: 1,
        urgencyLevel: 1,
        title: '',
        summary: '',
        healthAdvice: '',
        abnormalIndicators: '',
        retestAdvice: '',
        remark: '',
        isPublic: false,
        isUrgent: false,
        file: null
      }
      this.$refs.uploadFormRef?.resetFields()
      this.$refs.uploadRef?.clearFiles()
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-container {
  height: 100vh;
}

.admin-layout {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;

  .sidebar-header {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    border-bottom: 1px solid #3d5362;

    .logo {
      width: 32px;
      height: 32px;
      margin-right: 12px;
    }

    .system-name {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      margin: 0;
    }
  }

  .sidebar-menu {
    border: none;
    margin-top: 0;

    :deep(.el-menu-item) {
      &:hover {
        background-color: #3d5362 !important;
      }
    }
  }
}

.main-container {
  display: flex;
  flex-direction: column;
}

.main-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;

  .header-left {
    display: flex;
    align-items: center;

    .el-breadcrumb {
      margin-left: 20px;
    }
  }

  .header-right {
    display: flex;
    align-items: center;

    .notification-badge {
      margin-right: 20px;
    }

    .admin-user {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 8px 12px;
      border-radius: 6px;
      transition: background-color 0.3s;

      &:hover {
        background-color: #f5f5f5;
      }

      .username {
        margin: 0 8px;
        color: #333;
      }
    }
  }
}

.main-content {
  background-color: #f0f2f5;
  padding: 24px;
  overflow-y: auto;
}

.dialog-footer {
  text-align: right;
}

// 上传组件样式
:deep(.el-upload) {
  width: 100%;

  .el-upload-dragger {
    width: 100%;
    height: 180px;
  }
}

:deep(.el-upload__tip) {
  color: #666;
  font-size: 12px;
  margin-top: 8px;
}
</style>