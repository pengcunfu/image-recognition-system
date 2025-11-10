<template>
  <div class="contact-page">
    <!-- 顶部横幅 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1>联系我们</h1>
        <p>我们随时为您提供帮助和支持</p>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <a-row :gutter="[24, 24]">
        <!-- 联系表单 -->
        <a-col :xs="24" :lg="14">
          <a-card title="发送消息" class="contact-card">
            <a-form
              ref="contactForm"
              :model="formData"
              :rules="formRules"
              layout="vertical"
              @finish="submitForm"
            >
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="姓名" name="name">
                    <a-input v-model:value="formData.name" placeholder="请输入您的姓名" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="邮箱" name="email">
                    <a-input v-model:value="formData.email" placeholder="请输入您的邮箱" />
                  </a-form-item>
                </a-col>
              </a-row>
              
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="电话" name="phone">
                    <a-input v-model:value="formData.phone" placeholder="请输入您的电话" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="主题" name="subject">
                    <a-select v-model:value="formData.subject" placeholder="请选择咨询主题">
                      <a-select-option value="general">一般咨询</a-select-option>
                      <a-select-option value="technical">技术支持</a-select-option>
                      <a-select-option value="business">商务合作</a-select-option>
                      <a-select-option value="feedback">意见反馈</a-select-option>
                      <a-select-option value="bug">问题报告</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
              </a-row>

              <a-form-item label="详细信息" name="message">
                <a-textarea 
                  v-model:value="formData.message" 
                  placeholder="请详细描述您的问题或需求"
                  :rows="6"
                  show-count
                  :maxlength="500"
                />
              </a-form-item>

              <a-form-item>
                <a-space>
                  <a-button type="primary" html-type="submit" size="large" :loading="submitting">
                    <i class="fas fa-paper-plane"></i>
                    发送消息
                  </a-button>
                  <a-button @click="resetForm" size="large">
                    <i class="fas fa-redo"></i>
                    重置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-form>
          </a-card>
        </a-col>

        <!-- 联系信息 -->
        <a-col :xs="24" :lg="10">
          <a-card title="联系方式" class="info-card">
            <div class="contact-methods">
              <div class="contact-method">
                <div class="method-icon">
                  <i class="fas fa-envelope"></i>
                </div>
                <div class="method-content">
                  <h4>邮箱咨询</h4>
                  <p>contact@image-ai.com</p>
                  <p class="desc">我们会在24小时内回复您的邮件</p>
                </div>
              </div>

              <div class="contact-method">
                <div class="method-icon">
                  <i class="fas fa-phone"></i>
                </div>
                <div class="method-content">
                  <h4>电话咨询</h4>
                  <p>400-123-4567</p>
                  <p class="desc">工作日 9:00-18:00 提供电话支持</p>
                </div>
              </div>

              <div class="contact-method">
                <div class="method-icon">
                  <i class="fas fa-map-marker-alt"></i>
                </div>
                <div class="method-content">
                  <h4>公司地址</h4>
                  <p>北京市朝阳区科技园区</p>
                  <p class="desc">欢迎预约参观我们的办公室</p>
                </div>
              </div>

              <div class="contact-method">
                <div class="method-icon">
                  <i class="fas fa-comments"></i>
                </div>
                <div class="method-content">
                  <h4>在线客服</h4>
                  <p>智能客服机器人</p>
                  <p class="desc">7x24小时在线为您服务</p>
                  <a-button type="primary" size="small" style="margin-top: 8px;">
                    <i class="fas fa-comment-dots"></i>
                    启动客服
                  </a-button>
                </div>
              </div>
            </div>
          </a-card>

          <!-- 常见问题 -->
          <a-card title="常见问题" class="faq-card" style="margin-top: 24px;">
            <a-collapse>
              <a-collapse-panel key="1" header="如何提高图像识别准确率？">
                <p>您可以通过以下方式提高识别准确率：</p>
                <ul>
                  <li>确保图片清晰度足够高</li>
                  <li>避免图片过暗或过亮</li>
                  <li>选择合适的拍摄角度</li>
                  <li>使用我们的VIP高级识别服务</li>
                </ul>
              </a-collapse-panel>
              
              <a-collapse-panel key="2" header="支持哪些图片格式？">
                <p>我们支持以下常见图片格式：</p>
                <ul>
                  <li>JPEG (.jpg, .jpeg)</li>
                  <li>PNG (.png)</li>
                  <li>GIF (.gif)</li>
                  <li>WebP (.webp)</li>
                  <li>BMP (.bmp)</li>
                </ul>
              </a-collapse-panel>
              
              <a-collapse-panel key="3" header="如何升级到VIP服务？">
                <p>您可以在个人中心的VIP服务页面选择适合的套餐进行升级。VIP用户享有：</p>
                <ul>
                  <li>更高的识别准确率</li>
                  <li>批量识别功能</li>
                  <li>API访问权限</li>
                  <li>优先客服支持</li>
                </ul>
              </a-collapse-panel>
              
              <a-collapse-panel key="4" header="数据安全如何保障？">
                <p>我们非常重视用户数据安全：</p>
                <ul>
                  <li>所有数据传输采用HTTPS加密</li>
                  <li>图片处理后会自动删除</li>
                  <li>不会存储用户的个人图片</li>
                  <li>符合相关数据保护法规</li>
                </ul>
              </a-collapse-panel>
            </a-collapse>
          </a-card>
        </a-col>
      </a-row>

      <!-- 地图区域 -->
      <a-row style="margin-top: 40px;">
        <a-col :span="24">
          <a-card title="找到我们" class="map-card">
            <div class="map-container">
              <div class="map-placeholder">
                <i class="fas fa-map-marked-alt"></i>
                <h3>北京市朝阳区科技园区</h3>
                <p>地图功能开发中，敬请期待</p>
                <a-button type="primary">
                  <i class="fas fa-directions"></i>
                  获取路线
                </a-button>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

