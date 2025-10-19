import { type Directive } from 'vue'

/**
 * 全局加载状态管理
 */
class LoadingManager {
  private loadingCount = 0
  private loadingElement: HTMLElement | null = null

  /**
   * 显示全局加载状态
   */
  show() {
    this.loadingCount++
    if (this.loadingCount === 1) {
      this.createLoadingElement()
      console.log('[Loading] Start')
    }
  }

  /**
   * 隐藏全局加载状态
   */
  hide() {
    this.loadingCount = Math.max(0, this.loadingCount - 1)
    if (this.loadingCount === 0) {
      this.removeLoadingElement()
      console.log('[Loading] End')
    }
  }

  /**
   * 创建加载元素
   */
  private createLoadingElement() {
    if (this.loadingElement) return

    this.loadingElement = document.createElement('div')
    this.loadingElement.className = 'global-loading'
    this.loadingElement.innerHTML = `
      <div class="global-loading-backdrop"></div>
      <div class="global-loading-spinner">
        <div class="spinner"></div>
      </div>
    `

    // 添加样式
    const style = document.createElement('style')
    style.textContent = `
      .global-loading {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 9999;
        pointer-events: none;
      }
      .global-loading-backdrop {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.1);
        pointer-events: auto;
      }
      .global-loading-spinner {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
      }
      .global-loading-spinner .spinner {
        width: 40px;
        height: 40px;
        border: 3px solid rgba(24, 144, 255, 0.2);
        border-top-color: #1890ff;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
      }
      @keyframes spin {
        to { transform: rotate(360deg); }
      }
    `
    document.head.appendChild(style)
    document.body.appendChild(this.loadingElement)
  }

  /**
   * 移除加载元素
   */
  private removeLoadingElement() {
    if (this.loadingElement) {
      document.body.removeChild(this.loadingElement)
      this.loadingElement = null
    }
  }

  /**
   * 强制重置加载状态
   */
  reset() {
    this.loadingCount = 0
    this.removeLoadingElement()
  }
}

// 创建全局加载管理器实例
export const loadingManager = new LoadingManager()

/**
 * v-loading 指令
 * 使用方式：v-loading="isLoading"
 */
export const vLoading: Directive = {
  mounted(el, binding) {
    if (binding.value) {
      el.classList.add('is-loading')
      createElementLoading(el)
    }
  },
  updated(el, binding) {
    if (binding.value !== binding.oldValue) {
      if (binding.value) {
        el.classList.add('is-loading')
        createElementLoading(el)
      } else {
        el.classList.remove('is-loading')
        removeElementLoading(el)
      }
    }
  },
  unmounted(el) {
    el.classList.remove('is-loading')
    removeElementLoading(el)
  }
}

/**
 * 创建元素级别的加载遮罩
 */
function createElementLoading(el: HTMLElement) {
  const existing = el.querySelector('.element-loading')
  if (existing) return

  const loading = document.createElement('div')
  loading.className = 'element-loading'
  loading.innerHTML = `
    <div class="element-loading-spinner">
      <div class="spinner"></div>
    </div>
  `

  // 添加样式
  if (!document.getElementById('element-loading-style')) {
    const style = document.createElement('style')
    style.id = 'element-loading-style'
    style.textContent = `
      .is-loading {
        position: relative;
        pointer-events: none;
      }
      .element-loading {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(255, 255, 255, 0.9);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 999;
      }
      .element-loading-spinner .spinner {
        width: 32px;
        height: 32px;
        border: 3px solid rgba(24, 144, 255, 0.2);
        border-top-color: #1890ff;
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
      }
    `
    document.head.appendChild(style)
  }

  // 确保父元素有 position
  const position = window.getComputedStyle(el).position
  if (position === 'static') {
    el.style.position = 'relative'
  }

  el.appendChild(loading)
}

/**
 * 移除元素级别的加载遮罩
 */
function removeElementLoading(el: HTMLElement) {
  const loading = el.querySelector('.element-loading')
  if (loading) {
    el.removeChild(loading)
  }
}

export default vLoading

