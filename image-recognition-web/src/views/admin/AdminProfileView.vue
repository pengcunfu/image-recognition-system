<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: 100vh;">
    <div style="margin: 0 -12px;">
      <a-row :gutter="24">
        <!-- 左侧内容 -->
        <a-col :xs="24" :lg="16">
          <!-- 基本信息 -->
          <a-card title="基本信息" style="margin-bottom: 24px;">
            <a-form layout="vertical">
              <!-- 头像上传 -->
              <a-form-item label="头像">
                <div style="display: flex; align-items: center; gap: 16px;">
                  <div style="position: relative; cursor: pointer;" @click="triggerFileInput">
                    <a-avatar :size="80" :src="FileAPI.getImageUrl(adminInfo.avatar)" style="box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                      {{ adminInfo.name.charAt(0) }}
                    </a-avatar>
                    <div style="position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); border-radius: 50%; display: flex; flex-direction: column; align-items: center; justify-content: center; opacity: 0; transition: opacity 0.3s;" @mouseenter="(e: Event) => ((e.currentTarget as HTMLElement).style.opacity = '1')" @mouseleave="(e: Event) => ((e.currentTarget as HTMLElement).style.opacity = '0')">
                      <i class="fas fa-camera" style="color: white; font-size: 18px; margin-bottom: 4px;"></i>
                      <span style="color: white; font-size: 11px;">更换</span>
                    </div>
                  </div>
                  <div>
                    <div style="font-size: 13px; color: #595959; margin-bottom: 4px;">点击头像可以更换</div>
                    <div style="font-size: 12px; color: #8c8c8c;">支持 JPG、PNG 格式，不超过 2MB</div>
                  </div>
                  <!-- 隐藏的文件输入 -->
                  <input 
                    ref="fileInputRef" 
                    type="file" 
                    accept="image/jpeg,image/png" 
                    style="display: none;" 
                    @change="handleDirectUpload"
                  />
                </div>
              </a-form-item>
              
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
        </a-col>

        <!-- 右侧侧边栏 -->
        <a-col :xs="24" :lg="8">
          <div style="display: flex; flex-direction: column; gap: 16px;">
            <!-- 最近登录 -->
            <a-card title="最近登录记录">
              <div style="display: flex; flex-direction: column; gap: 12px;">
                <div 
                  v-for="login in recentLogins" 
                  :key="login.id"
                  style="display: flex; justify-content: space-between; align-items: center; padding: 12px; background: #fafafa; border-radius: 6px;"
                >
                  <div style="flex: 1;">
                    <div style="font-size: 13px; font-weight: 500; margin-bottom: 4px;">{{ login.time }}</div>
                    <div style="font-size: 12px; color: #8c8c8c;">
                      <span>{{ login.ipAddress }}</span>
                      <span style="margin-left: 8px;">{{ login.location }}</span>
                    </div>
                  </div>
                  <a-tag :color="login.success ? 'green' : 'red'" size="small">
                    {{ login.success ? '成功' : '失败' }}
                  </a-tag>
                </div>
              </div>
            </a-card>
          </div>
        </a-col>
      </a-row>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import FileAPI from '@/api/file'
import AdminAPI from '@/api/admin'

// 管理员信息
const adminInfo = reactive({
  id: 0,
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

// 最近登录记录
const recentLogins = ref<Array<{
  id: number
  time: string
  ipAddress: string
  location: string
  success: boolean
}>>([])

// 加载登录日志
async function loadLoginLogs() {
  try {
    if (adminInfo.id === 0) {
      return
    }
    
    const logs = await AdminAPI.getUserLoginLogs(adminInfo.id, 5)
    
    recentLogins.value = logs.map((log, index) => ({
      id: index + 1,
      time: log.time,
      ipAddress: log.ipAddress,
      location: log.location || '未知',
      success: log.success
    }))
    
  } catch (error) {
    console.error('加载登录日志失败:', error)
    // 静默失败，不显示错误
  }
}

// 头像上传相关变量
const fileInputRef = ref<HTMLInputElement | null>(null)
const uploadLoading = ref(false)

// 触发文件选择
function triggerFileInput() {
  if (fileInputRef.value) {
    fileInputRef.value.click()
  }
}

// 直接上传头像
async function handleDirectUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  
  if (!file) {
    return
  }

  // 验证文件类型
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片!')
    target.value = '' // 清空input
    return
  }

  // 验证文件大小
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    target.value = '' // 清空input
    return
  }

  // 开始上传
  uploadLoading.value = true
  const loadingMessage = message.loading('正在上传头像...', 0)
  
  try {
    // 1. 使用 FileAPI 上传图片
    const uploadedUrl = await FileAPI.uploadImage(file)
    
    // 2. 更新到后端
    await AdminAPI.updateAdminAvatar(uploadedUrl)
    
    // 3. 更新本地管理员头像
    adminInfo.avatar = uploadedUrl
    
    loadingMessage()
    message.success('头像更换成功!')
    
  } catch (error: unknown) {
    console.error('头像上传失败:', error)
    const errorMessage = error instanceof Error ? error.message : '头像上传失败，请重试'
    loadingMessage()
    message.error(errorMessage)
  } finally {
    uploadLoading.value = false
    target.value = '' // 清空input，允许重复选择同一文件
  }
}

// 保存个人资料
async function saveProfile() {
  const loadingMessage = message.loading('正在保存个人资料...', 0)
  
  try {
    // 调用 API 更新管理员资料
    await AdminAPI.updateAdminProfile({
      nickname: editInfo.name,
      phone: editInfo.phone,
      bio: editInfo.bio
    })
    
    // 更新本地数据
    Object.assign(adminInfo, editInfo)
    
    loadingMessage()
    message.success('个人资料保存成功')
    
  } catch (error: unknown) {
    console.error('保存个人资料失败:', error)
    const errorMessage = error instanceof Error ? error.message : '保存失败，请重试'
    loadingMessage()
    message.error(errorMessage)
  }
}

// 加载管理员信息
async function loadAdminProfile() {
  try {
    const profile = await AdminAPI.getAdminProfile()
    
    // 更新管理员信息
    adminInfo.id = profile.id
    adminInfo.name = profile.nickname || profile.username || '系统管理员'
    adminInfo.email = profile.email || ''
    adminInfo.phone = profile.phone || ''
    adminInfo.avatar = profile.avatar || ''
    adminInfo.bio = profile.bio || ''
    
    // 同步到编辑信息
    editInfo.name = adminInfo.name
    editInfo.email = adminInfo.email
    editInfo.phone = adminInfo.phone
    editInfo.bio = adminInfo.bio
    
  } catch (error: unknown) {
    console.error('加载管理员信息失败:', error)
    // 不显示错误消息，使用默认值
  }
}

onMounted(async () => {
  // 加载管理员信息
  await loadAdminProfile()
  // 加载登录日志
  await loadLoginLogs()
})
</script>

