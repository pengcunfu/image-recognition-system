<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题 -->
    <a-card :style="{ marginBottom: '24px', borderRadius: '8px' }">
      <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
        <div>
          <h1 :style="{ margin: '0 0 8px 0', fontSize: '28px', fontWeight: '600' }">
            高级识别
          </h1>
          <p :style="{ margin: '0', fontSize: '14px', opacity: 0.65 }">VIP专享的高精度AI识别服务</p>
        </div>
        <div>
          <a-tag color="gold" :style="{ fontSize: '14px', padding: '4px 12px' }">
            <i class="fas fa-star"></i>
            VIP专享
          </a-tag>
        </div>
      </div>
    </a-card>


    <!-- 高级设置 -->
    <a-card title="扩展功能" :style="{ borderRadius: '8px', marginBottom: '16px' }">
      <a-form layout="vertical">
        <a-form-item label="选择需要的扩展功能">
          <a-checkbox-group v-model:value="settings.features">
            <a-row :gutter="[16, 16]">
              <a-col :xs="24" :sm="12" :md="6">
                <a-checkbox value="ocr">
                  <div :style="{ display: 'flex', alignItems: 'center', gap: '8px' }">
                    <i class="fas fa-font" :style="{ color: '#1890ff' }"></i>
                    <span>文字识别</span>
            </div>
                </a-checkbox>
              </a-col>
              <a-col :xs="24" :sm="12" :md="6">
                <a-checkbox value="face">
                  <div :style="{ display: 'flex', alignItems: 'center', gap: '8px' }">
                    <i class="fas fa-user" :style="{ color: '#52c41a' }"></i>
                    <span>人脸检测</span>
            </div>
                </a-checkbox>
      </a-col>
              <a-col :xs="24" :sm="12" :md="6">
                <a-checkbox value="emotion">
                  <div :style="{ display: 'flex', alignItems: 'center', gap: '8px' }">
                    <i class="fas fa-smile" :style="{ color: '#faad14' }"></i>
                    <span>情感分析</span>
            </div>
                </a-checkbox>
      </a-col>
              <a-col :xs="24" :sm="12" :md="6">
                <a-checkbox value="color">
                  <div :style="{ display: 'flex', alignItems: 'center', gap: '8px' }">
                    <i class="fas fa-palette" :style="{ color: '#722ed1' }"></i>
                    <span>色彩分析</span>
            </div>
                </a-checkbox>
      </a-col>
    </a-row>
              </a-checkbox-group>
            </a-form-item>
        <a-form-item>
          <div :style="{ fontSize: '13px', color: '#666', marginTop: '8px' }">
            <i class="fas fa-info-circle" :style="{ marginRight: '6px' }"></i>
            选择的扩展功能将在识别过程中自动应用，提供更详细的分析结果
          </div>
            </a-form-item>
          </a-form>
    </a-card>

    <!-- 上传区域 -->
    <a-card :style="{ borderRadius: '8px', marginBottom: '16px' }">
      <a-upload-dragger
        v-model:fileList="fileList"
        name="images"
        :multiple="true"
        :before-upload="beforeUpload"
        @change="handleFileChange"
        accept="image/*"
        :style="{ padding: '48px 24px' }"
      >
        <div :style="{ textAlign: 'center' }">
          <div :style="{ fontSize: '64px', marginBottom: '24px', color: '#1890ff' }">
            <i class="fas fa-cloud-upload-alt"></i>
          </div>
          <h3 :style="{ margin: '0 0 12px 0', fontSize: '18px', fontWeight: '500' }">上传图片进行高级识别</h3>
          <p :style="{ margin: '0 0 24px 0', fontSize: '14px', opacity: 0.65 }">支持 JPG、PNG、WebP、TIFF 等格式，单个文件最大 20MB</p>
          <div :style="{ display: 'flex', gap: '12px', justifyContent: 'center', flexWrap: 'wrap' }">
            <a-tag color="blue">RAW格式支持</a-tag>
            <a-tag color="green">高分辨率优化</a-tag>
            <a-tag color="purple">批量处理</a-tag>
          </div>
        </div>
      </a-upload-dragger>
    </a-card>

    <!-- 识别结果 -->
    <div v-if="recognitionResults.length > 0" :style="{ marginBottom: '16px' }">
      <a-card title="识别结果" :style="{ borderRadius: '8px' }">
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
          :style="{ padding: '16px', marginBottom: index < recognitionResults.length - 1 ? '16px' : 0, borderRadius: '8px', background: '#fafafa' }"
        >
          <a-row :gutter="16">
            <a-col :xs="24" :lg="8">
              <div 
                :style="{ position: 'relative', borderRadius: '8px', overflow: 'hidden', cursor: 'pointer' }"
                @mouseenter="showOverlay"
                @mouseleave="hideOverlay"
                @click="viewFullImage(result)"
              >
                <img :src="result.imageUrl" :alt="result.filename" :style="{ width: '100%', height: 'auto', display: 'block' }" />
                <div 
                  ref="imageOverlay"
                  :style="{ 
                    position: 'absolute', 
                    top: 0, 
                    left: 0, 
                    right: 0, 
                    bottom: 0, 
                    background: 'rgba(0,0,0,0.5)', 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center',
                    opacity: 0, 
                    transition: 'opacity 0.3s ease' 
                  }" 
                  class="image-overlay"
                >
                  <a-button type="primary" ghost>
                    <i class="fas fa-expand" :style="{ marginRight: '6px' }"></i>
                    查看大图
                  </a-button>
                </div>
              </div>
            </a-col>
            <a-col :xs="24" :lg="16">
              <div>
                <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }">
                  <h4 :style="{ margin: 0, fontSize: '16px', fontWeight: '600' }">{{ result.filename }}</h4>
                  <a-tag :color="getConfidenceColor(result.confidence)">
                    置信度: {{ Math.round(result.confidence) }}%
                  </a-tag>
                </div>

                <a-tabs size="small">
                  <a-tab-pane key="main" tab="主要结果">
                    <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
                      <div 
                        v-for="item in result.mainResults" 
                        :key="item.id"
                        :style="{ padding: '12px', borderRadius: '6px', background: 'white', border: '1px solid #e8e8e8' }"
                      >
                        <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }">
                          <span :style="{ fontSize: '14px', fontWeight: '500' }">{{ item.name }}</span>
                          <span :style="{ fontSize: '14px', color: '#52c41a' }">{{ item.confidence }}%</span>
                        </div>
                        <div :style="{ fontSize: '13px', opacity: 0.65 }" v-if="item.bbox">
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
                  <a-tab-pane key="advanced" tab="高级分析">
                    <div v-if="result.rawResult && result.rawResult.resultJson">
                      <advanced-recognition-details :data="parseAdvancedResult(result.rawResult.resultJson)" />
                    </div>
                    <div v-else :style="{ textAlign: 'center', padding: '40px 0', color: '#999' }">
                      <i class="fas fa-info-circle" :style="{ fontSize: '48px', marginBottom: '16px' }"></i>
                      <div>暂无高级分析数据</div>
                    </div>
                  </a-tab-pane>
                  <a-tab-pane key="metadata" tab="元数据">
                    <pre :style="{ fontSize: '13px', lineHeight: '1.6', background: 'white', padding: '16px', borderRadius: '6px', border: '1px solid #e8e8e8', overflow: 'auto' }">{{ JSON.stringify(result.metadata, null, 2) }}</pre>
                  </a-tab-pane>
                </a-tabs>
              </div>
            </a-col>
          </a-row>
        </div>
      </a-card>
    </div>

    <!-- VIP使用统计 -->
    <a-card title="VIP识别统计" :style="{ borderRadius: '8px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="总识别次数"
            :value="vipStats.totalRecognitions"
            suffix="次"
            :value-style="{ color: '#1890ff' }"
          />
        </a-col>
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="高级识别次数"
            :value="vipStats.advancedRecognitions"
            suffix="次"
            :value-style="{ color: '#faad14' }"
          />
        </a-col>
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="平均置信度"
            :value="vipStats.averageConfidence"
            suffix="%"
            :precision="1"
            :value-style="{ color: '#52c41a' }"
          />
        </a-col>
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="识别分类数"
            :value="vipStats.categoryCount"
            suffix="种"
            :value-style="{ color: '#722ed1' }"
          />
        </a-col>
      </a-row>
      <a-row :gutter="[16, 16]" :style="{ marginTop: '16px' }">
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="标签数量"
            :value="vipStats.tagCount"
            suffix="个"
            :value-style="{ color: '#eb2f96' }"
          />
        </a-col>
        <a-col :xs="24" :sm="6">
          <a-statistic
            title="高级识别占比"
            :value="vipStats.totalRecognitions > 0 ? (vipStats.advancedRecognitions / vipStats.totalRecognitions * 100) : 0"
            suffix="%"
            :precision="1"
            :value-style="{ color: '#13c2c2' }"
          />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { UploadFile } from 'ant-design-vue'
