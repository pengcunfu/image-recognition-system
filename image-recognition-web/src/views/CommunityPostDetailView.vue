<template>
  <div :style="{ padding: '24px', background: '#f5f5f5', minHeight: '100vh' }">
    <!-- 返回按钮 -->
      <div :style="{ marginBottom: '16px' }">
        <a-button @click="goBack">
          <i class="fas fa-arrow-left" :style="{ marginRight: '8px' }"></i>
          返回社区
        </a-button>
      </div>

      <!-- 帖子内容 -->
      <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
        <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' }">
          <!-- 作者信息 -->
          <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
            <a-avatar :size="50" :src="post.authorAvatar">
              {{ post.author.charAt(0) }}
            </a-avatar>
            <div>
              <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }">
                <span :style="{ fontSize: '16px', fontWeight: '500', color: '#262626' }">{{ post.author }}</span>
                <a-tag v-if="post.authorLevel" :color="getAuthorLevelColor(post.authorLevel)" size="small">
                  {{ post.authorLevel }}
                </a-tag>
                <a-tag v-if="post.isVip" color="gold" size="small">
                  <i class="fas fa-crown" :style="{ marginRight: '4px' }"></i>
                  VIP
                </a-tag>
              </div>
              <div :style="{ display: 'flex', gap: '16px', fontSize: '13px', color: '#8c8c8c' }">
                <span><i class="fas fa-clock" :style="{ marginRight: '4px' }"></i>{{ post.createTime }}</span>
                <span v-if="post.lastEditTime"><i class="fas fa-edit" :style="{ marginRight: '4px' }"></i>最后编辑：{{ post.lastEditTime }}</span>
                <span><i class="fas fa-eye" :style="{ marginRight: '4px' }"></i>{{ post.views }} 浏览</span>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <a-dropdown v-if="isAuthor">
            <a-button type="text">
              <i class="fas fa-ellipsis-h"></i>
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="editPost">
                  <i class="fas fa-edit" :style="{ marginRight: '8px' }"></i>
                  编辑
                </a-menu-item>
                <a-menu-item @click="deletePost" danger>
                  <i class="fas fa-trash" :style="{ marginRight: '8px' }"></i>
                  删除
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>

        <!-- 帖子标题 -->
        <h1 :style="{ fontSize: '24px', fontWeight: '600', margin: '0 0 12px 0', lineHeight: '1.4' }">
          {{ post.title }}
        </h1>

        <!-- 标签 -->
        <div :style="{ marginBottom: '16px', display: 'flex', gap: '8px', flexWrap: 'wrap' }">
          <a-tag 
            v-for="tag in post.tags" 
            :key="tag" 
            :color="getTagColor(tag)"
            @click="searchByTag(tag)"
            :style="{ cursor: 'pointer', fontSize: '13px', padding: '2px 10px', borderRadius: '4px' }"
          >
            # {{ tag }}
          </a-tag>
          <a-tag :color="getTypeColor(post.type)" :style="{ fontSize: '13px', padding: '2px 10px', borderRadius: '4px' }">
            {{ getTypeName(post.type) }}
          </a-tag>
        </div>

        <!-- 帖子内容 -->
        <div :style="{ fontSize: '14px', lineHeight: '1.8', marginBottom: '16px', whiteSpace: 'pre-wrap', wordBreak: 'break-word' }">
          {{ post.content }}
        </div>

        <!-- 图片列表 -->
        <div v-if="post.images && post.images.length > 0" :style="{ marginBottom: '16px' }">
          <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '12px' }">
            <div 
              v-for="(image, index) in post.images" 
              :key="index"
              :style="{ 
                borderRadius: '8px', 
                overflow: 'hidden', 
                cursor: 'pointer',
                border: '1px solid #e8e8e8',
                transition: 'all 0.3s ease',
                position: 'relative',
                paddingBottom: '100%'
              }"
              @click="previewImage(index)"
            >
              <img 
                :src="image.url" 
                :alt="image.description || '图片'" 
                :style="{ 
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  width: '100%', 
                  height: '100%', 
                  objectFit: 'cover' 
                }" 
              />
              <div 
                v-if="image.description" 
                :style="{ 
                  position: 'absolute',
                  bottom: 0,
                  left: 0,
                  right: 0,
                  background: 'rgba(0,0,0,0.6)', 
                  color: 'white', 
                  padding: '8px', 
                  fontSize: '12px',
                  textAlign: 'center'
                }"
              >
                {{ image.description }}
              </div>
            </div>
          </div>
        </div>

        <!-- 识别结果展示 -->
        <div v-if="post.recognitionResults && post.recognitionResults.length > 0" :style="{ marginBottom: '16px', padding: '12px', background: '#fafafa', borderRadius: '8px' }">
          <h3 :style="{ fontSize: '15px', fontWeight: '500', marginBottom: '12px' }">
            <i class="fas fa-eye" :style="{ marginRight: '8px', color: '#1890ff' }"></i>
            识别结果
          </h3>
          <div :style="{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: '12px' }">
            <div 
              v-for="result in post.recognitionResults" 
              :key="result.id"
              :style="{ background: 'white', borderRadius: '8px', overflow: 'hidden', border: '1px solid #e8e8e8' }"
            >
              <div :style="{ position: 'relative', paddingBottom: '100%', background: '#f5f5f5' }">
                <img 
                  :src="result.image" 
                  :alt="result.label" 
                  :style="{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', objectFit: 'cover' }" 
                />
              </div>
              <div :style="{ padding: '12px' }">
                <div :style="{ fontSize: '14px', fontWeight: '500', color: '#262626', marginBottom: '8px' }">{{ result.label }}</div>
                <div>
                  <a-progress 
                    :percent="result.confidence" 
                    :show-info="false" 
                    size="small"
                    :stroke-color="getConfidenceColor(result.confidence)"
                  />
                  <span :style="{ fontSize: '12px', color: '#8c8c8c', marginTop: '4px', display: 'block' }">置信度: {{ result.confidence }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 互动区域 -->
        <div :style="{ 
          borderTop: '1px solid #f0f0f0', 
          paddingTop: '16px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '12px'
        }">
          <div :style="{ display: 'flex', gap: '16px', fontSize: '13px', opacity: 0.65 }">
            <span :style="{ display: 'flex', alignItems: 'center', gap: '6px' }">
              <i class="fas fa-thumbs-up"></i>
              {{ post.likes }} 点赞
            </span>
            <span :style="{ display: 'flex', alignItems: 'center', gap: '6px' }">
              <i class="fas fa-comment"></i>
              {{ post.comments || 0 }} 评论
            </span>
            <span :style="{ display: 'flex', alignItems: 'center', gap: '6px' }">
              <i class="fas fa-bookmark"></i>
              {{ post.bookmarks || 0 }} 收藏
            </span>
          </div>

          <div :style="{ display: 'flex', gap: '8px' }">
            <a-button 
              @click="toggleLike" 
              :type="isLiked ? 'primary' : 'default'"
            >
              <i class="fas fa-thumbs-up" :style="{ marginRight: '6px' }"></i>
              {{ isLiked ? '已点赞' : '点赞' }}
            </a-button>
            
            <a-button @click="toggleBookmark" :type="isBookmarked ? 'primary' : 'default'">
              <i class="fas fa-bookmark" :style="{ marginRight: '6px' }"></i>
              {{ isBookmarked ? '已收藏' : '收藏' }}
            </a-button>
            
            <a-button @click="scrollToComments">
              <i class="fas fa-comment" :style="{ marginRight: '6px' }"></i>
              评论
            </a-button>
          </div>
        </div>
      </a-card>

      <!-- 评论区域 -->
      <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
        <template #title>
          <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
            <span :style="{ fontSize: '16px', fontWeight: '500' }">评论区</span>
            <span :style="{ fontSize: '13px', opacity: 0.65, fontWeight: 'normal' }">共 {{ comments.length }} 条评论</span>
          </div>
        </template>

        <!-- 发表评论 -->
        <div :style="{ marginBottom: '16px', padding: '12px', background: '#fafafa', borderRadius: '8px' }">
          <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px' }">
            <a-avatar :src="ImageUtils.getImageUrl(currentUser.avatar)" :size="32">
              {{ currentUser.name.charAt(0) }}
            </a-avatar>
            <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ currentUser.name }}</span>
          </div>
          
          <div>
            <a-textarea
              v-model:value="newComment.content"
              placeholder="写下你的评论..."
              :rows="4"
              :maxlength="500"
              show-count
              :style="{ marginBottom: '12px' }"
            />
            
            <div :style="{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: '8px' }">
              <a-button @click="clearComment" size="small">清空</a-button>
              <a-button 
                type="primary" 
                @click="submitComment" 
                :loading="submittingComment"
                :disabled="!newComment.content.trim()"
                size="small"
              >
                发表评论
              </a-button>
            </div>
          </div>
        </div>

        <!-- 评论排序 -->
        <div :style="{ marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
          <a-radio-group v-model:value="commentSort" button-style="solid" size="small">
            <a-radio-button value="time">按时间</a-radio-button>
            <a-radio-button value="likes">按点赞</a-radio-button>
          </a-radio-group>
        </div>

        <!-- 评论列表 -->
        <div>
          <div 
            v-for="comment in sortedComments" 
            :key="comment.id"
            :style="{ 
              display: 'flex', 
              gap: '12px', 
              padding: '16px 0', 
              borderBottom: '1px solid #f0f0f0' 
            }"
          >
            <a-avatar :src="ImageUtils.getImageUrl(comment.authorAvatar)" :size="32">
              {{ comment.author.charAt(0) }}
            </a-avatar>
            
            <div :style="{ flex: 1 }">
              <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px', flexWrap: 'wrap' }">
                <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ comment.author }}</span>
                <a-tag v-if="comment.authorLevel" :color="getAuthorLevelColor(comment.authorLevel)" size="small">
                  {{ comment.authorLevel }}
                </a-tag>
                <span :style="{ fontSize: '12px', opacity: 0.65 }">{{ comment.createTime }}</span>
                <a-tag v-if="comment.isAuthor" color="blue" size="small">楼主</a-tag>
              </div>
              
              <div :style="{ fontSize: '14px', lineHeight: '1.6', marginBottom: '8px', whiteSpace: 'pre-wrap', wordBreak: 'break-word' }">
                {{ comment.content }}
              </div>
              
              <div v-if="comment.images" :style="{ display: 'flex', gap: '8px', marginBottom: '12px', flexWrap: 'wrap' }">
                <div
                  v-for="(image, index) in comment.images" 
                  :key="index"
                  :style="{ 
                    width: '80px', 
                    height: '80px', 
                    borderRadius: '6px', 
                    overflow: 'hidden',
                    cursor: 'pointer',
                    border: '1px solid #e8e8e8'
                  }"
                  @click="previewCommentImage(image)"
                >
                  <img 
                    :src="image" 
                    :style="{ width: '100%', height: '100%', objectFit: 'cover' }" 
                  />
                </div>
              </div>
              
              <div :style="{ display: 'flex', gap: '12px', alignItems: 'center' }">
                <a-button type="text" @click="toggleCommentLike(comment)" size="small" :style="{ padding: '0' }">
                  <i class="fas fa-thumbs-up" :style="{ marginRight: '4px', color: comment.isLiked ? '#1890ff' : undefined }"></i>
                  <span>{{ comment.likes || 0 }}</span>
                </a-button>
                
                <a-button type="text" @click="replyToComment(comment)" size="small" :style="{ padding: '0' }">
                  <i class="fas fa-reply" :style="{ marginRight: '4px' }"></i>
                  回复
                </a-button>
                
                <a-button 
                  v-if="comment.userId === userStore.userId && userStore.userId > 0" 
                  type="text" 
                  @click="deleteComment(comment)" 
                  size="small" 
                  :style="{ padding: '0', color: '#ff4d4f' }"
                >
                  <i class="fas fa-trash" :style="{ marginRight: '4px' }"></i>
                  删除
                </a-button>
              </div>
              
              <!-- 回复列表 -->
              <div v-if="comment.replies && comment.replies.length > 0" :style="{ marginTop: '8px', padding: '8px', background: '#fafafa', borderRadius: '6px' }">
                <div 
                  v-for="reply in comment.replies" 
                  :key="reply.id"
                  :style="{ display: 'flex', gap: '6px', marginBottom: '6px', alignItems: 'flex-start' }"
                >
                  <a-avatar :src="ImageUtils.getImageUrl(reply.authorAvatar)" :size="24">
                    {{ reply.author.charAt(0) }}
                  </a-avatar>
                  <div :style="{ flex: 1 }">
                    <span :style="{ fontSize: '13px', fontWeight: '500', marginRight: '6px' }">{{ reply.author }}:</span>
                    <span :style="{ fontSize: '13px' }">{{ reply.content }}</span>
                    <span :style="{ fontSize: '12px', opacity: 0.65, marginLeft: '8px' }">{{ reply.createTime }}</span>
                  </div>
                </div>
              </div>
              
              <!-- 回复输入框 -->
              <div v-if="replyingTo === comment.id" :style="{ marginTop: '8px' }">
                <a-textarea
                  v-model:value="replyContent"
                  :placeholder="`回复 ${comment.author}:`"
                  :rows="2"
                  :maxlength="200"
                  size="small"
                  :style="{ marginBottom: '8px' }"
                />
                <div :style="{ display: 'flex', justifyContent: 'flex-end', gap: '8px' }">
                  <a-button @click="cancelReply" size="small">取消</a-button>
                  <a-button type="primary" @click="submitReply(comment)" size="small">回复</a-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多评论 -->
        <div v-if="hasMoreComments" :style="{ textAlign: 'center', marginTop: '16px' }">
          <a-button @click="loadMoreComments" :loading="loadingComments" size="small">
            加载更多评论
          </a-button>
        </div>
      </a-card>

      <!-- 相关推荐 -->
      <a-card :style="{ borderRadius: '8px' }">
        <template #title>
          <span :style="{ fontSize: '16px', fontWeight: '500' }">相关推荐</span>
        </template>
        <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
          <div 
            v-for="relatedPost in relatedPosts" 
            :key="relatedPost.id"
            :style="{ 
              display: 'flex', 
              gap: '12px', 
              padding: '12px', 
              borderRadius: '8px',
              cursor: 'pointer',
              transition: 'all 0.3s ease',
              border: '1px solid #f0f0f0'
            }"
            @click="viewRelatedPost(relatedPost)"
          >
            <div :style="{ 
              width: '80px', 
              height: '60px', 
              borderRadius: '6px', 
              overflow: 'hidden',
              flexShrink: 0,
              background: '#f5f5f5'
            }">
              <img 
                :src="relatedPost.thumbnail || '/api/placeholder/80/60'" 
                :alt="relatedPost.title" 
                :style="{ width: '100%', height: '100%', objectFit: 'cover' }"
              />
            </div>
            <div :style="{ flex: 1, minWidth: 0 }">
              <div :style="{ fontSize: '14px', fontWeight: '500', color: '#262626', margin: '0 0 6px 0', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }">
                {{ relatedPost.title }}
              </div>
              <div :style="{ fontSize: '13px', opacity: 0.65, margin: '0 0 6px 0', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }">
                {{ relatedPost.excerpt }}
              </div>
              <div :style="{ display: 'flex', gap: '12px', fontSize: '12px', opacity: 0.65 }">
                <span>{{ relatedPost.author }}</span>
                <span>{{ relatedPost.createTime }}</span>
                <span>
                  <i class="fas fa-thumbs-up" :style="{ marginRight: '4px' }"></i>
                  {{ relatedPost.likes }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </a-card>
    </div>

    <!-- 图片预览 -->
    <a-modal
      v-model:open="imagePreviewVisible"
      :footer="null"
      width="80%"
      centered
    >
      <div :style="{ textAlign: 'center' }">
        <img 
          :src="previewImageUrl" 
          :alt="previewImageDescription" 
          :style="{ maxWidth: '100%', maxHeight: '80vh', borderRadius: '8px' }"
        />
        <p 
          v-if="previewImageDescription" 
          :style="{ marginTop: '16px', fontSize: '14px', opacity: 0.65 }"
        >
          {{ previewImageDescription }}
        </p>
      </div>
    </a-modal>

    <!-- 帖子编辑模态框 -->
    <PostEditModal
      v-model:visible="editModalVisible"
      :post="editPostData"
      @success="handleEditSuccess"
    />
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { CommunityAPI } from '@/api/community'
import { CommentAPI } from '@/api/comments'
import { ImageUtils } from '@/utils/image'
import { useUserStore } from '@/stores/user'
import PostEditModal from './user-profile/PostEditModal.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前用户信息（从store获取）
const currentUser = computed(() => ({
  id: userStore.userId,
  name: userStore.userNickname,
  avatar: userStore.userAvatar,
  level: '资深用户'
}))

// 加载状态
const loading = ref(false)

// 帖子数据
const post = ref({
  id: 0,
  title: '',
  author: '',
  authorId: 0,
  authorAvatar: '',
  authorLevel: '',
  isVip: false,
  type: '',
  createTime: '',
  lastEditTime: '',
  views: 0,
  likes: 0,
  comments: 0,
  bookmarks: 0,
  tags: [] as string[],
  content: '',
  images: [] as { url: string; description?: string }[],
  recognitionResults: [] as { id: number; image: string; label: string; confidence: number }[]
})

// 交互状态
const isLiked = ref(false)
const isBookmarked = ref(false)
const isAuthor = computed(() => post.value.authorId === userStore.userId && userStore.userId > 0)

// 评论相关
const comments = ref<any[]>([])

const newComment = reactive({
  content: ''
})

const commentSort = ref('time')
const submittingComment = ref(false)
const replyingTo = ref(null)
const replyContent = ref('')
const loadingComments = ref(false)
const hasMoreComments = ref(true)

// 相关推荐
const relatedPosts = ref<any[]>([])

// 图片预览
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')
const previewImageDescription = ref('')

// 帖子编辑
const editModalVisible = ref(false)
const editPostData = ref<any>(null)

// 排序后的评论
const sortedComments = computed(() => {
  const sorted = [...comments.value]
  if (commentSort.value === 'likes') {
    return sorted.sort((a, b) => (b.likes || 0) - (a.likes || 0))
  }
  return sorted.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
})

// 初始化
onMounted(async () => {
  // 从路由参数获取帖子ID
  const postId = route.params.id
  if (postId) {
    await loadPostDetail(postId as string)
  }
})

// 方法
function goBack() {
  router.push('/user/community')
}

// 加载帖子详情
async function loadPostDetail(postId: string | number) {
  try {
    loading.value = true
    console.log('加载帖子详情, ID:', postId)
    
    const postData = await CommunityAPI.getPostDetail(Number(postId))
    
    console.log('帖子详情数据:', postData)
    
    // 解析图片数据 - 使用 ImageUtils
    let parsedImages: { url: string; description?: string }[] = []
    if (postData.images) {
      try {
        // images 可能是字符串数组或 JSON 字符串
        const imageArray = Array.isArray(postData.images) 
          ? postData.images 
          : JSON.parse(postData.images)
        if (Array.isArray(imageArray)) {
          parsedImages = imageArray.map((url: string) => ({
            url: ImageUtils.getImageUrl(url),
            description: ''
          }))
        }
      } catch (e) {
        console.warn('解析图片失败:', postData.images, e)
        parsedImages = []
      }
    }
    
    // 转换数据格式
    post.value = {
      id: postData.id,
      title: postData.title,
      author: postData.authorName || '未知用户',
      authorId: postData.authorId,
      authorAvatar: ImageUtils.getImageUrl(postData.authorAvatar),
      authorLevel: '用户', // 可以根据用户角色设置
      isVip: false, // 需要从用户信息判断
      type: postData.category || 'discussion',
      createTime: formatTime(postData.createdAt),
      lastEditTime: postData.updatedAt ? formatTime(postData.updatedAt) : '',
      views: postData.viewCount,
      likes: postData.likeCount,
      comments: postData.commentCount,
      bookmarks: 0, // 需要从后端获取
      tags: postData.tags ? postData.tags.split(',').filter((t: string) => t.trim()) : [],
      content: postData.content,
      images: parsedImages,
      recognitionResults: []
    }
    
    console.log('转换后的帖子数据:', post.value)
    console.log('当前用户ID:', userStore.userId)
    console.log('帖子作者ID:', post.value.authorId)
    console.log('是否为作者:', isAuthor.value)
    
    // 加载评论
    await loadComments(postData.id)
    
    // 加载相关推荐
    await loadRelatedPosts(postData.id)
    
  } catch (error) {
    console.error('加载帖子详情失败:', error)
    message.error('加载帖子详情失败')
  } finally {
    loading.value = false
  }
}

// 加载相关推荐帖子
async function loadRelatedPosts(postId: number) {
  try {
    // 使用相关推荐API获取同分类的帖子
    const relatedData = await CommunityAPI.getRelatedPosts(postId)
    
    console.log('相关推荐数据:', relatedData)
    
    relatedPosts.value = relatedData.map((p: any) => ({
      id: p.id,
      title: p.title,
      excerpt: p.content ? p.content.substring(0, 50) + '...' : '',
      author: p.authorName || '未知用户',
      createTime: formatTime(p.createdAt),
      likes: p.likeCount,
      thumbnail: p.images && p.images.length > 0 ? ImageUtils.getImageUrl(p.images[0]) : '/api/placeholder/80/60'
    }))
  } catch (error) {
    console.error('加载相关推荐失败:', error)
    // 失败时显示空列表
    relatedPosts.value = []
  }
}

// 加载评论
async function loadComments(postId: number) {
  try {
    loadingComments.value = true
    const response = await CommentAPI.getComments({
      targetType: 1, // 1 表示帖子
      targetId: postId,
      page: 1,
      size: 20
    })
    
    console.log('评论数据:', response)
    
    // 转换评论数据
    comments.value = response.data.map((comment: any) => ({
      id: comment.id,
      userId: comment.userId,
      author: comment.username || '未知用户',
      authorAvatar: comment.avatar || '',
      authorLevel: '用户',
      isAuthor: comment.userId === post.value.authorId,
      createTime: formatTime(comment.createdAt),
      content: comment.content,
      likes: comment.likeCount || 0,
      isLiked: comment.isLiked || false,
      replies: comment.replies || []
    }))
    
    hasMoreComments.value = response.total > 20
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    loadingComments.value = false
  }
}

// 格式化时间
function formatTime(timeStr: string): string {
  const time = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    return time.toLocaleDateString()
  }
}

