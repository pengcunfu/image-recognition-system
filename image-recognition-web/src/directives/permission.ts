import type { Directive, DirectiveBinding } from 'vue'
import { AuthUtils } from '@/utils/auth'

/**
 * 权限指令
 * 用法:
 * v-permission="'admin'" - 仅管理员可见
 * v-permission="'vip'" - 仅VIP及以上可见
 * v-permission="['admin', 'vip']" - 管理员或VIP可见
 * v-permission:hide="'admin'" - 不满足条件时隐藏(display:none)而不是移除DOM
 */

type PermissionRole = 'admin' | 'vip' | 'user'

// 角色权重，用于判断权限等级
const ROLE_WEIGHT: Record<PermissionRole, number> = {
  user: 1,
  vip: 2,
  admin: 3
}

/**
 * 检查用户是否有指定权限
 */
function hasPermission(requiredRoles: PermissionRole | PermissionRole[]): boolean {
  const userInfo = AuthUtils.getUserInfo()
  const userRole = (userInfo?.role || 'user').toLowerCase() as PermissionRole

  // 如果是数组，只要满足其中一个角色即可
  if (Array.isArray(requiredRoles)) {
    return requiredRoles.some(role => {
      const required = role.toLowerCase() as PermissionRole
      return ROLE_WEIGHT[userRole] >= ROLE_WEIGHT[required]
    })
  }

  // 单个角色判断
  const required = requiredRoles.toLowerCase() as PermissionRole
  return ROLE_WEIGHT[userRole] >= ROLE_WEIGHT[required]
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
  const userInfo = AuthUtils.getUserInfo()
  return (userInfo?.role || 'user').toLowerCase() as PermissionRole
}

export default vPermission