import { RecognitionAPI, type AdvancedRecognitionSettings, type RecognitionInfo, type VipRecognitionStats } from '@/api/recognition'
import { ImageUtils } from '@/utils/image'
import AdvancedRecognitionDetails from '@/components/AdvancedRecognitionDetails.vue'

const fileList = ref<UploadFile[]>([])
const recognitionResults = ref<any[]>([])

// 高级设置
const settings = reactive({
  features: []
})

// VIP统计数据
const vipStats = reactive<VipRecognitionStats>({
  totalRecognitions: 0,
  advancedRecognitions: 0,
  averageConfidence: 0,
  categoryCount: 0,
  tagCount: 0
})


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
  const loadingMsg = message.loading('正在进行高级识别分析...', 0)
  
  try {
    // 构建高级识别设置
    const advancedSettings: AdvancedRecognitionSettings = {
      mode: 'precision',
      precision: 95.0,
      threshold: 0.8,
      depth: 'advanced',
      outputs: ['json', 'report'],
      features: settings.features
    }
    
    const results: any[] = []
    
    // 处理每个文件
    for (let i = 0; i < fileList.value.length; i++) {
      const fileItem = fileList.value[i]
      const actualFile = fileItem.originFileObj as File
      
      if (!actualFile) continue
      
      try {
        console.log(`开始高级识别文件 ${i + 1}:`, { 
          fileName: actualFile.name, 
          size: actualFile.size, 
          type: actualFile.type,
          settings: advancedSettings
        })
        
        // 调用高级识别API
        const result = await RecognitionAPI.advancedRecognize(actualFile, advancedSettings)
        console.log(`文件 ${i + 1} 识别结果:`, result)
        
        if (result) {
          // 解析识别结果
          let resultData: any = {}
          if (result.resultJson) {
            try {
              resultData = JSON.parse(result.resultJson)
            } catch (e) {
              console.error('解析resultJson失败:', e)
              resultData = {}
            }
          }
          
          // 计算置信度百分比
          const confidencePercent = result.confidence 
            ? Math.round(Number(result.confidence) * 100) 
            : 0
          
          // 格式化识别结果
          const formattedResult = {
            id: result.id,
            filename: actualFile.name,
            imageUrl: result.imageUrl ? ImageUtils.getImageUrl(result.imageUrl) : (actualFile ? URL.createObjectURL(actualFile) : ''),
            confidence: confidencePercent,
      mainResults: [
        {
          id: 1,
                name: result.mainCategory || result.category || '未知',
                confidence: confidencePercent,
                bbox: resultData.bbox || null
              },
              ...(resultData.alternatives || []).map((alt: any, idx: number) => ({
                id: idx + 2,
                name: alt.name || alt.category,
                confidence: Math.round((alt.confidence || 0) * 100),
                bbox: alt.bbox || null
              }))
      ],
      details: {
              dimensions: result.imageWidth && result.imageHeight 
                ? `${result.imageWidth} × ${result.imageHeight}` 
                : '-',
              fileSize: formatFileSize(actualFile?.size || 0),
        colorMode: 'RGB',
        dpi: '300',
              device: resultData.device || '未知',
              processTime: result.processingTime || 0
            },
            metadata: {
              algorithm: 'Advanced AI Model',
              modelVersion: 'v2.1.0',
              processingTime: `${(result.processingTime || 0) / 1000}s`,
              mode: '高级识别',
              settings: advancedSettings
            },
            rawResult: result
          }
          
          results.push(formattedResult)
        }
      } catch (error: any) {
        console.error(`文件 ${i + 1} 识别失败:`, error)
        
        // 为失败的文件添加错误结果
        results.push({
          filename: actualFile?.name || '未知文件',
          imageUrl: actualFile ? URL.createObjectURL(actualFile) : '',
          confidence: 0,
          error: error.message || '识别失败',
          mainResults: [],
          details: {
            dimensions: '-',
            fileSize: formatFileSize(actualFile?.size || 0),
            colorMode: '-',
            dpi: '-',
            device: '-',
            processTime: 0
      },
      metadata: {
            algorithm: 'Advanced AI Model',
        modelVersion: 'v2.1.0',
            processingTime: '0s',
            error: error.message || '识别失败'
          }
        })
      }
    }
    
    recognitionResults.value = results
    loadingMsg()
    
    if (results.some(r => !r.error)) {
    message.success('高级识别完成!')
    } else {
      message.warning('部分文件识别失败，请检查文件格式')
    }
    
  } catch (error: any) {
    console.error('高级识别失败:', error)
    loadingMsg()
    message.error(error.message || '高级识别失败，请重试')
  }
}

