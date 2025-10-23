/**
 * 用户角色常量
 * 与后端 UserRole 枚举保持一致
 */
export enum UserRole {
  /** 普通用户 */
  USER = 0,
  /** VIP会员 */
  VIP = 1,
  /** 管理员 */
  ADMIN = 2
}

/**
 * 角色名称映射
 */
export const RoleNames: Record<UserRole, string> = {
  [UserRole.USER]: '普通用户',
  [UserRole.VIP]: 'VIP会员',
  [UserRole.ADMIN]: '管理员'
}

/**
 * 角色颜色映射（用于标签显示）
 */
export const RoleColors: Record<UserRole, string> = {
  [UserRole.USER]: 'default',
  [UserRole.VIP]: 'gold',
  [UserRole.ADMIN]: 'red'
}

/**
 * 判断是否为管理员
 */
export function isAdmin(role?: number): boolean {
  return role === UserRole.ADMIN
}

/**
 * 判断是否为VIP（包括管理员）
 */
export function isVip(role?: number): boolean {
  return role === UserRole.VIP || role === UserRole.ADMIN
}

/**
 * 判断是否为普通用户
 */
export function isNormalUser(role?: number): boolean {
  return role === UserRole.USER
}

