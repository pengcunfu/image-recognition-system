import type { App } from 'vue'
import { vLoading } from './loading'
import { vPermission } from './permission'

/**
 * 注册全局指令
 */
export function registerDirectives(app: App) {
  app.directive('loading', vLoading)
  app.directive('permission', vPermission)
}

export { vLoading, vPermission }
