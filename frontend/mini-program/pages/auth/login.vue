<template>
  <view class="login-page">
    <view class="login-container">
      <!-- Logo区域 -->
      <view class="logo-section">
        <image class="app-logo" src="/static/images/logo.png" mode="aspectFit" />
        <text class="app-name">灵力检测</text>
        <text class="app-slogan">专业的体检检测服务平台</text>
      </view>

      <!-- 登录表单 -->
      <view class="form-section">
        <view class="form-tabs">
          <view class="tab-item" :class="{ active: loginType === 'username' }" @click="switchLoginType('username')">
            账户登录
          </view>
          <view class="tab-item" :class="{ active: loginType === 'phone' }" @click="switchLoginType('phone')">
            手机登录
          </view>
        </view>

        <form class="login-form" @submit="handleLogin">
          <!-- 用户名登录 -->
          <view class="input-group" v-if="loginType === 'username'">
            <view class="input-item">
              <text class="input-icon">👤</text>
              <input 
                class="input-field" 
                type="text" 
                placeholder="请输入用户名" 
                v-model="loginForm.username"
                maxlength="20"
              />
            </view>
            
            <view class="input-item">
              <text class="input-icon">🔒</text>
              <input 
                class="input-field" 
                :type="showPassword ? 'text' : 'password'" 
                placeholder="请输入密码" 
                v-model="loginForm.password"
                maxlength="20"
              />
              <text class="password-toggle" @click="togglePassword">
                {{ showPassword ? '👁️' : '👁️‍🗨️' }}
              </text>
            </view>
          </view>

          <!-- 手机号登录 -->
          <view class="input-group" v-else>
            <view class="input-item">
              <text class="input-icon">📱</text>
              <input 
                class="input-field" 
                type="number" 
                placeholder="请输入手机号" 
                v-model="loginForm.phone"
                maxlength="11"
              />
            </view>
            
            <view class="input-item">
              <text class="input-icon">🔐</text>
              <input 
                class="input-field sms-input" 
                type="number" 
                placeholder="请输入验证码" 
                v-model="loginForm.smsCode"
                maxlength="6"
              />
              <button 
                class="sms-btn" 
                :disabled="smsCountdown > 0 || !isValidPhone"
                @click="sendSmsCode"
              >
                {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
              </button>
            </view>
          </view>

          <!-- 记住密码 -->
          <view class="remember-section">
            <label class="checkbox-label">
              <checkbox 
                class="checkbox" 
                :checked="loginForm.rememberMe"
                @click="toggleRemember"
              />
              <text class="checkbox-text">记住密码</text>
            </label>
            
            <text class="forgot-password" @click="goToForgotPassword">
              忘记密码？
            </text>
          </view>

          <!-- 登录按钮 -->
          <button class="login-btn" form-type="submit" :disabled="!canLogin">
            {{ loginType === 'phone' ? '验证码登录' : '登录' }}
          </button>
        </form>

        <!-- 第三方登录 -->
        <view class="third-party-section">
          <view class="divider">
            <text class="divider-text">或</text>
          </view>
          
          <view class="third-party-options">
            <button class="third-party-btn wechat-btn" @click="loginWithWechat">
              <text class="third-party-icon">📱</text>
              <text class="third-party-text">微信登录</text>
            </button>
            
            <button class="third-party-btn qq-btn" @click="loginWithQQ">
              <text class="third-party-icon">🐧</text>
              <text class="third-party-text">QQ登录</text>
            </button>
          </view>
        </view>
      </view>

      <!-- 注册链接 -->
      <view class="register-section">
        <text class="register-text">还没有账号？</text>
        <text class="register-link" @click="goToRegister">立即注册</text>
      </view>
    </view>

    <!-- 服务协议 -->
    <view class="agreement-section">
      <text class="agreement-text">
        登录即表示同意
        <text class="agreement-link" @click="viewUserAgreement">《用户协议》</text>
        和
        <text class="agreement-link" @click="viewPrivacyPolicy">《隐私政策》</text>
      </text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'LoginPage',
  
  data() {
    return {
      loginType: 'username', // username | phone
      showPassword: false,
      loginForm: {
        username: '',
        password: '',
        phone: '',
        smsCode: '',
        rememberMe: false
      },
      smsCountdown: 0,
      smsTimer: null
    }
  },
  
  computed: {
    // 是否可以登录
    canLogin() {
      if (this.loginType === 'username') {
        return this.loginForm.username.trim() && this.loginForm.password.trim()
      } else {
        return this.isValidPhone && this.loginForm.smsCode.trim().length === 6
      }
    },
    
    // 是否为有效手机号
    isValidPhone() {
      return /^1[3-9]\d{9}$/.test(this.loginForm.phone)
    }
  },
  
  onLoad() {
    // 检查是否已登录
    this.checkLoginStatus()
    
    // 加载记住的账号信息
    this.loadRememberedAccount()
  },
  
  onUnload() {
    // 清除定时器
    if (this.smsTimer) {
      clearInterval(this.smsTimer)
    }
  },
  
  methods: {
    // 检查登录状态
    checkLoginStatus() {
      const token = uni.getStorageSync('access_token')
      if (token) {
        uni.showToast({
          title: '已登录',
          icon: 'success'
        })
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          })
        }, 1000)
      }
    },
    
    // 加载记住的账号信息
    loadRememberedAccount() {
      const remembered = uni.getStorageSync('remembered_account')
      if (remembered) {
        this.loginForm.username = remembered.username
        this.loginForm.password = remembered.password
        this.loginForm.rememberMe = true
      }
    },
    
    // 切换登录方式
    switchLoginType(type) {
      if (this.loginType === type) return
      
      this.loginType = type
      
      // 清空表单
      if (type === 'username') {
        this.loginForm.phone = ''
        this.loginForm.smsCode = ''
      } else {
        this.loginForm.username = ''
        this.loginForm.password = ''
      }
    },
    
    // 切换密码显示
    togglePassword() {
      this.showPassword = !this.showPassword
    },
    
    // 切换记住密码
    toggleRemember() {
      this.loginForm.rememberMe = !this.loginForm.rememberMe
      
      if (!this.loginForm.rememberMe) {
        // 清除记住的账号信息
        uni.removeStorageSync('remembered_account')
      }
    },
    
    // 处理登录
    async handleLogin(e) {
      e.preventDefault()
      
      if (!this.canLogin) return
      
      try {
        uni.showLoading({
          title: '登录中...',
          mask: true
        })
        
        let loginData
        if (this.loginType === 'username') {
          loginData = {
            username: this.loginForm.username.trim(),
            password: this.loginForm.password
          }
        } else {
          loginData = {
            username: this.loginForm.phone,
            smsCode: this.loginForm.smsCode.trim()
          }
        }
        
        const response = await this.$api.auth.login(loginData)
        
        if (response.success) {
          // 保存token
          const { token, refreshToken } = response.data
          uni.setStorageSync('access_token', token)
          uni.setStorageSync('refresh_token', refreshToken)
          uni.setStorageSync('user_info', response.data)
          
          // 如果记住密码，保存账号信息
          if (this.loginForm.rememberMe && this.loginType === 'username') {
            uni.setStorageSync('remembered_account', {
              username: this.loginForm.username,
              password: this.loginForm.password
            })
          } else {
            uni.removeStorageSync('remembered_account')
          }
          
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          })
          
          // 触发登录成功事件
          uni.$emit('login_success')
          
          // 延迟跳转到首页
          setTimeout(() => {
            const pages = getPages()
            const indexPage = pages.find(p => p.route === 'pages/index/index')
            if (indexPage) {
              uni.switchTab({
                url: '/pages/index/index'
              })
            } else {
              uni.navigateBack({
                delta: 1
              })
            }
          }, 1500)
        }
        
      } catch (error) {
        console.error('登录失败:', error)
        uni.showToast({
          title: error.message || '登录失败',
          icon: 'none'
        })
      } finally {
        uni.hideLoading()
      }
    },
    
    // 发送验证码
    async sendSmsCode() {
      if (!this.isValidPhone) {
        uni.showToast({
          title: '请输入有效的手机号',
          icon: 'none'
        })
        return
      }
      
      try {
        uni.showLoading({
          title: '发送中...',
          mask: true
        })
        
        const response = await this.$api.auth.sendSmsCode({
          phone: this.loginForm.phone,
          type: 'login'
        })
        
        if (response.success) {
          uni.showToast({
            title: '验证码已发送',
            icon: 'success'
          })
          
          // 开始倒计时
          this.startSmsCountdown()
        }
        
      } catch (error) {
        console.error('发送验证码失败:', error)
        uni.showToast({
          title: error.message || '发送失败',
          icon: 'none'
        })
      } finally {
        uni.hideLoading()
      }
    },
    
    // 开始短信倒计时
    startSmsCountdown() {
      this.smsCountdown = 60
      
      this.smsTimer = setInterval(() => {
        this.smsCountdown--
        if (this.smsCountdown <= 0) {
          clearInterval(this.smsTimer)
          this.smsTimer = null
        }
      }, 1000)
    },
    
    // 微信登录
    loginWithWechat() {
      uni.showToast({
        title: '微信登录功能开发中',
        icon: 'none'
      })
    },
    
    // QQ登录
    loginWithQQ() {
      uni.showToast({
        title: 'QQ登录功能开发中',
        icon: 'none'
      })
    },
    
    // 页面跳转
    goToRegister() {
      uni.navigateTo({
        url: '/pages/auth/register'
      })
    },
    
    goToForgotPassword() {
      uni.navigateTo({
        url: '/pages/auth/forgot-password'
      })
    },
    
    viewUserAgreement() {
      uni.navigateTo({
        url: '/pages/help/user-agreement'
      })
    },
    
    viewPrivacyPolicy() {
      uni.navigateTo({
        url: '/pages/help/privacy-policy'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  .login-container {
    flex: 1;
    padding: 60rpx 40rpx 40rpx;
  }

  .logo-section {
    text-align: center;
    margin-bottom: 80rpx;

    .app-logo {
      width: 120rpx;
      height: 120rpx;
      margin-bottom: 30rpx;
    }

    .app-name {
      display: block;
      font-size: 48rpx;
      font-weight: bold;
      color: #ffffff;
      margin-bottom: 15rpx;
    }

    .app-slogan {
      display: block;
      font-size: 28rpx;
      color: rgba(255, 255, 255, 0.8);
    }
  }

  .form-section {
    background: #ffffff;
    border-radius: 20rpx;
    padding: 40rpx;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);

    .form-tabs {
      display: flex;
      margin-bottom: 40rpx;
      background-color: #f8f9fa;
      border-radius: 12rpx;
      padding: 8rpx;

      .tab-item {
        flex: 1;
        text-align: center;
        padding: 20rpx;
        border-radius: 8rpx;
        font-size: 28rpx;
        color: #666;
        transition: all 0.3s ease;

        &.active {
          background-color: #4a90e2;
          color: #ffffff;
          font-weight: bold;
        }
      }
    }

    .login-form {
      .input-group {
        .input-item {
          position: relative;
          display: flex;
          align-items: center;
          margin-bottom: 30rpx;
          padding: 20rpx;
          background-color: #f8f9fa;
          border-radius: 12rpx;
          border: 2rpx solid transparent;
          transition: all 0.3s ease;

          &:focus-within {
            border-color: #4a90e2;
            background-color: #ffffff;
          }

          .input-icon {
            font-size: 32rpx;
            margin-right: 20rpx;
            width: 40rpx;
            text-align: center;
          }

          .input-field {
            flex: 1;
            border: none;
            background: transparent;
            font-size: 28rpx;
            color: #333;

            &::placeholder {
              color: #999;
            }
          }

          .password-toggle {
            font-size: 28rpx;
            padding: 10rpx;
            cursor: pointer;
          }

          .sms-input {
            flex: 1;
          }

          .sms-btn {
            background-color: #4a90e2;
            color: #ffffff;
            border: none;
            border-radius: 8rpx;
            padding: 15rpx 25rpx;
            font-size: 24rpx;
            margin-left: 20rpx;

            &:disabled {
              background-color: #ccc;
            }
          }
        }
      }

      .remember-section {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 40rpx;

        .checkbox-label {
          display: flex;
          align-items: center;
          cursor: pointer;

          .checkbox {
            transform: scale(0.8);
            margin-right: 10rpx;
          }

          .checkbox-text {
            font-size: 26rpx;
            color: #666;
          }
        }

        .forgot-password {
          font-size: 26rpx;
          color: #4a90e2;
          cursor: pointer;
        }
      }

      .login-btn {
        width: 100%;
        background: linear-gradient(45deg, #4a90e2, #357abd);
        color: #ffffff;
        border: none;
        border-radius: 12rpx;
        padding: 25rpx;
        font-size: 32rpx;
        font-weight: bold;
        margin-bottom: 40rpx;
        transition: all 0.3s ease;

        &:disabled {
          background: #ccc;
        }

        &:active:not(:disabled) {
          transform: scale(0.98);
        }
      }
    }

    .third-party-section {
      .divider {
        position: relative;
        text-align: center;
        margin: 40rpx 0;

        &::before {
          content: '';
          position: absolute;
          top: 50%;
          left: 0;
          right: 0;
          height: 1rpx;
          background-color: #e0e0e0;
        }

        .divider-text {
          background-color: #ffffff;
          padding: 0 30rpx;
          font-size: 24rpx;
          color: #999;
        }
      }

      .third-party-options {
        display: flex;
        gap: 30rpx;

        .third-party-btn {
          flex: 1;
          display: flex;
          flex-direction: column;
          align-items: center;
          background-color: #f8f9fa;
          border: none;
          border-radius: 12rpx;
          padding: 30rpx 20rpx;

          .third-party-icon {
            font-size: 48rpx;
            margin-bottom: 15rpx;
          }

          .third-party-text {
            font-size: 26rpx;
            color: #333;
          }

          &.wechat-btn {
            border: 2rpx solid #07c160;

            .third-party-icon {
              color: #07c160;
            }
          }

          &.qq-btn {
            border: 2rpx solid #12b7f5;

            .third-party-icon {
              color: #12b7f5;
            }
          }
        }
      }
    }
  }

  .register-section {
    text-align: center;
    margin-top: 40rpx;

    .register-text {
      font-size: 26rpx;
      color: rgba(255, 255, 255, 0.8);
    }

    .register-link {
      font-size: 26rpx;
      color: #ffffff;
      font-weight: bold;
      margin-left: 10rpx;
      text-decoration: underline;
      cursor: pointer;
    }
  }

  .agreement-section {
    padding: 20rpx 40rpx;
    text-align: center;

    .agreement-text {
      font-size: 22rpx;
      color: rgba(255, 255, 255, 0.7);
      line-height: 1.5;

      .agreement-link {
        color: #ffffff;
        text-decoration: underline;
        cursor: pointer;
      }
    }
  }
}
</style>