<template>
  <div class="post-detail-page">
    <!-- 返回按钮 -->
    <div class="back-section">
      <a-button @click="goBack" size="large">
        <i class="fas fa-arrow-left"></i>
        返回社区
      </a-button>
    </div>

    <!-- 帖子内容 -->
    <a-card class="post-card">
      <div class="post-header">
        <!-- 作者信息 -->
        <div class="author-info">
          <a-avatar :size="50" :src="post.authorAvatar" class="author-avatar">
            {{ post.author.charAt(0) }}
          </a-avatar>
          <div class="author-details">
            <div class="author-name">
              <span>{{ post.author }}</span>
              <a-tag v-if="post.authorLevel" :color="getAuthorLevelColor(post.authorLevel)">
                {{ post.authorLevel }}
              </a-tag>
              <a-tag v-if="post.isVip" color="gold">
                <i class="fas fa-crown"></i>
                VIP
              </a-tag>
            </div>
            <div class="post-meta">
              <span><i class="fas fa-clock"></i> {{ post.createTime }}</span>
              <span v-if="post.lastEditTime"><i class="fas fa-edit"></i> 最后编辑：{{ post.lastEditTime }}</span>
              <span><i class="fas fa-eye"></i> {{ post.views }} 浏览</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="post-actions">
          <a-dropdown>
            <a-button type="text">
              <i class="fas fa-ellipsis-h"></i>
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="sharePost">
                  <i class="fas fa-share"></i>
                  分享
                </a-menu-item>
                <a-menu-item @click="reportPost">
                  <i class="fas fa-flag"></i>
                  举报
                </a-menu-item>
                <a-menu-item v-if="isAuthor" @click="editPost">
                  <i class="fas fa-edit"></i>
                  编辑
                </a-menu-item>
                <a-menu-item v-if="isAuthor" @click="deletePost" class="danger-item">
                  <i class="fas fa-trash"></i>
                  删除
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </div>

      <!-- 帖子标题 -->
      <h1 class="post-title">{{ post.title }}</h1>

      <!-- 标签 -->
      <div class="post-tags">
        <a-tag 
          v-for="tag in post.tags" 
          :key="tag" 
          :color="getTagColor(tag)"
          @click="searchByTag(tag)"
          class="clickable-tag"
        >
          {{ tag }}
        </a-tag>
        <a-tag :color="getTypeColor(post.type)">
          {{ getTypeName(post.type) }}
        </a-tag>
      </div>

      <!-- 帖子内容 -->
      <div class="post-content" v-html="post.content"></div>

      <!-- 图片列表 -->
      <div v-if="post.images && post.images.length > 0" class="post-images">
        <div class="images-grid">
          <div 
            v-for="(image, index) in post.images" 
            :key="index"
            class="image-item"
            @click="previewImage(index)"
          >
            <img :src="image.url" :alt="image.description || '图片'" />
            <div v-if="image.description" class="image-description">
              {{ image.description }}
            </div>
          </div>
        </div>
      </div>

      <!-- 识别结果展示 -->
      <div v-if="post.recognitionResults && post.recognitionResults.length > 0" class="recognition-results">
        <h3><i class="fas fa-eye"></i> 识别结果</h3>
        <div class="results-grid">
          <div 
            v-for="result in post.recognitionResults" 
            :key="result.id"
            class="result-item"
          >
            <div class="result-image">
              <img :src="result.image" :alt="result.label" />
            </div>
            <div class="result-info">
              <div class="result-label">{{ result.label }}</div>
              <div class="result-confidence">
                <a-progress 
                  :percent="result.confidence" 
                  :show-info="false" 
                  size="small"
                  :stroke-color="getConfidenceColor(result.confidence)"
                />
                <span class="confidence-text">{{ result.confidence }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 互动区域 -->
      <div class="post-interactions">
        <div class="interaction-stats">
          <span class="stat-item">
            <i class="fas fa-thumbs-up"></i>
            {{ post.likes }} 点赞
          </span>
          <span class="stat-item">
            <i class="fas fa-comment"></i>
            {{ post.comments || 0 }} 评论
          </span>
          <span class="stat-item">
            <i class="fas fa-bookmark"></i>
            {{ post.bookmarks || 0 }} 收藏
          </span>
        </div>

        <div class="interaction-buttons">
          <a-button 
            @click="toggleLike" 
            :type="isLiked ? 'primary' : 'default'"
            :class="{ 'liked': isLiked }"
          >
            <i class="fas fa-thumbs-up"></i>
            {{ isLiked ? '已点赞' : '点赞' }}
          </a-button>
          
          <a-button @click="toggleBookmark" :type="isBookmarked ? 'primary' : 'default'">
            <i class="fas fa-bookmark"></i>
            {{ isBookmarked ? '已收藏' : '收藏' }}
          </a-button>
          
          <a-button @click="scrollToComments">
            <i class="fas fa-comment"></i>
            评论
          </a-button>
        </div>
      </div>
    </a-card>

    <!-- 评论区域 -->
    <a-card class="comments-section" title="评论区">
      <template #extra>
        <span class="comments-count">共 {{ comments.length }} 条评论</span>
      </template>

      <!-- 发表评论 -->
      <div class="comment-form">
        <div class="comment-form-header">
          <a-avatar :src="currentUser.avatar" class="user-avatar">
            {{ currentUser.name.charAt(0) }}
          </a-avatar>
          <span class="user-name">{{ currentUser.name }}</span>
        </div>
        
        <div class="comment-editor">
          <a-textarea
            v-model:value="newComment.content"
            placeholder="写下你的评论..."
            :rows="4"
            :maxlength="500"
            show-count
          />
          
          <div class="comment-actions">
            <div class="comment-tools">
              <a-button type="text" @click="insertEmoji">
                <i class="fas fa-smile"></i>
                表情
              </a-button>
              <a-button type="text" @click="uploadImage">
                <i class="fas fa-image"></i>
                图片
              </a-button>
            </div>
            
            <div class="submit-actions">
              <a-button @click="clearComment">清空</a-button>
              <a-button 
                type="primary" 
                @click="submitComment" 
                :loading="submittingComment"
                :disabled="!newComment.content.trim()"
              >
                发表评论
              </a-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 评论排序 -->
      <div class="comments-header">
        <a-radio-group v-model:value="commentSort" button-style="solid" size="small">
          <a-radio-button value="time">按时间</a-radio-button>
          <a-radio-button value="likes">按点赞</a-radio-button>
        </a-radio-group>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div 
          v-for="comment in sortedComments" 
          :key="comment.id"
          class="comment-item"
        >
          <div class="comment-avatar">
            <a-avatar :src="comment.authorAvatar">
              {{ comment.author.charAt(0) }}
            </a-avatar>
          </div>
          
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.author }}</span>
              <a-tag v-if="comment.authorLevel" :color="getAuthorLevelColor(comment.authorLevel)" size="small">
                {{ comment.authorLevel }}
              </a-tag>
              <span class="comment-time">{{ comment.createTime }}</span>
              <a-tag v-if="comment.isAuthor" color="blue" size="small">楼主</a-tag>
            </div>
            
            <div class="comment-text" v-html="comment.content"></div>
            
            <div v-if="comment.images" class="comment-images">
              <img 
                v-for="(image, index) in comment.images" 
                :key="index"
                :src="image" 
                @click="previewCommentImage(image)"
                class="comment-image"
              />
            </div>
            
            <div class="comment-actions">
              <a-button type="text" @click="toggleCommentLike(comment)" size="small">
                <i class="fas fa-thumbs-up" :class="{ 'liked': comment.isLiked }"></i>
                {{ comment.likes || 0 }}
              </a-button>
              
              <a-button type="text" @click="replyToComment(comment)" size="small">
                <i class="fas fa-reply"></i>
                回复
              </a-button>
              
              <a-dropdown>
                <a-button type="text" size="small">
                  <i class="fas fa-ellipsis-h"></i>
                </a-button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="reportComment(comment)">
                      <i class="fas fa-flag"></i>
                      举报
                    </a-menu-item>
                    <a-menu-item v-if="comment.author === currentUser.name" @click="deleteComment(comment)">
                      <i class="fas fa-trash"></i>
                      删除
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>
            
            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
              <div 
                v-for="reply in comment.replies" 
                :key="reply.id"
                class="reply-item"
              >
                <a-avatar :src="reply.authorAvatar" size="small">
                  {{ reply.author.charAt(0) }}
                </a-avatar>
                <div class="reply-content">
                  <span class="reply-author">{{ reply.author }}</span>
                  <span class="reply-text">{{ reply.content }}</span>
                  <span class="reply-time">{{ reply.createTime }}</span>
                </div>
              </div>
            </div>
            
            <!-- 回复输入框 -->
            <div v-if="replyingTo === comment.id" class="reply-form">
              <a-textarea
                v-model:value="replyContent"
                :placeholder="`回复 ${comment.author}:`"
                :rows="2"
                :maxlength="200"
              />
              <div class="reply-actions">
                <a-button @click="cancelReply" size="small">取消</a-button>
                <a-button type="primary" @click="submitReply(comment)" size="small">回复</a-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载更多评论 -->
      <div v-if="hasMoreComments" class="load-more-comments">
        <a-button @click="loadMoreComments" :loading="loadingComments">
          加载更多评论
        </a-button>
      </div>
    </a-card>

    <!-- 相关推荐 -->
    <a-card class="related-posts" title="相关推荐">
      <div class="related-posts-list">
        <div 
          v-for="relatedPost in relatedPosts" 
          :key="relatedPost.id"
          class="related-post-item"
          @click="viewRelatedPost(relatedPost)"
        >
          <div class="related-post-image">
            <img :src="relatedPost.thumbnail || '/api/placeholder/80/60'" :alt="relatedPost.title" />
          </div>
          <div class="related-post-content">
            <h4>{{ relatedPost.title }}</h4>
            <p>{{ relatedPost.excerpt }}</p>
            <div class="related-post-meta">
              <span>{{ relatedPost.author }}</span>
              <span>{{ relatedPost.createTime }}</span>
              <span>{{ relatedPost.likes }} 点赞</span>
            </div>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 图片预览 -->
    <a-modal
      v-model:open="imagePreviewVisible"
      :footer="null"
      width="80%"
      centered
      class="image-preview-modal"
    >
      <div class="image-preview-container">
        <img :src="previewImageUrl" :alt="previewImageDescription" />
        <p v-if="previewImageDescription" class="preview-description">
          {{ previewImageDescription }}
        </p>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()

