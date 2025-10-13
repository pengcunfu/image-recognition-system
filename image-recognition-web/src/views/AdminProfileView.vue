<template>
  <div class="admin-profile-page">
    <div class="profile-header">
      <div class="header-content">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <a-avatar :size="100" :src="adminInfo.avatar" class="admin-avatar">
              {{ adminInfo.name.charAt(0) }}
            </a-avatar>
            <div class="avatar-overlay" @click="showAvatarModal">
              <i class="fas fa-camera"></i>
              <span>更换头像</span>
            </div>
          </div>
        </div>
        
        <div class="admin-info">
          <h1>{{ adminInfo.name }}</h1>
          <div class="admin-role">
            <a-tag color="red" class="role-tag">
              <i class="fas fa-crown"></i>
              系统管理员
            </a-tag>
          </div>
          <p>{{ adminInfo.bio }}</p>
          <div class="admin-meta">
            <span>
              <i class="fas fa-calendar"></i>
              管理员权限获得于 {{ adminInfo.adminSince }}
            </span>
            <span>
              <i class="fas fa-shield-alt"></i>
              权限等级：{{ adminInfo.permissionLevel }}
            </span>
            <span>
              <i class="fas fa-clock"></i>
              最后登录：{{ adminInfo.lastLogin }}
            </span>
          </div>
        </div>
        
        <div class="admin-stats">
          <div class="stat-item">
            <div class="stat-number">{{ adminStats.totalUsers }}</div>
            <div class="stat-label">用户总数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ adminStats.todayRecognitions }}</div>
            <div class="stat-label">今日识别</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ adminStats.systemHealth }}</div>
            <div class="stat-label">系统健康度</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ adminStats.pendingReviews }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <a-row :gutter="24">
        <!-- 左侧内容 -->
        <a-col :xs="24" :lg="16">
          <a-tabs v-model:activeKey="activeTab">
            <!-- 管理活动 -->
            <a-tab-pane key="activities" tab="管理活动">
              <div class="activities-list">
                <div 
                  v-for="activity in adminActivities" 
                  :key="activity.id"
                  class="activity-item"
                >
                  <div class="activity-icon">
                    <i :class="getActivityIcon(activity.type)"></i>
                  </div>
                  <div class="activity-content">
                    <div class="activity-text">{{ activity.description }}</div>
                    <div class="activity-time">{{ activity.time }}</div>
                    <div class="activity-details" v-if="activity.details">
                      <a-tag :color="getActivityColor(activity.type)" size="small">
                        {{ activity.details }}
                      </a-tag>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 系统监控 -->
            <a-tab-pane key="monitoring" tab="系统监控">
              <div class="monitoring-dashboard">
                <a-row :gutter="16" class="monitoring-stats">
                  <a-col :span="6">
                    <a-statistic 
                      title="在线用户" 
                      :value="systemStats.onlineUsers" 
                      suffix="人"
                      :value-style="{ color: '#3f8600' }"
                    />
                  </a-col>
                  <a-col :span="6">
                    <a-statistic 
                      title="今日识别" 
                      :value="systemStats.todayRecognitions" 
                      suffix="次"
                      :value-style="{ color: '#1890ff' }"
                    />
                  </a-col>
                  <a-col :span="6">
                    <a-statistic 
                      title="系统负载" 
                      :value="systemStats.systemLoad" 
                      suffix="%"
                      :value-style="{ color: systemStats.systemLoad > 80 ? '#cf1322' : '#3f8600' }"
                    />
                  </a-col>
                  <a-col :span="6">
                    <a-statistic 
                      title="存储使用" 
                      :value="systemStats.storageUsed" 
                      suffix="%"
                      :value-style="{ color: systemStats.storageUsed > 85 ? '#cf1322' : '#3f8600' }"
                    />
                  </a-col>
                </a-row>

                <div class="monitoring-charts">
                  <a-row :gutter="16">
                    <a-col :span="12">
                      <a-card title="用户活跃度趋势" size="small">
                        <div class="chart-placeholder">
                          <i class="fas fa-chart-line"></i>
                          <span>7天用户活跃度变化</span>
                        </div>
                      </a-card>
                    </a-col>
                    <a-col :span="12">
                      <a-card title="识别请求分布" size="small">
                        <div class="chart-placeholder">
                          <i class="fas fa-chart-pie"></i>
                          <span>24小时识别请求分布</span>
                        </div>
                      </a-card>
                    </a-col>
                  </a-row>
                </div>

                <a-card title="系统状态" size="small" class="system-status">
                  <div class="status-grid">
                    <div 
                      v-for="service in systemServices" 
                      :key="service.name"
                      class="service-item"
                      :class="{ 'service-error': service.status === 'error', 'service-warning': service.status === 'warning' }"
                    >
                      <div class="service-icon">
                        <i :class="service.icon"></i>
                      </div>
                      <div class="service-info">
                        <div class="service-name">{{ service.name }}</div>
                        <div class="service-status">
                          <a-tag :color="getStatusColor(service.status)">
                            {{ getStatusText(service.status) }}
                          </a-tag>
                        </div>
                      </div>
                      <div class="service-metrics">
                        <div class="metric">响应时间: {{ service.responseTime }}ms</div>
                        <div class="metric">可用性: {{ service.uptime }}%</div>
                      </div>
                    </div>
                  </div>
                </a-card>
              </div>
            </a-tab-pane>

            <!-- 操作日志 -->
            <a-tab-pane key="logs" tab="操作日志">
              <div class="logs-container">
                <div class="logs-filter">
                  <a-row :gutter="16">
                    <a-col :span="6">
                      <a-select v-model:value="logFilter.type" placeholder="操作类型" style="width: 100%">
                        <a-select-option value="">全部类型</a-select-option>
                        <a-select-option value="user">用户管理</a-select-option>
                        <a-select-option value="content">内容管理</a-select-option>
                        <a-select-option value="system">系统设置</a-select-option>
                        <a-select-option value="security">安全操作</a-select-option>
                      </a-select>
                    </a-col>
                    <a-col :span="6">
                      <a-select v-model:value="logFilter.level" placeholder="日志级别" style="width: 100%">
                        <a-select-option value="">全部级别</a-select-option>
                        <a-select-option value="info">信息</a-select-option>
                        <a-select-option value="warning">警告</a-select-option>
                        <a-select-option value="error">错误</a-select-option>
                      </a-select>
                    </a-col>
                    <a-col :span="8">
                      <a-range-picker v-model:value="logFilter.dateRange" style="width: 100%" />
                    </a-col>
                    <a-col :span="4">
                      <a-button type="primary" @click="filterLogs">
                        <i class="fas fa-search"></i>
                        筛选
                      </a-button>
                    </a-col>
                  </a-row>
                </div>

                <div class="logs-list">
                  <div 
                    v-for="log in operationLogs" 
                    :key="log.id"
                    class="log-item"
                    :class="`log-${log.level}`"
                  >
                    <div class="log-icon">
                      <i :class="getLogIcon(log.level)"></i>
                    </div>
                    <div class="log-content">
                      <div class="log-header">
                        <span class="log-action">{{ log.action }}</span>
                        <span class="log-time">{{ log.timestamp }}</span>
                      </div>
                      <div class="log-description">{{ log.description }}</div>
                      <div class="log-meta">
                        <a-tag :color="getLogLevelColor(log.level)" size="small">
                          {{ log.level.toUpperCase() }}
                        </a-tag>
                        <span class="log-ip">IP: {{ log.ipAddress }}</span>
                        <span class="log-user" v-if="log.targetUser">目标用户: {{ log.targetUser }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 权限管理 -->
            <a-tab-pane key="permissions" tab="权限管理">
              <div class="permissions-container">
                <a-card title="我的权限" class="permissions-card">
                  <div class="permissions-grid">
                    <div 
                      v-for="permission in adminPermissions" 
                      :key="permission.key"
                      class="permission-item"
                      :class="{ 'permission-granted': permission.granted }"
                    >
                      <div class="permission-icon">
                        <i :class="permission.icon"></i>
                      </div>
                      <div class="permission-info">
                        <div class="permission-name">{{ permission.name }}</div>
                        <div class="permission-desc">{{ permission.description }}</div>
                      </div>
                      <div class="permission-status">
                        <a-tag :color="permission.granted ? 'green' : 'red'">
                          {{ permission.granted ? '已授权' : '未授权' }}
                        </a-tag>
                      </div>
                    </div>
                  </div>
                </a-card>

                <a-card title="管理员等级说明" class="admin-levels-card">
                  <div class="levels-list">
                    <div 
                      v-for="level in adminLevels" 
                      :key="level.level"
                      class="level-item"
                      :class="{ 'current-level': level.level === adminInfo.permissionLevel }"
                    >
                      <div class="level-badge">
                        <i :class="level.icon"></i>
                        <span>{{ level.name }}</span>
                      </div>
                      <div class="level-description">{{ level.description }}</div>
                      <div class="level-permissions">
                        <a-tag 
                          v-for="perm in level.permissions" 
                          :key="perm"
                          size="small"
                          color="blue"
                        >
                          {{ perm }}
                        </a-tag>
                      </div>
                    </div>
                  </div>
                </a-card>
              </div>
            </a-tab-pane>

            <!-- 个人设置 -->
            <a-tab-pane key="settings" tab="个人设置">
              <a-card title="基本信息" class="settings-card">
                <a-form layout="vertical">
                  <a-row :gutter="16">
                    <a-col :span="12">
                      <a-form-item label="管理员姓名">
                        <a-input v-model:value="editInfo.name" />
                      </a-form-item>
                    </a-col>
                    <a-col :span="12">
                      <a-form-item label="邮箱地址">
                        <a-input v-model:value="editInfo.email" />
                      </a-form-item>
                    </a-col>
                  </a-row>
                  <a-form-item label="个人简介">
                    <a-textarea v-model:value="editInfo.bio" :rows="3" />
                  </a-form-item>
                  <a-form-item label="联系电话">
                    <a-input v-model:value="editInfo.phone" />
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveProfile">
                      保存修改
                    </a-button>
                  </a-form-item>
                </a-form>
              </a-card>

              <a-card title="安全设置" class="settings-card">
                <a-form layout="vertical">
                  <a-form-item>
                    <a-checkbox v-model:checked="securitySettings.twoFactorAuth">
                      启用双重认证
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="securitySettings.loginNotification">
                      登录邮件通知
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="securitySettings.operationLog">
                      记录详细操作日志
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item label="会话超时时间">
                    <a-select v-model:value="securitySettings.sessionTimeout" style="width: 200px">
                      <a-select-option :value="30">30分钟</a-select-option>
                      <a-select-option :value="60">1小时</a-select-option>
                      <a-select-option :value="120">2小时</a-select-option>
                      <a-select-option :value="240">4小时</a-select-option>
                    </a-select>
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveSecurity">
                      保存设置
                    </a-button>
                  </a-form-item>
                </a-form>
              </a-card>

              <a-card title="通知设置" class="settings-card">
                <a-form layout="vertical">
                  <a-form-item>
                    <a-checkbox v-model:checked="notificationSettings.systemAlerts">
                      系统异常警报
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="notificationSettings.userReports">
                      用户举报通知
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="notificationSettings.contentReview">
                      内容审核提醒
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-checkbox v-model:checked="notificationSettings.dailyReport">
                      每日数据报告
                    </a-checkbox>
                  </a-form-item>
                  <a-form-item>
                    <a-button type="primary" @click="saveNotifications">
                      保存设置
                    </a-button>
                  </a-form-item>
                </a-form>
              </a-card>
            </a-tab-pane>
          </a-tabs>
        </a-col>

        <!-- 右侧侧边栏 -->
        <a-col :xs="24" :lg="8">
          <!-- 快捷操作 -->
          <a-card title="快捷操作" class="quick-actions-card">
            <div class="actions-grid">
              <div class="action-item" @click="goToUserManagement">
                <div class="action-icon">
                  <i class="fas fa-users"></i>
                </div>
                <div class="action-text">用户管理</div>
              </div>
              <div class="action-item" @click="goToContentManagement">
                <div class="action-icon">
                  <i class="fas fa-file-alt"></i>
                </div>
                <div class="action-text">内容管理</div>
              </div>
              <div class="action-item" @click="goToSystemSettings">
                <div class="action-icon">
                  <i class="fas fa-cog"></i>
                </div>
                <div class="action-text">系统设置</div>
              </div>
              <div class="action-item" @click="goToAnalytics">
                <div class="action-icon">
                  <i class="fas fa-chart-bar"></i>
                </div>
                <div class="action-text">数据分析</div>
              </div>
            </div>
          </a-card>

          <!-- 系统警报 -->
          <a-card title="系统警报" class="alerts-card">
            <div class="alerts-list">
              <div 
                v-for="alert in systemAlerts" 
                :key="alert.id"
                class="alert-item"
                :class="`alert-${alert.level}`"
              >
                <div class="alert-icon">
                  <i :class="getAlertIcon(alert.level)"></i>
                </div>
                <div class="alert-content">
                  <div class="alert-title">{{ alert.title }}</div>
                  <div class="alert-time">{{ alert.time }}</div>
                </div>
              </div>
            </div>
          </a-card>

          <!-- 最近登录 -->
          <a-card title="最近登录记录" class="login-history-card">
            <div class="login-list">
              <div 
                v-for="login in recentLogins" 
                :key="login.id"
                class="login-item"
              >
                <div class="login-info">
                  <div class="login-time">{{ login.time }}</div>
                  <div class="login-details">
                    <span class="login-ip">{{ login.ipAddress }}</span>
                    <span class="login-location">{{ login.location }}</span>
                  </div>
                </div>
                <div class="login-status">
                  <a-tag :color="login.success ? 'green' : 'red'" size="small">
                    {{ login.success ? '成功' : '失败' }}
                  </a-tag>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 头像上传模态框 -->
    <a-modal
      v-model:open="avatarModalVisible"
      title="更换管理员头像"
      :width="600"
      @ok="handleAvatarUpload"
      @cancel="cancelAvatarUpload"
      :confirm-loading="uploadLoading"
    >
      <div class="avatar-upload-container">
        <!-- 当前头像预览 -->
        <div class="current-avatar">
          <h4>当前头像</h4>
          <a-avatar :size="80" :src="adminInfo.avatar" class="preview-avatar">
            {{ adminInfo.name.charAt(0) }}
          </a-avatar>
        </div>

        <!-- 新头像预览 -->
        <div class="new-avatar" v-if="newAvatarUrl">
          <h4>新头像预览</h4>
          <a-avatar :size="80" :src="newAvatarUrl" class="preview-avatar" />
        </div>

        <!-- 上传区域 -->
        <div class="upload-section">
          <a-upload
            v-model:file-list="avatarFileList"
            name="avatar"
            list-type="picture-card"
            class="avatar-uploader"
            :show-upload-list="false"
            :before-upload="beforeAvatarUpload"
            @change="handleAvatarChange"
            accept="image/*"
          >
            <div v-if="!newAvatarUrl" class="upload-placeholder">
              <i class="fas fa-plus"></i>
              <div class="upload-text">选择图片</div>
            </div>
            <img v-else :src="newAvatarUrl" alt="avatar" class="upload-preview" />
          </a-upload>
          
          <div class="upload-tips">
            <p><i class="fas fa-info-circle"></i> 支持 JPG、PNG 格式</p>
            <p><i class="fas fa-info-circle"></i> 建议尺寸 200x200 像素</p>
            <p><i class="fas fa-info-circle"></i> 文件大小不超过 2MB</p>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const router = useRouter()
const activeTab = ref('activities')

// 管理员信息
const adminInfo = reactive({
  name: '系统管理员',
  bio: '负责系统运维、用户管理和内容审核的高级管理员',
  avatar: '',
  adminSince: '2024年1月',
  permissionLevel: '超级管理员',
  lastLogin: '2025年10月13日 14:30',
  email: 'admin@example.com',
  phone: '138****8888'
})

// 编辑信息
const editInfo = reactive({
  name: adminInfo.name,
  bio: adminInfo.bio,
  email: adminInfo.email,
  phone: adminInfo.phone
})

// 管理员统计
const adminStats = reactive({
  totalUsers: 1248,
  todayRecognitions: 356,
  systemHealth: '98%',
  pendingReviews: 12
})

// 系统统计
const systemStats = reactive({
  onlineUsers: 89,
  todayRecognitions: 356,
  systemLoad: 45,
  storageUsed: 67
})

// 安全设置
const securitySettings = reactive({
  twoFactorAuth: true,
  loginNotification: true,
  operationLog: true,
  sessionTimeout: 120
})

// 通知设置
const notificationSettings = reactive({
  systemAlerts: true,
  userReports: true,
  contentReview: true,
  dailyReport: false
})

// 日志筛选
const logFilter = reactive({
  type: '',
  level: '',
  dateRange: null
})

// 管理活动记录
const adminActivities = ref([
  {
    id: 1,
    type: 'user_management',
    description: '禁用了违规用户账号',
    time: '30分钟前',
    details: '用户ID: 12345'
  },
  {
    id: 2,
    type: 'content_review',
    description: '审核通过了3篇知识文章',
    time: '1小时前',
    details: '知识管理'
  },
  {
    id: 3,
    type: 'system_config',
    description: '更新了系统配置参数',
    time: '2小时前',
    details: '系统设置'
  },
  {
    id: 4,
    type: 'security',
    description: '处理了安全警报',
    time: '3小时前',
    details: '安全事件'
  }
])

// 系统服务状态
const systemServices = ref([
  {
    name: '图像识别服务',
    status: 'healthy',
    icon: 'fas fa-eye',
    responseTime: 120,
    uptime: 99.9
  },
  {
    name: '用户认证服务',
    status: 'healthy',
    icon: 'fas fa-shield-alt',
    responseTime: 45,
    uptime: 99.8
  },
  {
    name: '数据库服务',
    status: 'warning',
    icon: 'fas fa-database',
    responseTime: 200,
    uptime: 98.5
  },
  {
    name: '文件存储服务',
    status: 'healthy',
    icon: 'fas fa-cloud',
    responseTime: 80,
    uptime: 99.7
  }
])

// 操作日志
const operationLogs = ref([
  {
    id: 1,
    action: '用户管理',
    description: '禁用用户账号 "test_user" 由于违规行为',
    level: 'warning',
    timestamp: '2025-10-13 14:25:30',
    ipAddress: '192.168.1.100',
    targetUser: 'test_user'
  },
  {
    id: 2,
    action: '内容审核',
    description: '批量审核通过知识文章，共3篇',
    level: 'info',
    timestamp: '2025-10-13 13:45:15',
    ipAddress: '192.168.1.100',
    targetUser: null
  },
  {
    id: 3,
    action: '系统配置',
    description: '修改系统参数：最大上传文件大小调整为10MB',
    level: 'info',
    timestamp: '2025-10-13 12:30:45',
    ipAddress: '192.168.1.100',
    targetUser: null
  },
  {
    id: 4,
    action: '安全处理',
    description: '处理异常登录尝试，IP已加入黑名单',
    level: 'error',
    timestamp: '2025-10-13 11:15:20',
    ipAddress: '192.168.1.100',
    targetUser: null
  }
])

// 管理员权限
const adminPermissions = ref([
  {
    key: 'user_management',
    name: '用户管理',
    description: '创建、编辑、删除用户账号',
    icon: 'fas fa-users',
    granted: true
  },
  {
    key: 'content_management',
    name: '内容管理',
    description: '审核、编辑、删除用户内容',
    icon: 'fas fa-file-alt',
    granted: true
  },
  {
    key: 'system_config',
    name: '系统配置',
    description: '修改系统参数和配置',
    icon: 'fas fa-cog',
    granted: true
  },
  {
    key: 'data_analytics',
    name: '数据分析',
    description: '查看系统数据和统计报告',
    icon: 'fas fa-chart-bar',
    granted: true
  },
  {
    key: 'security_management',
    name: '安全管理',
    description: '处理安全事件和权限管理',
    icon: 'fas fa-shield-alt',
    granted: true
  },
  {
    key: 'backup_restore',
    name: '备份恢复',
    description: '系统备份和数据恢复',
    icon: 'fas fa-database',
    granted: false
  }
])

// 管理员等级
const adminLevels = ref([
  {
    level: '超级管理员',
    name: '超级管理员',
    icon: 'fas fa-crown',
    description: '拥有系统最高权限，可以执行所有管理操作',
    permissions: ['用户管理', '内容管理', '系统配置', '数据分析', '安全管理']
  },
  {
    level: '高级管理员',
    name: '高级管理员',
    icon: 'fas fa-star',
    description: '拥有大部分管理权限，负责日常运营管理',
    permissions: ['用户管理', '内容管理', '数据分析']
  },
  {
    level: '普通管理员',
    name: '普通管理员',
    icon: 'fas fa-user-tie',
    description: '拥有基础管理权限，主要负责内容审核',
    permissions: ['内容管理', '数据分析']
  }
])

// 系统警报
const systemAlerts = ref([
  {
    id: 1,
    title: '存储空间使用率过高',
    level: 'warning',
    time: '10分钟前'
  },
  {
    id: 2,
    title: '数据库连接异常',
    level: 'error',
    time: '1小时前'
  },
  {
    id: 3,
    title: '新用户注册激增',
    level: 'info',
    time: '2小时前'
  }
])

// 最近登录记录
const recentLogins = ref([
  {
    id: 1,
    time: '2025-10-13 14:30',
    ipAddress: '192.168.1.100',
    location: '北京市',
    success: true
  },
  {
    id: 2,
    time: '2025-10-12 09:15',
    ipAddress: '192.168.1.100',
    location: '北京市',
    success: true
  },
  {
    id: 3,
    time: '2025-10-11 16:45',
    ipAddress: '10.0.0.50',
    location: '上海市',
    success: false
  }
])

// 头像上传相关变量
const avatarModalVisible = ref(false)
const uploadLoading = ref(false)
const newAvatarUrl = ref('')
const avatarFileList = ref([])

// 方法
function showAvatarModal() {
  avatarModalVisible.value = true
  newAvatarUrl.value = ''
  avatarFileList.value = []
}

function cancelAvatarUpload() {
  avatarModalVisible.value = false
  newAvatarUrl.value = ''
  avatarFileList.value = []
}

function beforeAvatarUpload(file: any): boolean {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  return false // 阻止自动上传，我们手动处理
}

function handleAvatarChange(info: any) {
  if (info.file && info.file.originFileObj) {
    const reader = new FileReader()
    reader.addEventListener('load', () => {
      newAvatarUrl.value = reader.result as string
    })
    reader.readAsDataURL(info.file.originFileObj)
  }
}

async function handleAvatarUpload() {
  if (!newAvatarUrl.value) {
    message.warning('请选择头像')
    return
  }

  uploadLoading.value = true
  
  try {
    // 模拟上传过程
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    // 更新管理员头像
    adminInfo.avatar = newAvatarUrl.value
    
    message.success('头像更换成功!')
    avatarModalVisible.value = false
    
    // 重置状态
    newAvatarUrl.value = ''
    avatarFileList.value = []
    
  } catch (error: unknown) {
    console.error('头像上传失败:', error)
    const errorMessage = error instanceof Error ? error.message : '头像上传失败，请重试'
    message.error(errorMessage)
  } finally {
    uploadLoading.value = false
  }
}

function getActivityIcon(type: string): string {
  const icons: Record<string, string> = {
    'user_management': 'fas fa-users',
    'content_review': 'fas fa-file-alt',
    'system_config': 'fas fa-cog',
    'security': 'fas fa-shield-alt'
  }
  return icons[type] || 'fas fa-circle'
}

function getActivityColor(type: string): string {
  const colors: Record<string, string> = {
    'user_management': 'blue',
    'content_review': 'green',
    'system_config': 'orange',
    'security': 'red'
  }
  return colors[type] || 'default'
}

function getStatusColor(status: string): string {
  const colors: Record<string, string> = {
    'healthy': 'green',
    'warning': 'orange',
    'error': 'red'
  }
  return colors[status] || 'default'
}

function getStatusText(status: string): string {
  const texts: Record<string, string> = {
    'healthy': '正常',
    'warning': '警告',
    'error': '错误'
  }
  return texts[status] || '未知'
}

function getLogIcon(level: string): string {
  const icons: Record<string, string> = {
    'info': 'fas fa-info-circle',
    'warning': 'fas fa-exclamation-triangle',
    'error': 'fas fa-times-circle'
  }
  return icons[level] || 'fas fa-circle'
}

function getLogLevelColor(level: string): string {
  const colors: Record<string, string> = {
    'info': 'blue',
    'warning': 'orange',
    'error': 'red'
  }
  return colors[level] || 'default'
}

function getAlertIcon(level: string): string {
  const icons: Record<string, string> = {
    'info': 'fas fa-info-circle',
    'warning': 'fas fa-exclamation-triangle',
    'error': 'fas fa-times-circle'
  }
  return icons[level] || 'fas fa-circle'
}

function filterLogs() {
  message.info('筛选功能开发中...')
}

function goToUserManagement() {
  router.push('/users')
}

function goToContentManagement() {
  router.push('/posts-management')
}

function goToSystemSettings() {
  router.push('/settings')
}

function goToAnalytics() {
  router.push('/analytics')
}

function saveProfile() {
  Object.assign(adminInfo, editInfo)
  message.success('个人资料保存成功')
}

function saveSecurity() {
  message.success('安全设置保存成功')
}

function saveNotifications() {
  message.success('通知设置保存成功')
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped lang="scss">
.admin-profile-page {
  padding: 0;
}

/* 头部区域 */
.profile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 24px;
  color: white;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.header-content {
  display: flex;
  align-items: flex-start;
  gap: 32px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.admin-avatar {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-size: 32px;
  font-weight: bold;
  border: 3px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
  color: white;
  font-size: 12px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay i {
  font-size: 16px;
  margin-bottom: 4px;
}

.avatar-overlay span {
  font-size: 10px;
}

.admin-info {
  flex: 1;
}

.admin-info h1 {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 12px;
  color: white;
}

.admin-role {
  margin-bottom: 16px;
}

.role-tag {
  font-size: 14px;
  padding: 4px 12px;
  border-radius: 16px;
  
  i {
    margin-right: 6px;
  }
}

.admin-info p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 16px;
  line-height: 1.6;
}

.admin-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.admin-meta span {
  display: flex;
  align-items: center;
  gap: 8px;
}

.admin-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 20px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  min-width: 100px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
  color: white;
}

.stat-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
}

/* 内容区域 */
.profile-content {
  margin-top: 24px;
}

/* 活动列表 */
.activities-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
}

