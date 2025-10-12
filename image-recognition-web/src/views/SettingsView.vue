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
      
      <!-- 第三方登录配置 -->
      <a-col :xs="24" :lg="12">
        <a-card title="第三方登录配置" class="oauth-card">
          <a-tabs v-model:activeKey="activeOAuthTab" type="card">
            <!-- GitHub配置 -->
            <a-tab-pane key="github" tab="GitHub">
              <template #tab>
                <span>
                  <i class="fab fa-github"></i>
                  GitHub
                </span>
              </template>
              <a-form layout="vertical">
                <a-form-item>
                  <template #label>
                    <span>
                      启用GitHub登录
                      <a-tooltip title="允许用户使用GitHub账号登录系统">
                        <i class="fas fa-question-circle"></i>
                      </a-tooltip>
                    </span>
                  </template>
                  <a-switch 
                    v-model:checked="oauthSettings.github.enabled" 
                    :loading="oauthSettings.github.loading"
                    @change="toggleGitHubOAuth"
                  />
                </a-form-item>
                
                <div v-if="oauthSettings.github.enabled">
                  <a-form-item label="Client ID">
                    <a-input 
                      v-model:value="oauthSettings.github.clientId" 
                      placeholder="GitHub应用的Client ID"
                    />
                  </a-form-item>
                  <a-form-item label="Client Secret">
                    <a-input-password 
                      v-model:value="oauthSettings.github.clientSecret" 
                      placeholder="GitHub应用的Client Secret"
                    />
                  </a-form-item>
                  <a-form-item label="回调URL">
                    <a-input 
                      v-model:value="oauthSettings.github.callbackUrl" 
                      placeholder="OAuth回调地址"
                      disabled
                    />
                  </a-form-item>
                  <a-form-item>
                    <a-space>
                      <a-button type="primary" @click="saveGitHubSettings">
                        保存配置
                      </a-button>
                      <a-button @click="testGitHubConnection">
                        测试连接
                      </a-button>
                    </a-space>
                  </a-form-item>
                  
                  <!-- GitHub统计信息 -->
                  <a-divider />
                  <div class="oauth-stats">
                    <h4>GitHub登录统计</h4>
                    <a-row :gutter="16">
                      <a-col :span="12">
                        <a-statistic title="绑定用户" :value="oauthSettings.github.stats.boundUsers" />
                      </a-col>
                      <a-col :span="12">
                        <a-statistic title="本月登录" :value="oauthSettings.github.stats.monthlyLogins" />
                      </a-col>
                    </a-row>
                  </div>
                </div>
              </a-form>
            </a-tab-pane>
            
            <!-- Gitee配置 -->
            <a-tab-pane key="gitee" tab="Gitee">
              <template #tab>
                <span>
                  <i class="fas fa-code-branch"></i>
                  Gitee
                </span>
              </template>
              <a-form layout="vertical">
                <a-form-item>
                  <template #label>
                    <span>
                      启用Gitee登录
                      <a-tooltip title="允许用户使用Gitee账号登录系统">
                        <i class="fas fa-question-circle"></i>
                      </a-tooltip>
                    </span>
                  </template>
                  <a-switch 
                    v-model:checked="oauthSettings.gitee.enabled" 
                    :loading="oauthSettings.gitee.loading"
                    @change="toggleGiteeOAuth"
                  />
                </a-form-item>
                
                <div v-if="oauthSettings.gitee.enabled">
                  <a-form-item label="Client ID">
                    <a-input 
                      v-model:value="oauthSettings.gitee.clientId" 
                      placeholder="Gitee应用的Client ID"
                    />
                  </a-form-item>
                  <a-form-item label="Client Secret">
                    <a-input-password 
                      v-model:value="oauthSettings.gitee.clientSecret" 
                      placeholder="Gitee应用的Client Secret"
                    />
                  </a-form-item>
                  <a-form-item label="回调URL">
                    <a-input 
                      v-model:value="oauthSettings.gitee.callbackUrl" 
                      placeholder="OAuth回调地址"
                      disabled
                    />
                  </a-form-item>
                  <a-form-item>
                    <a-space>
                      <a-button type="primary" @click="saveGiteeSettings">
                        保存配置
                      </a-button>
                      <a-button @click="testGiteeConnection">
                        测试连接
                      </a-button>
                    </a-space>
                  </a-form-item>
                  
                  <!-- Gitee统计信息 -->
                  <a-divider />
                  <div class="oauth-stats">
                    <h4>Gitee登录统计</h4>
                    <a-row :gutter="16">
                      <a-col :span="12">
                        <a-statistic title="绑定用户" :value="oauthSettings.gitee.stats.boundUsers" />
                      </a-col>
                      <a-col :span="12">
                        <a-statistic title="本月登录" :value="oauthSettings.gitee.stats.monthlyLogins" />
                      </a-col>
                    </a-row>
                  </div>
                </div>
              </a-form>
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>
    </a-row>

    <!-- 第三方账号绑定管理 -->
    <a-row :gutter="[24, 24]">
      <a-col :xs="24">
        <a-card title="第三方账号绑定管理">
          <a-table 
            :columns="oauthBindingColumns" 
            :data-source="oauthBindings"
            :pagination="false"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'provider'">
                <div class="provider-info">
                  <i :class="getProviderIcon(record.provider)"></i>
                  <span>{{ getProviderName(record.provider) }}</span>
                </div>
              </template>
              
              <template v-else-if="column.key === 'status'">
                <a-tag :color="record.status === 'active' ? 'success' : 'error'">
                  {{ record.status === 'active' ? '已绑定' : '已解绑' }}
                </a-tag>
              </template>
              
              <template v-else-if="column.key === 'action'">
                <a-space>
                  <a-button 
                    v-if="record.status === 'active'" 
                    size="small" 
                    danger
                    @click="unbindOAuth(record)"
                  >
                    解绑
                  </a-button>
                  <a-button 
                    v-else 
                    type="primary" 
                    size="small"
                    @click="rebindOAuth(record)"
                  >
                    重新绑定
                  </a-button>
                  <a-button size="small" @click="viewOAuthDetails(record)">
                    详情
                  </a-button>
                </a-space>
              </template>
            </template>
          </a-table>
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
import { reactive, ref } from 'vue'
import { message, Modal } from 'ant-design-vue'

