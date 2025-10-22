<template>
  <div :style="{ 
    background: `url(${bgImage}) center/cover no-repeat, #f5f5f5`, 
    minHeight: '100vh', 
    display: 'flex', 
    alignItems: 'center', 
    justifyContent: 'center', 
    padding: '20px',
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
      <!-- 注册表单 -->
      <div :style="{ 
        display: 'flex', 
        flexDirection: 'column', 
        justifyContent: 'center'
      }">
        <div :style="{ textAlign: 'center', marginBottom: '32px' }">
          <h1 :style="{ fontSize: '32px', fontWeight: 'bold', color: '#000', marginBottom: '8px' }">
            智能图像识别系统
          </h1>
          <p :style="{ color: '#666', fontSize: '14px', margin: 0 }">
            填写以下信息完成注册
          </p>
        </div>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          :style="{ maxWidth: '320px', margin: '0 auto', width: '100%' }"
          @finish="handleRegister"
        >
          <!-- 用户名输入 -->
          <a-form-item name="username">
            <a-input
              v-model:value="formData.username"
              size="large"
              placeholder="请输入用户名（3-20位字符）"
            >
              <template #prefix>
                <UserOutlined />
              </template>
              <template #suffix>
                <CheckOutlined v-if="usernameValid" :style="{ color: '#52c41a' }" />
              </template>
            </a-input>
          </a-form-item>
          
          <!-- 邮箱输入 -->
          <a-form-item name="email">
            <a-input
              v-model:value="formData.email"
              size="large"
              placeholder="请输入邮箱地址"
              type="email"
            >
              <template #prefix>
                <MailOutlined />
              </template>
              <template #suffix>
                <CheckOutlined v-if="emailValid" :style="{ color: '#52c41a' }" />
              </template>
            </a-input>
          </a-form-item>
          
          <!-- 邮箱验证码 -->
          <a-form-item name="emailCode">
            <div :style="{ display: 'flex', gap: '12px' }">
              <a-input
                v-model:value="formData.emailCode"
                size="large"
                placeholder="请输入邮箱验证码"
                :maxlength="6"
                :style="{ flex: 1 }"
              >
                <template #prefix>
                  <SafetyOutlined />
                </template>
              </a-input>
              <a-button 
                size="large"
                :disabled="codeCountdown > 0 || !emailValid"
                @click="sendEmailCode"
              >
                {{ codeCountdown > 0 ? `${codeCountdown}秒后重发` : '发送验证码' }}
              </a-button>
            </div>
          </a-form-item>
          
          <!-- 密码输入 -->
          <a-form-item name="password">
            <a-input-password
              v-model:value="formData.password"
              size="large"
              placeholder="请设置密码（至少6位）"
              @input="updatePasswordStrength"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
            
            <!-- 密码强度指示器 -->
            <div v-if="formData.password" :style="{ marginTop: '8px' }">
              <div :style="{ fontSize: '12px', color: '#666', marginBottom: '4px' }">
                密码强度：
                <span :style="{ 
                  color: passwordStrength === 'weak' ? '#ff4d4f' : passwordStrength === 'medium' ? '#faad14' : '#52c41a',
                  fontWeight: 500 
                }">{{ strengthText }}</span>
              </div>
              <div :style="{ height: '4px', background: '#f0f0f0', borderRadius: '2px', overflow: 'hidden' }">
                <div :style="{ 
                  height: '100%',
                  width: passwordStrength === 'weak' ? '33%' : passwordStrength === 'medium' ? '66%' : '100%',
                  background: passwordStrength === 'weak' ? '#ff4d4f' : passwordStrength === 'medium' ? '#faad14' : '#52c41a',
                  transition: 'all 0.3s ease'
                }"></div>
              </div>
            </div>
          </a-form-item>
          
          <!-- 确认密码 -->
          <a-form-item name="confirmPassword">
            <a-input-password
              v-model:value="formData.confirmPassword"
              size="large"
              placeholder="请再次输入密码"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>
          
          <!-- 注册按钮 -->
          <a-form-item>
            <a-button 
              type="primary" 
              size="large" 
              block
              html-type="submit"
              :loading="loading"
              :style="{ height: '48px', fontSize: '16px', fontWeight: 500 }"
            >
              完成注册
            </a-button>
          </a-form-item>
        </a-form>
        
        <!-- 登录链接 -->
        <div :style="{ textAlign: 'center', color: '#666', fontSize: '14px', marginTop: '16px' }">
          已有账户？<router-link to="/login" :style="{ color: '#1890ff', textDecoration: 'none', fontWeight: 500 }">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import { AuthAPI } from '@/api/auth'
