<template>
  <div :style="{ 
    background: '#f5f5f5', 
    minHeight: '100vh', 
    display: 'flex', 
    alignItems: 'center', 
    justifyContent: 'center', 
    padding: '20px' 
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
      overflow: 'hidden' 
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
        color: 'white' 
      }">
        <div :style="{ fontSize: '48px', marginBottom: '24px' }">
          <RocketOutlined />
        </div>
        <h3 :style="{ fontSize: '24px', fontWeight: 'bold', marginBottom: '12px', textAlign: 'center' }">
          开启智能识别之旅
        </h3>
        <p :style="{ fontSize: '14px', opacity: 0.9, textAlign: 'center', lineHeight: 1.6, maxWidth: '300px', marginBottom: '40px' }">
          注册成为我们的用户，享受强大的图像识别服务和知识社区
        </p>
        
        <ul :style="{ listStyle: 'none', padding: 0, margin: 0, width: '100%' }">
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <ThunderboltOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>毫秒级图像识别</span>
          </li>
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <DatabaseOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>海量知识库支持</span>
          </li>
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <TeamOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>专业社区交流</span>
          </li>
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <CloudOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>云端存储历史</span>
          </li>
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <MobileOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>多平台同步使用</span>
          </li>
          <li :style="{ display: 'flex', alignItems: 'center', marginBottom: '16px', fontSize: '14px', opacity: 0.9 }">
            <CustomerServiceOutlined :style="{ marginRight: '12px', fontSize: '16px' }" />
            <span>24/7 技术支持</span>
          </li>
        </ul>
      </div>
      
      <!-- 右侧注册表单 -->
      <div :style="{ 
        flex: 1, 
        padding: '60px 40px', 
        display: 'flex', 
        flexDirection: 'column', 
        justifyContent: 'center',
        overflowY: 'auto' 
      }">
        <div :style="{ textAlign: 'center', marginBottom: '32px' }">
          <h2 :style="{ fontSize: '24px', fontWeight: 'bold', color: '#262626', marginBottom: '8px' }">
            创建账户
          </h2>
          <p :style="{ color: '#666', fontSize: '14px', margin: 0 }">
            填写以下信息完成注册，开启智能识别之旅
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
          
          <!-- 协议同意 -->
          <a-form-item name="agreement">
            <a-checkbox v-model:checked="formData.agreement">
              我已阅读并同意
              <router-link to="/terms" :style="{ color: '#1890ff', textDecoration: 'none' }">《用户服务协议》</router-link>
              和
              <router-link to="/privacy" :style="{ color: '#1890ff', textDecoration: 'none' }">《隐私政策》</router-link>
            </a-checkbox>
          </a-form-item>
          
          <!-- 邮件订阅 -->
          <a-form-item>
            <a-checkbox v-model:checked="formData.newsletter">
              订阅系统更新和功能推荐邮件
            </a-checkbox>
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
  CheckOutlined,
  RocketOutlined,
  ThunderboltOutlined,
  DatabaseOutlined,
  TeamOutlined,
  CloudOutlined,
  MobileOutlined,
  CustomerServiceOutlined
} from '@ant-design/icons-vue'

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
  emailCode: '',
  agreement: false,
  newsletter: false
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
  ],
  agreement: [
    { validator: validateAgreement }
  ]
}

// 密码确认验证器
function validateConfirmPassword(_rule: Rule, value: string): Promise<void> {
  if (value && value !== formData.password) {
    return Promise.reject(new Error('两次输入的密码不一致'))
  }
  return Promise.resolve()
}

// 协议验证器
function validateAgreement(_rule: Rule, _value: boolean): Promise<void> {
  if (!formData.agreement) {
    return Promise.reject(new Error('请阅读并同意用户协议'))
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
    await AuthAPI.sendEmailCode({
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
    const result = await AuthAPI.register({
      username: formData.username,
      email: formData.email,
      password: formData.password,
      emailCode: formData.emailCode
    })
    
    if (result.code == 200) {
      message.success('注册成功！即将跳转到登录页面')
      
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      message.error(result.message || '注册失败，请重试')
    }
  } catch (error: unknown) {
    const errorMessage = error instanceof Error ? error.message : '注册失败，请重试'
    message.error(errorMessage)
  } finally {
    loading.value = false
  }
}
</script>