// 系统设置
const systemSettings = reactive({
  name: '智能图像识别系统',
  email: 'admin@example.com',
  description: '基于深度学习的通用图像识别平台，提供识别、学习、交流一体化服务',
  version: '1.0.0'
})

// OAuth设置
const activeOAuthTab = ref('github')
const oauthSettings = reactive({
  github: {
    enabled: true,
    loading: false,
    clientId: 'your_github_client_id',
    clientSecret: 'your_github_client_secret',
    callbackUrl: `${window.location.origin}/auth/github/callback`,
    stats: {
      boundUsers: 156,
      monthlyLogins: 892
    }
  },
  gitee: {
    enabled: false,
    loading: false,
    clientId: '',
    clientSecret: '',
    callbackUrl: `${window.location.origin}/auth/gitee/callback`,
    stats: {
      boundUsers: 89,
      monthlyLogins: 234
    }
  }
})

// OAuth绑定表格列定义
const oauthBindingColumns = [
  { title: '平台', dataIndex: 'provider', key: 'provider', width: 120 },
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '绑定时间', dataIndex: 'bindTime', key: 'bindTime', width: 180 },
  { title: '最后登录', dataIndex: 'lastLogin', key: 'lastLogin', width: 180 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 200 }
]

// OAuth绑定数据
const oauthBindings = reactive([
  {
    id: 1,
    provider: 'github',
    username: 'developer123',
    email: 'dev@github.com',
    bindTime: '2024-12-01 10:30',
    lastLogin: '2025-01-15 14:20',
    status: 'active'
  },
  {
    id: 2,
    provider: 'gitee',
    username: 'coder456',
    email: 'coder@gitee.com',
    bindTime: '2024-11-15 16:45',
    lastLogin: '2025-01-10 09:15',
    status: 'active'
  },
  {
    id: 3,
    provider: 'github',
    username: 'olduser',
    email: 'old@github.com',
    bindTime: '2024-10-01 12:00',
    lastLogin: '2024-12-20 18:30',
    status: 'inactive'
  }
])

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

