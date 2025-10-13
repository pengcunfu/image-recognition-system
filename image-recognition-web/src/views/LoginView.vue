<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="bg-animation">
      <div class="floating-shape">
        <i class="fas fa-brain"></i>
      </div>
      <div class="floating-shape">
        <i class="fas fa-eye"></i>
      </div>
      <div class="floating-shape">
        <i class="fas fa-camera"></i>
      </div>
    </div>

    <div class="login-box">
      <!-- 左侧信息区域 -->
      <div class="login-info">
        <!-- <div class="system-logo">
          <i class="fas fa-eye"></i>
        </div> -->
        <h1 class="system-title">智能图像识别系统</h1>
        <p class="system-subtitle">基于深度学习的通用图像识别平台，提供识别、学习、交流一体化服务</p>
        
        <ul class="feature-list">
          <li class="feature-item">
            <i class="fas fa-zap"></i>
            <span>毫秒级识别响应</span>
          </li>
          <li class="feature-item">
            <i class="fas fa-brain"></i>
            <span>智能知识延展</span>
          </li>
          <li class="feature-item">
            <i class="fas fa-users"></i>
            <span>社区互动交流</span>
          </li>
          <li class="feature-item">
            <i class="fas fa-shield-alt"></i>
            <span>企业级安全保障</span>
          </li>
        </ul>
      </div>
      
      <!-- 右侧登录表单 -->
      <div class="login-form-container">
        <div class="form-header">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的账户继续使用服务</p>
        </div>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          class="login-form"
          @finish="handleLogin"
        >
          <!-- 用户名/邮箱输入 -->
          <a-form-item name="username">
            <a-input
              v-model:value="formData.username"
              size="large"
              placeholder="请输入用户名或邮箱"
            >
              <template #prefix>
                <i class="fas fa-user"></i>
              </template>
            </a-input>
          </a-form-item>
          
          <!-- 密码输入 -->
          <a-form-item name="password">
            <a-input-password
              v-model:value="formData.password"
              size="large"
              placeholder="请输入密码"
            >
              <template #prefix>
                <i class="fas fa-lock"></i>
              </template>
            </a-input-password>
          </a-form-item>
          
          <!-- 验证码输入 -->
          <a-form-item name="captcha">
            <div class="captcha-group">
              <a-input
                v-model:value="formData.captcha"
                size="large"
                placeholder="请输入验证码"
                :maxlength="4"
                class="captcha-input"
              >
                <template #prefix>
                  <i class="fas fa-shield-alt"></i>
                </template>
              </a-input>
              <div class="captcha-image" @click="refreshCaptcha">
                <img 
                  v-if="captchaImageUrl" 
                  :src="captchaImageUrl" 
                  alt="验证码"
                  :key="captchaKey"
                />
                <div v-else class="captcha-loading">
                  <i class="fas fa-spinner fa-spin"></i>
                </div>
              </div>
            </div>
          </a-form-item>
          
          <!-- 表单选项 -->
          <div class="form-options">
            <a-checkbox v-model:checked="formData.rememberMe">记住我</a-checkbox>
            <router-link to="/forgot-password" class="forgot-password">忘记密码？</router-link>
          </div>
          
          <!-- 登录按钮 -->
          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
              class="login-btn"
            >
              立即登录
            </a-button>
          </a-form-item>
          
          <!-- 第三方登录 -->
          <a-divider>或使用第三方账户登录</a-divider>
          
          <div class="social-login">
            <a-button class="social-btn github" @click="socialLogin('github')">
              <i class="fab fa-github"></i>
              GitHub
            </a-button>
            <a-button class="social-btn gitee" @click="socialLogin('gitee')">
              <i class="fab fa-git-alt"></i>
              Gitee
            </a-button>
          </div>
          
          <!-- 注册链接 -->
          <div class="register-link">
            还没有账户？<router-link to="/register">立即注册</router-link>
          </div>
        </a-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import { AuthAPI } from '@/api/auth'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaImageUrl = ref('')
const captchaKey = ref(0) // 用于强制刷新图片

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  captcha: '',
  rememberMe: false
})

