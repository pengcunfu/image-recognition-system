<template>
  <div class="register-container">
    <!-- 动态背景 -->
    <div class="bg-animation">
      <div class="floating-shape">
        <i class="fas fa-user-plus"></i>
      </div>
      <div class="floating-shape">
        <i class="fas fa-shield-alt"></i>
      </div>
      <div class="floating-shape">
        <i class="fas fa-rocket"></i>
      </div>
      <div class="floating-shape">
        <i class="fas fa-star"></i>
      </div>
    </div>

    <div class="register-box">
      <!-- 左侧注册表单 -->
      <div class="register-form-container">
        <div class="form-header">
          <h2 class="form-title">创建账户</h2>
          <p class="form-subtitle">填写以下信息完成注册，开启智能识别之旅</p>
        </div>
        
        <!-- 步骤指示器 -->
        <a-steps :current="currentStep - 1" size="small" class="step-indicator">
          <a-step title="基本信息" />
          <a-step title="验证手机" />
          <a-step title="完成注册" />
        </a-steps>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          class="register-form"
          @finish="handleRegister"
        >
          <!-- 第一步：基本信息 -->
          <div v-show="currentStep === 1" class="form-step">
            <a-form-item name="username">
              <a-input
                v-model:value="formData.username"
                size="large"
                placeholder="请输入用户名（3-20位字符）"
              >
                <template #prefix>
                  <i class="fas fa-user"></i>
                </template>
                <template #suffix>
                  <i v-if="usernameValid" class="fas fa-check" style="color: #52c41a;"></i>
                </template>
              </a-input>
            </a-form-item>
            
            <a-form-item name="email">
              <a-input
                v-model:value="formData.email"
                size="large"
                placeholder="请输入邮箱地址"
                type="email"
              >
                <template #prefix>
                  <i class="fas fa-envelope"></i>
                </template>
                <template #suffix>
                  <i v-if="emailValid" class="fas fa-check" style="color: #52c41a;"></i>
                </template>
              </a-input>
            </a-form-item>
            
            <a-form-item name="password">
              <a-input-password
                v-model:value="formData.password"
                size="large"
                placeholder="请设置密码（至少6位）"
                @input="updatePasswordStrength"
              >
                <template #prefix>
                  <i class="fas fa-lock"></i>
                </template>
              </a-input-password>
              
              <!-- 密码强度指示器 -->
              <div v-if="formData.password" class="password-strength">
                <div class="strength-label">密码强度：<span>{{ strengthText }}</span></div>
                <div class="strength-bar">
                  <div 
                    class="strength-fill" 
                    :class="`strength-${passwordStrength}`"
                  ></div>
                </div>
              </div>
            </a-form-item>
            
            <a-form-item name="confirmPassword">
              <a-input-password
                v-model:value="formData.confirmPassword"
                size="large"
                placeholder="请再次输入密码"
              >
                <template #prefix>
                  <i class="fas fa-lock"></i>
                </template>
              </a-input-password>
            </a-form-item>
            
            <a-button type="primary" size="large" block @click="nextStep">
              下一步
              <template #icon>
                <i class="fas fa-arrow-right"></i>
              </template>
            </a-button>
          </div>
          
          <!-- 第二步：手机验证 -->
          <div v-show="currentStep === 2" class="form-step">
            <a-form-item name="phone">
              <a-input
                v-model:value="formData.phone"
                size="large"
                placeholder="请输入手机号码"
              >
                <template #prefix>
                  <i class="fas fa-phone"></i>
                </template>
              </a-input>
            </a-form-item>
            
            <a-form-item name="smsCode">
              <div class="phone-group">
                <a-input
                  v-model:value="formData.smsCode"
                  size="large"
                  placeholder="请输入验证码"
                  maxlength="6"
                  class="phone-input"
                >
                  <template #prefix>
                    <i class="fas fa-shield-alt"></i>
                  </template>
                </a-input>
                <a-button 
                  :disabled="codeCountdown > 0"
                  @click="sendSmsCode"
                  class="send-code-btn"
                >
                  {{ codeCountdown > 0 ? `${codeCountdown}秒后重发` : '发送验证码' }}
                </a-button>
              </div>
            </a-form-item>
            
            <div class="btn-group">
              <a-button size="large" @click="prevStep">
                <template #icon>
                  <i class="fas fa-arrow-left"></i>
                </template>
                上一步
              </a-button>
              <a-button type="primary" size="large" @click="nextStep">
                下一步
                <template #icon>
                  <i class="fas fa-arrow-right"></i>
                </template>
              </a-button>
            </div>
          </div>
          
          <!-- 第三步：完成注册 -->
          <div v-show="currentStep === 3" class="form-step">
            <a-form-item name="agreement">
              <a-checkbox v-model:checked="formData.agreement">
                我已阅读并同意
                <router-link to="/terms" class="agreement-link">《用户服务协议》</router-link>
                和
                <router-link to="/privacy" class="agreement-link">《隐私政策》</router-link>
              </a-checkbox>
            </a-form-item>
            
            <a-form-item>
              <a-checkbox v-model:checked="formData.newsletter">
                订阅系统更新和功能推荐邮件
              </a-checkbox>
            </a-form-item>
            
            <div class="btn-group">
              <a-button size="large" @click="prevStep">
                <template #icon>
                  <i class="fas fa-arrow-left"></i>
                </template>
                上一步
              </a-button>
              <a-button 
                type="primary" 
                size="large" 
                html-type="submit"
                :loading="loading"
              >
                <template #icon>
                  <i class="fas fa-user-plus"></i>
                </template>
                完成注册
              </a-button>
            </div>
          </div>
        </a-form>
        
        <!-- 登录链接 -->
        <div class="login-link">
          已有账户？<router-link to="/login">立即登录</router-link>
        </div>
      </div>
      
      <!-- 右侧信息区域 -->
      <div class="register-info">
        <div class="info-icon">
          <i class="fas fa-rocket"></i>
        </div>
        <h3 class="info-title">开启智能识别之旅</h3>
        <p class="info-subtitle">注册成为我们的用户，享受强大的图像识别服务和知识社区</p>
        
        <ul class="benefits-list">
          <li class="benefit-item">
            <i class="fas fa-bolt"></i>
            <span>毫秒级图像识别</span>
          </li>
          <li class="benefit-item">
            <i class="fas fa-database"></i>
            <span>海量知识库支持</span>
          </li>
          <li class="benefit-item">
            <i class="fas fa-users"></i>
            <span>专业社区交流</span>
          </li>
          <li class="benefit-item">
            <i class="fas fa-cloud"></i>
            <span>云端存储历史</span>
          </li>
          <li class="benefit-item">
            <i class="fas fa-mobile-alt"></i>
            <span>多平台同步使用</span>
          </li>
          <li class="benefit-item">
            <i class="fas fa-headset"></i>
            <span>24/7 技术支持</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const currentStep = ref(1)