function getAuthorLevelColor(level: string) {
  const colors: Record<string, string> = {
    '新手': 'green',
    '资深用户': 'blue',
    '专家': 'purple'
  }
  return colors[level] || 'default'
}

function getTagColor(tag: string) {
  const colors = ['blue', 'green', 'orange', 'purple', 'cyan', 'magenta']
  return colors[tag.length % colors.length]
}

function getTypeColor(type: string) {
  const colors: Record<string, string> = {
    'share': 'blue',
    'question': 'orange',
    'discussion': 'purple'
  }
  return colors[type] || 'default'
}

function getTypeName(type: string) {
  const names: Record<string, string> = {
    'share': '分享',
    'question': '问答',
    'discussion': '讨论'
  }
  return names[type] || '其他'
}

function getConfidenceColor(confidence: number) {
  if (confidence >= 90) return '#52c41a'
  if (confidence >= 70) return '#faad14'
  return '#ff4d4f'
}

function searchByTag(tag: string) {
  router.push(`/user/community?tag=${encodeURIComponent(tag)}`)
}

function previewImage(index: number) {
  const image = post.value.images[index]
  previewImageUrl.value = image.url
  previewImageDescription.value = image.description || ''
  imagePreviewVisible.value = true
}

function previewCommentImage(imageUrl: string) {
  previewImageUrl.value = imageUrl
  previewImageDescription.value = ''
  imagePreviewVisible.value = true
}

