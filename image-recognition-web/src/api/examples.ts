/**
 * API使用示例
 * 
 * 这个文件展示了如何使用各个API模块
 * 在实际开发中，这些调用通常会在Vue组件或Pinia store中使用
 */

import API, { AuthAPI, KnowledgeAPI, CommunityAPI } from './index'
import type { LoginRequest, CreatePostRequest } from './types'

// ==================== 认证API使用示例 ====================

/**
 * 用户登录示例
 */
export async function loginExample() {
  try {
    const loginData: LoginRequest = {
      username: 'testuser',
      password: 'password123',
      captcha: '1234',
      rememberMe: true
    }

    const response = await AuthAPI.login(loginData)
    
    if (response.success && response.data.success) {
      // 登录成功，保存token
      const token = response.data.token
      if (token) {
        localStorage.setItem('userToken', token)
      }
      
      console.log('登录成功:', response.data.user)
      return response.data
    } else {
      console.error('登录失败:', response.data.message)
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('登录异常:', error)
    throw error
  }
}

/**
 * 获取验证码示例
 */
export async function getCaptchaExample() {
  try {
    const response = await AuthAPI.getCaptcha()
    
    if (response.success) {
      console.log('验证码获取成功:', response.data)
      return response.data
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
    throw error
  }
}

/**
 * 用户注册示例
 */
export async function registerExample() {
  try {
    const registerData = {
      username: 'newuser',
      email: 'newuser@example.com',
      password: 'password123',
      confirmPassword: 'password123',
      captcha: '1234'
    }

    const response = await AuthAPI.register(registerData)
    
    if (response.success && response.data.success) {
      console.log('注册成功:', response.data.user)
      return response.data
    } else {
      console.error('注册失败:', response.data.message)
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('注册异常:', error)
    throw error
  }
}

// ==================== 知识库API使用示例 ====================

/**
 * 获取知识分类示例
 */
export async function getKnowledgeCategoriesExample() {
  try {
    const response = await KnowledgeAPI.getCategories()
    
    if (response.success) {
      console.log('知识分类:', response.data)
      return response.data
    }
  } catch (error) {
    console.error('获取知识分类失败:', error)
    throw error
  }
}

/**
 * 搜索知识条目示例
 */
export async function searchKnowledgeExample() {
  try {
    const response = await KnowledgeAPI.searchItems({
      keyword: '图像识别',
      page: 1,
      size: 10
    })
    
    if (response.success) {
      console.log('搜索结果:', response.data)
      return response.data
    }
  } catch (error) {
    console.error('搜索知识失败:', error)
    throw error
  }
}

/**
 * 获取知识详情示例
 */
export async function getKnowledgeDetailExample(itemId: string) {
  try {
    const response = await KnowledgeAPI.getItemDetail(itemId)
    
    if (response.success) {
      console.log('知识详情:', response.data)
      return response.data
    }
  } catch (error) {
    console.error('获取知识详情失败:', error)
    throw error
  }
}

/**
 * 点赞知识条目示例
 */
export async function likeKnowledgeExample(itemId: number) {
  try {
    const response = await KnowledgeAPI.likeItem(itemId)
    
    if (response.success && response.data.success) {
      console.log('点赞成功:', response.data.message)
      return true
    } else {
      console.error('点赞失败:', response.data.message)
      return false
    }
  } catch (error) {
    console.error('点赞异常:', error)
    throw error
  }
}

// ==================== 社区API使用示例 ====================

/**
 * 获取帖子列表示例
 */
export async function getPostsExample() {
  try {
    const response = await CommunityAPI.getPosts({
      page: 1,
      size: 10,
      category: 'tech',
      sort: 'latest'
    })
    
    if (response.success) {
      console.log('帖子列表:', response.data)
      return response.data
    }
  } catch (error) {
    console.error('获取帖子列表失败:', error)
    throw error
  }
}

/**
 * 创建帖子示例
 */
export async function createPostExample() {
  try {
    const postData: CreatePostRequest = {
      title: '如何提高图像识别准确率',
      content: '这里是帖子的详细内容...',
      category: 'tech',
      tags: ['图像识别', '深度学习', '技术分享'],
      images: []
    }

    const response = await CommunityAPI.createPost(postData)
    
    if (response.success && response.data.success) {
      console.log('帖子创建成功:', response.data.post)
      return response.data
    } else {
      console.error('创建帖子失败:', response.data.message)
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('创建帖子异常:', error)
    throw error
  }
}

/**
 * 获取帖子详情示例
 */
export async function getPostDetailExample(postId: number) {
  try {
    const response = await CommunityAPI.getPostDetail(postId)
    
    if (response.success) {
      console.log('帖子详情:', response.data)
      return response.data
    }
  } catch (error) {
    console.error('获取帖子详情失败:', error)
    throw error
  }
}

/**
 * 添加评论示例
 */
export async function addCommentExample(postId: number) {
  try {
    const response = await CommunityAPI.addComment(postId, {
      content: '这是一个很有用的分享，谢谢！',
      parentId: undefined // 如果是回复评论，则传入父评论ID
    })
    
    if (response.success && response.data.success) {
      console.log('评论添加成功:', response.data.comment)
      return response.data
    } else {
      console.error('添加评论失败:', response.data.message)
      throw new Error(response.data.message)
    }
  } catch (error) {
    console.error('添加评论异常:', error)
    throw error
  }
}

/**
 * 点赞帖子示例
 */
export async function likePostExample(postId: number) {
  try {
    const response = await CommunityAPI.likePost(postId)
    
    if (response.success && response.data.success) {
      console.log('点赞成功:', response.data.message)
      return true
    } else {
      console.error('点赞失败:', response.data.message)
      return false
    }
  } catch (error) {
    console.error('点赞异常:', error)
    throw error
  }
}

// ==================== 综合使用示例 ====================

/**
 * 用户完整登录流程示例
 */
export async function completeLoginFlowExample() {
  try {
    // 1. 获取验证码
    const captcha = await getCaptchaExample()
    
    // 2. 用户输入验证码后登录
    const loginResult = await loginExample()
    
    // 3. 验证token
    const tokenValidation = await AuthAPI.validateToken()
    
    if (tokenValidation.success && tokenValidation.data.valid) {
      console.log('登录流程完成，用户信息:', tokenValidation.data.userInfo)
      return tokenValidation.data.userInfo
    }
  } catch (error) {
    console.error('登录流程异常:', error)
    throw error
  }
}

/**
 * 社区互动流程示例
 */
export async function communityInteractionExample() {
  try {
    // 1. 获取帖子列表
    const posts = await getPostsExample()
    
    if (posts && posts.posts.length > 0) {
      const firstPost = posts.posts[0]
      
      // 2. 查看帖子详情
      const postDetail = await getPostDetailExample(firstPost.id)
      
      // 3. 点赞帖子
      await likePostExample(firstPost.id)
      
      // 4. 添加评论
      await addCommentExample(firstPost.id)
      
      console.log('社区互动完成')
    }
  } catch (error) {
    console.error('社区互动异常:', error)
    throw error
  }
}

/**
 * 知识学习流程示例
 */
export async function knowledgeLearningExample() {
  try {
    // 1. 获取知识分类
    const categories = await getKnowledgeCategoriesExample()
    
    // 2. 搜索感兴趣的知识
    const searchResults = await searchKnowledgeExample()
    
    if (searchResults && searchResults.items.length > 0) {
      const firstItem = searchResults.items[0]
      
      // 3. 查看知识详情
      const itemDetail = await getKnowledgeDetailExample(firstItem.id)
      
      // 4. 点赞知识条目
      await likeKnowledgeExample(parseInt(firstItem.id))
      
      // 5. 标记为已学习
      await KnowledgeAPI.markAsLearned(parseInt(firstItem.id))
      
      console.log('知识学习完成')
    }
  } catch (error) {
    console.error('知识学习异常:', error)
    throw error
  }
}

// 导出所有示例函数
export default {
  // 认证相关
  loginExample,
  getCaptchaExample,
  registerExample,
  
  // 知识库相关
  getKnowledgeCategoriesExample,
  searchKnowledgeExample,
  getKnowledgeDetailExample,
  likeKnowledgeExample,
  
  // 社区相关
  getPostsExample,
  createPostExample,
  getPostDetailExample,
  addCommentExample,
  likePostExample,
  
  // 综合流程
  completeLoginFlowExample,
  communityInteractionExample,
  knowledgeLearningExample
}