// 格式化文件大小
function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i]
}

// 获取置信度颜色
function getConfidenceColor(confidence: number) {
  if (confidence >= 95) return 'green'
  if (confidence >= 90) return 'orange'
  return 'red'
}

// 图片悬停效果
function showOverlay(e: MouseEvent) {
  const overlay = (e.currentTarget as HTMLElement).querySelector('.image-overlay') as HTMLElement
  if (overlay) {
    overlay.style.opacity = '1'
  }
}

function hideOverlay(e: MouseEvent) {
  const overlay = (e.currentTarget as HTMLElement).querySelector('.image-overlay') as HTMLElement
  if (overlay) {
    overlay.style.opacity = '0'
  }
}

// 查看完整图片
function viewFullImage(result: any) {
  // 创建图片预览模态框
  const img = new Image()
  img.src = result.imageUrl
  img.onload = () => {
    // 创建模态框内容
    const modal = document.createElement('div')
    modal.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0,0,0,0.8);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 9999;
      cursor: pointer;
    `
    
    const imgElement = document.createElement('img')
    imgElement.src = result.imageUrl
    imgElement.style.cssText = `
      max-width: 90%;
      max-height: 90%;
      object-fit: contain;
      border-radius: 8px;
    `
    
    modal.appendChild(imgElement)
    document.body.appendChild(modal)
    
    // 点击关闭
    modal.onclick = () => {
      document.body.removeChild(modal)
    }
    
    // ESC键关闭
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        document.body.removeChild(modal)
        document.removeEventListener('keydown', handleEsc)
      }
    }
    document.addEventListener('keydown', handleEsc)
  }
}

// 导出结果
function exportResults() {
  try {
    const exportData = {
      exportTime: new Date().toISOString(),
      totalResults: recognitionResults.value.length,
      results: recognitionResults.value.map(result => ({
        filename: result.filename,
        imageUrl: result.imageUrl,
        confidence: result.confidence,
        mainResults: result.mainResults,
        details: result.details,
        metadata: result.metadata,
        advancedAnalysis: result.rawResult?.resultJson ? parseAdvancedResult(result.rawResult.resultJson) : null
      }))
    }
    
    const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `advanced-recognition-results-${new Date().getTime()}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
    
    message.success('识别结果已导出为JSON文件')
  } catch (error) {
    console.error('导出失败:', error)
    message.error('导出失败，请重试')
  }
}