// 当前用户信息
const currentUser = reactive({
  name: '张三',
  avatar: '',
  level: '资深用户'
})

// 帖子数据
const post = ref({
  id: 1,
  title: 'AI图像识别技巧分享：如何提高识别准确率',
  author: '技术专家李四',
  authorAvatar: '',
  authorLevel: '专家',
  isVip: true,
  type: 'share',
  createTime: '2小时前',
  lastEditTime: '1小时前',
  views: 1234,
  likes: 89,
  comments: 23,
  bookmarks: 45,
  tags: ['图像识别', 'AI技术', '准确率优化', '深度学习'],
  content: `
    <p>大家好！今天我想和大家分享一些在使用AI图像识别过程中提高准确率的实用技巧。</p>
    
    <h3>1. 图片质量优化</h3>
    <p>图片质量是影响识别准确率的关键因素：</p>
    <ul>
      <li><strong>分辨率</strong>：建议使用至少512x512像素的图片</li>
      <li><strong>清晰度</strong>：避免模糊、抖动的图片</li>
      <li><strong>光照条件</strong>：确保良好的光照，避免过暗或过亮</li>
      <li><strong>角度选择</strong>：选择正面或侧面清晰的角度</li>
    </ul>
    
    <h3>2. 背景处理</h3>
    <p>简洁的背景有助于AI更好地识别主体：</p>
    <ul>
      <li>尽量选择纯色或简单的背景</li>
      <li>避免复杂的背景干扰</li>
      <li>确保主体与背景有明显对比</li>
    </ul>
    
    <h3>3. 多角度拍摄</h3>
    <p>对于复杂物体，可以尝试多个角度：</p>
    <ul>
      <li>正面、侧面、俯视等不同角度</li>
      <li>局部特征的特写</li>
      <li>整体轮廓的展示</li>
    </ul>
    
    <p>希望这些技巧对大家有帮助！欢迎在评论区分享你们的经验。</p>
  `,
  images: [
    {
      url: '/api/placeholder/400/300',
      description: '高质量图片示例'
    },
    {
      url: '/api/placeholder/400/300',
      description: '背景处理对比'
    }
  ],
  recognitionResults: [
    {
      id: 1,
      image: '/api/placeholder/150/150',
      label: '金毛犬',
      confidence: 95
    },
    {
      id: 2,
      image: '/api/placeholder/150/150',
      label: '玫瑰花',
      confidence: 88
    }
  ]
})