const submitting = ref(false)

// 表单数据
const formData = reactive({
  name: '',
  email: '',
  phone: '',
  subject: '',
  message: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  subject: [
    { required: true, message: '请选择咨询主题', trigger: 'change' }
  ],
  message: [
    { required: true, message: '请输入详细信息', trigger: 'blur' },
    { min: 10, message: '详细信息至少10个字符', trigger: 'blur' }
  ]
}

// 提交表单
async function submitForm() {
  submitting.value = true
  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 2000))
    message.success('消息发送成功！我们会尽快回复您。')
    resetForm()
  } catch (error) {
    message.error('发送失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 重置表单
function resetForm() {
  Object.assign(formData, {
    name: '',
    email: '',
    phone: '',
    subject: '',
    message: ''
  })
}
</script>

<style scoped>
.contact-page {
  min-height: 100vh;
  background: #f0f2f5;
}

/* 顶部横幅 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 0;
  text-align: center;
  margin-bottom: 40px;
}

.hero-content h1 {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  color: white;
}

.hero-content p {
  font-size: 20px;
  opacity: 0.9;
  max-width: 600px;
  margin: 0 auto;
}

/* 主要内容 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px 40px;
}

.contact-card,
.info-card,
.faq-card,
.map-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  height: 100%;
}

/* 联系方式 */
.contact-methods {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.contact-method {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.3s;
}

.contact-method:hover {
  background: #f0f2ff;
  transform: translateY(-2px);
}

.method-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.method-content h4 {
  color: #262626;
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 600;
}

.method-content p {
  color: #666;
  margin-bottom: 4px;
  font-size: 14px;
}

.method-content .desc {
  color: #999;
  font-size: 12px;
  line-height: 1.4;
}

/* 地图容器 */
.map-container {
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
}

.map-placeholder {
  height: 100%;
  background: linear-gradient(45deg, #f0f2f5 0%, #e6f7ff 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
  gap: 16px;
}

.map-placeholder i {
  font-size: 48px;
  color: #1890ff;
}

.map-placeholder h3 {
  color: #262626;
  margin: 0;
}

.map-placeholder p {
  margin: 0;
  color: #999;
}

/* 表单样式优化 */
:deep(.ant-form-item-label > label) {
  font-weight: 500;
  color: #262626;
}

:deep(.ant-input),
:deep(.ant-select),
:deep(.ant-input-number) {
  border-radius: 8px;
}

:deep(.ant-btn) {
  border-radius: 8px;
  font-weight: 500;
}

:deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

:deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
}

/* 折叠面板样式 */
:deep(.ant-collapse) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.ant-collapse-item) {
  border-radius: 0;
}

:deep(.ant-collapse-header) {
  font-weight: 500;
  color: #262626;
}

:deep(.ant-collapse-content-box) {
  color: #666;
  line-height: 1.6;
}

:deep(.ant-collapse-content-box ul) {
  margin: 12px 0;
  padding-left: 20px;
}

:deep(.ant-collapse-content-box li) {
  margin-bottom: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-content h1 {
    font-size: 32px;
  }
  
  .hero-content p {
    font-size: 16px;
  }
  
  .hero-section {
    padding: 60px 0;
  }
  
  .contact-method {
    flex-direction: column;
    text-align: center;
    align-items: center;
  }
}

@media (max-width: 576px) {
  .main-content {
    padding: 0 16px 40px;
  }
  
  .hero-section {
    padding: 40px 0;
  }
  
  .method-icon {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
  
  .map-placeholder i {
    font-size: 32px;
  }
}
</style>
