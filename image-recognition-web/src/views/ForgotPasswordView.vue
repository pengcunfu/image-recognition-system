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
      borderRadius: '20px', 
      boxShadow: '0 8px 32px rgba(0, 0, 0, 0.12)', 
      border: '1px solid #e8e8e8', 
      width: '480px', 
      maxWidth: '95vw', 
      padding: '60px 50px',
      position: 'relative',
      zIndex: 1,
      backdropFilter: 'blur(10px)'
    }">
      <!-- 表单区域 -->
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
            请输入邮箱地址和验证码，然后设置新密码
          </p>
        </div>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          :style="{ maxWidth: '320px', margin: '0 auto', width: '100%' }"
          @finish="handleSubmit"
        >
          <!-- 邮箱输入 -->
          <a-form-item name="email">
            <a-input
              v-model:value="formData.email"
              size="large"
              placeholder="请输入注册时使用的邮箱"
            >
              <template #prefix>
                <MailOutlined />
              </template>
            </a-input>
          </a-form-item>
          
          <!-- 验证码输入 -->
          <a-form-item name="verificationCode">
            <div :style="{ display: 'flex', gap: '12px' }">
              <a-input
                v-model:value="formData.verificationCode"
                size="large"
                placeholder="请输入6位验证码"
                :maxlength="6"
                :style="{ flex: 1 }"
              >
                <template #prefix>
                  <SafetyOutlined />
                </template>
              </a-input>
              <a-button 
                size="large"
                :disabled="!formData.email || codeCountdown > 0"
                @click="sendVerificationCode"
              >
                {{ codeCountdown > 0 ? `${codeCountdown}秒后重发` : '发送验证码' }}
              </a-button>
            </div>
          </a-form-item>
          
          <!-- 新密码输入 -->
          <a-form-item name="newPassword">
            <a-input-password
              v-model:value="formData.newPassword"
              size="large"
              placeholder="请输入新密码"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>
          
          <!-- 确认密码输入 -->
          <a-form-item name="confirmNewPassword">
            <a-input-password
              v-model:value="formData.confirmNewPassword"
              size="large"
              placeholder="请再次输入新密码"
            >
              <template #prefix>
                <LockOutlined />
              </template>
            </a-input-password>
          </a-form-item>
          
          <!-- 重置密码按钮 -->
          <a-form-item>
            <a-button 
              type="primary" 
              size="large" 
              block
              :loading="loading"
              html-type="submit"
              :style="{ height: '48px', fontSize: '16px', fontWeight: 500 }"
            >
              重置密码
            </a-button>
          </a-form-item>
        </a-form>
        
        <!-- 返回登录 -->
        <div :style="{ textAlign: 'center', color: '#666', fontSize: '14px', marginTop: '16px' }">
          记起密码了？<router-link to="/login" :style="{ color: '#1890ff', textDecoration: 'none', fontWeight: 500 }">返回登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import { AuthAPI } from '@/api/auth'
import {
  MailOutlined,
  LockOutlined,
  SafetyOutlined
} from '@ant-design/icons-vue'
import bgImage from '@/assets/iamges/bg.png'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const codeCountdown = ref(0)

// 表单数据
const formData = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmNewPassword: ''
})

// 验证规则
const rules: Record<string, Rule[]> = {
  email: [
    { required: true, message: '请输入邮箱地址' },
    { type: 'email', message: '请输入正确的邮箱地址格式' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码' },
    { pattern: /^\d{6}$/, message: '请输入6位数字验证码' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码长度至少6位' }
  ],
  confirmNewPassword: [
    { required: true, message: '请确认新密码' },
    { validator: validateConfirmPassword }
  ]
}

// 确认密码验证器
function validateConfirmPassword(_rule: Rule, value: string): Promise<void> {
  if (value && value !== formData.newPassword) {
    return Promise.reject(new Error('两次输入的密码不一致'))
  }
  return Promise.resolve()
}

// 掩码联系方式
function maskContact(contact: string) {
  if (!contact) return ''
  
  if (contact.includes('@')) {
    // 邮箱掩码
    const [username, domain] = contact.split('@')
    const maskedUsername = username.substring(0, 3) + '****'
    return maskedUsername + '@' + domain
  }
  return contact
}

// 发送验证码
async function sendVerificationCode() {
  try {
    // 先验证邮箱格式
    await formRef.value?.validateFields(['email'])
    
    await AuthAPI.sendVerificationCode({
      email: formData.email,
      type: 'reset'
    })
    
    message.success(`验证码已发送至您的邮箱：${maskContact(formData.email)}`)
    
    // 开始倒计时
    codeCountdown.value = 60
    const countdown = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(countdown)
      }
    }, 1000)
    
  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : '发送验证码失败，请重试'
    message.error(errorMessage)
  }
}

// 表单提交 - 重置密码
async function handleSubmit() {
  try {
    loading.value = true
    
    await AuthAPI.forgotPassword({
      email: formData.email,
      emailCode: formData.verificationCode,
      newPassword: formData.newPassword
    })
    
    message.success('密码重置成功，请使用新密码登录')
    
    // 延迟跳转到登录页面
    setTimeout(() => {
      router.push('/login')
    }, 2000)
    
  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : '重置密码失败，请重试'
    message.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>