// 交互状态
const isLiked = ref(false)
const isBookmarked = ref(false)
const isAuthor = computed(() => post.value.author === currentUser.name)

// 评论相关
const comments = ref([
  {
    id: 1,
    author: '新手小王',
    authorAvatar: '',
    authorLevel: '新手',
    isAuthor: false,
    createTime: '1小时前',
    content: '非常实用的技巧！我试了一下，识别准确率确实提高了不少。特别是背景处理这个建议很有用。',
    likes: 12,
    isLiked: false,
    replies: [
      {
        id: 11,
        author: '技术专家李四',
        authorAvatar: '',
        createTime: '30分钟前',
        content: '很高兴能帮到你！继续加油！'
      }
    ]
  },
  {
    id: 2,
    author: 'AI爱好者',
    authorAvatar: '',
    authorLevel: '资深用户',
    isAuthor: false,
    createTime: '45分钟前',
    content: '请问楼主，对于动物识别，有什么特别的建议吗？我在识别宠物时准确率不太理想。',
    likes: 8,
    isLiked: true,
    replies: []
  }
])

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
const relatedPosts = ref([
  {
    id: 2,
    title: '深度学习在图像识别中的应用',
    excerpt: '探讨现代深度学习技术在图像识别领域的最新应用...',
    author: '研究员张三',
    createTime: '1天前',
    likes: 156,
    thumbnail: '/api/placeholder/80/60'
  },
  {
    id: 3,
    title: '常见动物识别问题解答',
    excerpt: '总结了用户在动物识别过程中遇到的常见问题...',
    author: 'AI专家王五',
    createTime: '2天前',
    likes: 98,
    thumbnail: '/api/placeholder/80/60'
  },
  {
    id: 4,
    title: '图像预处理技术详解',
    excerpt: '详细介绍各种图像预处理技术对识别效果的影响...',
    author: '算法工程师',
    createTime: '3天前',
    likes: 134,
    thumbnail: '/api/placeholder/80/60'
  }
])