// 基本设置方法
function saveSettings() {
  message.success('系统设置已保存')
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

// OAuth相关方法
function toggleGitHubOAuth(enabled: boolean) {
  oauthSettings.github.loading = true
  
  setTimeout(() => {
    oauthSettings.github.enabled = enabled
    oauthSettings.github.loading = false
    message.success(enabled ? 'GitHub登录已启用' : 'GitHub登录已禁用')
  }, 1000)
}

function toggleGiteeOAuth(enabled: boolean) {
  oauthSettings.gitee.loading = true
  
  setTimeout(() => {
    oauthSettings.gitee.enabled = enabled
    oauthSettings.gitee.loading = false
    message.success(enabled ? 'Gitee登录已启用' : 'Gitee登录已禁用')
  }, 1000)
}

function saveGitHubSettings() {
  if (!oauthSettings.github.clientId || !oauthSettings.github.clientSecret) {
    message.error('请填写完整的GitHub应用配置')
    return
  }
  
  // 这里应该调用API保存配置
  message.success('GitHub配置已保存')
}

function saveGiteeSettings() {
  if (!oauthSettings.gitee.clientId || !oauthSettings.gitee.clientSecret) {
    message.error('请填写完整的Gitee应用配置')
    return
  }
  
  // 这里应该调用API保存配置
  message.success('Gitee配置已保存')
}

function testGitHubConnection() {
  message.loading('正在测试GitHub连接...', 2)
  
  setTimeout(() => {
    message.success('GitHub连接测试成功')
  }, 2000)
}

function testGiteeConnection() {
  message.loading('正在测试Gitee连接...', 2)
  
  setTimeout(() => {
    message.success('Gitee连接测试成功')
  }, 2000)
}

// OAuth绑定管理方法
function getProviderIcon(provider: string) {
  switch (provider) {
    case 'github':
      return 'fab fa-github'
    case 'gitee':
      return 'fas fa-code-branch'
    default:
      return 'fas fa-question'
  }
}

function getProviderName(provider: string) {
  switch (provider) {
    case 'github':
      return 'GitHub'
    case 'gitee':
      return 'Gitee'
    default:
      return '未知'
  }
}

function unbindOAuth(record: any) {
  Modal.confirm({
    title: '确认解绑',
    content: `确定要解绑 ${getProviderName(record.provider)} 账号 "${record.username}" 吗？`,
    okType: 'danger',
    onOk() {
      record.status = 'inactive'
      message.success('账号已解绑')
    }
  })
}

function rebindOAuth(record: any) {
  Modal.confirm({
    title: '确认重新绑定',
    content: `确定要重新绑定 ${getProviderName(record.provider)} 账号 "${record.username}" 吗？`,
    onOk() {
      record.status = 'active'
      record.lastLogin = new Date().toLocaleString()
      message.success('账号已重新绑定')
    }
  })
}

function viewOAuthDetails(record: any) {
  Modal.info({
    title: `${getProviderName(record.provider)} 账号详情`,
    content: `
      用户名: ${record.username}
      邮箱: ${record.email}
      绑定时间: ${record.bindTime}
      最后登录: ${record.lastLogin}
      状态: ${record.status === 'active' ? '已绑定' : '已解绑'}
    `,
    width: 500
  })
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

/* OAuth配置卡片 */
.oauth-card {
  min-height: 500px;
}

.oauth-card :deep(.ant-tabs-content-holder) {
  padding: 16px 0;
}

.oauth-card :deep(.ant-tabs-tab) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.oauth-card :deep(.ant-tabs-tab i) {
  font-size: 16px;
}

.oauth-card :deep(.fab.fa-github) {
  color: #333;
}

.oauth-card :deep(.fas.fa-code-branch) {
  color: #c71d23;
}

/* OAuth统计信息 */
.oauth-stats {
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.oauth-stats h4 {
  margin: 0 0 16px 0;
  color: #262626;
  font-size: 16px;
}

/* 第三方账号绑定表格 */
.provider-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.provider-info i {
  font-size: 18px;
  width: 20px;
}

.provider-info .fab.fa-github {
  color: #333;
}

.provider-info .fas.fa-code-branch {
  color: #c71d23;
}

/* 表单项样式 */
:deep(.ant-form-item-label) {
  display: flex;
  align-items: center;
  gap: 8px;
}

:deep(.ant-form-item-label .fas.fa-question-circle) {
  color: #8c8c8c;
  cursor: help;
}

:deep(.ant-form-item-label .fas.fa-question-circle:hover) {
  color: #1890ff;
}

/* 开关样式 */
:deep(.ant-switch-checked) {
  background-color: #52c41a;
}

/* 按钮组样式 */
:deep(.ant-space) {
  width: 100%;
}

/* 统计卡片样式 */
:deep(.ant-statistic-title) {
  color: #8c8c8c;
  font-size: 14px;
}

:deep(.ant-statistic-content) {
  color: #262626;
  font-size: 20px;
  font-weight: 600;
}

/* 响应式 */
@media (max-width: 768px) {
  .oauth-card :deep(.ant-tabs-tab) {
    font-size: 12px;
    padding: 8px 12px;
  }
  
  .oauth-stats {
    padding: 12px;
  }
  
  .provider-info {
    flex-direction: column;
    text-align: center;
    gap: 4px;
  }
  
  .provider-info i {
    font-size: 24px;
  }
}

@media (max-width: 576px) {
  .oauth-card :deep(.ant-tabs-content-holder) {
    padding: 8px 0;
  }
  
  .oauth-stats h4 {
    font-size: 14px;
  }
  
  :deep(.ant-statistic-content) {
    font-size: 16px;
  }
}
</style>
