<template>
  <div class="settings-page">
    <div class="settings-header">
      <h1 class="page-title">系统设置</h1>
      <p class="page-description">管理系统的各项配置和参数</p>
    </div>
    
    <div class="settings-layout">
      <!-- 左侧菜单 -->
      <div class="settings-sidebar">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="inline"
          class="settings-menu"
          @click="handleMenuClick"
        >
          <a-menu-item key="basic">
            <template #icon>
              <i class="fas fa-cog"></i>
            </template>
            基本设置
          </a-menu-item>
          
          <a-menu-item key="oauth">
            <template #icon>
              <i class="fas fa-key"></i>
            </template>
            第三方登录
          </a-menu-item>
          
          <a-menu-item key="oauth-binding">
            <template #icon>
              <i class="fas fa-link"></i>
            </template>
            账号绑定管理
          </a-menu-item>
          
          <a-menu-item key="security">
            <template #icon>
              <i class="fas fa-shield-alt"></i>
            </template>
            安全设置
          </a-menu-item>
          
          <a-menu-item key="data">
            <template #icon>
              <i class="fas fa-database"></i>
            </template>
            数据管理
          </a-menu-item>
        </a-menu>
      </div>
      
      <!-- 右侧内容区域 -->
      <div class="settings-content">
        <!-- 基本设置 -->
        <div v-show="activeSection === 'basic'" class="settings-section">
          <div class="section-header">
            <h2>基本设置</h2>
            <p>配置系统的基本信息和参数</p>
          </div>
          
          <a-card>
            <a-form layout="vertical" :model="systemSettings">
              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="系统名称">
                    <a-input v-model:value="systemSettings.name" placeholder="请输入系统名称" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="管理员邮箱">
                    <a-input v-model:value="systemSettings.email" type="email" placeholder="请输入管理员邮箱" />
                  </a-form-item>
                </a-col>
              </a-row>
              
              <a-form-item label="系统描述">
                <a-textarea 
                  v-model:value="systemSettings.description" 
                  :rows="4" 
                  placeholder="请输入系统描述"
                />
              </a-form-item>
              
              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="系统版本">
                    <a-input v-model:value="systemSettings.version" disabled />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="系统状态">
                    <a-tag color="success">运行中</a-tag>
                  </a-form-item>
                </a-col>
              </a-row>
              
              <a-form-item>
                <a-space>
                  <a-button type="primary" @click="saveSettings">
                    <i class="fas fa-save"></i>
                    保存设置
                  </a-button>
                  <a-button @click="resetSettings">
                    <i class="fas fa-undo"></i>
                    重置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-form>
          </a-card>
        </div>
        
        <!-- 第三方登录配置 -->
        <div v-show="activeSection === 'oauth'" class="settings-section">
          <div class="section-header">
            <h2>第三方登录配置</h2>
            <p>配置GitHub和Gitee第三方登录服务</p>
          </div>
          
          <a-tabs v-model:activeKey="activeOAuthTab" type="card" class="oauth-tabs">
            <!-- GitHub配置 -->
            <a-tab-pane key="github">
              <template #tab>
                <span class="tab-title">
                  <i class="fab fa-github"></i>
                  GitHub
                </span>
              </template>
              
              <a-card>
                <a-form layout="vertical">
                  <a-form-item>
                    <template #label>
                      <span class="form-label">
                        启用GitHub登录
                        <a-tooltip title="允许用户使用GitHub账号登录系统">
                          <i class="fas fa-question-circle help-icon"></i>
                        </a-tooltip>
                      </span>
                    </template>
                    <a-switch 
                      v-model:checked="oauthSettings.github.enabled" 
                      :loading="oauthSettings.github.loading"
                      @change="toggleGitHubOAuth"
                    />
                  </a-form-item>
                  
                  <div v-if="oauthSettings.github.enabled" class="oauth-config">
                    <a-row :gutter="24">
                      <a-col :span="12">
                        <a-form-item label="Client ID">
                          <a-input 
                            v-model:value="oauthSettings.github.clientId" 
                            placeholder="GitHub应用的Client ID"
                          />
                        </a-form-item>
                      </a-col>
                      <a-col :span="12">
                        <a-form-item label="Client Secret">
                          <a-input-password 
                            v-model:value="oauthSettings.github.clientSecret" 
                            placeholder="GitHub应用的Client Secret"
                          />
                        </a-form-item>
                      </a-col>
                    </a-row>
                    
                    <a-form-item label="回调URL">
                      <a-input 
                        v-model:value="oauthSettings.github.callbackUrl" 
                        disabled
                        addon-before="回调地址"
                      />
                    </a-form-item>
                    
                    <a-form-item>
                      <a-space>
                        <a-button type="primary" @click="saveGitHubSettings">
                          <i class="fas fa-save"></i>
                          保存配置
                        </a-button>
                        <a-button @click="testGitHubConnection">
                          <i class="fas fa-plug"></i>
                          测试连接
                        </a-button>
                      </a-space>
                    </a-form-item>
                    
                    <!-- GitHub统计信息 -->
                    <a-divider>统计信息</a-divider>
                    <div class="oauth-stats">
                      <a-row :gutter="16">
                        <a-col :span="8">
                          <a-statistic title="绑定用户" :value="oauthSettings.github.stats.boundUsers" />
                        </a-col>
                        <a-col :span="8">
                          <a-statistic title="本月登录" :value="oauthSettings.github.stats.monthlyLogins" />
                        </a-col>
                        <a-col :span="8">
                          <a-statistic title="成功率" :value="96.8" suffix="%" />
                        </a-col>
                      </a-row>
                    </div>
                  </div>
                </a-form>
              </a-card>
            </a-tab-pane>
            
            <!-- Gitee配置 -->
            <a-tab-pane key="gitee">
              <template #tab>
                <span class="tab-title">
                  <i class="fas fa-code-branch"></i>
                  Gitee
                </span>
              </template>
              
              <a-card>
                <a-form layout="vertical">
                  <a-form-item>
                    <template #label>
                      <span class="form-label">
                        启用Gitee登录
                        <a-tooltip title="允许用户使用Gitee账号登录系统">
                          <i class="fas fa-question-circle help-icon"></i>
                        </a-tooltip>
                      </span>
                    </template>
                    <a-switch 
                      v-model:checked="oauthSettings.gitee.enabled" 
                      :loading="oauthSettings.gitee.loading"
                      @change="toggleGiteeOAuth"
                    />
                  </a-form-item>
                  
                  <div v-if="oauthSettings.gitee.enabled" class="oauth-config">
                    <a-row :gutter="24">
                      <a-col :span="12">
                        <a-form-item label="Client ID">
                          <a-input 
                            v-model:value="oauthSettings.gitee.clientId" 
                            placeholder="Gitee应用的Client ID"
                          />
                        </a-form-item>
                      </a-col>
                      <a-col :span="12">
                        <a-form-item label="Client Secret">
                          <a-input-password 
                            v-model:value="oauthSettings.gitee.clientSecret" 
                            placeholder="Gitee应用的Client Secret"
                          />
                        </a-form-item>
                      </a-col>
                    </a-row>
                    
                    <a-form-item label="回调URL">
                      <a-input 
                        v-model:value="oauthSettings.gitee.callbackUrl" 
                        disabled
                        addon-before="回调地址"
                      />
                    </a-form-item>
                    
                    <a-form-item>
                      <a-space>
                        <a-button type="primary" @click="saveGiteeSettings">
                          <i class="fas fa-save"></i>
                          保存配置
                        </a-button>
                        <a-button @click="testGiteeConnection">
                          <i class="fas fa-plug"></i>
                          测试连接
                        </a-button>
                      </a-space>
                    </a-form-item>
                    
                    <!-- Gitee统计信息 -->
                    <a-divider>统计信息</a-divider>
                    <div class="oauth-stats">
                      <a-row :gutter="16">
                        <a-col :span="8">
                          <a-statistic title="绑定用户" :value="oauthSettings.gitee.stats.boundUsers" />
                        </a-col>
                        <a-col :span="8">
                          <a-statistic title="本月登录" :value="oauthSettings.gitee.stats.monthlyLogins" />
                        </a-col>
                        <a-col :span="8">
                          <a-statistic title="成功率" :value="94.2" suffix="%" />
                        </a-col>
                      </a-row>
                    </div>
                  </div>
                </a-form>
              </a-card>
            </a-tab-pane>
          </a-tabs>
        </div>
        
        <!-- 第三方账号绑定管理 -->
        <div v-show="activeSection === 'oauth-binding'" class="settings-section">
          <div class="section-header">
            <h2>第三方账号绑定管理</h2>
            <p>管理用户的第三方账号绑定情况</p>
          </div>
          
          <a-card>
            <template #extra>
              <a-space>
                <a-input-search
                  v-model:value="bindingSearchKeyword"
                  placeholder="搜索用户名或邮箱"
                  style="width: 250px"
                  @search="handleBindingSearch"
                />
                <a-button @click="refreshBindings">
                  <i class="fas fa-sync-alt"></i>
                  刷新
                </a-button>
              </a-space>
            </template>
            
            <a-table 
              :columns="oauthBindingColumns" 
              :data-source="filteredOauthBindings"
              :pagination="bindingPagination"
              row-key="id"
              @change="handleBindingTableChange"
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
        </div>
        
        <!-- 安全设置 -->
        <div v-show="activeSection === 'security'" class="settings-section">
          <div class="section-header">
            <h2>安全设置</h2>
            <p>配置系统的安全策略和认证方式</p>
          </div>
          
          <a-card>
            <a-form layout="vertical" :model="securitySettings">
              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="登录超时时间">
                    <a-input-number 
                      v-model:value="securitySettings.loginTimeout" 
                      :min="5" 
                      :max="1440"
                      style="width: 100%"
                      addon-after="分钟"
                    />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="密码策略">
                    <a-select v-model:value="securitySettings.passwordPolicy" style="width: 100%">
                      <a-select-option value="low">低（6位以上）</a-select-option>
                      <a-select-option value="medium">中（8位，包含字母数字）</a-select-option>
                      <a-select-option value="high">高（8位，包含字母数字特殊字符）</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
              </a-row>
              
              <a-form-item label="安全选项">
                <a-space direction="vertical">
                  <a-checkbox v-model:checked="securitySettings.twoFactorAuth">
                    启用双因子认证
                  </a-checkbox>
                  <a-checkbox v-model:checked="securitySettings.captchaEnabled">
                    启用验证码
                  </a-checkbox>
                  <a-checkbox v-model:checked="securitySettings.ipWhitelist">
                    启用IP白名单
                  </a-checkbox>
                </a-space>
              </a-form-item>
              
              <a-form-item>
                <a-space>
                  <a-button type="primary" @click="saveSecuritySettings">
                    <i class="fas fa-save"></i>
                    保存设置
                  </a-button>
                  <a-button @click="resetSecuritySettings">
                    <i class="fas fa-undo"></i>
                    重置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-form>
          </a-card>
        </div>
        
        <!-- 数据管理 -->
        <div v-show="activeSection === 'data'" class="settings-section">
          <div class="section-header">
            <h2>数据管理</h2>
            <p>管理系统数据备份、日志和存储</p>
          </div>
          
          <a-row :gutter="24">
            <a-col :span="12">
              <a-card title="备份设置" class="data-card">
                <a-form layout="vertical" :model="dataSettings">
                  <a-form-item label="数据备份频率">
                    <a-select v-model:value="dataSettings.backupFrequency" style="width: 100%">
                      <a-select-option value="daily">每日</a-select-option>
                      <a-select-option value="weekly">每周</a-select-option>
                      <a-select-option value="monthly">每月</a-select-option>
                    </a-select>
                  </a-form-item>
                  
                  <a-form-item label="备份保留数量">
                    <a-input-number 
                      v-model:value="dataSettings.backupRetention" 
                      :min="1" 
                      :max="30"
                      style="width: 100%"
                      addon-after="个"
                    />
                  </a-form-item>
                  
                  <a-form-item>
                    <a-space direction="vertical" style="width: 100%">
                      <a-button type="primary" block @click="backupData">
                        <i class="fas fa-download"></i>
                        立即备份
                      </a-button>
                      <a-button block @click="viewBackupHistory">
                        <i class="fas fa-history"></i>
                        备份历史
                      </a-button>
                    </a-space>
                  </a-form-item>
                </a-form>
              </a-card>
            </a-col>
            
            <a-col :span="12">
              <a-card title="日志管理" class="data-card">
                <a-form layout="vertical" :model="dataSettings">
                  <a-form-item label="日志保留天数">
                    <a-input-number 
                      v-model:value="dataSettings.logRetentionDays" 
                      :min="7" 
                      :max="365"
                      style="width: 100%"
                      addon-after="天"
                    />
                  </a-form-item>
                  
                  <a-form-item label="日志级别">
                    <a-select v-model:value="dataSettings.logLevel" style="width: 100%">
                      <a-select-option value="error">错误</a-select-option>
                      <a-select-option value="warn">警告</a-select-option>
                      <a-select-option value="info">信息</a-select-option>
                      <a-select-option value="debug">调试</a-select-option>
                    </a-select>
                  </a-form-item>
                  
                  <a-form-item>
                    <a-space direction="vertical" style="width: 100%">
                      <a-button type="primary" block @click="clearLogs">
                        <i class="fas fa-trash"></i>
                        清理日志
                      </a-button>
                      <a-button block @click="downloadLogs">
                        <i class="fas fa-download"></i>
                        下载日志
                      </a-button>
                    </a-space>
                  </a-form-item>
                </a-form>
              </a-card>
            </a-col>
          </a-row>
          
          <a-card title="存储统计" style="margin-top: 24px">
            <a-row :gutter="16">
              <a-col :span="6">
                <a-statistic title="数据库大小" value="2.1" suffix="GB" />
              </a-col>
              <a-col :span="6">
                <a-statistic title="图片存储" value="15.6" suffix="GB" />
              </a-col>
              <a-col :span="6">
                <a-statistic title="日志大小" value="892" suffix="MB" />
              </a-col>
              <a-col :span="6">
                <a-statistic title="缓存大小" value="156" suffix="MB" />
              </a-col>
            </a-row>
            
            <a-form-item style="margin-top: 24px">
              <a-button type="primary" @click="saveDataSettings">
                <i class="fas fa-save"></i>
                保存设置
              </a-button>
            </a-form-item>
          </a-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'

