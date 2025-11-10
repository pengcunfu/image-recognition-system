import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'
import { UserRole } from '@/constants/role'

/**
 * 权限指令
 * 用法:
 * v-permission="'admin'" - 仅管理员可见
 * v-permission="'vip'" - 仅VIP及以上可见
 * v-permission="['admin', 'vip']" - 管理员或VIP可见
 * v-permission:hide="'admin'" - 不满足条件时隐藏(display:none)而不是移除DOM
 */

type PermissionRole = 'admin' | 'vip' | 'user'

// 角色值映射（字符串到数字）
const ROLE_VALUE: Record<PermissionRole, number> = {
  user: UserRole.USER,    // 0
  vip: UserRole.VIP,      // 1
  admin: UserRole.ADMIN   // 2
}

/**
 * 检查用户是否有指定权限
 */
function hasPermission(requiredRoles: PermissionRole | PermissionRole[]): boolean {
  const userStore = useUserStore()
  const userRoleValue = userStore.userInfo?.role ?? UserRole.USER

  // 如果是数组，只要满足其中一个角色即可
  if (Array.isArray(requiredRoles)) {
    return requiredRoles.some(role => {
      const required = role.toLowerCase() as PermissionRole
      return userRoleValue >= ROLE_VALUE[required]
    })
  }

  // 单个角色判断
  const required = requiredRoles.toLowerCase() as PermissionRole
  return userRoleValue >= ROLE_VALUE[required]
}

export const vPermission: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const { value, arg } = binding
    
    if (!value) {
      console.warn('[v-permission] 缺少权限值')
      return
    }

    const permitted = hasPermission(value)

    if (!permitted) {
      if (arg === 'hide') {
        // 使用 display:none 隐藏，保留DOM结构
        el.style.display = 'none'
      } else {
        // 默认移除DOM元素
        el.parentNode?.removeChild(el)
      }
    }
  },

  updated(el: HTMLElement, binding: DirectiveBinding) {
    const { value, arg } = binding
    
    if (!value) return

    const permitted = hasPermission(value)

    if (arg === 'hide') {
      // 动态切换显示/隐藏
      el.style.display = permitted ? '' : 'none'
    }
  }
}

/**
 * 检查权限的辅助函数，可在组件中使用
 */
export function checkPermission(requiredRoles: PermissionRole | PermissionRole[]): boolean {
  return hasPermission(requiredRoles)
}

/**
 * 获取当前用户角色
 */
export function getCurrentRole(): PermissionRole {
  const userStore = useUserStore()
  const roleValue = userStore.userInfo?.role ?? UserRole.USER
  
  // 将数字角色值转换为字符串
  if (roleValue === UserRole.ADMIN) return 'admin'
  if (roleValue === UserRole.VIP) return 'vip'
  return 'user'
}

export default vPermission

