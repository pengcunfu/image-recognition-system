<template>
  <a-modal
    :open="visible"
    title="更换头像"
    :width="600"
    @ok="handleUpload"
    @cancel="handleCancel"
    :confirmLoading="uploadLoading"
  >
    <div :style="{ display: 'flex', flexDirection: 'column', gap: '24px' }">
      <!-- 当前头像预览 -->
      <div :style="{ textAlign: 'center' }">
        <h4 :style="{ fontSize: '14px', marginBottom: '12px', color: '#262626' }">
          {{ newAvatarUrl ? '新头像预览' : '当前头像' }}
        </h4>
        <a-avatar :size="80" :src="newAvatarUrl || currentAvatar" :style="{ background: '#1890ff', fontSize: '32px' }">
          {{ (userName || 'U').charAt(0) }}
        </a-avatar>
      </div>

      <!-- 上传区域 -->
      <div :style="{ display: 'flex', flexDirection: 'column', alignItems: 'center' }">
        <div :style="{ display: 'flex', justifyContent: 'center' }">
          <a-upload
            v-model:file-list="fileList"
            name="avatar"
            list-type="picture-card"
            :show-upload-list="false"
            :before-upload="beforeUpload"
            @change="handleChange"
            accept="image/*"
          >
            <div v-if="!uploadPreviewUrl" :style="{ width: '100px', height: '100px', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '8px' }">
              <i class="fas fa-plus" :style="{ fontSize: '24px', color: '#8c8c8c' }"></i>
              <div :style="{ fontSize: '14px', color: '#8c8c8c' }">选择图片</div>
            </div>
            <img v-else :src="uploadPreviewUrl" alt="avatar" :style="{ width: '100px', height: '100px', objectFit: 'cover', borderRadius: '4px' }" />
          </a-upload>
        </div>
        
        <div :style="{ marginTop: '16px', display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '13px', color: '#8c8c8c', alignItems: 'center' }">
          <p :style="{ margin: 0, display: 'flex', alignItems: 'center', gap: '6px' }">
            <i class="fas fa-info-circle" :style="{ color: '#1890ff' }"></i> 
            支持 JPG、PNG 格式
          </p>
          <p :style="{ margin: 0, display: 'flex', alignItems: 'center', gap: '6px' }">
            <i class="fas fa-info-circle" :style="{ color: '#1890ff' }"></i> 
            建议尺寸 200x200 像素
          </p>
          <p :style="{ margin: 0, display: 'flex', alignItems: 'center', gap: '6px' }">
            <i class="fas fa-info-circle" :style="{ color: '#1890ff' }"></i> 
            文件大小不超过 2MB
          </p>
        </div>
      </div>

      <!-- 预设头像选择 -->
      <div>
        <h4 :style="{ fontSize: '14px', marginBottom: '12px', color: '#262626' }">选择预设头像</h4>
        <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(80px, 1fr))', gap: '12px' }">
          <div 
            v-for="(preset, index) in presetAvatars" 
            :key="index"
            :style="{ 
              display: 'flex', 
              flexDirection: 'column', 
              alignItems: 'center', 
              gap: '6px',
              padding: '8px',
              borderRadius: '6px',
              border: `2px solid ${selectedPreset === index ? '#1890ff' : '#f0f0f0'}`,
              cursor: 'pointer',
              transition: 'all 0.3s'
            }"
            @click="selectPreset(preset, index)"
          >
            <a-avatar :size="50" :src="preset.url" />
            <span :style="{ fontSize: '12px', color: '#8c8c8c' }">{{ preset.name }}</span>
          </div>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'

defineProps<{
  visible: boolean
  currentAvatar: string
  userName: string
  uploadLoading: boolean
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  upload: [file: File | null, presetUrl: string | null]
  cancel: []
}>()

const newAvatarUrl = ref('')
const uploadPreviewUrl = ref('') // 上传文件的预览URL
const fileList = ref([])
const selectedPreset = ref(-1)
const uploadFile = ref<File | null>(null)

const presetAvatars = ref([
  { name: '默认1', url: 'https://gw.alipayobjects.com/zos/rmsportal/KDpgvguMpGfqaHPjicRK.svg' },
  { name: '默认2', url: 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png' },
  { name: '默认3', url: 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png' },
  { name: '默认4', url: 'https://gw.alipayobjects.com/zos/antfincdn/efFD%24IOql2/weixintupian_20170331104822.jpg' },
  { name: '默认5', url: 'https://gw.alipayobjects.com/zos/rmsportal/ThXAXghbEsBCCSDihZxY.png' },
  { name: '默认6', url: 'https://gw.alipayobjects.com/zos/rmsportal/OKJXDXrmkNshAMvwtvhu.png' }
])

function beforeUpload(file: any) {
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
  return false
}

function handleChange(info: any) {
  console.log('handleChange 被调用:', info)
  
  if (info.file) {
    // 获取文件对象：优先使用 originFileObj，如果没有则使用 file 本身
    const fileObj = info.file.originFileObj || info.file
    
    console.log('文件对象:', fileObj)
    uploadFile.value = fileObj
    
    const reader = new FileReader()
    reader.addEventListener('load', () => {
      uploadPreviewUrl.value = reader.result as string
      newAvatarUrl.value = reader.result as string
      selectedPreset.value = -1
      console.log('文件读取完成，设置预览URL')
    })
    reader.readAsDataURL(fileObj)
  } else {
    console.log('没有找到文件')
  }
}

function selectPreset(preset: any, index: number) {
  console.log('选择预设头像:', preset.name, index)
  selectedPreset.value = index
  newAvatarUrl.value = preset.url
  fileList.value = []
  uploadFile.value = null
  // 不修改 uploadPreviewUrl，保持上传组件的状态
}

function handleUpload() {
  if (!newAvatarUrl.value) {
    message.warning('请选择头像')
    return
  }
  
  console.log('触发上传:', {
    hasFile: !!uploadFile.value,
    hasPreset: selectedPreset.value >= 0,
    presetUrl: selectedPreset.value >= 0 ? newAvatarUrl.value : null
  })
  
  // 如果是上传的文件，传递文件对象；如果是预设头像，传递URL
  emit('upload', uploadFile.value, selectedPreset.value >= 0 ? newAvatarUrl.value : null)
}

function handleCancel() {
  console.log('点击取消按钮')
  newAvatarUrl.value = ''
  uploadPreviewUrl.value = ''
  fileList.value = []
  selectedPreset.value = -1
  uploadFile.value = null
  emit('cancel')
}
</script>

