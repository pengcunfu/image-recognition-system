<template>
  <div class="page">
    <h1 class="page-title">系统设置</h1>
    
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :lg="12">
        <a-card title="基本设置">
          <a-form layout="vertical">
            <a-form-item label="系统名称">
              <a-input v-model:value="systemSettings.name" />
            </a-form-item>
            <a-form-item label="管理员邮箱">
              <a-input v-model:value="systemSettings.email" type="email" />
            </a-form-item>
            <a-form-item label="系统描述">
              <a-textarea 
                v-model:value="systemSettings.description" 
                :rows="4" 
              />
            </a-form-item>
            <a-form-item label="系统版本">
              <a-input v-model:value="systemSettings.version" disabled />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="saveSettings">
                保存设置
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="通知设置">
          <a-form layout="vertical">
            <a-form-item>
              <a-checkbox v-model:checked="notificationSettings.email">
                邮件通知
              </a-checkbox>
            </a-form-item>
            <a-form-item>
              <a-checkbox v-model:checked="notificationSettings.sms">
                短信通知
              </a-checkbox>
            </a-form-item>
            <a-form-item>
              <a-checkbox v-model:checked="notificationSettings.push">
                推送通知
              </a-checkbox>
            </a-form-item>
            <a-form-item>
              <a-checkbox v-model:checked="notificationSettings.orderAlert">
                订单提醒
              </a-checkbox>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="saveNotificationSettings">
                保存设置
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :lg="12">
        <a-card title="安全设置">
          <a-form layout="vertical">
            <a-form-item label="登录超时时间（分钟）">
              <a-input-number 
                v-model:value="securitySettings.loginTimeout" 
                :min="5" 
                :max="1440"
                style="width: 100%"
              />
            </a-form-item>
            <a-form-item label="密码策略">
              <a-select v-model:value="securitySettings.passwordPolicy" style="width: 100%">
                <a-select-option value="low">低（6位以上）</a-select-option>
                <a-select-option value="medium">中（8位，包含字母数字）</a-select-option>
                <a-select-option value="high">高（8位，包含字母数字特殊字符）</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-checkbox v-model:checked="securitySettings.twoFactorAuth">
                启用双因子认证
              </a-checkbox>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="saveSecuritySettings">
                保存设置
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="数据管理">
          <a-form layout="vertical">
            <a-form-item label="数据备份频率">
              <a-select v-model:value="dataSettings.backupFrequency" style="width: 100%">
                <a-select-option value="daily">每日</a-select-option>
                <a-select-option value="weekly">每周</a-select-option>
                <a-select-option value="monthly">每月</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="日志保留天数">
              <a-input-number 
                v-model:value="dataSettings.logRetentionDays" 
                :min="7" 
                :max="365"
                style="width: 100%"
              />
            </a-form-item>
            <a-form-item>
              <a-space>
                <a-button type="primary" @click="backupData">
                  立即备份
                </a-button>
                <a-button @click="clearLogs">
                  清理日志
                </a-button>
              </a-space>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="saveDataSettings">
                保存设置
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { message } from 'ant-design-vue'

// 系统设置
const systemSettings = reactive({
  name: '智能图像识别系统',
  email: 'admin@example.com',
  description: '基于深度学习的通用图像识别平台，提供识别、学习、交流一体化服务',
  version: '1.0.0'
})

// 通知设置
const notificationSettings = reactive({
  email: true,
  sms: false,
  push: true,
  orderAlert: true
})

// 安全设置
const securitySettings = reactive({
  loginTimeout: 30,
  passwordPolicy: 'medium',
  twoFactorAuth: false
})

// 数据设置
const dataSettings = reactive({
  backupFrequency: 'daily',
  logRetentionDays: 30
})

// 方法
function saveSettings() {
  message.success('系统设置已保存')
}

function saveNotificationSettings() {
  message.success('通知设置已保存')
}

function saveSecuritySettings() {
  message.success('安全设置已保存')
}

function saveDataSettings() {
  message.success('数据设置已保存')
}

function backupData() {
  message.success('数据备份已开始')
}

function clearLogs() {
  message.success('日志清理完成')
}
</script>

<style scoped>
.page {
  width: 100%;
}

.page-title {
  margin-bottom: 24px;
  color: #262626;
  font-size: 24px;
  font-weight: 600;
}
</style>