.activity-text {
  color: #262626;
  margin-bottom: 4px;
  font-weight: 500;
}

.activity-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.activity-details {
  margin-top: 8px;
}

/* 监控面板 */
.monitoring-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.monitoring-stats {
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.monitoring-charts {
  .chart-placeholder {
    height: 200px;
    background: #f5f5f5;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #666;
    gap: 8px;
    
    i {
      font-size: 32px;
    }
  }
}

.system-status {
  margin-top: 16px;
}

.status-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  transition: all 0.3s;
  
  &.service-warning {
    border-color: #faad14;
    background: #fffbe6;
  }
  
  &.service-error {
    border-color: #ff4d4f;
    background: #fff2f0;
  }
}

.service-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.service-info {
  flex: 1;
}

.service-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.service-metrics {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

/* 日志容器 */
.logs-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.logs-filter {
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.logs-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.log-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  
  &.log-warning {
    border-left: 4px solid #faad14;
  }
  
  &.log-error {
    border-left: 4px solid #ff4d4f;
  }
  
  &.log-info {
    border-left: 4px solid #1890ff;
  }
}

.log-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  
  .log-info & {
    background: #e6f7ff;
    color: #1890ff;
  }
  
  .log-warning & {
    background: #fff7e6;
    color: #faad14;
  }
  
  .log-error & {
    background: #fff2f0;
    color: #ff4d4f;
  }
}

