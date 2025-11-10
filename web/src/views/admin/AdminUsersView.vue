<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: 100vh;">
    <!-- 筛选和搜索栏 -->
    <a-card style="margin-bottom: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '16px' }">
      <div style="display: flex; align-items: center; gap: 16px;">
        <a-select v-model:value="filterRole" style="width: 120px;" @change="handleFilterChange">
          <a-select-option value="">全部角色</a-select-option>
          <a-select-option :value="UserRole.USER">普通用户</a-select-option>
          <a-select-option :value="UserRole.VIP">VIP用户</a-select-option>
          <a-select-option :value="UserRole.ADMIN">管理员</a-select-option>
        </a-select>
        
        <a-select v-model:value="filterStatus" style="width: 120px;" @change="handleFilterChange">
          <a-select-option value="">全部状态</a-select-option>
          <a-select-option :value="UserStatus.ACTIVE">活跃</a-select-option>
          <a-select-option :value="UserStatus.INACTIVE">未激活</a-select-option>
          <a-select-option :value="UserStatus.BANNED">已禁用</a-select-option>
        </a-select>
        
        <a-select v-model:value="sortBy" placeholder="排序方式" style="width: 120px;" @change="handleFilterChange">
          <a-select-option value="createTime">注册时间</a-select-option>
          <a-select-option value="lastLoginTime">最后登录</a-select-option>
          <a-select-option value="username">用户名</a-select-option>
        </a-select>
        
        <a-select v-model:value="sortOrder" placeholder="排序顺序" style="width: 100px;" @change="handleFilterChange">
          <a-select-option value="desc">降序</a-select-option>
          <a-select-option value="asc">升序</a-select-option>
        </a-select>
        
        <a-input 
          v-model:value="searchKeyword" 
          placeholder="搜索用户名、邮箱或手机号"
          style="width: 260px;" 
          @pressEnter="handleSearch" 
        />
        
        <a-button type="primary" @click="handleSearch">
          <template #icon>
            <i class="fas fa-search"></i>
          </template>
          搜索
        </a-button>
        
        <a-button @click="handleReset">
          <template #icon>
            <i class="fas fa-redo"></i>
          </template>
          重置
        </a-button>
        
        <div style="flex: 1;"></div>
        
        <a-button type="primary" @click="showAddModal">
          <template #icon>
            <i class="fas fa-plus"></i>
          </template>
          添加用户
        </a-button>
      </div>
    </a-card>

    <a-card style="border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06);" :bodyStyle="{ padding: '24px' }">
      <a-table 
        :columns="userColumns" 
        :data-source="users" 
        :pagination="pagination" 
        :loading="tableLoading"
        :row-selection="rowSelection" 
        @change="handleTableChange"
        :scroll="{ x: 1500 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <a-avatar :src="FileAPI.getImageUrl(record.avatar)" :size="32">
              {{ record.nickname?.charAt(0) || record.username?.charAt(0) }}
            </a-avatar>
          </template>
          <template v-else-if="column.key === 'role'">
            <a-tag :color="getRoleColor(record.role)">
              {{ getRoleText(record.role) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)" class="status-tag">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'balance'">
            <span :style="{ fontWeight: 'bold', color: '#52c41a' }">¥{{ (record.balance || 0).toFixed(2) }}</span>
          </template>
          <template v-else-if="column.key === 'vipLevel'">
            <a-tag v-if="record.role === 1" color="gold">
              VIP {{ record.vipLevel }}
            </a-tag>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.key === 'createdAt'">
            {{ formatDateTime(record.createdAt) }}
          </template>
          <template v-else-if="column.key === 'lastLoginTime'">
            {{ record.lastLoginTime ? formatDateTime(record.lastLoginTime) : '从未登录' }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewUser(record)">
                查看
              </a-button>
              <a-button type="link" size="small" @click="editUser(record)">
                编辑
              </a-button>
              <a-dropdown>
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="manageBalance(record)">
                      管理余额
                    </a-menu-item>
                    <a-menu-item @click="toggleUserStatus(record)">
                      {{ record.status === 'ACTIVE' ? '禁用' : '启用' }}
                    </a-menu-item>
                    <a-menu-item @click="resetPassword(record)">
                      重置密码
                    </a-menu-item>
                    <a-menu-item @click="viewLoginHistory(record)">
                      登录历史
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item danger @click="deleteUser(record)">
                      删除用户
                    </a-menu-item>
                  </a-menu>
                </template>
                <a-button type="link" size="small">
                  更多 <i class="fas fa-chevron-down"></i>
                </a-button>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>

      <!-- 批量操作 -->
      <div 
        v-if="selectedRowKeys.length > 0" 
        style="margin-top: 16px; padding: 12px 16px; background: #e6f7ff; border: 1px solid #91d5ff; border-radius: 6px; display: flex; align-items: center; gap: 12px;"
      >
        <span style="font-weight: 500; color: #1890ff;">已选择 {{ selectedRowKeys.length }} 项</span>
        <a-button size="small" @click="batchEnable">批量启用</a-button>
        <a-button size="small" @click="batchDisable">批量禁用</a-button>
        <a-button size="small" danger @click="batchDelete">批量删除</a-button>
      </div>
    </a-card>

    <!-- 添加/编辑用户模态框 -->
    <a-modal 
      v-model:open="modalVisible" 
      :title="isEditing ? '编辑用户' : '添加用户'" 
      :width="600" 
      @ok="handleSubmit"
      @cancel="handleCancel"
      :bodyStyle="{ padding: '24px' }"
    >
      <a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
        <!-- 头像上传 -->
        <a-form-item label="用户头像">
          <a-upload 
            v-model:file-list="avatarFileList" 
            name="avatar" 
            list-type="picture-card"
            :show-upload-list="false" 
            :before-upload="beforeAvatarUpload" 
            @change="handleAvatarChange"
          >
            <div style="width: 104px; height: 104px; display: flex; flex-direction: column; align-items: center; justify-content: center; border-radius: 6px; overflow: hidden;">
              <img 
                v-if="formData.avatar" 
                :src="FileAPI.getImageUrl(formData.avatar)" 
                alt="avatar" 
                style="width: 100%; height: 100%; object-fit: cover;"
              />
              <div v-else style="display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px;">
                <i class="fas fa-plus" style="font-size: 24px; color: #8c8c8c;"></i>
                <div style="font-size: 14px; color: #8c8c8c;">上传头像</div>
              </div>
            </div>
          </a-upload>
          <div style="margin-top: 8px; font-size: 13px; color: #8c8c8c;">支持 JPG、PNG 格式，大小不超过 2MB</div>
        </a-form-item>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="用户名" name="username">
              <a-input v-model:value="formData.username" :disabled="isEditing" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="邮箱" name="email">
              <a-input v-model:value="formData.email" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="手机号" name="phone">
              <a-input v-model:value="formData.phone" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="昵称" name="nickname">
              <a-input v-model:value="formData.nickname" placeholder="选填" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="角色" name="role">
              <a-select v-model:value="formData.role">
                <a-select-option :value="UserRole.USER">普通用户</a-select-option>
                <a-select-option :value="UserRole.VIP">VIP用户</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formData.status">
                <a-select-option :value="UserStatus.ACTIVE">活跃</a-select-option>
                <a-select-option :value="UserStatus.INACTIVE">未激活</a-select-option>
                <a-select-option :value="UserStatus.BANNED">已禁用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item v-if="!isEditing" label="密码" name="password">
          <a-input-password v-model:value="formData.password" placeholder="请输入密码（至少6位）" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 用户详情抽屉 -->
    <a-drawer 
      v-model:open="drawerVisible" 
      title="用户详情" 
      :width="600" 
      placement="right"
      :bodyStyle="{ padding: '24px' }"
    >
      <div v-if="selectedUser">
        <a-descriptions :column="1" bordered size="middle">
          <a-descriptions-item label="头像">
            <a-avatar :src="FileAPI.getImageUrl(selectedUser.avatar)" :size="64">
              {{ selectedUser.nickname?.charAt(0) || selectedUser.username?.charAt(0) }}
            </a-avatar>
          </a-descriptions-item>
          <a-descriptions-item label="用户名">{{ selectedUser.username }}</a-descriptions-item>
          <a-descriptions-item label="邮箱">{{ selectedUser.email }}</a-descriptions-item>
          <a-descriptions-item label="手机号">{{ selectedUser.phone || '-' }}</a-descriptions-item>
          <a-descriptions-item label="昵称">{{ selectedUser.nickname || '-' }}</a-descriptions-item>
          <a-descriptions-item label="余额">
            <span :style="{ fontWeight: 'bold', color: '#52c41a' }">¥{{ (selectedUser.balance || 0).toFixed(2) }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="角色">
            <a-tag :color="getRoleColor(selectedUser.role)">
              {{ getRoleText(selectedUser.role) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(selectedUser.status)">
              {{ getStatusText(selectedUser.status) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedUser.role === 1" label="VIP等级">
            VIP {{ selectedUser.vipLevel }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedUser.role === 1" label="VIP到期时间">
            {{ selectedUser.vipExpireTime ? formatDateTime(selectedUser.vipExpireTime) : '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="注册时间">{{ formatDateTime(selectedUser.createdAt) }}</a-descriptions-item>
          <a-descriptions-item label="最后登录">
            {{ selectedUser.lastLoginTime ? formatDateTime(selectedUser.lastLoginTime) : '从未登录' }}
          </a-descriptions-item>
          <a-descriptions-item label="最后登录IP">{{ selectedUser.lastLoginIp || '-' }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </a-drawer>

    <!-- 余额管理模态框 -->
    <a-modal 
      v-model:open="balanceModalVisible" 
      title="管理用户余额" 
      :width="500"
      @ok="handleBalanceSubmit"
      @cancel="balanceModalVisible = false"
    >
      <div :style="{ padding: '16px 0' }">
        <a-form layout="vertical">
          <a-form-item label="用户">
            <a-input :value="balanceFormData.username" disabled />
          </a-form-item>
          
          <a-form-item label="当前余额">
            <a-input 
              :value="'¥' + balanceFormData.currentBalance.toFixed(2)" 
              disabled 
              :style="{ fontWeight: 'bold', color: '#52c41a' }"
            />
          </a-form-item>
          
          <a-form-item label="操作类型">
            <a-radio-group v-model:value="balanceFormData.type">
              <a-radio value="add">充值</a-radio>
              <a-radio value="deduct">扣除</a-radio>
            </a-radio-group>
          </a-form-item>
          
          <a-form-item label="金额">
            <a-input-number 
              v-model:value="balanceFormData.amount" 
              :min="0.01" 
              :precision="2" 
              placeholder="请输入金额"
              :style="{ width: '100%' }"
            />
          </a-form-item>
          
          <a-form-item label="操作原因">
            <a-textarea 
              v-model:value="balanceFormData.reason" 
              placeholder="请输入操作原因（选填）"
              :rows="3"
            />
          </a-form-item>
          
          <a-alert 
            v-if="balanceFormData.amount"
            :message="balanceFormData.type === 'add' 
              ? `充值后余额：¥${(balanceFormData.currentBalance + balanceFormData.amount).toFixed(2)}`
              : `扣除后余额：¥${(balanceFormData.currentBalance - balanceFormData.amount).toFixed(2)}`"
            :type="balanceFormData.type === 'add' ? 'success' : 'warning'"
            show-icon
          />
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { AdminAPI } from '@/api/admin'
import { UserAPI } from '@/api/user'
import FileAPI from '@/api/file'
import type { UserInfo } from '@/api/types'

// 用户角色枚举
enum UserRole {
  USER = 0,
  VIP = 1,
  ADMIN = 2
}

// 用户状态枚举
enum UserStatus {
  INACTIVE = 0,
  ACTIVE = 1,
  BANNED = 2
}

// 响应式数据
const loading = ref(false)
const tableLoading = computed(() => ({
  spinning: loading.value,
  tip: '加载中...'
}))
const users = ref<UserInfo[]>([])
const searchKeyword = ref('')
const filterRole = ref<number | string>('')
const filterStatus = ref<number | string>('')
const sortBy = ref('createTime')
const sortOrder = ref('desc')

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

// 表格列定义
const userColumns = [
  { title: '头像', dataIndex: 'avatar', key: 'avatar', width: 80 },
  { title: '用户名', dataIndex: 'username', key: 'username', width: 120 },
  { title: '邮箱', dataIndex: 'email', key: 'email', width: 200 },
  { title: '手机号', dataIndex: 'phone', key: 'phone', width: 120 },
  { title: '昵称', dataIndex: 'nickname', key: 'nickname', width: 100 },
  { title: '余额', dataIndex: 'balance', key: 'balance', width: 100 },
  { title: '角色', dataIndex: 'role', key: 'role', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: 'VIP等级', dataIndex: 'vipLevel', key: 'vipLevel', width: 100 },
  { title: '注册时间', dataIndex: 'createdAt', key: 'createdAt', width: 150 },
  { title: '最后登录', dataIndex: 'lastLoginTime', key: 'lastLoginTime', width: 150 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' }
]

// 行选择
const selectedRowKeys = ref<number[]>([])
const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: number[]) => {
    selectedRowKeys.value = keys
  }
}))

// 模态框和表单
const modalVisible = ref(false)
const isEditing = ref(false)
const formRef = ref()
const formData = reactive<any>({
  id: undefined,
  username: '',
  email: '',
  phone: '',
  nickname: '',
  password: '',
  role: UserRole.USER,
  status: UserStatus.ACTIVE,
  avatar: ''
})

// 头像上传
const avatarFileList = ref<any[]>([])
const uploadingAvatar = ref(false)

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 用户详情抽屉
const drawerVisible = ref(false)
const selectedUser = ref<UserInfo>()

// 余额管理模态框
const balanceModalVisible = ref(false)
const balanceFormData = reactive({
  userId: 0,
  username: '',
  currentBalance: 0,
  type: 'add',
  amount: undefined as number | undefined,
  reason: ''
})

// 初始化
onMounted(() => {
  loadUsers()
})

// 加载用户列表
async function loadUsers() {
  try {
    loading.value = true
    const params: any = {
      page: pagination.current,
      size: pagination.pageSize,
      keyword: searchKeyword.value || undefined,
      status: filterStatus.value === '' ? undefined : filterStatus.value,
      role: filterRole.value === '' ? undefined : filterRole.value,
      sortBy: sortBy.value,
      sortOrder: sortOrder.value
    }

    console.log('用户查询参数:', params)

    const response = await AdminAPI.getUsers(params)
    users.value = response.data || []
    pagination.total = response.total || 0
    pagination.current = response.page || 1
  } catch (error) {
    console.error('加载用户列表失败:', error)
    message.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
function handleSearch() {
  pagination.current = 1
  loadUsers()
}

// 重置处理
function handleReset() {
  filterRole.value = ''
  filterStatus.value = ''
  sortBy.value = 'createTime'
  sortOrder.value = 'desc'
  searchKeyword.value = ''
  pagination.current = 1
  loadUsers()
}

// 筛选处理
function handleFilterChange() {
  pagination.current = 1
  loadUsers()
}

// 表格变化处理
function handleTableChange(pag: any) {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadUsers()
}

// 显示添加用户模态框
function showAddModal() {
  isEditing.value = false
  resetForm()
  modalVisible.value = true
}

// 编辑用户
async function editUser(record: UserInfo) {
  try {
    isEditing.value = true
    Object.assign(formData, {
      id: record.id,
      username: record.username,
      email: record.email,
      phone: record.phone || '',
      nickname: record.nickname || '',
      role: record.role,
      status: record.status,
      avatar: record.avatar || ''
    })
    modalVisible.value = true
  } catch (error) {
    console.error('编辑用户失败:', error)
    message.error('编辑用户失败')
  }
}

// 头像上传前验证
function beforeAvatarUpload(file: File) {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片！')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 头像上传处理
async function handleAvatarChange(info: any) {
  if (info.file.status === 'uploading') {
    return
  }

  if (info.file.originFileObj) {
    try {
      uploadingAvatar.value = true
      message.loading({ content: '正在上传头像...', key: 'uploadAvatar' })

      const response = await FileAPI.uploadImage(info.file.originFileObj)
      formData.avatar = response

      message.success({ content: '头像上传成功', key: 'uploadAvatar' })
    } catch (error) {
      console.error('头像上传失败:', error)
      message.error({ content: '头像上传失败', key: 'uploadAvatar' })
    } finally {
      uploadingAvatar.value = false
    }
  }
}

// 查看用户详情
async function viewUser(record: UserInfo) {
  try {
    selectedUser.value = record
    drawerVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
    message.error('获取用户详情失败')
  }
}

// 切换用户状态
async function toggleUserStatus(record: UserInfo) {
  const newStatus = record.status === UserStatus.ACTIVE ? UserStatus.BANNED : UserStatus.ACTIVE
  const action = newStatus === UserStatus.ACTIVE ? '启用' : '禁用'

  Modal.confirm({
    title: `确认${action}用户`,
    content: `确定要${action}用户 ${record.username} 吗？`,
    onOk: async () => {
      try {
        await AdminAPI.updateUserStatus(record.id, newStatus)
        message.success(`用户${action}成功`)
        loadUsers()
      } catch (error) {
        console.error(`${action}用户失败:`, error)
        message.error(`${action}用户失败`)
      }
    }
  })
}

// 重置密码
function resetPassword(record: UserInfo) {
  Modal.confirm({
    title: '重置密码',
    content: `确定要重置用户 ${record.username} 的密码吗？新密码将为：123456`,
    onOk: async () => {
      try {
        await AdminAPI.resetUserPassword(record.id, '123456')
        message.success('密码重置成功，新密码为：123456')
      } catch (error) {
        console.error('重置密码失败:', error)
        message.error('重置密码失败')
      }
    }
  })
}

// 查看登录历史
async function viewLoginHistory(record: UserInfo) {
  try {
    // 登录历史功能暂未实现
    message.info('登录历史功能开发中')
  } catch (error) {
    console.error('获取登录历史失败:', error)
    message.error('获取登录历史失败')
  }
}

// 管理余额
function manageBalance(record: UserInfo) {
  balanceFormData.userId = record.id
  balanceFormData.username = record.username
  balanceFormData.currentBalance = record.balance || 0
  balanceFormData.type = 'add'
  balanceFormData.amount = undefined
  balanceFormData.reason = ''
  balanceModalVisible.value = true
}

// 提交余额更新
async function handleBalanceSubmit() {
  if (!balanceFormData.amount || balanceFormData.amount <= 0) {
    message.warning('请输入有效的金额')
    return
  }

  try {
    const loadingMsg = message.loading('正在更新余额...', 0)
    
    await UserAPI.updateBalance(balanceFormData.userId, {
      type: balanceFormData.type as 'add' | 'deduct',
      amount: balanceFormData.amount,
      reason: balanceFormData.reason || undefined
    })
    
    loadingMsg()
    message.success('余额更新成功')
    balanceModalVisible.value = false
    loadUsers()
  } catch (error: any) {
    console.error('更新余额失败:', error)
    message.error(error.message || '更新余额失败')
  }
}

// 删除用户
function deleteUser(record: UserInfo) {
  Modal.confirm({
    title: '删除用户',
    content: `确定要删除用户 ${record.username} 吗？此操作不可恢复！`,
    okType: 'danger',
    onOk: async () => {
      try {
        await AdminAPI.deleteUser(record.id)
        message.success('用户删除成功')
        loadUsers()
      } catch (error) {
        console.error('删除用户失败:', error)
        message.error('删除用户失败')
      }
    }
  })
}

// 批量操作
async function batchEnable() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要操作的用户')
    return
  }

  Modal.confirm({
    title: '批量启用',
    content: `确定要启用选中的 ${selectedRowKeys.value.length} 个用户吗？`,
    onOk: async () => {
      try {
        // 批量更新用户状态
        for (const userId of selectedRowKeys.value) {
          await AdminAPI.updateUserStatus(userId, UserStatus.ACTIVE)
        }
        message.success('批量启用成功')
        selectedRowKeys.value = []
        loadUsers()
      } catch (error) {
        console.error('批量启用失败:', error)
        message.error('批量启用失败')
      }
    }
  })
}

async function batchDisable() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要操作的用户')
    return
  }

  Modal.confirm({
    title: '批量禁用',
    content: `确定要禁用选中的 ${selectedRowKeys.value.length} 个用户吗？`,
    onOk: async () => {
      try {
        // 批量更新用户状态
        for (const userId of selectedRowKeys.value) {
          await AdminAPI.updateUserStatus(userId, UserStatus.BANNED)
        }
        message.success('批量禁用成功')
        selectedRowKeys.value = []
        loadUsers()
      } catch (error) {
        console.error('批量禁用失败:', error)
        message.error('批量禁用失败')
      }
    }
  })
}

async function batchDelete() {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要操作的用户')
    return
  }

  Modal.confirm({
    title: '批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个用户吗？此操作不可恢复！`,
    okType: 'danger',
    onOk: async () => {
      try {
        // 批量删除用户
        for (const userId of selectedRowKeys.value) {
          await AdminAPI.deleteUser(userId)
        }
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadUsers()
      } catch (error) {
        console.error('批量删除失败:', error)
        message.error('批量删除失败')
      }
    }
  })
}

// 表单提交
async function handleSubmit() {
  try {
    await formRef.value.validate()

    if (isEditing.value) {
      // 编辑用户
      const loadingMsg = message.loading('正在更新用户信息...', 0)
      try {
        await AdminAPI.updateUser(formData.id, {
          email: formData.email,
          nickname: formData.nickname,
          phone: formData.phone,
          avatar: formData.avatar,
          role: formData.role,
          status: formData.status
        })
        
        loadingMsg()
        message.success('用户信息更新成功')
        modalVisible.value = false
        resetForm()
        loadUsers()
      } catch (error: any) {
        loadingMsg()
        console.error('更新用户失败:', error)
        message.error(error.message || '用户信息更新失败')
      }
    } else {
      // 创建用户
      const loadingMsg = message.loading('正在创建用户...', 0)
      try {
        await AdminAPI.createUser({
          username: formData.username,
          email: formData.email,
          password: formData.password,
          nickname: formData.nickname,
          phone: formData.phone,
          avatar: formData.avatar,
          role: formData.role,
          status: formData.status
        })
        
        loadingMsg()
        message.success('用户创建成功')
        modalVisible.value = false
        resetForm()
        loadUsers()
      } catch (error: any) {
        loadingMsg()
        console.error('创建用户失败:', error)
        message.error(error.message || '用户创建失败')
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 取消表单
function handleCancel() {
  modalVisible.value = false
  resetForm()
}

// 重置表单
function resetForm() {
  Object.assign(formData, {
    id: undefined,
    username: '',
    email: '',
    phone: '',
    nickname: '',
    password: '',
    role: UserRole.USER,
    status: UserStatus.ACTIVE,
    avatar: ''
  })
  avatarFileList.value = []
  formRef.value?.resetFields()
}

// 辅助函数
function getRoleColor(role: number) {
  const colors = {
    0: 'blue',    // USER
    1: 'gold',    // VIP
    2: 'red'      // ADMIN
  }
  return colors[role as keyof typeof colors] || 'default'
}

function getRoleText(role: number) {
  const texts = {
    0: '普通用户',
    1: 'VIP用户',
    2: '管理员'
  }
  return texts[role as keyof typeof texts] || String(role)
}

function getStatusColor(status: number | string | undefined) {
  if (status === undefined || status === null) return 'default'
  
  // 支持数字和字符串类型
  if (typeof status === 'number') {
    switch (status) {
      case UserStatus.ACTIVE: return 'success'
      case UserStatus.INACTIVE: return 'warning'
      case UserStatus.BANNED: return 'error'
      default: return 'default'
    }
  }
  
  const colors = {
    'ACTIVE': 'success',
    'INACTIVE': 'warning',
    'BANNED': 'error'
  }
  return colors[status as keyof typeof colors] || 'default'
}

function getStatusText(status: number | string | undefined) {
  if (status === undefined || status === null) return '未知'
  
  // 支持数字和字符串类型
  if (typeof status === 'number') {
    switch (status) {
      case UserStatus.ACTIVE: return '活跃'
      case UserStatus.INACTIVE: return '未激活'
      case UserStatus.BANNED: return '已禁用'
      default: return '未知'
    }
  }
  
  const texts = {
    'ACTIVE': '活跃',
    'INACTIVE': '未激活',
    'BANNED': '已禁用'
  }
  return texts[status as keyof typeof texts] || status
}

function formatDateTime(dateTime: string | undefined) {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

