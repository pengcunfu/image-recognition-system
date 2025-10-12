<template>
  <div class="forgot-container">
    <!-- 动态背景 -->
    <div class="bg-animation">
      <div class="floating-shape">
        <i class="fas fa-key"></i>
      </div>
      <div class="floating-shape">
        <i class="fas fa-lock"></i>
      </div>
      <div class="floating-shape">
        <i class="fas fa-shield-alt"></i>
      </div>
      <div class="floating-shape">
        <i class="fas fa-envelope"></i>
      </div>
    </div>

    <div class="forgot-box">
      <!-- 左侧信息区域 -->
      <div class="forgot-info">
        <div class="info-icon">
          <i class="fas fa-key"></i>
        </div>
        <h3 class="info-title">安全找回密码</h3>
        <p class="info-subtitle">通过多重验证确保您的账户安全，快速找回密码重新使用系统</p>
        
        <ul class="security-tips">
          <li class="security-tip">
            <i class="fas fa-shield-alt"></i>
            <span>多重身份验证保护</span>
          </li>
          <li class="security-tip">
            <i class="fas fa-clock"></i>
            <span>验证码5分钟内有效</span>
          </li>
          <li class="security-tip">
            <i class="fas fa-lock"></i>
            <span>密码加密安全传输</span>
          </li>
          <li class="security-tip">
            <i class="fas fa-history"></i>
            <span>操作记录可追溯</span>
          </li>
          <li class="security-tip">
            <i class="fas fa-user-shield"></i>
            <span>账户异常监控保护</span>
          </li>
        </ul>
      </div>
      
      <!-- 右侧表单区域 -->
      <div class="forgot-form-container">
        <div class="form-header">
          <h2 class="form-title">找回密码</h2>
          <p class="form-subtitle">请按照以下步骤验证身份并重置密码</p>
        </div>
        
        <!-- 步骤指示器 -->
        <a-steps :current="currentStep - 1" size="small" class="step-indicator">
          <a-step title="邮箱验证" />
          <a-step title="输入验证码" />
          <a-step title="重置密码" />
          <a-step title="完成" />
        </a-steps>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          class="forgot-form"
          @finish="handleSubmit"
        >
          <!-- 第一步：邮箱验证 -->
          <div v-show="currentStep === 1" class="form-step">
            <a-alert
              message="邮箱验证"
              description="请输入注册时使用的邮箱地址，系统将向您发送验证码"
              type="info"
              show-icon
              style="margin-bottom: 24px;"
            />
            
            <!-- 邮箱输入 -->
            <a-form-item name="email">
              <a-input
                v-model:value="formData.email"
                size="large"
                placeholder="请输入注册时使用的邮箱"
              >
                <template #prefix>
                  <i class="fas fa-envelope"></i>
                </template>
              </a-input>
            </a-form-item>
            
            <a-button type="primary" size="large" block @click="nextStep">
              发送验证码
              <template #icon>
                <i class="fas fa-paper-plane"></i>
              </template>
            </a-button>
          </div>
          
          <!-- 第二步：身份验证 -->
          <div v-show="currentStep === 2" class="form-step">
            <a-alert
              message="身份验证"
              :description="verificationText"
              type="warning"
              show-icon
              style="margin-bottom: 24px;"
            />
            
            <a-form-item name="verificationCode">
              <div class="code-group">
                <a-input
                  v-model:value="formData.verificationCode"
                  size="large"
                  placeholder="请输入6位验证码"
                  maxlength="6"
                  class="code-input"
                >
                  <template #prefix>
                    <i class="fas fa-shield-alt"></i>
                  </template>
                </a-input>
                <a-button 
                  :disabled="codeCountdown > 0"
                  @click="resendCode"
                  class="resend-btn"
                >
                  {{ codeCountdown > 0 ? `${codeCountdown}秒后重发` : '重新发送' }}
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
                验证
                <template #icon>
                  <i class="fas fa-check"></i>
                </template>
              </a-button>
            </div>
          </div>
          
          <!-- 第三步：重置密码 -->
          <div v-show="currentStep === 3" class="form-step">
            <a-alert
              message="设置新密码"
              description="请设置一个安全的新密码，建议包含字母、数字和特殊字符"
              type="success"
              show-icon
              style="margin-bottom: 24px;"
            />
            
            <a-form-item name="newPassword">
              <a-input-password
                v-model:value="formData.newPassword"
                size="large"
                placeholder="请输入新密码"
                @input="checkPasswordRequirements"
              >
                <template #prefix>
                  <i class="fas fa-lock"></i>
                </template>
              </a-input-password>
              
              <!-- 密码要求 -->
              <div v-if="formData.newPassword" class="password-requirements">
                <div class="requirements-title">密码要求：</div>
                <div 
                  class="requirement-item" 
                  :class="{ valid: requirements.length }"
                >
                  <i :class="requirements.length ? 'fas fa-check-circle' : 'fas fa-circle'"></i>
                  <span>至少8位字符</span>
                </div>
                <div 
                  class="requirement-item" 
                  :class="{ valid: requirements.letter }"
                >
                  <i :class="requirements.letter ? 'fas fa-check-circle' : 'fas fa-circle'"></i>
                  <span>包含大小写字母</span>
                </div>
                <div 
                  class="requirement-item" 
                  :class="{ valid: requirements.number }"
                >
                  <i :class="requirements.number ? 'fas fa-check-circle' : 'fas fa-circle'"></i>
                  <span>包含数字</span>
                </div>
                <div 
                  class="requirement-item" 
                  :class="{ valid: requirements.special }"
                >
                  <i :class="requirements.special ? 'fas fa-check-circle' : 'fas fa-circle'"></i>
                  <span>包含特殊字符</span>
                </div>
              </div>
            </a-form-item>
            
            <a-form-item name="confirmNewPassword">
              <a-input-password
                v-model:value="formData.confirmNewPassword"
                size="large"
                placeholder="请再次输入新密码"
              >
                <template #prefix>
                  <i class="fas fa-lock"></i>
                </template>
              </a-input-password>
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
                :loading="loading"
                @click="resetPassword"
              >
                重置密码
                <template #icon>
                  <i class="fas fa-save"></i>
                </template>
              </a-button>
            </div>
          </div>
          
          <!-- 第四步：完成 -->
          <div v-show="currentStep === 4" class="form-step">
            <div class="success-container">
              <div class="success-icon">
                <i class="fas fa-check-circle"></i>
              </div>
              <h3 class="success-title">密码重置成功</h3>
              <p class="success-text">
                您的密码已成功重置，请使用新密码登录系统。<br>
                为了账户安全，建议您定期更换密码。
              </p>
              <a-button type="primary" size="large" @click="goToLogin">
                立即登录
                <template #icon>
                  <i class="fas fa-sign-in-alt"></i>
                </template>
              </a-button>
            </div>
          </div>
        </a-form>
        
        <!-- 返回登录 -->
        <div class="back-to-login">
          记起密码了？<router-link to="/login">返回登录</router-link>
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

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const currentStep = ref(1)
const codeCountdown = ref(0)

