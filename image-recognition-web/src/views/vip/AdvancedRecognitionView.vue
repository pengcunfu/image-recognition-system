<template>
  <div class="advanced-recognition-page">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>
            <i class="fas fa-crown vip-icon"></i>
            高级识别
          </h1>
          <p>VIP专享的高精度AI识别服务</p>
        </div>
        <div class="vip-badge">
          <a-tag color="gold" class="vip-tag">
            <i class="fas fa-star"></i>
            VIP专享
          </a-tag>
        </div>
      </div>
    </div>

    <!-- 高级识别选项 -->
    <a-row :gutter="[24, 24]" class="recognition-modes">
      <a-col :xs="24" :sm="12" :lg="8">
        <a-card 
          class="mode-card"
          :class="{ 'active': selectedMode === 'precision' }"
          @click="selectMode('precision')"
        >
          <div class="mode-icon">
            <i class="fas fa-search-plus"></i>
          </div>
          <h3>超精度识别</h3>
          <p>使用最新AI模型，识别精度高达99.5%</p>
          <div class="mode-features">
            <span>• 微小特征检测</span>
            <span>• 亚像素级分析</span>
            <span>• 深度学习优化</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="8">
        <a-card 
          class="mode-card"
          :class="{ 'active': selectedMode === 'multi' }"
          @click="selectMode('multi')"
        >
          <div class="mode-icon">
            <i class="fas fa-layer-group"></i>
          </div>
          <h3>多目标识别</h3>
          <p>同时识别图片中的多个对象</p>
          <div class="mode-features">
            <span>• 目标分割</span>
            <span>• 位置标注</span>
            <span>• 关系分析</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="8">
        <a-card 
          class="mode-card"
          :class="{ 'active': selectedMode === 'scene' }"
          @click="selectMode('scene')"
        >
          <div class="mode-icon">
            <i class="fas fa-map"></i>
          </div>
          <h3>场景理解</h3>
          <p>深度理解图片内容和场景语义</p>
          <div class="mode-features">
            <span>• 场景分类</span>
            <span>• 语义分析</span>
            <span>• 内容描述</span>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 高级设置 -->
    <a-card title="高级设置" class="settings-card">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :md="12">
          <a-form layout="vertical">
            <a-form-item label="识别精度">
              <a-slider 
                v-model:value="settings.precision" 
                :min="90" 
                :max="100" 
                :step="0.1"
                :marks="precisionMarks"
              />
            </a-form-item>
            <a-form-item label="检测阈值">
              <a-slider 
                v-model:value="settings.threshold" 
                :min="0.1" 
                :max="1" 
                :step="0.05"
                :marks="thresholdMarks"
              />
            </a-form-item>
            <a-form-item label="分析深度">
              <a-select v-model:value="settings.depth" style="width: 100%">
                <a-select-option value="basic">基础分析</a-select-option>
                <a-select-option value="advanced">高级分析</a-select-option>
                <a-select-option value="expert">专家级分析</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-col>
        <a-col :xs="24" :md="12">
          <a-form layout="vertical">
            <a-form-item label="输出格式">
              <a-checkbox-group v-model:value="settings.outputs">
                <a-checkbox value="json">JSON数据</a-checkbox>
                <a-checkbox value="xml">XML格式</a-checkbox>
                <a-checkbox value="csv">CSV表格</a-checkbox>
                <a-checkbox value="report">详细报告</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            <a-form-item label="扩展功能">
              <a-checkbox-group v-model:value="settings.features">
                <a-checkbox value="ocr">文字识别</a-checkbox>
                <a-checkbox value="face">人脸检测</a-checkbox>
                <a-checkbox value="emotion">情感分析</a-checkbox>
                <a-checkbox value="color">色彩分析</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
    </a-card>

    <!-- 上传区域 -->
    <a-card class="upload-card">
      <a-upload-dragger
        v-model:fileList="fileList"
        name="images"
        :multiple="true"
        :before-upload="beforeUpload"
        @change="handleFileChange"
        class="advanced-upload"
        accept="image/*"
      >
        <div class="upload-content">
          <div class="upload-icon">
            <i class="fas fa-cloud-upload-alt"></i>
          </div>
          <h3>上传图片进行高级识别</h3>
          <p>支持 JPG、PNG、WebP、TIFF 等格式，单个文件最大 20MB</p>
          <div class="upload-features">
            <a-tag color="blue">RAW格式支持</a-tag>
            <a-tag color="green">高分辨率优化</a-tag>
            <a-tag color="purple">批量处理</a-tag>
          </div>
        </div>
      </a-upload-dragger>
    </a-card>

    <!-- 识别结果 -->
    <div v-if="recognitionResults.length > 0" class="results-section">
      <a-card title="识别结果" class="results-card">
        <template #extra>
          <a-space>
            <a-button @click="exportResults">
              <i class="fas fa-download"></i>
              导出结果
            </a-button>
            <a-button @click="saveToCloud">
              <i class="fas fa-cloud"></i>
              保存到云端
            </a-button>
          </a-space>
        </template>

        <div 
          v-for="(result, index) in recognitionResults" 
          :key="index"
          class="result-item"
        >
          <a-row :gutter="24">
            <a-col :xs="24" :lg="8">
              <div class="result-image">
                <img :src="result.imageUrl" :alt="result.filename" />
                <div class="image-overlay">
                  <a-button type="primary" ghost @click="viewFullImage(result)">
                    <i class="fas fa-expand"></i>
                  </a-button>
                </div>
              </div>
            </a-col>
            <a-col :xs="24" :lg="16">
              <div class="result-details">
                <div class="result-header">
                  <h4>{{ result.filename }}</h4>
                  <a-tag :color="getConfidenceColor(result.confidence)">
                    置信度: {{ result.confidence }}%
                  </a-tag>
                </div>

                <a-tabs size="small">
                  <a-tab-pane key="main" tab="主要结果">
                    <div class="main-results">
                      <div 
                        v-for="item in result.mainResults" 
                        :key="item.id"
                        class="result-object"
                      >
                        <div class="object-info">
                          <span class="object-name">{{ item.name }}</span>
                          <span class="object-confidence">{{ item.confidence }}%</span>
                        </div>
                        <div class="object-position" v-if="item.bbox">
                          位置: ({{ item.bbox.x }}, {{ item.bbox.y }}) 
                          {{ item.bbox.width }}×{{ item.bbox.height }}
                        </div>
                      </div>
                    </div>
                  </a-tab-pane>
                  <a-tab-pane key="details" tab="详细分析">
                    <a-descriptions size="small" :column="2">
                      <a-descriptions-item label="图像尺寸">
                        {{ result.details.dimensions }}
                      </a-descriptions-item>
                      <a-descriptions-item label="文件大小">
                        {{ result.details.fileSize }}
                      </a-descriptions-item>
                      <a-descriptions-item label="色彩模式">
                        {{ result.details.colorMode }}
                      </a-descriptions-item>
                      <a-descriptions-item label="DPI">
                        {{ result.details.dpi }}
                      </a-descriptions-item>
                      <a-descriptions-item label="拍摄设备">
                        {{ result.details.device || '未知' }}
                      </a-descriptions-item>
                      <a-descriptions-item label="处理时间">
                        {{ result.details.processTime }}ms
                      </a-descriptions-item>
                    </a-descriptions>
                  </a-tab-pane>
                  <a-tab-pane key="metadata" tab="元数据">
                    <pre class="metadata-content">{{ JSON.stringify(result.metadata, null, 2) }}</pre>
                  </a-tab-pane>
                </a-tabs>
              </div>
            </a-col>
          </a-row>
        </div>
      </a-card>
    </div>

    <!-- VIP使用统计 -->
    <a-card title="VIP使用统计" class="stats-card">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="本月高级识别"
            :value="vipStats.monthlyAdvanced"
            suffix="次"
            :value-style="{ color: '#faad14' }"
          />
        </a-col>
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="剩余额度"
            :value="vipStats.remainingQuota"
            suffix="次"
            :value-style="{ color: '#52c41a' }"
          />
        </a-col>
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="平均精度"
            :value="vipStats.averageAccuracy"
            suffix="%"
            :precision="1"
            :value-style="{ color: '#1890ff' }"
          />
        </a-col>
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="节省时间"
            :value="vipStats.timeSaved"
            suffix="小时"
            :precision="1"
            :value-style="{ color: '#722ed1' }"
          />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import type { UploadFile } from 'ant-design-vue'