// 图片预览
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')
const previewImageDescription = ref('')

// 排序后的评论
const sortedComments = computed(() => {
  const sorted = [...comments.value]
  if (commentSort.value === 'likes') {
    return sorted.sort((a, b) => (b.likes || 0) - (a.likes || 0))
  }
  return sorted.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
})

// 初始化
onMounted(() => {
  // 从路由参数获取帖子ID
  const postId = route.params.id
  if (postId) {
    loadPostDetail(postId)
  }
  
  // 增加浏览量
  post.value.views++
})

// 方法
function goBack() {
  router.push('/user/community')
}

function loadPostDetail(postId: string | number) {
  // 这里应该调用API获取帖子详情
  console.log('Loading post detail for ID:', postId)
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

function toggleLike() {
  isLiked.value = !isLiked.value
  post.value.likes += isLiked.value ? 1 : -1
  message.success(isLiked.value ? '点赞成功' : '取消点赞')
}

function toggleBookmark() {
  isBookmarked.value = !isBookmarked.value
  post.value.bookmarks += isBookmarked.value ? 1 : -1
  message.success(isBookmarked.value ? '收藏成功' : '取消收藏')
}

function scrollToComments() {
  const commentsSection = document.querySelector('.comments-section')
  if (commentsSection) {
    commentsSection.scrollIntoView({ behavior: 'smooth' })
  }
}

function sharePost() {
  message.info('分享功能开发中')
}

function reportPost() {
  message.info('举报功能开发中')
}

function editPost() {
  message.info('编辑功能开发中')
}

function deletePost() {
  message.info('删除功能开发中')
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
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const comment = {
      id: Date.now(),
      author: currentUser.name,
      authorAvatar: currentUser.avatar,
      authorLevel: currentUser.level,
      isAuthor: false,
      createTime: '刚刚',
      content: newComment.content,
      likes: 0,
      isLiked: false,
      replies: []
    }
    
    comments.value.unshift(comment)
    post.value.comments++
    newComment.content = ''
    message.success('评论发表成功')
  } catch (error) {
    message.error('评论发表失败')
  } finally {
    submittingComment.value = false
  }
}

function toggleCommentLike(comment: any) {
  comment.isLiked = !comment.isLiked
  comment.likes = (comment.likes || 0) + (comment.isLiked ? 1 : -1)
}

function replyToComment(comment: any) {
  replyingTo.value = comment.id
  replyContent.value = ''
}

function cancelReply() {
  replyingTo.value = null
  replyContent.value = ''
}

function submitReply(comment: any) {
  if (!replyContent.value.trim()) return
  
  const reply = {
    id: Date.now(),
    author: currentUser.name,
    authorAvatar: currentUser.avatar,
    createTime: '刚刚',
    content: replyContent.value
  }
  
  if (!comment.replies) {
    comment.replies = []
  }
  comment.replies.push(reply)
  
  cancelReply()
  message.success('回复发表成功')
}

function reportComment(comment: any) {
  message.info('举报功能开发中')
}

