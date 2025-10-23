<template>
  <div :style="{ 
    background: `url(${bgImage}) center/cover no-repeat, #f5f5f5`, 
    minHeight: '100vh', 
    display: 'flex', 
    alignItems: 'center', 
    justifyContent: 'center', 
    position: 'relative', 
    overflow: 'hidden' 
  }">
    <div :style="{ 
      background: 'rgba(255, 255, 255, 0.95)', 
      borderRadius: '8px', 
      boxShadow: '0 8px 32px rgba(0, 0, 0, 0.12)', 
      border: '1px solid #e8e8e8', 
      width: '480px', 
      maxWidth: '95vw', 
      padding: '60px 50px', 
      position: 'relative', 
      zIndex: 1,
      backdropFilter: 'blur(10px)'
    }">
      <!-- 登录表单 -->
      <div :style="{ 
        display: 'flex', 
        flexDirection: 'column', 
        justifyContent: 'center' 
      }">
        <div :style="{ textAlign: 'center', marginBottom: '40px' }">
          <h1 :style="{ fontSize: '32px', fontWeight: 'bold', color: '#000', marginBottom: '8px' }">智能图像识别系统</h1>
          <p :style="{ color: '#666', fontSize: '14px' }">基于深度学习的通用图像识别平台</p>
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
                  :style="{ width: '100%', height: '100%', objectFit: 'contain', borderRadius: '6px' }"
                />
                <LoadingOutlined v-else :spin="true" />
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
import { useUserStore } from '@/stores/user'
import { 
  UserOutlined, 
  LockOutlined, 
  SafetyOutlined, 
  LoadingOutlined
} from '@ant-design/icons-vue'
import bgImage from '@/assets/iamges/bg.png'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaId = ref('')
const captchaImageUrl = ref('')

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
    const result = await AuthAPI.getCaptcha()
    if (result) {
      captchaId.value = result.captchaId
      // 服务端返回的图片已包含 data:image/png;base64, 前缀
      captchaImageUrl.value = result.captchaImage
    }
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
      captchaId: captchaId.value,
      captchaCode: formData.captcha
    })

    if (result && result.token && result.user) {
      message.success('登录成功')
      
      // 使用user store保存登录状态
      userStore.setLoginInfo(result.token, {
        id: result.user.id,
        username: result.user.username,
        nickname: result.user.nickname,
        email: result.user.email,
        phone: result.user.phone,
        avatar: result.user.avatar,
        role: result.user.role,
        vipLevel: result.user.vipLevel,
        vipExpireTime: result.user.vipExpireTime
      })
      
      if (formData.rememberMe) {
        localStorage.setItem('rememberedUser', JSON.stringify({
          username: formData.username
        }))
      }
      
      // 根据用户角色跳转 (0=普通用户, 1=管理员)
      const isAdmin = result.user.role === 1
      const redirectPath = isAdmin ? '/dashboard' : '/user/dashboard'
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
