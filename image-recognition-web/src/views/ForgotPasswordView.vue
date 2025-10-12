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
          <p class="form-subtitle">请输入邮箱地址和验证码，然后设置新密码</p>
        </div>
        
        <a-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          class="forgot-form"
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
                <i class="fas fa-envelope"></i>
              </template>
            </a-input>
          </a-form-item>
          
          <!-- 验证码输入 -->
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
                :disabled="!formData.email || codeCountdown > 0"
                @click="sendVerificationCode"
                class="resend-btn"
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
                <i class="fas fa-lock"></i>
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
                <i class="fas fa-lock"></i>
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
            >
              重置密码
              <template #icon>
                <i class="fas fa-save"></i>
              </template>
            </a-button>
          </a-form-item>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import type { FormInstance, Rule } from 'ant-design-vue/es/form'
import { AuthAPI } from '@/api/auth'

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

// 发送验证码
async function sendVerificationCode() {
  try {
    // 先验证邮箱格式
    await formRef.value?.validateFields(['email'])
    
    await AuthAPI.forgotPassword({
      email: formData.email
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
    
  } catch (error: any) {
    message.error(error.message || '发送验证码失败，请重试')
  }
}

// 表单提交 - 重置密码
async function handleSubmit() {
  try {
    loading.value = true
    
    await AuthAPI.resetPassword({
      email: formData.email,
      newPassword: formData.newPassword,
      confirmPassword: formData.confirmNewPassword,
      emailCode: formData.verificationCode
    })
    
    message.success('密码重置成功，请使用新密码登录')
    
    // 延迟跳转到登录页面
    setTimeout(() => {
      router.push('/login')
    }, 2000)
    
  } catch (error: any) {
    message.error(error.message || '重置密码失败，请重试')
  } finally {
    loading.value = false
  }
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

/* 表单 */
.forgot-form {
  max-width: 360px;
  margin: 0 auto;
  width: 100%;
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
}
</style>