// 页面状态
const selectedKeys = ref(['basic'])
const activeSection = ref('basic')

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

// OAuth绑定管理
const bindingSearchKeyword = ref('')
const bindingPagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
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

// 过滤后的OAuth绑定数据
const filteredOauthBindings = computed(() => {
  let result = oauthBindings
  
  if (bindingSearchKeyword.value) {
    const keyword = bindingSearchKeyword.value.toLowerCase()
    result = result.filter(binding => 
      binding.username.toLowerCase().includes(keyword) ||
      binding.email.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 安全设置
const securitySettings = reactive({
  loginTimeout: 30,
  passwordPolicy: 'medium',
  twoFactorAuth: false,
  captchaEnabled: true,
  ipWhitelist: false
})

// 数据设置
const dataSettings = reactive({
  backupFrequency: 'daily',
  logRetentionDays: 30,
  backupRetention: 7,
  logLevel: 'info'
})

// 页面导航方法
function handleMenuClick({ key }: { key: string }) {
  selectedKeys.value = [key]
  activeSection.value = key
}

// 基本设置方法
function saveSettings() {
  message.success('系统设置已保存')
}

function resetSettings() {
  Modal.confirm({
    title: '确认重置',
    content: '确定要重置系统设置吗？这将恢复到默认配置。',
    onOk() {
      systemSettings.name = '智能图像识别系统'
      systemSettings.email = 'admin@example.com'
      systemSettings.description = '基于深度学习的通用图像识别平台，提供识别、学习、交流一体化服务'
      message.success('系统设置已重置')
    }
  })
}

// 安全设置方法
function saveSecuritySettings() {
  message.success('安全设置已保存')
}

function resetSecuritySettings() {
  Modal.confirm({
    title: '确认重置',
    content: '确定要重置安全设置吗？',
    onOk() {
      securitySettings.loginTimeout = 30
      securitySettings.passwordPolicy = 'medium'
      securitySettings.twoFactorAuth = false
      securitySettings.captchaEnabled = true
      securitySettings.ipWhitelist = false
      message.success('安全设置已重置')
    }
  })
}

// 数据管理方法
function saveDataSettings() {
  message.success('数据设置已保存')
}

function backupData() {
  message.loading('正在创建数据备份...', 3)
  setTimeout(() => {
    message.success('数据备份已完成')
  }, 3000)
}

function viewBackupHistory() {
  Modal.info({
    title: '备份历史',
    content: `
      最近备份记录：
      • 2025-01-15 02:00 - 自动备份 (2.1GB)
      • 2025-01-14 02:00 - 自动备份 (2.0GB)
      • 2025-01-13 02:00 - 自动备份 (1.9GB)
      • 2025-01-12 15:30 - 手动备份 (1.9GB)
    `,
    width: 500
  })
}

function clearLogs() {
  Modal.confirm({
    title: '确认清理日志',
    content: '确定要清理系统日志吗？这将删除所有过期的日志文件。',
    onOk() {
      message.loading('正在清理日志...', 2)
      setTimeout(() => {
        message.success('日志清理完成，释放空间 156MB')
      }, 2000)
    }
  })
}

function downloadLogs() {
  message.loading('正在打包日志文件...', 2)
  setTimeout(() => {
    message.success('日志文件下载已开始')
  }, 2000)
}

// OAuth绑定管理方法
function handleBindingSearch() {
  bindingPagination.current = 1
}

function refreshBindings() {
  message.loading('正在刷新数据...', 1)
  setTimeout(() => {
    message.success('数据已刷新')
  }, 1000)
}

function handleBindingTableChange(pag: any) {
  bindingPagination.current = pag.current
  bindingPagination.pageSize = pag.pageSize
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