.log-content {
  flex: 1;
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.log-action {
  font-weight: 500;
  color: #262626;
}

.log-time {
  font-size: 12px;
  color: #999;
}

.log-description {
  color: #666;
  margin-bottom: 8px;
  line-height: 1.5;
}

.log-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

/* 权限管理 */
.permissions-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.permissions-card,
.admin-levels-card {
  border-radius: 12px;
}

.permissions-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.permission-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  transition: all 0.3s;
  
  &.permission-granted {
    background: #f6ffed;
    border-color: #b7eb8f;
  }
}

.permission-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.permission-info {
  flex: 1;
}

.permission-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.permission-desc {
  font-size: 12px;
  color: #666;
}

.levels-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.level-item {
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  transition: all 0.3s;
  
  &.current-level {
    background: #e6f7ff;
    border-color: #1890ff;
  }
}

.level-badge {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  
  i {
    color: #1890ff;
  }
}

.level-description {
  color: #666;
  margin-bottom: 12px;
  line-height: 1.5;
}

.level-permissions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 设置卡片 */
.settings-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

/* 快捷操作 */
.quick-actions-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    background: #fafafa;
    border-color: #1890ff;
    transform: translateY(-2px);
  }
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 12px;
}

.action-text {
  font-size: 14px;
  color: #262626;
  font-weight: 500;
}