const selectedMode = ref('precision')
const fileList = ref<UploadFile[]>([])
const recognitionResults = ref<any[]>([])

// 高级设置
const settings = reactive({
  precision: 95.0,
  threshold: 0.8,
  depth: 'advanced',
  outputs: ['json', 'report'],
  features: ['ocr', 'color']
})

// 精度标记
const precisionMarks = {
  90: '90%',
  95: '95%',
  99: '99%',
  100: '100%'
}

// 阈值标记
const thresholdMarks = {
  0.1: '0.1',
  0.5: '0.5',
  0.8: '0.8',
  1.0: '1.0'
}

// VIP统计数据
const vipStats = reactive({
  monthlyAdvanced: 156,
  remainingQuota: 844,
  averageAccuracy: 97.8,
  timeSaved: 24.5
})

// 选择识别模式
function selectMode(mode: string) {
  selectedMode.value = mode
  message.info(`已选择${getModeTitle(mode)}模式`)
}

function getModeTitle(mode: string) {
  const titles: Record<string, string> = {
    'precision': '超精度识别',
    'multi': '多目标识别',
    'scene': '场景理解'
  }
  return titles[mode] || ''
}

// 文件上传前验证
function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  
  const isLt20M = file.size / 1024 / 1024 < 20
  if (!isLt20M) {
    message.error('图片大小不能超过 20MB!')
    return false
  }
  
  return false // 阻止自动上传
}

