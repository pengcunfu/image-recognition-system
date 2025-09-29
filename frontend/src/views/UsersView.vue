<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <a-button type="primary" @click="addUser">
        <template #icon>
          <i class="fas fa-plus"></i>
        </template>
        添加用户
      </a-button>
    </div>
    
    <a-card class="table-card">
      <a-table 
        :columns="userColumns" 
        :data-source="users"
        :pagination="{ pageSize: 10 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag 
              :color="record.status === '活跃' ? 'success' : 'error'"
              class="status-tag"
            >
              {{ record.status }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="editUser(record)">
                编辑
              </a-button>
              <a-button 
                :type="record.status === '活跃' ? 'default' : 'primary'"
                size="small" 
                @click="toggleUserStatus(record)"
              >
                {{ record.status === '活跃' ? '禁用' : '启用' }}
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'

// 表格列定义
const userColumns = [
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '邮箱', dataIndex: 'email', key: 'email' },
  { title: '注册时间', dataIndex: 'registerTime', key: 'registerTime' },
  { title: '最后登录', dataIndex: 'lastLogin', key: 'lastLogin' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action' }
]

// 用户数据
const users = ref([
  { 
    username: '张三', 
    email: 'zhangsan@example.com', 
    registerTime: '2024-01-15', 
    lastLogin: '2024-03-20',
    status: '活跃' 
  },
  { 
    username: '李四', 
    email: 'lisi@example.com', 
    registerTime: '2024-02-20', 
    lastLogin: '2024-03-19',
    status: '活跃' 
  },
  { 
    username: '王五', 
    email: 'wangwu@example.com', 
    registerTime: '2024-03-10', 
    lastLogin: '2024-03-15',
    status: '禁用' 
  },
  { 
    username: '赵六', 
    email: 'zhaoliu@example.com', 
    registerTime: '2024-02-28', 
    lastLogin: '2024-03-18',
    status: '活跃' 
  },
  { 
    username: '钱七', 
    email: 'qianqi@example.com', 
    registerTime: '2024-01-08', 
    lastLogin: '2024-03-21',
    status: '活跃' 
  }
])

// 方法
function addUser() {
  message.info('添加用户功能开发中')
}

function editUser(record: any) {
  message.info(`编辑用户：${record.username}`)
}

function toggleUserStatus(record: any) {
  const newStatus = record.status === '活跃' ? '禁用' : '活跃'
  record.status = newStatus
  message.success(`用户${record.username}已${newStatus}`)
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}

.status-tag {
  font-size: 12px;
  font-weight: 500;
}

/* Responsive */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
}
</style>
