<template>
  <a-modal
    v-model:open="visible"
    title="发布帖子"
    width="600px"
    :footer="null"
    @cancel="handleCancel"
  >
    <div :style="{ display: 'flex', flexDirection: 'column', gap: '24px', padding: '24px 0' }">
      <!-- 帖子类型选择 -->
      <div>
        <label :style="{ display: 'block', marginBottom: '8px', fontWeight: 500, color: '#262626' }">帖子类型</label>
        <a-radio-group 
          v-model:value="formData.type" 
          button-style="solid"
          :style="{ width: '100%', display: 'flex' }"
        >
          <a-radio-button value="share" :style="{ flex: 1, textAlign: 'center' }">
            <ShareAltOutlined />
            分享
          </a-radio-button>
          <a-radio-button value="question" :style="{ flex: 1, textAlign: 'center' }">
            <QuestionCircleOutlined />
            问答
          </a-radio-button>
          <a-radio-button value="discussion" :style="{ flex: 1, textAlign: 'center' }">
            <CommentOutlined />
            讨论
          </a-radio-button>
        </a-radio-group>
      </div>

      <!-- 标题输入 -->
      <div>
        <label :style="{ display: 'block', marginBottom: '8px', fontWeight: 500, color: '#262626' }">标题</label>
        <a-input
          v-model:value="formData.title"
          placeholder="请输入帖子标题..."
          :maxlength="100"
          show-count
        />
      </div>

      <!-- 内容输入 -->
      <div>
        <label :style="{ display: 'block', marginBottom: '8px', fontWeight: 500, color: '#262626' }">内容</label>
        <a-textarea
          v-model:value="formData.content"
          placeholder="详细描述您的想法、经验或问题..."
          :rows="6"
          :maxlength="2000"
          show-count
        />
      </div>

      <!-- 图片上传 -->
      <div>
        <label :style="{ display: 'block', marginBottom: '8px', fontWeight: 500, color: '#262626' }">图片</label>
        <a-upload
          v-model:file-list="formData.images"
          list-type="picture-card"
          :before-upload="beforeUpload"
          @preview="handlePreviewImage"
          @remove="handleRemoveImage"
        >
          <div v-if="formData.images.length < 9" :style="{ textAlign: 'center' }">
            <PlusOutlined />
            <div :style="{ marginTop: '8px', fontSize: '12px', color: '#666' }">上传图片</div>
          </div>
        </a-upload>
        <div :style="{ marginTop: '8px', fontSize: '12px', color: '#999' }">最多可上传9张图片，支持jpg、png格式</div>
      </div>

      <!-- 标签选择 -->
      <div>
        <label :style="{ display: 'block', marginBottom: '8px', fontWeight: 500, color: '#262626' }">标签</label>
        <a-select
          v-model:value="formData.tags"
          mode="multiple"
          placeholder="选择相关标签..."
          :options="tagOptions"
          :max-tag-count="5"
          style="width: 100%"
        />
      </div>

      <!-- 发布选项 -->
      <div :style="{ display: 'flex', gap: '24px' }">
        <a-checkbox v-model:checked="formData.allowComments">
          允许评论
        </a-checkbox>
        <a-checkbox v-model:checked="formData.isPublic">
          公开发布
        </a-checkbox>
      </div>

      <!-- 操作按钮 -->
      <div :style="{ 
        display: 'flex', 
        justifyContent: 'flex-end', 
        gap: '12px', 
        paddingTop: '16px', 
        borderTop: '1px solid #f0f0f0' 
      }">
        <a-button @click="handleCancel">取消</a-button>
        <a-button @click="handleSaveDraft" :loading="savingDraft">保存草稿</a-button>
        <a-button 
          type="primary" 
          @click="handlePublish"
          :loading="publishing"
          :disabled="!canPublish"
        >
          发布
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue'
import { message } from 'ant-design-vue'
import { ShareAltOutlined, QuestionCircleOutlined, CommentOutlined, PlusOutlined } from '@ant-design/icons-vue'

// Props
interface Props {
  open: boolean
  currentUser?: {
    name: string
    avatar: string
  }
}

const props = withDefaults(defineProps<Props>(), {
  open: false,
  currentUser: () => ({
    name: '张三',
    avatar: ''
  })
})

// Emits
interface Emits {
  (e: 'update:open', value: boolean): void
  (e: 'publish', postData: any): void
  (e: 'saveDraft', postData: any): void
}

const emit = defineEmits<Emits>()

// Reactive data
const visible = ref(false)
const publishing = ref(false)
const savingDraft = ref(false)

const formData = reactive({
  type: 'share',
  title: '',
  content: '',
  images: [],
  tags: [],
  allowComments: true,
  isPublic: true
})

// 标签选项
const tagOptions = [
  { label: 'AI识别', value: 'AI识别' },
  { label: '深度学习', value: '深度学习' },
  { label: '图像处理', value: '图像处理' },
  { label: '机器学习', value: '机器学习' },
  { label: '计算机视觉', value: '计算机视觉' },
  { label: '神经网络', value: '神经网络' },
  { label: '数据分析', value: '数据分析' },
  { label: '技术分享', value: '技术分享' },
  { label: '经验总结', value: '经验总结' },
  { label: '问题求助', value: '问题求助' }
]

// Computed
const canPublish = computed(() => {
  return formData.title.trim() && formData.content.trim()
})

// Watch props.open to sync with internal visible state
watch(() => props.open, (newValue) => {
  visible.value = newValue
})

watch(visible, (newValue) => {
  emit('update:open', newValue)
})

// Methods
function resetForm() {
  Object.assign(formData, {
    type: 'share',
    title: '',
    content: '',
    images: [],
    tags: [],
    allowComments: true,
    isPublic: true
  })
}

function handleCancel() {
  visible.value = false
  resetForm()
}

function handlePublish() {
  if (!canPublish.value) {
    message.warning('请填写标题和内容')
    return
  }
  
  publishing.value = true
  
  // 模拟发布过程
  setTimeout(() => {
    publishing.value = false
    message.success('发布成功!')
    
    // 构造帖子数据
    const postData = {
      id: Date.now(),
      title: formData.title,
      content: formData.content,
      type: formData.type,
      tags: formData.tags,
      images: formData.images.map((img: any) => img.url || img.thumbUrl),
      author: {
        name: props.currentUser.name,
        avatar: props.currentUser.avatar,
        level: '资深用户'
      },
      createTime: '刚刚',
      views: 0,
      likes: 0,
      replies: 0,
      isLiked: false,
      isFavorited: false,
      topReplies: []
    }
    
    emit('publish', postData)
    visible.value = false
    resetForm()
  }, 2000)
}

function handleSaveDraft() {
  savingDraft.value = true
  setTimeout(() => {
    savingDraft.value = false
    message.success('草稿已保存')
    
    const draftData = {
      type: formData.type,
      title: formData.title,
      content: formData.content,
      images: formData.images,
      tags: formData.tags,
      allowComments: formData.allowComments,
      isPublic: formData.isPublic
    }
    
    emit('saveDraft', draftData)
  }, 1000)
}

function beforeUpload(file: any) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('图片大小不能超过5MB!')
    return false
  }
  
  return false // 阻止自动上传
}

function handleRemoveImage(file: any) {
  const index = formData.images.findIndex((img: any) => img.uid === file.uid)
  if (index > -1) {
    formData.images.splice(index, 1)
  }
}

function handlePreviewImage(file: any) {
  message.info(`预览图片: ${file.name}`)
}
</script>