/* 系统警报 */
.alerts-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.alerts-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  
  &.alert-info {
    background: #e6f7ff;
    border-left: 3px solid #1890ff;
  }
  
  &.alert-warning {
    background: #fff7e6;
    border-left: 3px solid #faad14;
  }
  
  &.alert-error {
    background: #fff2f0;
    border-left: 3px solid #ff4d4f;
  }
}

.alert-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .alert-info & {
    color: #1890ff;
  }
  
  .alert-warning & {
    color: #faad14;
  }
  
  .alert-error & {
    color: #ff4d4f;
  }
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 2px;
}

.alert-time {
  font-size: 12px;
  color: #999;
}

/* 登录历史 */
.login-history-card {
  border-radius: 12px;
}

.login-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.login-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
}

.login-info {
  flex: 1;
}

.login-time {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.login-details {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: 24px;
  }
  
  .admin-stats {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .admin-meta {
    align-items: center;
  }
  
  .actions-grid {
    grid-template-columns: 1fr;
  }
  
  .monitoring-stats {
    .ant-col {
      margin-bottom: 16px;
    }
  }
}

@media (max-width: 576px) {
  .profile-header {
    padding: 24px;
  }
  
  .admin-info h1 {
    font-size: 24px;
  }
  
  .admin-stats {
    gap: 12px;
  }
  
  .stat-item {
    min-width: 80px;
    padding: 16px 12px;
  }
}

/* 头像上传模态框样式 */
.avatar-upload-container {
  padding: 20px 0;
}

.current-avatar,
.new-avatar {
  text-align: center;
  margin-bottom: 24px;
}

.current-avatar h4,
.new-avatar h4 {
  margin-bottom: 12px;
  color: #333;
  font-weight: 500;
}

.preview-avatar {
  border: 2px solid #f0f0f0;
}

.upload-section {
  margin-bottom: 32px;
}

.avatar-uploader .ant-upload {
  width: 128px;
  height: 128px;
  margin: 0 auto 16px;
  display: block;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
  background: #fafafa;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  transition: border-color 0.3s ease;
}

.upload-placeholder:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.upload-placeholder i {
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
}

.upload-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.upload-tips {
  text-align: center;
  color: #666;
}

.upload-tips p {
  margin: 4px 0;
  font-size: 12px;
}

.upload-tips i {
  margin-right: 4px;
  color: #1890ff;
}
</style>
