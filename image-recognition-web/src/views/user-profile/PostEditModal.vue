<template>
  <a-modal
    :visible="visible"
    title="编辑帖子"
    width="800px"
    :confirm-loading="loading"
    @ok="handleSubmit"
    @cancel="handleCancel"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :label-col="{ span: 4 }"
      :wrapper-col="{ span: 20 }"
    >
      <!-- 标题 -->
      <a-form-item
        label="标题"
        name="title"
        :rules="[{ required: true, message: '请输入帖子标题' }]"
      >
        <a-input
          v-model:value="formData.title"
          placeholder="请输入帖子标题"
          :maxlength="100"
          show-count
        />
      </a-form-item>

      <!-- 分类 -->
      <a-form-item
        label="分类"
        name="category"
        :rules="[{ required: true, message: '请选择分类' }]"
      >
        <a-select
          v-model:value="formData.category"
          placeholder="请选择分类"
          :loading="categoriesLoading"
        >
          <a-select-option
            v-for="cat in categories"
            :key="cat.name"
            :value="cat.name"
          >
            {{ cat.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <!-- 标签 -->
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="formData.tags"
          mode="tags"
          placeholder="请输入或选择标签"
          :max-tag-count="5"
        >
          <a-select-option
            v-for="tag in allTags"
            :key="tag.name"
            :value="tag.name"
          >
            {{ tag.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <!-- 内容 -->
      <a-form-item
        label="内容"
        name="content"
        :rules="[{ required: true, message: '请输入帖子内容' }]"
      >
        <a-textarea
          v-model:value="formData.content"
          placeholder="请输入帖子内容"
          :rows="10"
          :maxlength="5000"
          show-count
        />
      </a-form-item>

      <!-- 图片 -->
      <a-form-item label="图片">
        <a-upload
          v-model:file-list="fileList"
          list-type="picture-card"
          :before-upload="beforeUpload"
          :remove="handleRemove"
          :max-count="9"
          accept="image/*"
        >
          <div v-if="fileList.length < 9">
            <plus-outlined />
            <div style="margin-top: 8px">上传图片</div>
          </div>
        </a-upload>
        <div :style="{ fontSize: '12px', color: '#8c8c8c', marginTop: '8px' }">
          最多上传9张图片，支持jpg、png、gif格式，单张不超过5MB
        </div>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { CommunityAPI } from '@/api/community'
import { FileAPI } from '@/api/file'
import type { UploadProps } from 'ant-design-vue'

interface Props {
  visible: boolean
  post: any
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:visible': [value: boolean]
  'success': []
}>()

const formRef = ref()
const loading = ref(false)
const categoriesLoading = ref(false)
const categories = ref<any[]>([])
const allTags = ref<any[]>([])
const fileList = ref<any[]>([])

const formData = reactive({
  title: '',
  category: '',
  tags: [] as string[],
  content: '',
  images: [] as string[]
})

// 加载分类和标签
async function loadCategoriesAndTags() {
  try {
    categoriesLoading.value = true
    const [categoriesData, tagsData] = await Promise.all([
      CommunityAPI.getCategories(),
      CommunityAPI.getTags()
    ])
    categories.value = categoriesData
    allTags.value = tagsData
  } catch (error: any) {
    console.error('加载分类和标签失败:', error)
  } finally {
    categoriesLoading.value = false
  }
}

// 监听帖子数据变化
watch(() => props.post, (post) => {
  if (post && props.visible) {
    // 填充表单数据
    formData.title = post.title || ''
    formData.category = post.category || ''
    formData.content = post.content || ''
    
    // 解析标签
    if (post.tags) {
      formData.tags = typeof post.tags === 'string' 
        ? post.tags.split(',').filter((t: string) => t.trim()) 
        : post.tags
    } else {
      formData.tags = []
    }
    
    // 解析图片
    fileList.value = []
    if (post.image) {
      // 如果有缩略图,添加到文件列表
      fileList.value.push({
        uid: '-1',
        name: 'image.jpg',
        status: 'done',
        url: post.image
      })
      formData.images = [post.image]
    }
  }
}, { immediate: true })

// 图片上传前处理
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
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

  // 创建预览
  const reader = new FileReader()
  reader.onload = (e) => {
    file.url = e.target?.result as string
    file.thumbUrl = e.target?.result as string
  }
  reader.readAsDataURL(file)
  
  return false // 阻止自动上传
}

// 移除图片
function handleRemove(file: any) {
  const index = fileList.value.indexOf(file)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
}

// 提交表单
async function handleSubmit() {
  try {
    // 验证表单
    await formRef.value?.validate()
    
    loading.value = true
    
    // 1. 上传新增的图片
    const uploadedUrls: string[] = []
    for (const file of fileList.value) {
      if (file.originFileObj) {
        // 新上传的图片
        const url = await FileAPI.uploadImage(file.originFileObj)
        uploadedUrls.push(url)
      } else if (file.url) {
        // 已有的图片
        uploadedUrls.push(file.url)
      }
    }
    
    // 2. 更新帖子
    await CommunityAPI.updatePost(props.post.id, {
      title: formData.title,
      category: formData.category,
      tags: formData.tags,
      content: formData.content,
      images: uploadedUrls
    })
    
    message.success('帖子更新成功!')
    emit('success')
    handleCancel()
    
  } catch (error: any) {
    console.error('更新帖子失败:', error)
    if (error.errorFields) {
      message.error('请检查表单填写')
    } else {
      message.error(error.message || '更新失败，请重试')
    }
  } finally {
    loading.value = false
  }
}

// 取消
function handleCancel() {
  emit('update:visible', false)
  // 重置表单
  formRef.value?.resetFields()
  fileList.value = []
}

// 组件挂载时加载分类和标签
onMounted(() => {
  loadCategoriesAndTags()
})
</script>

<style scoped>
:deep(.ant-upload-list-picture-card-container) {
  width: 104px;
  height: 104px;
}

:deep(.ant-upload-select-picture-card) {
  width: 104px;
  height: 104px;
}
</style>

