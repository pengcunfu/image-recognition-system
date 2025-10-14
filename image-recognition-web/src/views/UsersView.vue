<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <div class="header-actions">
        <a-input-search
          v-model:value="searchKeyword"
          placeholder="搜索用户名、邮箱或手机号"
          style="width: 300px; margin-right: 16px"
          @search="handleSearch"
          @change="handleSearchChange"
        />
        <a-button type="primary" @click="showAddModal">
        <template #icon>
          <i class="fas fa-plus"></i>
        </template>
        添加用户
      </a-button>
    </div>
    </div>

    <!-- 筛选器 -->
    <a-card class="filter-card" size="small">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-select
            v-model:value="filterRole"
            placeholder="选择角色"
            style="width: 100%"
            @change="handleFilterChange"
          >
            <a-select-option :value="undefined">全部角色</a-select-option>
            <a-select-option :value="UserRole.USER">普通用户</a-select-option>
            <a-select-option :value="UserRole.VIP">VIP用户</a-select-option>
            <a-select-option :value="UserRole.ADMIN">管理员</a-select-option>
          </a-select>
        </a-col>
        <a-col :span="6">
          <a-select
            v-model:value="filterStatus"
            placeholder="选择状态"
            style="width: 100%"
            @change="handleFilterChange"
          >
            <a-select-option :value="undefined">全部状态</a-select-option>
            <a-select-option :value="UserStatus.ACTIVE">活跃</a-select-option>
            <a-select-option :value="UserStatus.INACTIVE">未激活</a-select-option>
            <a-select-option :value="UserStatus.BANNED">已禁用</a-select-option>
          </a-select>
        </a-col>
        <a-col :span="6">
          <a-select
            v-model:value="sortBy"
            placeholder="排序方式"
            style="width: 100%"
            @change="handleFilterChange"
          >
            <a-select-option value="createTime">注册时间</a-select-option>
            <a-select-option value="lastLoginTime">最后登录</a-select-option>
            <a-select-option value="username">用户名</a-select-option>
          </a-select>
        </a-col>
        <a-col :span="6">
          <a-select
            v-model:value="sortOrder"
            placeholder="排序顺序"
            style="width: 100%"
            @change="handleFilterChange"
          >
            <a-select-option value="desc">降序</a-select-option>
            <a-select-option value="asc">升序</a-select-option>
          </a-select>
        </a-col>
      </a-row>
    </a-card>
    
    <a-card class="table-card">
      <a-table 
        :columns="userColumns" 
        :data-source="users"
        :pagination="pagination"
        :loading="loading"
        :row-selection="rowSelection"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <a-avatar :src="FileAPI.getImageUrl(record.avatar)" :size="32">
              {{ record.name?.charAt(0) || record.username?.charAt(0) }}
            </a-avatar>
          </template>
          <template v-else-if="column.key === 'role'">
            <a-tag :color="getRoleColor(record.role)">
              {{ getRoleText(record.role) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag 
              :color="getStatusColor(record.status)"
              class="status-tag"
            >
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'vipLevel'">
            <a-tag v-if="record.role === 'VIP'" color="gold">
              VIP {{ record.vipLevel }}
            </a-tag>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatDateTime(record.createTime) }}
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
      <div v-if="selectedRowKeys.length > 0" class="batch-actions">
        <a-space>
          <span>已选择 {{ selectedRowKeys.length }} 项</span>
          <a-button @click="batchEnable">批量启用</a-button>
          <a-button @click="batchDisable">批量禁用</a-button>
          <a-button danger @click="batchDelete">批量删除</a-button>
        </a-space>
      </div>
    </a-card>

    <!-- 添加/编辑用户模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditing ? '编辑用户' : '添加用户'"
      :width="600"
      @ok="handleSubmit"
      @cancel="handleCancel"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <!-- 头像上传 -->
        <a-form-item label="用户头像">
          <a-upload
            v-model:file-list="avatarFileList"
            name="avatar"
            list-type="picture-card"
            class="avatar-uploader"
            :show-upload-list="false"
            :before-upload="beforeAvatarUpload"
            @change="handleAvatarChange"
          >
            <div v-if="formData.avatar" class="avatar-preview">
              <img :src="FileAPI.getImageUrl(formData.avatar)" alt="avatar" />
            </div>
            <div v-else class="upload-placeholder">
              <i class="fas fa-plus"></i>
              <div>上传头像</div>
            </div>
          </a-upload>
          <div class="upload-tips">支持 JPG、PNG 格式，大小不超过 2MB</div>
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
            <a-form-item label="姓名" name="name">
              <a-input v-model:value="formData.name" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="角色" name="role">
              <a-select v-model:value="formData.role">
                <a-select-option value="USER">普通用户</a-select-option>
                <a-select-option value="VIP">VIP用户</a-select-option>
                <a-select-option value="ADMIN">管理员</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formData.status">
                <a-select-option value="ACTIVE">活跃</a-select-option>
                <a-select-option value="INACTIVE">未激活</a-select-option>
                <a-select-option value="BANNED">已禁用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item v-if="!isEditing" label="密码" name="password">
          <a-input-password v-model:value="formData.password" />
        </a-form-item>
        <a-form-item v-if="formData.role === 'VIP'" label="VIP等级" name="vipLevel">
          <a-input-number v-model:value="formData.vipLevel" :min="1" :max="10" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 用户详情抽屉 -->
    <a-drawer
      v-model:open="drawerVisible"
      title="用户详情"
      :width="600"
      placement="right"
    >
      <div v-if="selectedUser" class="user-detail">
        <a-descriptions :column="1" bordered>
          <a-descriptions-item label="头像">
            <a-avatar :src="FileAPI.getImageUrl(selectedUser.avatar)" :size="64">
              {{ selectedUser.name?.charAt(0) || selectedUser.username?.charAt(0) }}
            </a-avatar>
          </a-descriptions-item>
          <a-descriptions-item label="用户名">{{ selectedUser.username }}</a-descriptions-item>
          <a-descriptions-item label="邮箱">{{ selectedUser.email }}</a-descriptions-item>
          <a-descriptions-item label="手机号">{{ selectedUser.phone || '-' }}</a-descriptions-item>
          <a-descriptions-item label="姓名">{{ selectedUser.name || '-' }}</a-descriptions-item>
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
          <a-descriptions-item v-if="selectedUser.role === 'VIP'" label="VIP等级">
            VIP {{ selectedUser.vipLevel }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedUser.role === 'VIP'" label="VIP到期时间">
            {{ selectedUser.vipExpireTime ? formatDateTime(selectedUser.vipExpireTime) : '-' }}
          </a-descriptions-item>
          <a-descriptions-item label="注册时间">{{ formatDateTime(selectedUser.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="最后登录">
            {{ selectedUser.lastLoginTime ? formatDateTime(selectedUser.lastLoginTime) : '从未登录' }}
          </a-descriptions-item>
          <a-descriptions-item label="最后登录IP">{{ selectedUser.lastLoginIp || '-' }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { UserAPI } from '@/api/user'
import FileAPI from '@/api/file'
import type { 
  User, 
  UserListResponse, 
  AdminUserCreateRequest, 
  AdminUserUpdateRequest,
  UserQueryParams,
  BatchUserOperationRequest
} from '@/api/types'
import { UserRole, UserStatus } from '@/api/types'

// 响应式数据
const loading = ref(false)
const users = ref<User[]>([])
const searchKeyword = ref('')
const filterRole = ref<number | undefined>()
const filterStatus = ref<number | undefined>()
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
  { title: '姓名', dataIndex: 'name', key: 'name', width: 100 },
  { title: '角色', dataIndex: 'role', key: 'role', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: 'VIP等级', dataIndex: 'vipLevel', key: 'vipLevel', width: 100 },
  { title: '注册时间', dataIndex: 'createTime', key: 'createTime', width: 150 },
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
const formData = reactive<AdminUserCreateRequest & { id?: number; avatar?: string }>({
  username: '',
  email: '',
  phone: '',
  name: '',
  password: '',
  role: 'USER' as 'USER' | 'VIP' | 'ADMIN',
  status: 'ACTIVE' as 'ACTIVE' | 'INACTIVE' | 'BANNED',
  vipLevel: 1,
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
const selectedUser = ref<User>()

// 初始化
onMounted(() => {
  loadUsers()
})

// 加载用户列表
async function loadUsers() {
  try {
    loading.value = true
    const params: UserQueryParams = {
      page: pagination.current,
      size: pagination.pageSize,
      keyword: searchKeyword.value || undefined,
      role: filterRole.value,  // 直接使用数字枚举值
      status: filterStatus.value,  // 直接使用数字枚举值
      sortBy: sortBy.value as 'createTime' | 'lastLoginTime' | 'username',
      sortOrder: sortOrder.value as 'asc' | 'desc'
    }

    console.log('用户查询参数:', params)

    const response = await UserAPI.getUsers(params)
    users.value = response.data.users
    pagination.total = response.data.total
    pagination.current = response.data.page
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

function handleSearchChange() {
  if (!searchKeyword.value) {
    handleSearch()
  }
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
async function editUser(record: User) {
  try {
    const response = await UserAPI.getUserDetail(record.id)
    const user = response.data
    
    isEditing.value = true
    Object.assign(formData, {
      id: user.id,
      username: user.username,
      email: user.email,
      phone: user.phone,
      name: user.name,
      role: user.role,
      status: user.status,
      vipLevel: user.vipLevel,
      avatar: user.avatar || ''
    })
    modalVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
    message.error('获取用户详情失败')
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
      
      const response = await FileAPI.uploadFile(info.file.originFileObj)
      formData.avatar = response.data.url
      
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
async function viewUser(record: User) {
  try {
    const response = await UserAPI.getUserDetail(record.id)
    selectedUser.value = response.data
    drawerVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
    message.error('获取用户详情失败')
  }
}

// 切换用户状态
async function toggleUserStatus(record: User) {
  const newStatus = record.status === 'ACTIVE' ? 'BANNED' : 'ACTIVE'
  const action = newStatus === 'ACTIVE' ? '启用' : '禁用'
  
  Modal.confirm({
    title: `确认${action}用户`,
    content: `确定要${action}用户 ${record.username} 吗？`,
    onOk: async () => {
      try {
        await UserAPI.toggleUserStatus(record.id, newStatus)
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
function resetPassword(record: User) {
  Modal.confirm({
    title: '重置密码',
    content: `确定要重置用户 ${record.username} 的密码吗？新密码将为：123456`,
    onOk: async () => {
      try {
        await UserAPI.resetPassword(record.id, '123456')
        message.success('密码重置成功，新密码为：123456')
      } catch (error) {
        console.error('重置密码失败:', error)
        message.error('重置密码失败')
      }
    }
  })
}

// 查看登录历史
async function viewLoginHistory(record: User) {
  try {
    const response = await UserAPI.getUserLoginHistory(record.id, { page: 1, size: 20 })
    // 这里可以打开一个新的模态框显示登录历史
    console.log('登录历史:', response.data)
    message.info('登录历史功能开发中')
  } catch (error) {
    console.error('获取登录历史失败:', error)
    message.error('获取登录历史失败')
  }
}

// 删除用户
function deleteUser(record: User) {
  Modal.confirm({
    title: '删除用户',
    content: `确定要删除用户 ${record.username} 吗？此操作不可恢复！`,
    okType: 'danger',
    onOk: async () => {
      try {
        await UserAPI.deleteUser(record.id)
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
        await UserAPI.batchUpdateUsers(selectedRowKeys.value, 'activate')
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
        await UserAPI.batchUpdateUsers(selectedRowKeys.value, 'deactivate')
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
        await UserAPI.batchUpdateUsers(selectedRowKeys.value, 'delete')
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
      const updateData: AdminUserUpdateRequest & { avatar?: string } = {
        email: formData.email,
        phone: formData.phone,
        name: formData.name,
        role: formData.role,
        status: formData.status,
        vipLevel: formData.vipLevel,
        avatar: formData.avatar
      }
      await UserAPI.updateUser(formData.id!, updateData)
      message.success('用户更新成功')
    } else {
      const createData: AdminUserCreateRequest & { avatar?: string } = {
        username: formData.username,
        email: formData.email,
        phone: formData.phone,
        name: formData.name,
        password: formData.password,
        role: formData.role,
        status: formData.status,
        vipLevel: formData.vipLevel,
        avatar: formData.avatar
      }
      await UserAPI.createUser(createData)
      message.success('用户创建成功')
    }
    
    modalVisible.value = false
    resetForm()
    loadUsers()
  } catch (error) {
    console.error('提交失败:', error)
    message.error(isEditing.value ? '用户更新失败' : '用户创建失败')
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
    name: '',
    password: '',
    role: 'USER' as 'USER' | 'VIP' | 'ADMIN',
    status: 'ACTIVE' as 'ACTIVE' | 'INACTIVE' | 'BANNED',
    vipLevel: 1,
    avatar: ''
  })
  avatarFileList.value = []
  formRef.value?.resetFields()
}

// 辅助函数
function getRoleColor(role: string) {
  const colors = {
    'USER': 'blue',
    'VIP': 'gold',
    'ADMIN': 'red'
  }
  return colors[role as keyof typeof colors] || 'default'
}

function getRoleText(role: string) {
  const texts = {
    'USER': '普通用户',
    'VIP': 'VIP用户',
    'ADMIN': '管理员'
  }
  return texts[role as keyof typeof texts] || role
}

function getStatusColor(status: string) {
  const colors = {
    'ACTIVE': 'success',
    'INACTIVE': 'warning',
    'BANNED': 'error'
  }
  return colors[status as keyof typeof colors] || 'default'
}

function getStatusText(status: string) {
  const texts = {
    'ACTIVE': '活跃',
    'INACTIVE': '未激活',
    'BANNED': '已禁用'
  }
  return texts[status as keyof typeof texts] || status
}

function formatDateTime(dateTime: string) {
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

<style lang="scss" scoped>
// 变量定义
$primary-color: #1890ff;
$text-color: #262626;
$text-color-secondary: #595959;
$border-color: #f0f0f0;
$background-color: #fafafa;
$background-color-light: #f0f2f5;
$hover-color: #f5f5f5;
$selected-color: #e6f7ff;

.page {
  width: 100%;
}

.page-title {
  margin: 0;
  color: $text-color;
  font-size: 24px;
  font-weight: 600;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.filter-card {
  margin-bottom: 16px;
}

.table-card {
  margin-bottom: 24px;
}

.status-tag {
  font-size: 12px;
  font-weight: 500;
}

.batch-actions {
  margin-top: 16px;
  padding: 12px 16px;
  background-color: $background-color-light;
  border-radius: 6px;
}

.user-detail {
  padding: 16px 0;

  :deep(.ant-descriptions-item-label) {
    font-weight: 600;
    color: $text-color;
  }

  :deep(.ant-descriptions-item-content) {
    color: $text-color-secondary;
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .header-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
    
    .ant-input-search {
      width: 100% !important;
      margin-right: 0 !important;
    }
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .filter-card {
    .ant-row {
      flex-direction: column;
    }
    
    .ant-col {
      width: 100% !important;
      margin-bottom: 12px;
    }
  }
  
  .table-card {
    :deep(.ant-table-wrapper) {
      overflow-x: auto;
    }
  }
  
  .batch-actions {
    text-align: center;
  }
}

// 表格样式优化
.table-card {
  :deep(.ant-table-thead > tr > th) {
    background-color: $background-color;
    font-weight: 600;
    color: $text-color;
  }

  :deep(.ant-table-tbody > tr:hover > td) {
    background-color: $hover-color;
  }

  :deep(.ant-table-row-selected > td) {
    background-color: $selected-color;
  }
}

// 模态框样式
.ant-modal {
  :deep(.ant-modal-header) {
    border-bottom: 1px solid $border-color;
  }

  :deep(.ant-modal-title) {
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
  }
}

// 抽屉样式
.ant-drawer {
  :deep(.ant-drawer-header) {
    border-bottom: 1px solid $border-color;
  }

  :deep(.ant-drawer-title) {
    font-size: 18px;
    font-weight: 600;
    color: $text-color;
  }
}

// 表单样式
.ant-form {
  :deep(.ant-form-item-label > label) {
    font-weight: 600;
    color: $text-color;
  }

  :deep(.ant-form-item-explain-error) {
    font-size: 12px;
  }
}

// 按钮样式
.ant-btn-primary {
  border-radius: 6px;
}

.ant-btn-link {
  padding: 0;
  height: auto;
}

// 标签样式
.ant-tag {
  border-radius: 4px;
  font-size: 12px;
  line-height: 20px;
  padding: 0 8px;
}

// 头像样式
.ant-avatar {
  background-color: $primary-color;
  font-weight: 600;
}

// 头像上传样式
.avatar-uploader {
  :deep(.ant-upload) {
    width: 128px;
    height: 128px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  }
}

.avatar-preview {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  
  i {
    font-size: 24px;
    margin-bottom: 8px;
  }
  
  div {
    font-size: 14px;
  }
}

.upload-tips {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}
</style>