import {
  UserOutlined,
  MailOutlined,
  LockOutlined,
  SafetyOutlined,
  CheckOutlined
} from '@ant-design/icons-vue'
import bgImage from '@/assets/iamges/bg.png'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const codeCountdown = ref(0)
const passwordStrength = ref('weak')

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  emailCode: ''
})

// 计算属性
const usernameValid = computed(() => {
  return formData.username.length >= 3 && /^[a-zA-Z0-9_]{3,20}$/.test(formData.username)
})

const emailValid = computed(() => {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)
})

const strengthText = computed(() => {
  switch (passwordStrength.value) {
    case 'weak': return '弱'
    case 'medium': return '中'
    case 'strong': return '强'
    default: return '弱'
  }
})

// 验证规则
const rules: Record<string, Rule[]> = {
  username: [
    { required: true, message: '请输入用户名' },
    { pattern: /^[a-zA-Z0-9_]{3,20}$/, message: '用户名长度为3-20位，只能包含字母、数字和下划线' }
  ],
  password: [
    { required: true, message: '请设置密码' },
    { min: 6, message: '密码长度至少6位，建议包含字母、数字和特殊字符' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: validateConfirmPassword }
  ],
  email: [
    { required: true, message: '请输入邮箱地址' },
    { type: 'email', message: '请输入正确的邮箱地址格式' }
  ],
  emailCode: [
    { required: true, message: '请输入邮箱验证码' },
    { pattern: /^\d{6}$/, message: '请输入正确的6位验证码' }
  ]
}

// 密码确认验证器
function validateConfirmPassword(_rule: Rule, value: string): Promise<void> {
  if (value && value !== formData.password) {
    return Promise.reject(new Error('两次输入的密码不一致'))
  }
  return Promise.resolve()
}

// 更新密码强度
function updatePasswordStrength() {
  const password = formData.password
  let score = 0
  
  if (password.length >= 6) score++
  if (password.length >= 8) score++
  if (/[a-z]/.test(password)) score++
  if (/[A-Z]/.test(password)) score++
  if (/\d/.test(password)) score++
  if (/[^a-zA-Z0-9]/.test(password)) score++
  
  if (score <= 2) {
    passwordStrength.value = 'weak'
  } else if (score <= 4) {
    passwordStrength.value = 'medium'
  } else {
    passwordStrength.value = 'strong'
  }
}

// 发送邮箱验证码
async function sendEmailCode() {
  try {
    await formRef.value?.validateFields(['email'])
    
    // 调用邮箱验证码API
    await AuthAPI.sendVerificationCode({
      email: formData.email,
      type: 'register'
    })
    
    // 开始倒计时
    codeCountdown.value = 60
    const countdown = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(countdown)
      }
    }, 1000)
    
    message.success('验证码已发送到您的邮箱，请注意查收')
  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : '发送验证码失败，请重试'
    message.error(errorMessage)
  }
}

// 注册处理
async function handleRegister() {
  try {
    loading.value = true
    
    // 调用注册API
    await AuthAPI.register({
      username: formData.username,
      email: formData.email,
      password: formData.password,
      emailCode: formData.emailCode
    })
    
    message.success('注册成功！即将跳转到登录页面')
    
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : '注册失败，请重试'
    message.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>
