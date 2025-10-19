import type { App } from 'vue'
import { vLoading } from './loading'

/**
 * 注册全局指令
 */
export function registerDirectives(app: App) {
  app.directive('loading', vLoading)
}

export { vLoading }

