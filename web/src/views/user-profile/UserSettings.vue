<template>
  <div>
    <a-card title="个人资料" :style="{ borderRadius: '8px' }">
      <a-form layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="昵称">
              <a-input v-model:value="editInfo.name" placeholder="请输入昵称" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="手机号">
              <a-input v-model:value="editInfo.phone" placeholder="请输入手机号" maxlength="20" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="邮箱">
          <a-input v-model:value="editInfo.email" disabled :style="{ backgroundColor: '#f5f5f5', cursor: 'not-allowed' }" />
          <div :style="{ fontSize: '12px', color: '#8c8c8c', marginTop: '4px' }">
            <i class="fas fa-info-circle"></i> 邮箱不可修改
          </div>
        </a-form-item>
        <a-form-item label="个人简介">
          <a-textarea v-model:value="editInfo.bio" :rows="4" placeholder="介绍一下自己吧..." :maxlength="500" show-count />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSave" :loading="saving">
            保存修改
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, watch, ref } from 'vue'

const props = defineProps<{
  userInfo: {
    name: string
    bio: string
    phone: string
    email: string
  }
}>()

const editInfo = reactive({
  name: props.userInfo.name,
  bio: props.userInfo.bio,
  phone: props.userInfo.phone,
  email: props.userInfo.email
})

const saving = ref(false)

// 监听 props 变化，同步更新编辑信息
watch(() => props.userInfo, (newUserInfo) => {
  editInfo.name = newUserInfo.name
  editInfo.bio = newUserInfo.bio
  editInfo.phone = newUserInfo.phone
  editInfo.email = newUserInfo.email
}, { deep: true })

const emit = defineEmits<{
  'save-profile': [info: typeof editInfo]
}>()

function handleSave() {
  // 简单验证
  if (!editInfo.name || editInfo.name.trim() === '') {
    return
  }
  
  saving.value = true
  emit('save-profile', editInfo)
  
  // 保存完成后，由父组件控制 loading 状态
  setTimeout(() => {
    saving.value = false
  }, 100)
}
</script>