// 文件变化处理
function handleFileChange(info: any) {
  if (info.fileList.length > 0) {
    startAdvancedRecognition()
  }
}

// 开始高级识别
async function startAdvancedRecognition() {
  message.loading('正在进行高级识别分析...', 0)
  
  try {
    // 模拟高级识别过程
    await new Promise(resolve => setTimeout(resolve, 5000))
    
    // 生成模拟结果
    recognitionResults.value = fileList.value.map((file, index) => ({
      filename: file.name,
      imageUrl: URL.createObjectURL(file.originFileObj),
      confidence: 95 + Math.random() * 4, // 95-99%
      mainResults: [
        {
          id: 1,
          name: '金毛寻回犬',
          confidence: 97.8,
          bbox: { x: 120, y: 80, width: 300, height: 250 }
        },
        {
          id: 2,
          name: '草地背景',
          confidence: 89.5,
          bbox: { x: 0, y: 0, width: 640, height: 480 }
        }
      ],
      details: {
        dimensions: '1920×1080',
        fileSize: '2.5MB',
        colorMode: 'RGB',
        dpi: '300',
        device: 'Canon EOS R5',
        processTime: 3247
      },
      metadata: {
        algorithm: 'YOLOv8-Ultra',
        modelVersion: 'v2.1.0',
        processingTime: '3.247s',
        gpuUsage: '78%',
        memoryUsage: '2.1GB'
      }
    }))
    
    message.destroy()
    message.success('高级识别完成!')
  } catch (error) {
    message.destroy()
    message.error('识别失败，请重试')
  }
}

// 获取置信度颜色
function getConfidenceColor(confidence: number) {
  if (confidence >= 95) return 'green'
  if (confidence >= 90) return 'orange'
  return 'red'
}

// 查看完整图片
function viewFullImage(result: any) {
  message.info(`查看完整图片：${result.filename}`)
}

// 导出结果
function exportResults() {
  message.info('正在导出识别结果...')
}

// 保存到云端
function saveToCloud() {
  message.success('结果已保存到VIP云端存储')
}
</script>

<style scoped>
.advanced-recognition-page {
  padding: 0;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #ffd700 0%, #ffb300 100%);
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 24px;
  color: #333;
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
  opacity: 0.8;
}

.vip-tag {
  font-size: 14px;
  font-weight: bold;
  padding: 8px 16px;
}

/* 识别模式 */
.recognition-modes {
  margin-bottom: 24px;
}

.mode-card {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  height: 100%;
}

.mode-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
}

.mode-card.active {
  border-color: #ffd700;
  box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.3);
}

.mode-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #ffd700, #ffb300);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.mode-card h3 {
  font-size: 18px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.mode-card p {
  color: #666;
  margin-bottom: 16px;
}

.mode-features {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

/* 设置卡片 */
.settings-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

/* 上传区域 */
.upload-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.advanced-upload {
  border: 2px dashed #ffd700 !important;
  background: linear-gradient(135deg, #fffbf0 0%, #fff8e1 100%) !important;
}

.upload-content {
  padding: 40px;
  text-align: center;
}

.upload-icon {
  font-size: 64px;
  color: #ffd700;
  margin-bottom: 16px;
}

.upload-content h3 {
  font-size: 18px;
  color: #262626;
  margin-bottom: 8px;
}

.upload-content p {
  color: #666;
  margin-bottom: 16px;
}

.upload-features {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

/* 识别结果 */
.results-section {
  margin-bottom: 24px;
}

.results-card {
  border-radius: 12px;
}

.result-item {
  padding: 24px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  margin-bottom: 16px;
}

.result-image {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  height: 200px;
}

.result-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.result-image:hover .image-overlay {
  opacity: 1;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.result-header h4 {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.main-results {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-object {
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
}

.object-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.object-name {
  font-weight: 500;
  color: #262626;
}

.object-confidence {
  color: #52c41a;
  font-weight: 500;
}

.object-position {
  font-size: 12px;
  color: #999;
}

.metadata-content {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 6px;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
}

/* 统计卡片 */
.stats-card {
  border-radius: 12px;
  border-left: 4px solid #ffd700;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .mode-features {
    font-size: 11px;
  }
  
  .upload-content {
    padding: 24px;
  }
  
  .upload-icon {
    font-size: 48px;
  }
}

@media (max-width: 576px) {
  .page-header {
    padding: 20px;
  }
  
  .title-section h1 {
    font-size: 24px;
  }
  
  .result-item {
    padding: 16px;
  }
}
</style>