async function toggleLike() {
  try {
    if (isLiked.value) {
      await CommunityAPI.unlikePost(post.value.id)
      isLiked.value = false
      post.value.likes--
      message.success('取消点赞')
    } else {
      await CommunityAPI.likePost(post.value.id)
      isLiked.value = true
      post.value.likes++
      message.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    message.error('操作失败，请重试')
  }
}

async function toggleBookmark() {
  try {
    if (isBookmarked.value) {
      await CommunityAPI.uncollectPost(post.value.id)
      isBookmarked.value = false
      post.value.bookmarks--
      message.success('取消收藏')
    } else {
      await CommunityAPI.collectPost(post.value.id)
      isBookmarked.value = true
      post.value.bookmarks++
      message.success('收藏成功')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    message.error('操作失败，请重试')
  }
}

function scrollToComments() {
  const commentsSection = document.querySelector('.comments-section')
  if (commentsSection) {
    commentsSection.scrollIntoView({ behavior: 'smooth' })
  }
}

function editPost() {
  console.log('编辑帖子:', post.value)
  
  // 准备编辑数据，需要将图片从对象数组转换为URL数组
  const imageUrls = post.value.images.map((img: any) => {
    // 移除 ImageUtils 处理后的完整URL，提取原始路径
    const url = img.url || img
    // 如果URL包含服务器地址，提取路径部分
    if (typeof url === 'string') {
      const match = url.match(/\/tos\/[^?]+/)
      return match ? match[0] : url
    }
    return url
  })
  
  editPostData.value = {
    id: post.value.id,
    title: post.value.title,
    category: post.value.type,
    tags: post.value.tags.join(','),
    content: post.value.content,
    image: imageUrls.length > 0 ? imageUrls[0] : undefined,
    images: imageUrls,
    status: 1
  }
  
  editModalVisible.value = true
}

// 编辑成功回调
async function handleEditSuccess() {
  message.success('帖子更新成功!')
  editModalVisible.value = false
  // 重新加载帖子详情
  const postId = route.params.id
  if (postId) {
    await loadPostDetail(postId as string)
  }
}

async function deletePost() {
  try {
    await CommunityAPI.deletePost(post.value.id)
    message.success('删除成功')
    // 返回社区列表
    router.push('/user/community')
  } catch (error) {
    console.error('删除帖子失败:', error)
    message.error('删除失败')
  }
}

function insertEmoji() {
  message.info('表情功能开发中')
}

function uploadImage() {
  message.info('图片上传功能开发中')
}

function clearComment() {
  newComment.content = ''
}

async function submitComment() {
  if (!newComment.content.trim()) return
  
  submittingComment.value = true
  try {
    await CommentAPI.createComment({
      targetType: 1, // 1 表示帖子
      targetId: post.value.id,
      content: newComment.content
    })
    
    // 重新加载评论
    await loadComments(post.value.id)
    
    post.value.comments++
    newComment.content = ''
    message.success('评论发表成功')
  } catch (error) {
    console.error('评论发表失败:', error)
    message.error('评论发表失败')
  } finally {
    submittingComment.value = false
  }
}

async function toggleCommentLike(comment: any) {
  try {
    if (comment.isLiked) {
      await CommentAPI.unlikeComment(comment.id)
      comment.isLiked = false
      comment.likes--
    } else {
      await CommentAPI.likeComment(comment.id)
      comment.isLiked = true
      comment.likes++
    }
  } catch (error) {
    console.error('评论点赞操作失败:', error)
    message.error('操作失败，请重试')
  }
}

function replyToComment(comment: any) {
  replyingTo.value = comment.id
  replyContent.value = ''
}

function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
}

async function submitReply(comment: any) {
  if (!replyContent.value.trim()) return
  
  try {
    const newReply = await CommentAPI.createComment({
      targetType: 1, // 1 表示帖子
      targetId: post.value.id,
      content: replyContent.value,
      parentId: comment.id
    })
    
    // 添加到回复列表
    if (!comment.replies) {
      comment.replies = []
    }
    comment.replies.push({
      id: newReply.id,
      author: newReply.username || currentUser.value.name,
      authorAvatar: newReply.avatar || currentUser.value.avatar,
      createTime: '刚刚',
      content: replyContent.value
    })
    
    cancelReply()
    message.success('回复发表成功')
  } catch (error) {
    console.error('回复发表失败:', error)
    message.error('回复发表失败')
  }
}

async function deleteComment(comment: any) {
  try {
    await CommentAPI.deleteComment(comment.id)
    // 从列表中移除
    const index = comments.value.findIndex(c => c.id === comment.id)
    if (index > -1) {
      comments.value.splice(index, 1)
      post.value.comments--
    }
    message.success('删除成功')
  } catch (error) {
    console.error('删除评论失败:', error)
    message.error('删除失败')
  }
}

function loadMoreComments() {
  loadingComments.value = true
  // 模拟加载更多评论
  setTimeout(() => {
    loadingComments.value = false
    hasMoreComments.value = false
    message.success('已加载全部评论')
  }, 1000)
}

async function viewRelatedPost(relatedPost: any) {
  // 跳转到相关帖子并重新加载
  await router.push(`/user/community/post/${relatedPost.id}`)
  await loadPostDetail(relatedPost.id)
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