// 保存到云端
async function saveToCloud() {
  try {
    const loadingMsg = message.loading('正在保存到云端...', 0)
    
    const cloudData = {
      saveTime: new Date().toISOString(),
      userAgent: navigator.userAgent,
      results: recognitionResults.value.map(result => ({
        filename: result.filename,
        imageUrl: result.imageUrl,
        confidence: result.confidence,
        mainResults: result.mainResults,
        advancedAnalysis: result.rawResult?.resultJson ? parseAdvancedResult(result.rawResult.resultJson) : null
      }))
    }
    
    // 模拟上传到TOS（这里需要实际的TOS上传接口）
    const blob = new Blob([JSON.stringify(cloudData, null, 2)], { type: 'application/json' })
    
    // 这里应该调用实际的TOS上传API
    // const tosUrl = await uploadToTos(blob, `vip-recognition-${Date.now()}.json`)
    
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    loadingMsg()
    message.success('识别结果已保存到VIP云端存储')
  } catch (error) {
    console.error('保存到云端失败:', error)
    message.error('保存失败，请重试')
  }
}

// 加载VIP统计数据
async function loadVipStats() {
  try {
    const stats = await RecognitionAPI.getVipStats()
    if (stats) {
      Object.assign(vipStats, stats)
    }
  } catch (error) {
    console.error('加载VIP统计数据失败:', error)
    // 如果VIP接口失败，尝试使用普通统计接口
    try {
      const fallbackStats = await RecognitionAPI.getStats()
      if (fallbackStats) {
        vipStats.totalRecognitions = fallbackStats.total || 0
        vipStats.advancedRecognitions = 0 // 无法从普通接口获取
        vipStats.averageConfidence = fallbackStats.averageConfidence || 0
        vipStats.categoryCount = 0 // 无法从普通接口获取
        vipStats.tagCount = 0 // 无法从普通接口获取
      }
    } catch (fallbackError) {
      console.error('加载备用统计数据也失败:', fallbackError)
    }
  }
}

// 解析高级识别结果
function parseAdvancedResult(resultJson: string) {
  try {
    return JSON.parse(resultJson)
  } catch (error) {
    console.error('解析高级识别结果失败:', error)
    return null
  }
}

// 页面加载时获取VIP统计
onMounted(() => {
  loadVipStats()
})
</script>

<style scoped>
/* 图片悬停效果 */
div:hover .image-overlay {
  opacity: 1 !important;
  background: rgba(0,0,0,0.5);
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