// 验证规则
const rules: Record<string, Rule[]> = {
  username: [
    { required: true, message: '请输入用户名或邮箱' },
    { pattern: /^[a-zA-Z0-9_@.-]+$/, message: '请输入正确的用户名或邮箱格式' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码长度至少6位字符' }
  ],
  captcha: [
    { required: true, message: '请输入验证码' },
    { min: 1, message: '请输入验证码' }
  ]
}

// 获取验证码图片
async function getCaptchaImage() {
  try {
    captchaImageUrl.value = AuthAPI.getCaptchaUrl()
    captchaKey.value = Date.now()
  } catch (error) {
    console.error('获取验证码失败:', error)
    message.error('获取验证码失败，请重试')
  }
}

// 刷新验证码
function refreshCaptcha() {
  getCaptchaImage()
  formData.captcha = ''
}

// 登录处理
async function handleLogin() {
  try {
    loading.value = true
    
    // 调用AuthAPI的login方法
    const result = await AuthAPI.login({
      username: formData.username,
      password: formData.password,
      captcha: formData.captcha
    })

    if (result.data && result.data.token && result.data.user) {
      message.success('登录成功')
      
      // 保存登录状态
      localStorage.setItem('userToken', result.data.token)
      localStorage.setItem('userRole', result.data.user.role || 'user')
      localStorage.setItem('userInfo', JSON.stringify(result.data.user))
      
      if (formData.rememberMe) {
        localStorage.setItem('rememberedUser', JSON.stringify({
          username: formData.username
        }))
      }
      
      // 根据用户角色跳转
      const redirectPath = result.data.user.role === 'admin' ? '/dashboard' : '/user/dashboard'
      router.push(redirectPath)
    } else {
      message.error('登录响应数据不完整')
      refreshCaptcha() // 刷新验证码
    }
  } catch (error: unknown) {
    console.error('登录请求失败:', error)
    const errorMessage = error instanceof Error ? error.message : '网络错误，请重试'
    message.error(errorMessage)
    refreshCaptcha() // 刷新验证码
  } finally {
    loading.value = false
  }
}

// 第三方登录
function socialLogin(provider: string) {
  const providerName = provider === 'github' ? 'GitHub' : 'Gitee'
  message.info(`${providerName}登录功能开发中`)
}

// 页面加载时初始化
onMounted(() => {
  refreshCaptcha()
  
  // 检查是否有记住的用户信息
  const rememberedUser = localStorage.getItem('rememberedUser')
  if (rememberedUser) {
    const userData = JSON.parse(rememberedUser)
    formData.username = userData.username
    formData.rememberMe = true
  }
})
</script>

<style scoped lang="scss">
.login-container {
  background: #f5f5f5;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 动态背景 */
.bg-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.floating-shape {
  position: absolute;
  opacity: 0.06;
  animation: float 6s ease-in-out infinite;
  font-size: 60px;
  color: #1890ff;

  &:nth-child(1) {
    top: 10%;
    left: 10%;
    animation-delay: 0s;
  }

  &:nth-child(2) {
    top: 20%;
    right: 10%;
    animation-delay: 2s;
    font-size: 80px;
  }

  &:nth-child(3) {
    bottom: 10%;
    left: 20%;
    animation-delay: 4s;
    font-size: 50px;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 主容器 */
.login-box {
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid #e8e8e8;
  width: 900px;
  max-width: 95vw;
  min-height: 600px;
  display: flex;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* 左侧信息区域 */
.login-info {
  flex: 1;
  background: #1890ff;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="20" cy="20" r="2" fill="white" opacity="0.1"/><circle cx="80" cy="40" r="3" fill="white" opacity="0.1"/><circle cx="40" cy="80" r="1" fill="white" opacity="0.1"/></svg>');
    animation: twinkle 3s ease-in-out infinite;
  }
}

@keyframes twinkle {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.system-logo {
  font-size: 64px;
  margin-bottom: 32px;
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.system-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 16px;
  text-align: center;
}

.system-subtitle {
  font-size: 16px;
  opacity: 0.9;
  text-align: center;
  line-height: 1.6;
  max-width: 300px;
}

.feature-list {
  margin-top: 40px;
  list-style: none;
}

.feature-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  opacity: 0.9;

  i {
    margin-right: 12px;
    font-size: 16px;
  }
}

/* 右侧登录表单 */
.login-form-container {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-title {
  font-size: 24px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.form-subtitle {
  color: #666;
  font-size: 14px;
}

.login-form {
  max-width: 320px;
  margin: 0 auto;
  width: 100%;
}

/* 验证码组合 */
.captcha-group {
  display: flex;
  gap: 12px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 100px;
  height: 40px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  color: #1890ff;
  user-select: none;
  transition: all 0.3s;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 6px;
  }

  &:hover {
    border-color: #1890ff;
  }
}

.captcha-loading {
  color: #1890ff;
  font-size: 14px;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.forgot-password {
  color: #1890ff;
  text-decoration: none;

  &:hover {
    color: #40a9ff;
  }
}

/* 登录按钮 */
.login-btn {
  background: #1890ff;
  border: none;
  height: 48px;
  margin-bottom: 16px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(24, 144, 255, 0.3);
  }
}

/* 第三方登录 */
.social-login {
  display: flex;
  gap: 12px;
  margin-bottom: 32px;
}

.social-btn {
  flex: 1;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  &.github:hover {
    border-color: #333;
    color: #333;
  }

  &.gitee:hover {
    border-color: #c71d23;
    color: #c71d23;
  }
}

/* 注册链接 */
.register-link {
  text-align: center;
  color: #666;
  font-size: 14px;

  a {
    color: #1890ff;
    text-decoration: none;
    font-weight: 500;

    &:hover {
      color: #40a9ff;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-box {
    flex-direction: column;
    width: 95vw;
    min-height: auto;
  }
  
  .login-info {
    padding: 40px 20px;
    order: 2;
  }
  
  .login-form-container {
    padding: 40px 20px;
    order: 1;
  }
  
  .system-title {
    font-size: 24px;
  }
  
  .system-logo {
    font-size: 48px;
  }
  
  .feature-list {
    margin-top: 20px;
  }
}

@media (max-width: 480px) {
  .login-box {
    margin: 20px;
    border-radius: 16px;
  }
  
  .login-info,
  .login-form-container {
    padding: 30px 20px;
  }
  
  .social-login {
    flex-direction: column;
  }
}
</style>
