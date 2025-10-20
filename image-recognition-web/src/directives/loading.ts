import { type Directive, createApp, h } from 'vue'
import { Spin } from 'ant-design-vue'

/**
 * 全局加载状态管理（基于 Ant Design Vue 的 Spin）
 */
class LoadingManager {
  private loadingCount = 0
  private loadingInstance: any = null

  /**
   * 显示全局加载状态
   */
  show() {
    this.loadingCount++
    if (this.loadingCount === 1 && !this.loadingInstance) {
      this.loadingInstance = Spin.setDefaultIndicator({
        indicator: true
      })
      console.log('[Loading] Start')
    }
  }

  /**
   * 隐藏全局加载状态
   */
  hide() {
    this.loadingCount = Math.max(0, this.loadingCount - 1)
    if (this.loadingCount === 0 && this.loadingInstance) {
      this.loadingInstance = null
      console.log('[Loading] End')
    }
  }

  /**
   * 强制重置加载状态
   */
  reset() {
    this.loadingCount = 0
    this.loadingInstance = null
  }
}

// 创建全局加载管理器实例
export const loadingManager = new LoadingManager()

/**
 * v-loading 指令
 * 使用方式：v-loading="isLoading"
 * 基于 Ant Design Vue Spin 组件实现
 */
export const vLoading: Directive = {
  mounted(el, binding) {
    if (binding.value) {
      createElementLoading(el)
    }
  },
  updated(el, binding) {
    if (binding.value !== binding.oldValue) {
      if (binding.value) {
        createElementLoading(el)
      } else {
        removeElementLoading(el)
      }
    }
  },
  unmounted(el) {
    removeElementLoading(el)
  }
}

/**
 * 创建元素级别的加载遮罩（使用 Ant Design Vue Spin）
 */
function createElementLoading(el: HTMLElement) {
  // 检查是否已存在
  if (el.querySelector('.ant-spin-nested-loading')) {
    return
  }

  // 确保父元素有 position
  const position = window.getComputedStyle(el).position
  if (position === 'static') {
    el.style.position = 'relative'
  }

  // 保存原始内容
  const originalContent = el.innerHTML
  el.setAttribute('data-original-content', originalContent)

  // 创建 Spin 包装容器
  const spinWrapper = document.createElement('div')
  spinWrapper.className = 'v-loading-wrapper'
  
  // 使用 Vue 的 h 函数创建 Spin 组件实例
  const spinApp = createApp({
    render() {
      return h(Spin, { spinning: true, tip: '加载中...' }, {
        default: () => h('div', { innerHTML: originalContent })
      })
    }
  })

  spinApp.mount(spinWrapper)
  el.innerHTML = ''
  el.appendChild(spinWrapper)

  // 保存实例以便后续销毁
  ;(el as any).__spinApp__ = spinApp
}

/**
 * 移除元素级别的加载遮罩
 */
function removeElementLoading(el: HTMLElement) {
  const spinApp = (el as any).__spinApp__
  if (spinApp) {
    spinApp.unmount()
    delete (el as any).__spinApp__
  }

  // 恢复原始内容
  const originalContent = el.getAttribute('data-original-content')
  if (originalContent) {
    el.innerHTML = originalContent
    el.removeAttribute('data-original-content')
  }
}

export default vLoading