function deleteComment(comment: any) {
  message.info('删除功能开发中')
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

function viewRelatedPost(post: any) {
  router.push(`/user/community/post/${post.id}`)
}
</script>

<style scoped>
.post-detail-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 返回按钮 */
.back-section {
  margin-bottom: 16px;
}

/* 帖子卡片 */
.post-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 帖子头部 */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.author-info {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.author-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: bold;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.author-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.post-meta i {
  margin-right: 4px;
}

/* 帖子标题 */
.post-title {
  font-size: 28px;
  font-weight: bold;
  color: #262626;
  margin: 24px 0;
  line-height: 1.4;
}

/* 标签 */
.post-tags {
  margin-bottom: 24px;
}

.clickable-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.clickable-tag:hover {
  transform: translateY(-1px);
}

/* 帖子内容 */
.post-content {
  font-size: 16px;
  line-height: 1.8;
  color: #262626;
  margin-bottom: 32px;
}

.post-content h3 {
  color: #262626;
  font-size: 20px;
  font-weight: 600;
  margin: 24px 0 16px;
}

.post-content ul {
  margin: 16px 0;
  padding-left: 24px;
}

.post-content li {
  margin-bottom: 8px;
}

.post-content strong {
  color: #1890ff;
  font-weight: 600;
}

/* 图片网格 */
.post-images {
  margin-bottom: 32px;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.image-item {
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.image-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.image-item img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.image-description {
  padding: 12px;
  background: #fafafa;
  font-size: 14px;
  color: #666;
  text-align: center;
}

/* 识别结果 */
.recognition-results {
  margin-bottom: 32px;
  padding: 24px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 8px;
}

.recognition-results h3 {
  color: #262626;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.recognition-results h3 i {
  color: #52c41a;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.result-image img {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
}

.result-info {
  flex: 1;
}

.result-label {
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.result-confidence {
  display: flex;
  align-items: center;
  gap: 8px;
}

.confidence-text {
  font-size: 12px;
  color: #666;
}

/* 互动区域 */
.post-interactions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
}

.interaction-stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  font-size: 14px;
  color: #666;
}

.stat-item i {
  margin-right: 4px;
  color: #999;
}

.interaction-buttons {
  display: flex;
  gap: 12px;
}

.interaction-buttons .liked {
  color: #1890ff;
}

/* 评论区域 */
.comments-section {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.comments-count {
  font-size: 14px;
  color: #666;
}

/* 评论表单 */
.comment-form {
  margin-bottom: 24px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
}

.comment-form-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: bold;
}

.user-name {
  font-weight: 500;
  color: #262626;
}

.comment-editor {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-tools {
  display: flex;
  gap: 8px;
}

.submit-actions {
  display: flex;
  gap: 8px;
}

/* 评论头部 */
.comments-header {
  margin-bottom: 24px;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.comment-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 500;
  color: #262626;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.comment-images {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.comment-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
  cursor: pointer;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.comment-actions .liked {
  color: #1890ff;
}

/* 回复列表 */
.replies-list {
  margin-top: 16px;
  padding-left: 20px;
  border-left: 2px solid #f0f0f0;
}

.reply-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 12px;
}

.reply-content {
  flex: 1;
  font-size: 14px;
}

.reply-author {
  font-weight: 500;
  color: #262626;
  margin-right: 8px;
}

.reply-text {
  color: #666;
  margin-right: 8px;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

/* 回复表单 */
.reply-form {
  margin-top: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 8px;
}

/* 加载更多 */
.load-more-comments {
  text-align: center;
  margin-top: 24px;
}

/* 相关推荐 */
.related-posts {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.related-posts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.related-post-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.related-post-item:hover {
  background: #fafafa;
}

.related-post-image img {
  width: 80px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
}

.related-post-content {
  flex: 1;
}

.related-post-content h4 {
  color: #262626;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
}

.related-post-content p {
  color: #666;
  font-size: 12px;
  margin-bottom: 8px;
  line-height: 1.4;
}

.related-post-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

/* 图片预览 */
.image-preview-container {
  text-align: center;
}

.image-preview-container img {
  max-width: 100%;
  max-height: 70vh;
  border-radius: 8px;
}

.preview-description {
  margin-top: 16px;
  color: #666;
  font-size: 14px;
}

/* 操作按钮样式 */
.danger-item {
  color: #ff4d4f;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-detail-page {
    padding: 16px;
  }
  
  .post-title {
    font-size: 22px;
  }
  
  .post-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .interaction-stats {
    flex-direction: column;
    gap: 8px;
  }
  
  .interaction-buttons {
    flex-wrap: wrap;
  }
  
  .comment-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .results-grid {
    grid-template-columns: 1fr;
  }
  
  .images-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .post-detail-page {
    padding: 12px;
  }
  
  .author-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .post-meta {
    flex-direction: column;
    gap: 4px;
  }
  
  .comment-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .related-post-item {
    flex-direction: column;
  }
}
</style>