const codeCountdown = ref(0)
const passwordStrength = ref('weak')

// 表单数据
const formData = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  smsCode: '',
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
  email: [
    { required: true, message: '请输入邮箱地址' },
    { type: 'email', message: '请输入正确的邮箱地址格式' }
  ],
  password: [
    { required: true, message: '请设置密码' },
    { min: 6, message: '密码长度至少6位，建议包含字母、数字和特殊字符' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: validateConfirmPassword }
  ],
  phone: [
    { required: true, message: '请输入手机号码' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码格式' }
  ],
  smsCode: [
    { required: true, message: '请输入验证码' },
    { pattern: /^\d{6}$/, message: '请输入正确的6位验证码' }
  ],
  agreement: [
    { validator: validateAgreement }
  ]
}

// 密码确认验证器
function validateConfirmPassword(_rule: Rule, value: string) {
  if (value && value !== formData.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

// 协议验证器
function validateAgreement(_rule: Rule, _value: boolean) {
  if (!formData.agreement) {
    return Promise.reject('请阅读并同意用户协议')
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

// 下一步
async function nextStep() {
  try {
    if (currentStep.value === 1) {
      // 验证第一步表单
      await formRef.value?.validateFields(['username', 'email', 'password', 'confirmPassword'])
      currentStep.value = 2
    } else if (currentStep.value === 2) {
      // 验证第二步表单
      await formRef.value?.validateFields(['phone', 'smsCode'])
      currentStep.value = 3
    }
  } catch (error) {
    // 验证失败
  }
}

// 上一步
function prevStep() {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

// 发送短信验证码
async function sendSmsCode() {
  try {
    await formRef.value?.validateFields(['phone'])
    
    // 开始倒计时
    codeCountdown.value = 60
    const countdown = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(countdown)
      }
    }, 1000)
    
    // 模拟发送验证码
    setTimeout(() => {
      message.success('验证码已发送到您的手机，请注意查收（演示用验证码：123456）')
    }, 1000)
  } catch (error) {
    // 验证失败
  }
}

// 注册处理
async function handleRegister() {
  try {
    loading.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    message.success('注册成功！即将跳转到登录页面')
    
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error) {
    message.error('注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
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
  color: #1890ff;
}

.floating-shape:nth-child(1) {
  top: 15%;
  left: 15%;
  animation-delay: 0s;
  font-size: 50px;
}

.floating-shape:nth-child(2) {
  top: 30%;
  right: 15%;
  animation-delay: 2s;
  font-size: 70px;
}

.floating-shape:nth-child(3) {
  bottom: 15%;
  left: 25%;
  animation-delay: 4s;
  font-size: 60px;
}

.floating-shape:nth-child(4) {
  bottom: 30%;
  right: 25%;
  animation-delay: 1s;
  font-size: 40px;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 主容器 */
.register-box {
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid #e8e8e8;
  width: 1000px;
  max-width: 95vw;
  min-height: 700px;
  display: flex;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* 左侧注册表单 */
.register-form-container {
  flex: 1.2;
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow-y: auto;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-title {
  font-size: 28px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.form-subtitle {
  color: #666;
  font-size: 14px;
}

/* 步骤指示器 */
.step-indicator {
  margin-bottom: 32px;
}

.register-form {
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.form-step {
  animation: slideIn 0.3s ease-in-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 密码强度指示器 */
.password-strength {
  margin-top: 8px;
}

.strength-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.strength-bar {
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s;
  border-radius: 2px;
}

.strength-fill.strength-weak {
  width: 33%;
  background: #ff4d4f;
}

.strength-fill.strength-medium {
  width: 66%;
  background: #faad14;
}

.strength-fill.strength-strong {
  width: 100%;
  background: #52c41a;
}

/* 手机验证码 */
.phone-group {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.phone-input {
  flex: 1;
}

.send-code-btn {
  height: 40px;
  white-space: nowrap;
}

/* 按钮组 */
.btn-group {
  display: flex;
  gap: 16px;
  margin-top: 32px;
}

.btn-group .ant-btn {
  flex: 1;
}

/* 协议链接 */
.agreement-link {
  color: #1890ff;
  text-decoration: none;
}

.agreement-link:hover {
  color: #40a9ff;
}

/* 登录链接 */
.login-link {
  text-align: center;
  margin-top: 24px;
  color: #666;
  font-size: 14px;
}

.login-link a {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  color: #40a9ff;
}

/* 右侧信息区域 */
.register-info {
  flex: 0.8;
  background: #1890ff;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  position: relative;
}

.register-info::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="10" cy="10" r="1" fill="white" opacity="0.1"/><circle cx="90" cy="30" r="2" fill="white" opacity="0.1"/><circle cx="30" cy="90" r="1.5" fill="white" opacity="0.1"/><circle cx="70" cy="70" r="1" fill="white" opacity="0.1"/></svg>');
  animation: twinkle 4s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

.info-icon {
  font-size: 64px;
  margin-bottom: 32px;
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.info-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 16px;
  text-align: center;
}

.info-subtitle {
  font-size: 16px;
  opacity: 0.9;
  text-align: center;
  line-height: 1.6;
  margin-bottom: 32px;
}

.benefits-list {
  list-style: none;
  width: 100%;
  max-width: 280px;
}

.benefit-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  opacity: 0.9;
}

.benefit-item i {
  margin-right: 12px;
  font-size: 18px;
  width: 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .register-box {
    width: 90vw;
  }
  
  .register-form-container,
  .register-info {
    padding: 40px 30px;
  }
}

@media (max-width: 768px) {
  .register-box {
    flex-direction: column-reverse;
    width: 95vw;
    min-height: auto;
  }
  
  .register-info {
    padding: 30px 20px;
  }
  
  .register-form-container {
    padding: 30px 20px;
  }
  
  .info-title {
    font-size: 20px;
  }
  
  .info-icon {
    font-size: 48px;
  }
}

@media (max-width: 480px) {
  .register-box {
    margin: 20px;
    border-radius: 16px;
  }
  
  .phone-group {
    flex-direction: column;
  }
  
  .send-code-btn {
    width: 100%;
  }
  
  .btn-group {
    flex-direction: column;
  }
}
</style>
