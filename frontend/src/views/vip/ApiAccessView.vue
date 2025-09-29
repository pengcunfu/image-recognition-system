<template>
  <div class="api-access-page">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>
            <i class="fas fa-key vip-icon"></i>
            API访问管理
          </h1>
          <p>VIP专享的API接口服务和密钥管理</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-label">本月调用</span>
            <span class="stat-value">{{ apiStats.monthlyUsage }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">剩余额度</span>
            <span class="stat-value">{{ apiStats.remainingQuota }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- API密钥管理 -->
    <a-card title="API密钥管理" class="api-keys-card">
      <template #extra>
        <a-button type="primary" @click="createApiKey">
          <i class="fas fa-plus"></i>
          创建新密钥
        </a-button>
      </template>

      <div class="api-keys-list">
        <div 
          v-for="apiKey in apiKeys" 
          :key="apiKey.id"
          class="api-key-item"
        >
          <div class="key-info">
            <div class="key-header">
              <h4>{{ apiKey.name }}</h4>
              <a-tag :color="getKeyStatusColor(apiKey.status)">
                {{ apiKey.status }}
              </a-tag>
            </div>
            <div class="key-details">
              <div class="key-field">
                <span class="field-label">密钥ID:</span>
                <span class="field-value">{{ apiKey.keyId }}</span>
                <a-button type="text" size="small" @click="copyToClipboard(apiKey.keyId)">
                  <i class="fas fa-copy"></i>
                </a-button>
              </div>
              <div class="key-field">
                <span class="field-label">密钥:</span>
                <span class="field-value">
                  {{ showFullKey[apiKey.id] ? apiKey.secretKey : maskKey(apiKey.secretKey) }}
                </span>
                <a-button type="text" size="small" @click="toggleKeyVisibility(apiKey.id)">
                  <i :class="showFullKey[apiKey.id] ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </a-button>
                <a-button type="text" size="small" @click="copyToClipboard(apiKey.secretKey)">
                  <i class="fas fa-copy"></i>
                </a-button>
              </div>
              <div class="key-meta">
                <span>创建时间: {{ apiKey.createTime }}</span>
                <span>最后使用: {{ apiKey.lastUsed }}</span>
                <span>调用次数: {{ apiKey.callCount }}</span>
              </div>
            </div>
          </div>
          <div class="key-actions">
            <a-dropdown>
              <a-button>
                操作
                <i class="fas fa-chevron-down"></i>
              </a-button>
              <template #overlay>
                <a-menu @click="({ key }) => handleKeyAction(key, apiKey)">
                  <a-menu-item key="regenerate">
                    <i class="fas fa-sync"></i>
                    重新生成
                  </a-menu-item>
                  <a-menu-item key="disable" v-if="apiKey.status === '活跃'">
                    <i class="fas fa-pause"></i>
                    禁用
                  </a-menu-item>
                  <a-menu-item key="enable" v-if="apiKey.status === '禁用'">
                    <i class="fas fa-play"></i>
                    启用
                  </a-menu-item>
                  <a-menu-item key="delete" danger>
                    <i class="fas fa-trash"></i>
                    删除
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </div>
      </div>
    </a-card>

    <!-- API文档 -->
    <a-card title="API文档" class="api-docs-card">
      <a-tabs>
        <a-tab-pane key="endpoints" tab="接口列表">
          <div class="endpoints-list">
            <div 
              v-for="endpoint in apiEndpoints" 
              :key="endpoint.path"
              class="endpoint-item"
            >
              <div class="endpoint-header">
                <a-tag :color="getMethodColor(endpoint.method)" class="method-tag">
                  {{ endpoint.method }}
                </a-tag>
                <span class="endpoint-path">{{ endpoint.path }}</span>
                <a-tag v-if="endpoint.vipOnly" color="gold" class="vip-only-tag">
                  <i class="fas fa-crown"></i>
                  VIP专享
                </a-tag>
              </div>
              <div class="endpoint-description">
                {{ endpoint.description }}
              </div>
              <div class="endpoint-details">
                <div class="detail-section">
                  <h5>请求参数</h5>
                  <div class="params-list">
                    <div 
                      v-for="param in endpoint.parameters" 
                      :key="param.name"
                      class="param-item"
                    >
                      <span class="param-name">{{ param.name }}</span>
                      <span class="param-type">{{ param.type }}</span>
                      <span class="param-required" v-if="param.required">必需</span>
                      <span class="param-description">{{ param.description }}</span>
                    </div>
                  </div>
                </div>
                <div class="detail-section">
                  <h5>响应示例</h5>
                  <pre class="response-example">{{ JSON.stringify(endpoint.responseExample, null, 2) }}</pre>
                </div>
              </div>
              <div class="endpoint-actions">
                <a-button @click="testEndpoint(endpoint)">
                  <i class="fas fa-play"></i>
                  测试接口
                </a-button>
                <a-button @click="generateCode(endpoint)">
                  <i class="fas fa-code"></i>
                  生成代码
                </a-button>
              </div>
            </div>
          </div>
        </a-tab-pane>
        <a-tab-pane key="quickstart" tab="快速开始">
          <div class="quickstart-content">
            <h3>1. 获取API密钥</h3>
            <p>首先在上方"API密钥管理"中创建一个新的API密钥。</p>
            
            <h3>2. 认证方式</h3>
            <p>所有API请求都需要在请求头中包含认证信息：</p>
            <pre class="code-block">Authorization: Bearer YOUR_API_KEY</pre>
            
            <h3>3. 基础请求示例</h3>
            <a-tabs type="card" size="small">
              <a-tab-pane key="curl" tab="cURL">
                <pre class="code-block">{{ curlExample }}</pre>
              </a-tab-pane>
              <a-tab-pane key="javascript" tab="JavaScript">
                <pre class="code-block">{{ jsExample }}</pre>
              </a-tab-pane>
              <a-tab-pane key="python" tab="Python">
                <pre class="code-block">{{ pythonExample }}</pre>
              </a-tab-pane>
            </a-tabs>
            
            <h3>4. 错误处理</h3>
            <p>API使用标准HTTP状态码，错误响应格式如下：</p>
            <pre class="code-block">{{ errorExample }}</pre>
          </div>
        </a-tab-pane>
        <a-tab-pane key="pricing" tab="计费说明">
          <div class="pricing-content">
            <h3>VIP API计费规则</h3>
            <div class="pricing-table">
              <div class="pricing-item">
                <div class="pricing-header">
                  <h4>基础识别</h4>
                  <div class="price">¥0.05/次</div>
                </div>
                <ul class="pricing-features">
                  <li>标准图像识别</li>
                  <li>95%以上准确率</li>
                  <li>平均响应时间 < 2s</li>
                </ul>
              </div>
              <div class="pricing-item featured">
                <div class="pricing-header">
                  <h4>高级识别</h4>
                  <div class="price">¥0.15/次</div>
                </div>
                <ul class="pricing-features">
                  <li>超精度识别算法</li>
                  <li>98%以上准确率</li>
                  <li>多目标检测</li>
                  <li>场景理解</li>
                </ul>
              </div>
              <div class="pricing-item">
                <div class="pricing-header">
                  <h4>批量处理</h4>
                  <div class="price">¥0.03/次</div>
                </div>
                <ul class="pricing-features">
                  <li>批量折扣优惠</li>
                  <li>异步处理</li>
                  <li>结果推送</li>
                </ul>
              </div>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 使用统计 -->
    <a-card title="使用统计" class="usage-stats-card">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :lg="16">
          <div class="usage-chart">
            <div class="chart-placeholder">
              <i class="fas fa-chart-bar"></i>
              <span>API调用量趋势图</span>
              <div class="chart-description">
                显示过去30天的API调用统计
              </div>
            </div>
          </div>
        </a-col>
        <a-col :xs="24" :lg="8">
          <div class="usage-breakdown">
            <h4>调用分布</h4>
            <div class="breakdown-list">
              <div class="breakdown-item">
                <span class="breakdown-label">基础识别</span>
                <div class="breakdown-bar">
                  <div class="breakdown-fill" style="width: 60%; background: #1890ff;"></div>
                </div>
                <span class="breakdown-value">60%</span>
              </div>
              <div class="breakdown-item">
                <span class="breakdown-label">高级识别</span>
                <div class="breakdown-bar">
                  <div class="breakdown-fill" style="width: 30%; background: #52c41a;"></div>
                </div>
                <span class="breakdown-value">30%</span>
              </div>
              <div class="breakdown-item">
                <span class="breakdown-label">批量处理</span>
                <div class="breakdown-bar">
                  <div class="breakdown-fill" style="width: 10%; background: #faad14;"></div>
                </div>
                <span class="breakdown-value">10%</span>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 创建API密钥模态框 -->
    <a-modal
      v-model:open="createKeyModalVisible"
      title="创建API密钥"
      @ok="handleCreateApiKey"
      width="500px"
    >
      <a-form layout="vertical">
        <a-form-item label="密钥名称" required>
          <a-input v-model:value="newApiKey.name" placeholder="输入密钥名称" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="newApiKey.description" :rows="3" placeholder="描述此密钥的用途" />
        </a-form-item>
        <a-form-item label="权限范围">
          <a-checkbox-group v-model:value="newApiKey.permissions">
            <a-checkbox value="basic_recognition">基础识别</a-checkbox>
            <a-checkbox value="advanced_recognition">高级识别</a-checkbox>
            <a-checkbox value="batch_processing">批量处理</a-checkbox>
            <a-checkbox value="model_training">模型训练</a-checkbox>
          </a-checkbox-group>
        </a-form-item>
        <a-form-item label="调用限制">
          <a-input-number 
            v-model:value="newApiKey.rateLimit" 
            :min="100" 
            :max="10000"
            placeholder="每小时最大调用次数"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

const createKeyModalVisible = ref(false)
const showFullKey = ref<Record<number, boolean>>({})

// API统计
const apiStats = reactive({
  monthlyUsage: '15,234',
  remainingQuota: '84,766'
})

// 新API密钥
const newApiKey = reactive({
  name: '',
  description: '',
  permissions: ['basic_recognition'],
  rateLimit: 1000
})

// API密钥列表
const apiKeys = ref([
  {
    id: 1,
    name: '生产环境密钥',
    keyId: 'sk-prod-abc123def456',
    secretKey: 'sk-prod-abc123def456ghi789jkl012mno345pqr678stu901vwx234yz567',
    status: '活跃',
    createTime: '2024-03-01',
    lastUsed: '2024-03-20 14:30',
    callCount: 8542
  },
  {
    id: 2,
    name: '测试环境密钥',
    keyId: 'sk-test-xyz789uvw456',
    secretKey: 'sk-test-xyz789uvw456rst123opq456lmn789hij012efg345bcd678abc901',
    status: '活跃',
    createTime: '2024-02-15',
    lastUsed: '2024-03-19 16:45',
    callCount: 2156
  },
  {
    id: 3,
    name: '开发环境密钥',
    keyId: 'sk-dev-def456ghi789',
    secretKey: 'sk-dev-def456ghi789jkl012mno345pqr678stu901vwx234yz567abc123',
    status: '禁用',
    createTime: '2024-01-20',
    lastUsed: '2024-03-10 09:15',
    callCount: 456
  }
])

// API接口列表
const apiEndpoints = ref([
  {
    method: 'POST',
    path: '/api/v1/recognition/basic',
    description: '基础图像识别接口',
    vipOnly: false,
    parameters: [
      { name: 'image', type: 'file', required: true, description: '要识别的图片文件' },
      { name: 'format', type: 'string', required: false, description: '返回格式 (json/xml)' }
    ],
    responseExample: {
      success: true,
      data: {
        result: '金毛犬',
        confidence: 0.95,
        category: '动物',
        processTime: 1247
      }
    }
  },
  {
    method: 'POST',
    path: '/api/v1/recognition/advanced',
    description: '高级图像识别接口，支持多目标检测',
    vipOnly: true,
    parameters: [
      { name: 'image', type: 'file', required: true, description: '要识别的图片文件' },
      { name: 'mode', type: 'string', required: false, description: '识别模式 (precision/multi/scene)' },
      { name: 'threshold', type: 'float', required: false, description: '检测阈值 (0.1-1.0)' }
    ],
    responseExample: {
      success: true,
      data: {
        objects: [
          { name: '金毛犬', confidence: 0.97, bbox: { x: 100, y: 50, width: 200, height: 180 } },
          { name: '草地', confidence: 0.89, bbox: { x: 0, y: 200, width: 400, height: 100 } }
        ],
        scene: '户外公园',
        processTime: 3247
      }
    }
  }
])

// 代码示例
const curlExample = `curl -X POST "https://api.example.com/v1/recognition/basic" \\
  -H "Authorization: Bearer YOUR_API_KEY" \\
  -H "Content-Type: multipart/form-data" \\
  -F "image=@/path/to/your/image.jpg"`

const jsExample = `const formData = new FormData();
formData.append('image', imageFile);

fetch('https://api.example.com/v1/recognition/basic', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer YOUR_API_KEY'
  },
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));`

const pythonExample = `import requests

url = 'https://api.example.com/v1/recognition/basic'
headers = {'Authorization': 'Bearer YOUR_API_KEY'}
files = {'image': open('image.jpg', 'rb')}

response = requests.post(url, headers=headers, files=files)
print(response.json())`

const errorExample = `{
  "success": false,
  "error": {
    "code": "INVALID_API_KEY",
    "message": "The provided API key is invalid or expired",
    "details": "Please check your API key and try again"
  }
}`

// 方法
function createApiKey() {
  createKeyModalVisible.value = true
}

function getKeyStatusColor(status: string) {
  return status === '活跃' ? 'success' : 'default'
}

function maskKey(key: string) {
  return key.substring(0, 12) + '...' + key.substring(key.length - 8)
}

function toggleKeyVisibility(keyId: number) {
  showFullKey.value[keyId] = !showFullKey.value[keyId]
}

function copyToClipboard(text: string) {
  navigator.clipboard.writeText(text).then(() => {
    message.success('已复制到剪贴板')
  }).catch(() => {
    message.error('复制失败')
  })
}

function handleKeyAction(action: string, apiKey: any) {
  switch (action) {
    case 'regenerate':
      message.info(`重新生成密钥：${apiKey.name}`)
      break
    case 'disable':
      apiKey.status = '禁用'
      message.success('密钥已禁用')
      break
    case 'enable':
      apiKey.status = '活跃'
      message.success('密钥已启用')
      break
    case 'delete':
      message.info(`删除密钥：${apiKey.name}`)
      break
  }
}

function getMethodColor(method: string) {
  const colors: Record<string, string> = {
    'GET': 'blue',
    'POST': 'green',
    'PUT': 'orange',
    'DELETE': 'red'
  }
  return colors[method] || 'default'
}

function testEndpoint(endpoint: any) {
  message.info(`测试接口：${endpoint.path}`)
}

function generateCode(endpoint: any) {
  message.info(`生成代码示例：${endpoint.path}`)
}

function handleCreateApiKey() {
  if (!newApiKey.name) {
    message.warning('请输入密钥名称')
    return
  }
  
  message.success('API密钥创建成功')
  createKeyModalVisible.value = false
  
  // 重置表单
  newApiKey.name = ''
  newApiKey.description = ''
  newApiKey.permissions = ['basic_recognition']
  newApiKey.rateLimit = 1000
}
</script>

<style scoped>
.api-access-page {
  padding: 0;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #52c41a 0%, #1890ff 100%);
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
  color: white;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section h1 {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.vip-icon {
  color: #ffd700;
  font-size: 32px;
}

.title-section p {
  font-size: 16px;
  opacity: 0.9;
}

.header-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
}

/* API密钥管理 */
.api-keys-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.api-keys-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.api-key-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  background: #fafafa;
}

.key-info {
  flex: 1;
}

.key-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.key-header h4 {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.key-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.key-field {
  display: flex;
  align-items: center;
  gap: 8px;
}

.field-label {
  font-size: 14px;
  color: #666;
  min-width: 60px;
}

.field-value {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #262626;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 4px;
  flex: 1;
  max-width: 400px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.key-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

/* API文档 */
.api-docs-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.endpoints-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.endpoint-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 20px;
}

.endpoint-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.method-tag {
  font-weight: bold;
  min-width: 60px;
  text-align: center;
}

.endpoint-path {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: #262626;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 4px;
}

.vip-only-tag {
  margin-left: auto;
}

.endpoint-description {
  color: #666;
  margin-bottom: 16px;
}

.endpoint-details {
  margin-bottom: 16px;
}

.detail-section {
  margin-bottom: 16px;
}

.detail-section h5 {
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 8px;
}

.params-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.param-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background: #fafafa;
  border-radius: 4px;
}

.param-name {
  font-family: 'Courier New', monospace;
  font-weight: bold;
  color: #1890ff;
  min-width: 80px;
}

.param-type {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}

.param-required {
  background: #ff4d4f;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.param-description {
  color: #666;
  font-size: 14px;
}

.response-example {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 6px;
  font-size: 12px;
  overflow-x: auto;
}

.endpoint-actions {
  display: flex;
  gap: 12px;
}

/* 快速开始 */
.quickstart-content h3 {
  color: #262626;
  margin: 24px 0 12px 0;
}

.quickstart-content h3:first-child {
  margin-top: 0;
}

.quickstart-content p {
  color: #666;
  margin-bottom: 16px;
  line-height: 1.6;
}

.code-block {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 6px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  overflow-x: auto;
  margin: 12px 0;
}

/* 计费说明 */
.pricing-content h3 {
  color: #262626;
  margin-bottom: 20px;
}

.pricing-table {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.pricing-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
}

.pricing-item.featured {
  border-color: #1890ff;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
}

.pricing-header {
  margin-bottom: 20px;
}

.pricing-header h4 {
  font-size: 18px;
  color: #262626;
  margin-bottom: 8px;
}

.price {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
}

.pricing-features {
  list-style: none;
  padding: 0;
  margin: 0;
}

.pricing-features li {
  padding: 8px 0;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

.pricing-features li:last-child {
  border-bottom: none;
}

/* 使用统计 */
.usage-stats-card {
  border-radius: 12px;
}

.usage-chart {
  height: 300px;
}

.chart-placeholder {
  height: 100%;
  background: #f5f5f5;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
  gap: 8px;
}

.chart-placeholder i {
  font-size: 48px;
}

.chart-description {
  font-size: 12px;
  opacity: 0.7;
}

.usage-breakdown h4 {
  margin-bottom: 16px;
  color: #262626;
}

.breakdown-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.breakdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.breakdown-label {
  flex: 0 0 80px;
  font-size: 14px;
  color: #262626;
}

.breakdown-bar {
  flex: 1;
  height: 8px;
  background: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
}

.breakdown-fill {
  height: 100%;
  border-radius: 4px;
}

.breakdown-value {
  flex: 0 0 40px;
  text-align: right;
  font-size: 12px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .header-stats {
    justify-content: center;
  }
  
  .api-key-item {
    flex-direction: column;
    gap: 16px;
  }
  
  .key-field {
    flex-direction: column;
    align-items: stretch;
    gap: 4px;
  }
  
  .field-value {
    max-width: none;
  }
  
  .key-meta {
    flex-direction: column;
    gap: 4px;
  }
  
  .endpoint-header {
    flex-wrap: wrap;
  }
  
  .param-item {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
}

@media (max-width: 576px) {
  .page-header {
    padding: 20px;
  }
  
  .title-section h1 {
    font-size: 24px;
  }
  
  .pricing-table {
    grid-template-columns: 1fr;
  }
}
</style>
