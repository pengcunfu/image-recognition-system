import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

import App from './App.vue'
import router from './router'
import { registerDirectives } from './directives'
import { AuthUtils } from './utils/auth'

// 设置 dayjs 为中文
dayjs.locale('zh-cn')

// 初始化认证工具的路由实例
AuthUtils.setRouter(router)

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)

// 注册全局指令
registerDirectives(app)

app.mount('#app')
