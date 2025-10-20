<template>
  <div :style="{ 
    background: '#f5f5f5', 
    minHeight: '100vh', 
    display: 'flex', 
    alignItems: 'center', 
    justifyContent: 'center', 
    position: 'relative', 
    overflow: 'hidden' 
  }">
    <div :style="{ 
      background: 'white', 
      borderRadius: '20px', 
      boxShadow: '0 8px 32px rgba(0, 0, 0, 0.12)', 
      border: '1px solid #e8e8e8', 
      width: '900px', 
      maxWidth: '95vw', 
      minHeight: '600px', 
      display: 'flex', 
      overflow: 'hidden', 
      position: 'relative', 
      zIndex: 1 
    }">
      <!-- 左侧信息区域 -->
      <div :style="{ 
        flex: 1, 
        background: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)', 
        padding: '60px 40px', 
        display: 'flex', 
        flexDirection: 'column', 
        justifyContent: 'center', 
        alignItems: 'center', 
        color: 'white', 
        position: 'relative' 
      }">
        <h1 :style="{ fontSize: '28px', fontWeight: 'bold', marginBottom: '16px', textAlign: 'center' }">智能图像识别系统</h1>
        <p :style="{ fontSize: '16px', opacity: 0.9, textAlign: 'center', lineHeight: 1.6, maxWidth: '300px' }">
          基于深度学习的通用图像识别平台，提供识别、学习、交流一体化服务
        </p>
        
        <ul :style="{ marginTop: '40px', listStyle: 'none', padding: 0 }">
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <ThunderboltOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>毫秒级识别响应</span>
          </li>
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <BulbOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>智能知识延展</span>
          </li>
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <TeamOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>社区互动交流</span>
          </li>
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <SafetyOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>企业级安全保障</span>
          </li>
        </ul>
      </div>
      
      <!-- 右侧登录表单 -->
      <div :style="{ 
        flex: 1, 
        padding: '60px 40px', 
        display: 'flex', 
        flexDirection: 'column', 
        justifyContent: 'center' 
      }">
        <div :style="{ textAlign: 'center', marginBottom: '40px' }">
          <h2 :style="{ fontSize: '24px', fontWeight: 'bold', color: '#262626', marginBottom: '8px' }">欢迎回来</h2>
          <p :style="{ color: '#666', fontSize: '14px' }">请登录您的账户继续使用服务</p>
        </div>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          :style="{ maxWidth: '320px', margin: '0 auto', width: '100%' }"
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
                <UserOutlined />
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
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>
          
          <!-- 验证码输入 -->
          <a-form-item name="captcha">
            <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
              <a-input
                v-model:value="formData.captcha"
                size="large"
                placeholder="请输入验证码"
                :maxlength="4"
                :style="{ flex: 1 }"
              >
                <template #prefix>
                  <SafetyOutlined />
                </template>
              </a-input>
              <div 
                :style="{ 
                  width: '100px', 
                  height: '40px', 
                  border: '2px solid #e8e8e8', 
                  borderRadius: '8px', 
                  cursor: 'pointer', 
                  background: '#f5f5f5', 
                  display: 'flex', 
                  alignItems: 'center', 
                  justifyContent: 'center', 
                  fontSize: '16px', 
                  fontWeight: 'bold', 
                  color: '#1890ff', 
                  userSelect: 'none', 
                  transition: 'all 0.3s', 
                  overflow: 'hidden' 
                }"
                @click="refreshCaptcha"
              >
                <img 
                  v-if="captchaImageUrl" 
                  :src="captchaImageUrl" 
                  alt="验证码"
                  :key="captchaKey"
                  :style="{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '6px' }"
                />
                <div v-else>
                  <LoadingOutlined :spin="true" />
                </div>
              </div>
            </div>
          </a-form-item>
          
          <!-- 表单选项 -->
          <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }">
            <a-checkbox v-model:checked="formData.rememberMe">记住我</a-checkbox>
            <router-link to="/forgot-password" :style="{ color: '#1890ff', textDecoration: 'none' }">忘记密码？</router-link>
          </div>
          
          <!-- 登录按钮 -->
          <a-form-item>
            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              :loading="loading"
              :style="{ background: '#1890ff', border: 'none', height: '48px', marginBottom: '16px' }"
            >
              立即登录
            </a-button>
          </a-form-item>
          
          <!-- 第三方登录 -->
          <a-divider>或使用第三方账户登录</a-divider>
          
          <div :style="{ display: 'flex', gap: '12px', marginBottom: '32px' }">
            <a-button 
              :style="{ flex: 1, height: '48px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }"
              @click="socialLogin('github')"
            >
              <GithubOutlined />
              GitHub
            </a-button>
            <a-button 
              :style="{ flex: 1, height: '48px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }"
              @click="socialLogin('gitee')"
            >
              <BranchesOutlined />
              Gitee
            </a-button>
          </div>
          
          <!-- 注册链接 -->
          <div :style="{ textAlign: 'center', color: '#666', fontSize: '14px' }">
            还没有账户？<router-link to="/register" :style="{ color: '#1890ff', textDecoration: 'none', fontWeight: 500 }">立即注册</router-link>
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
import { 
  UserOutlined, 
  LockOutlined, 
  SafetyOutlined, 
  LoadingOutlined, 
  GithubOutlined, 
  BranchesOutlined,
  ThunderboltOutlined,
  BulbOutlined,
  TeamOutlined
} from '@ant-design/icons-vue'

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