// 表单数据
const formData = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmNewPassword: ''
})

// 密码要求检查
const requirements = reactive({
  length: false,
  letter: false,
  number: false,
  special: false
})

// 计算属性
const verificationText = computed(() => {
  return `验证码已发送至您的邮箱：${maskContact(formData.email)}，请查收并输入验证码`
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
    { validator: validateNewPassword }
  ],
  confirmNewPassword: [
    { required: true, message: '请确认新密码' },
    { validator: validateConfirmPassword }
  ]
}

// 新密码验证器
function validateNewPassword(_rule: Rule, value: string) {
  if (!value) {
    return Promise.reject('请输入新密码')
  }
  
  const allRequirementsMet = Object.values(requirements).every(req => req)
  if (!allRequirementsMet) {
    return Promise.reject('密码不符合安全要求')
  }
  
  return Promise.resolve()
}

// 确认密码验证器
function validateConfirmPassword(_rule: Rule, value: string) {
  if (value && value !== formData.newPassword) {
    return Promise.reject('两次输入的密码不一致')
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

// 检查密码要求
function checkPasswordRequirements() {
  const password = formData.newPassword
  
  requirements.length = password.length >= 8
  requirements.letter = /[a-zA-Z]/.test(password)
  requirements.number = /\d/.test(password)
  requirements.special = /[^a-zA-Z0-9]/.test(password)
}

// 下一步
async function nextStep() {
  try {
    if (currentStep.value === 1) {
      // 验证邮箱并发送验证码
      await formRef.value?.validateFields(['email'])
      await sendVerificationCode()
      currentStep.value = 2
      
    } else if (currentStep.value === 2) {
      // 验证验证码
      await formRef.value?.validateFields(['verificationCode'])
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

// 发送验证码
async function sendVerificationCode() {
  try {
    await AuthAPI.forgotPassword({
      email: formData.email
    })
    message.success(`验证码已发送至您的邮箱：${maskContact(formData.email)}`)
  } catch (error: any) {
    message.error(error.message || '发送验证码失败，请重试')
    throw error
  }
}

// 重新发送验证码
async function resendCode() {
  try {
    // 开始倒计时
    codeCountdown.value = 60
    const countdown = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(countdown)
      }
    }, 1000)
    
    await sendVerificationCode()
  } catch (error) {
    // 发送失败，停止倒计时
    codeCountdown.value = 0
  }
}

// 重置密码
async function resetPassword() {
  try {
    await formRef.value?.validateFields(['newPassword', 'confirmNewPassword'])
    
    loading.value = true
    
    await AuthAPI.resetPassword({
      email: formData.email,
      newPassword: formData.newPassword,
      confirmPassword: formData.confirmNewPassword,
      emailCode: formData.verificationCode
    })
    
    message.success('密码重置成功')
    currentStep.value = 4
  } catch (error: any) {
    message.error(error.message || '重置密码失败，请重试')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
function goToLogin() {
  router.push('/login')
}

// 表单提交（不使用）
function handleSubmit() {
  // 这个方法不会被调用，因为我们使用自定义的步骤控制
}
</script>

<style scoped>
.forgot-container {
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
  animation: float 8s ease-in-out infinite;
  color: #1890ff;
}

.floating-shape:nth-child(1) {
  top: 20%;
  left: 10%;
  animation-delay: 0s;
  font-size: 50px;
}

.floating-shape:nth-child(2) {
  top: 10%;
  right: 20%;
  animation-delay: 3s;
  font-size: 70px;
}

.floating-shape:nth-child(3) {
  bottom: 20%;
  left: 15%;
  animation-delay: 6s;
  font-size: 60px;
}

.floating-shape:nth-child(4) {
  bottom: 30%;
  right: 10%;
  animation-delay: 2s;
  font-size: 40px;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-30px) rotate(180deg); }
}

/* 主容器 */
.forgot-box {
  background: white;
  border-radius: 24px;
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
.forgot-info {
  flex: 1;
  background: #1890ff;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  position: relative;
}

.forgot-info::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="25" cy="25" r="2" fill="white" opacity="0.1"/><circle cx="75" cy="35" r="3" fill="white" opacity="0.1"/><circle cx="45" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="65" cy="15" r="1.5" fill="white" opacity="0.1"/></svg>');
  animation: twinkle 5s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
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
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 16px;
  text-align: center;
}

.info-subtitle {
  font-size: 16px;
  opacity: 0.9;
  text-align: center;
  line-height: 1.6;
  margin-bottom: 40px;
  max-width: 300px;
}

.security-tips {
  list-style: none;
  width: 100%;
  max-width: 320px;
}

.security-tip {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  opacity: 0.9;
}

.security-tip i {
  margin-right: 12px;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

/* 右侧表单区域 */
.forgot-form-container {
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
  font-size: 28px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.form-subtitle {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
}

/* 步骤指示器 */
.step-indicator {
  margin-bottom: 40px;
}

.forgot-form {
  max-width: 360px;
  margin: 0 auto;
  width: 100%;
}

.form-step {
  animation: slideIn 0.4s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 验证码组合 */
.code-group {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.code-input {
  flex: 1;
}

.resend-btn {
  white-space: nowrap;
}

/* 密码要求 */
.password-requirements {
  margin-top: 8px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.requirements-title {
  font-size: 13px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.requirement-item {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 12px;
  color: #666;
}

.requirement-item i {
  margin-right: 8px;
  font-size: 10px;
}

.requirement-item.valid {
  color: #52c41a;
}

.requirement-item.valid i {
  color: #52c41a;
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

/* 成功页面 */
.success-container {
  text-align: center;
  padding: 40px 0;
}

.success-icon {
  font-size: 64px;
  color: #52c41a;
  margin-bottom: 24px;
  animation: bounce 1s ease-in-out;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

.success-title {
  font-size: 24px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 16px;
}

.success-text {
  color: #666;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 32px;
}

/* 返回登录 */
.back-to-login {
  text-align: center;
  margin-top: 24px;
  color: #666;
  font-size: 14px;
}

.back-to-login a {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
}

.back-to-login a:hover {
  color: #40a9ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .forgot-box {
    flex-direction: column;
    width: 95vw;
    min-height: auto;
  }
  
  .forgot-info {
    padding: 40px 20px;
    order: 2;
  }
  
  .forgot-form-container {
    padding: 40px 20px;
    order: 1;
  }
  
  .info-title {
    font-size: 24px;
  }
  
  .info-icon {
    font-size: 48px;
  }
}

@media (max-width: 480px) {
  .forgot-box {
    margin: 20px;
    border-radius: 16px;
  }
  
  .forgot-info,
  .forgot-form-container {
    padding: 30px 20px;
  }
  
  .code-group {
    flex-direction: column;
  }
  
  .resend-btn {
    width: 100%;
  }
  
  .btn-group {
    flex-direction: column;
  }
}
</style>
