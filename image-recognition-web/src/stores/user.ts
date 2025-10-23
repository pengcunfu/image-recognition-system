import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { UserAPI } from '@/api/user'
import { UserRole } from '@/constants/role'

export interface UserInfo {
  id: number
  username: string
  nickname?: string
  email?: string
  phone?: string
  avatar?: string
  role?: number
  vipLevel?: number
  vipExpireTime?: string
}

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref<UserInfo | null>(null)
  const token = ref<string>('')
  const isLoggedIn = ref(false)

  // 计算属性
  const userId = computed(() => userInfo.value?.id || 0)
  const userName = computed(() => userInfo.value?.username || '')
  const userNickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '用户')
  const userAvatar = computed(() => userInfo.value?.avatar || '')
  const isVip = computed(() => {
    if (!userInfo.value?.vipLevel) return false
    if (!userInfo.value?.vipExpireTime) return false
    return new Date(userInfo.value.vipExpireTime) > new Date()
  })
  const isAdmin = computed(() => userInfo.value?.role === UserRole.ADMIN)

  // 初始化：从localStorage加载token（仅token）
  function initFromStorage() {
    const storedToken = localStorage.getItem('token')
    if (storedToken) {
      token.value = storedToken
      // 有token但没有用户信息时，自动获取用户信息
      if (!userInfo.value) {
        fetchUserProfile().catch(() => {
          // 如果获取失败，清除token
          clearUserInfo()
        })
      }
    }
  }

  // 获取用户详细信息
  async function fetchUserProfile() {
    try {
      const profile = await UserAPI.getProfile()
      userInfo.value = {
        id: profile.id,
        username: profile.username,
        nickname: profile.nickname,
        email: profile.email,
        phone: profile.phone,
        avatar: profile.avatar,
        role: profile.role,
        vipLevel: profile.vipLevel,
        vipExpireTime: profile.vipExpireTime
      }
      isLoggedIn.value = true
      
      return userInfo.value
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 设置登录信息（登录后调用）
  function setLoginInfo(authToken: string, user?: UserInfo) {
    token.value = authToken
    isLoggedIn.value = true
    
    // 只保存token到localStorage
    localStorage.setItem('token', authToken)
    
    if (user) {
      userInfo.value = user
    } else {
      // 如果没有用户信息，则获取
      fetchUserProfile()
    }
  }

  // 设置用户信息
  function setUserInfo(user: UserInfo) {
    userInfo.value = user
    isLoggedIn.value = true
  }

  // 清除用户信息（退出登录）
  function clearUserInfo() {
    userInfo.value = null
    token.value = ''
    isLoggedIn.value = false
    
    // 只清除token
    localStorage.removeItem('token')
  }

  // 更新用户头像
  function updateAvatar(avatar: string) {
    if (userInfo.value) {
      userInfo.value.avatar = avatar
    }
  }

  // 更新用户昵称
  function updateNickname(nickname: string) {
    if (userInfo.value) {
      userInfo.value.nickname = nickname
    }
  }

  // 更新用户信息（部分更新）
  function updateUserInfo(updates: Partial<UserInfo>) {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...updates }
    }
  }

  return {
    // 状态
    userInfo,
    token,
    isLoggedIn,
    
    // 计算属性
    userId,
    userName,
    userNickname,
    userAvatar,
    isVip,
    isAdmin,
    
    // 方法
    initFromStorage,
    fetchUserProfile,
    setLoginInfo,
    setUserInfo,
    clearUserInfo,
    updateAvatar,
    updateNickname,
    updateUserInfo
  }
})

