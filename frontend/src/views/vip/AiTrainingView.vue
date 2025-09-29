<template>
  <div class="ai-training-page">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>
            <i class="fas fa-brain vip-icon"></i>
            AI模型训练
          </h1>
          <p>VIP专享的自定义AI模型训练服务</p>
        </div>
        <div class="training-status">
          <a-tag :color="getStatusColor(currentTraining.status)" class="status-tag">
            <i class="fas fa-circle"></i>
            {{ getStatusText(currentTraining.status) }}
          </a-tag>
        </div>
      </div>
    </div>

    <!-- 训练模式选择 -->
    <a-card title="选择训练模式" class="mode-selection-card">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :md="8">
          <div 
            class="training-mode"
            :class="{ 'active': selectedMode === 'custom' }"
            @click="selectMode('custom')"
          >
            <div class="mode-icon">
              <i class="fas fa-cogs"></i>
            </div>
            <h3>自定义训练</h3>
            <p>使用您的专有数据集训练专属模型</p>
            <ul class="mode-features">
              <li>完全自主控制</li>
              <li>高度定制化</li>
              <li>最佳性能优化</li>
            </ul>
          </div>
        </a-col>
        <a-col :xs="24" :md="8">
          <div 
            class="training-mode"
            :class="{ 'active': selectedMode === 'transfer' }"
            @click="selectMode('transfer')"
          >
            <div class="mode-icon">
              <i class="fas fa-share-alt"></i>
            </div>
            <h3>迁移学习</h3>
            <p>基于预训练模型进行快速优化</p>
            <ul class="mode-features">
              <li>训练速度快</li>
              <li>数据需求少</li>
              <li>成本效益高</li>
            </ul>
          </div>
        </a-col>
        <a-col :xs="24" :md="8">
          <div 
            class="training-mode"
            :class="{ 'active': selectedMode === 'finetune' }"
            @click="selectMode('finetune')"
          >
            <div class="mode-icon">
              <i class="fas fa-sliders-h"></i>
            </div>
            <h3>模型微调</h3>
            <p>在现有模型基础上进行精细调整</p>
            <ul class="mode-features">
              <li>保持稳定性</li>
              <li>精确优化</li>
              <li>风险可控</li>
            </ul>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 数据集管理 -->
    <a-card title="数据集管理" class="dataset-card">
      <div class="dataset-actions">
        <a-button type="primary" @click="showUploadModal">
          <i class="fas fa-upload"></i>
          上传数据集
        </a-button>
        <a-button @click="createDataset">
          <i class="fas fa-plus"></i>
          创建数据集
        </a-button>
        <a-button @click="importDataset">
          <i class="fas fa-download"></i>
          导入数据集
        </a-button>
      </div>

      <div class="dataset-list">
        <div 
          v-for="dataset in datasets" 
          :key="dataset.id"
          class="dataset-item"
          :class="{ 'selected': selectedDataset?.id === dataset.id }"
          @click="selectDataset(dataset)"
        >
          <div class="dataset-info">
            <div class="dataset-header">
              <h4>{{ dataset.name }}</h4>
              <a-tag :color="getDatasetStatusColor(dataset.status)">
                {{ dataset.status }}
              </a-tag>
            </div>
            <div class="dataset-meta">
              <span>{{ dataset.imageCount }} 张图片</span>
              <span>{{ dataset.categories }} 个类别</span>
              <span>{{ dataset.size }}</span>
            </div>
            <div class="dataset-description">
              {{ dataset.description }}
            </div>
          </div>
          <div class="dataset-actions-mini">
            <a-button type="text" size="small" @click.stop="editDataset(dataset)">
              <i class="fas fa-edit"></i>
            </a-button>
            <a-button type="text" size="small" @click.stop="deleteDataset(dataset)">
              <i class="fas fa-trash"></i>
            </a-button>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 训练配置 -->
    <a-card title="训练配置" class="config-card" v-if="selectedDataset">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :lg="12">
          <a-form layout="vertical">
            <a-form-item label="模型名称">
              <a-input v-model:value="trainingConfig.modelName" placeholder="输入模型名称" />
            </a-form-item>
            <a-form-item label="训练轮数 (Epochs)">
              <a-slider 
                v-model:value="trainingConfig.epochs" 
                :min="10" 
                :max="200" 
                :marks="epochMarks"
              />
            </a-form-item>
            <a-form-item label="学习率">
              <a-select v-model:value="trainingConfig.learningRate" style="width: 100%">
                <a-select-option value="0.001">0.001 (推荐)</a-select-option>
                <a-select-option value="0.01">0.01</a-select-option>
                <a-select-option value="0.0001">0.0001</a-select-option>
                <a-select-option value="auto">自动调整</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="批次大小">
              <a-select v-model:value="trainingConfig.batchSize" style="width: 100%">
                <a-select-option value="16">16</a-select-option>
                <a-select-option value="32">32 (推荐)</a-select-option>
                <a-select-option value="64">64</a-select-option>
                <a-select-option value="128">128</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-col>
        <a-col :xs="24" :lg="12">
          <a-form layout="vertical">
            <a-form-item label="优化器">
              <a-select v-model:value="trainingConfig.optimizer" style="width: 100%">
                <a-select-option value="adam">Adam (推荐)</a-select-option>
                <a-select-option value="sgd">SGD</a-select-option>
                <a-select-option value="adamw">AdamW</a-select-option>
                <a-select-option value="rmsprop">RMSprop</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="数据增强">
              <a-checkbox-group v-model:value="trainingConfig.augmentation">
                <a-checkbox value="rotation">旋转</a-checkbox>
                <a-checkbox value="flip">翻转</a-checkbox>
                <a-checkbox value="zoom">缩放</a-checkbox>
                <a-checkbox value="brightness">亮度调整</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            <a-form-item label="验证集比例">
              <a-slider 
                v-model:value="trainingConfig.validationSplit" 
                :min="0.1" 
                :max="0.3" 
                :step="0.05"
                :marks="validationMarks"
              />
            </a-form-item>
            <a-form-item label="提前停止">
              <a-checkbox v-model:checked="trainingConfig.earlyStopping">
                启用提前停止机制
              </a-checkbox>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <div class="config-actions">
        <a-button type="primary" size="large" @click="startTraining" :loading="isTraining">
          <i class="fas fa-play"></i>
          开始训练
        </a-button>
        <a-button @click="saveConfig">
          <i class="fas fa-save"></i>
          保存配置
        </a-button>
        <a-button @click="loadConfig">
          <i class="fas fa-folder-open"></i>
          加载配置
        </a-button>
      </div>
    </a-card>

    <!-- 训练进度 -->
    <a-card title="训练进度" class="progress-card" v-if="isTraining || trainingHistory.length > 0">
      <div v-if="isTraining" class="current-training">
        <div class="training-info">
          <h4>{{ trainingConfig.modelName }}</h4>
          <div class="progress-details">
            <div class="progress-item">
              <span>当前轮次:</span>
              <span>{{ currentProgress.currentEpoch }} / {{ trainingConfig.epochs }}</span>
            </div>
            <div class="progress-item">
              <span>训练准确率:</span>
              <span>{{ currentProgress.trainAccuracy }}%</span>
            </div>
            <div class="progress-item">
              <span>验证准确率:</span>
              <span>{{ currentProgress.valAccuracy }}%</span>
            </div>
            <div class="progress-item">
              <span>预计剩余时间:</span>
              <span>{{ currentProgress.estimatedTime }}</span>
            </div>
          </div>
        </div>
        
        <div class="progress-chart">
          <a-progress 
            :percent="Math.round((currentProgress.currentEpoch / trainingConfig.epochs) * 100)"
            :stroke-color="{ '0%': '#108ee9', '100%': '#87d068' }"
            stroke-linecap="round"
          />
          <div class="loss-chart">
            <div class="chart-placeholder">
              <i class="fas fa-chart-line"></i>
              <span>训练损失曲线</span>
            </div>
          </div>
        </div>

        <div class="training-controls">
          <a-button @click="pauseTraining">
            <i class="fas fa-pause"></i>
            暂停训练
          </a-button>
          <a-button danger @click="stopTraining">
            <i class="fas fa-stop"></i>
            停止训练
          </a-button>
        </div>
      </div>

      <!-- 训练历史 -->
      <div class="training-history">
        <h4>训练历史</h4>
        <a-table 
          :columns="historyColumns" 
          :data-source="trainingHistory"
          :pagination="{ pageSize: 5 }"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getTrainingStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space>
                <a-button type="link" size="small" @click="viewTrainingDetail(record)">
                  详情
                </a-button>
                <a-button type="link" size="small" @click="downloadModel(record)">
                  下载
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-card>

    <!-- 上传数据集模态框 -->
    <a-modal
      v-model:open="uploadModalVisible"
      title="上传数据集"
      @ok="handleUploadDataset"
      width="600px"
    >
      <a-form layout="vertical">
        <a-form-item label="数据集名称">
          <a-input v-model:value="newDataset.name" placeholder="输入数据集名称" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="newDataset.description" :rows="3" />
        </a-form-item>
        <a-form-item label="数据格式">
          <a-radio-group v-model:value="newDataset.format">
            <a-radio value="folder">文件夹结构</a-radio>
            <a-radio value="csv">CSV标注</a-radio>
            <a-radio value="json">JSON标注</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="上传文件">
          <a-upload-dragger
            v-model:fileList="uploadFileList"
            name="dataset"
            :multiple="true"
            accept=".zip,.tar,.gz"
          >
            <div class="upload-area">
              <i class="fas fa-cloud-upload-alt"></i>
              <p>点击或拖拽文件到此区域上传</p>
              <p class="upload-hint">支持 ZIP、TAR、GZ 格式，最大 2GB</p>
            </div>
          </a-upload-dragger>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'

const selectedMode = ref('custom')
const selectedDataset = ref<any>(null)
const isTraining = ref(false)
const uploadModalVisible = ref(false)
const uploadFileList = ref([])

// 当前训练状态
const currentTraining = reactive({
  status: 'idle' // idle, training, paused, completed, failed
})

// 训练配置
const trainingConfig = reactive({
  modelName: '',
  epochs: 50,
  learningRate: '0.001',
  batchSize: '32',
  optimizer: 'adam',
  augmentation: ['rotation', 'flip'],
  validationSplit: 0.2,
  earlyStopping: true
})

// 当前训练进度
const currentProgress = reactive({
  currentEpoch: 0,
  trainAccuracy: 0,
  valAccuracy: 0,
  estimatedTime: '00:00:00'
})

// 新数据集
const newDataset = reactive({
  name: '',
  description: '',
  format: 'folder'
})

// 数据集列表
const datasets = ref([
  {
    id: 1,
    name: '动物识别数据集',
    status: '就绪',
    imageCount: 5420,
    categories: 12,
    size: '2.1GB',
    description: '包含12种常见动物的高质量图片数据集'
  },
  {
    id: 2,
    name: '植物分类数据集',
    status: '处理中',
    imageCount: 3280,
    categories: 8,
    size: '1.5GB',
    description: '8种常见植物的分类标注数据'
  },
  {
    id: 3,
    name: '自定义产品数据集',
    status: '就绪',
    imageCount: 1560,
    categories: 5,
    size: '780MB',
    description: '企业定制产品识别数据集'
  }
])

// 训练历史
const trainingHistory = ref([
  {
    id: 1,
    modelName: '动物识别模型v1.0',
    dataset: '动物识别数据集',
    accuracy: '96.8%',
    trainTime: '2小时15分',
    status: '已完成',
    createTime: '2024-03-20 10:30'
  },
  {
    id: 2,
    modelName: '植物分类模型v1.0',
    dataset: '植物分类数据集',
    accuracy: '94.2%',
    trainTime: '1小时45分',
    status: '已完成',
    createTime: '2024-03-19 14:20'
  }
])

// 训练历史表格列
const historyColumns = [
  { title: '模型名称', dataIndex: 'modelName', key: 'modelName' },
  { title: '数据集', dataIndex: 'dataset', key: 'dataset' },
  { title: '准确率', dataIndex: 'accuracy', key: 'accuracy' },
  { title: '训练时间', dataIndex: 'trainTime', key: 'trainTime' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action' }
]

// 标记
const epochMarks = {
  10: '10',
  50: '50',
  100: '100',
  200: '200'
}

const validationMarks = {
  0.1: '10%',
  0.2: '20%',
  0.3: '30%'
}

// 方法
function selectMode(mode: string) {
  selectedMode.value = mode
  message.info(`已选择${getModeTitle(mode)}模式`)
}

function getModeTitle(mode: string) {
  const titles: Record<string, string> = {
    'custom': '自定义训练',
    'transfer': '迁移学习',
    'finetune': '模型微调'
  }
  return titles[mode] || ''
}

function selectDataset(dataset: any) {
  selectedDataset.value = dataset
  trainingConfig.modelName = `${dataset.name}_模型_v1.0`
}

function getStatusColor(status: string) {
  const colors: Record<string, string> = {
    'idle': 'default',
    'training': 'processing',
    'paused': 'warning',
    'completed': 'success',
    'failed': 'error'
  }
  return colors[status] || 'default'
}

function getStatusText(status: string) {
  const texts: Record<string, string> = {
    'idle': '空闲',
    'training': '训练中',
    'paused': '已暂停',
    'completed': '已完成',
    'failed': '训练失败'
  }
  return texts[status] || '未知'
}

function getDatasetStatusColor(status: string) {
  const colors: Record<string, string> = {
    '就绪': 'success',
    '处理中': 'processing',
    '错误': 'error'
  }
  return colors[status] || 'default'
}

function getTrainingStatusColor(status: string) {
  const colors: Record<string, string> = {
    '已完成': 'success',
    '训练中': 'processing',
    '已失败': 'error'
  }
  return colors[status] || 'default'
}

function showUploadModal() {
  uploadModalVisible.value = true
}

function createDataset() {
  message.info('创建数据集功能开发中')
}

function importDataset() {
  message.info('导入数据集功能开发中')
}

function editDataset(dataset: any) {
  message.info(`编辑数据集：${dataset.name}`)
}

function deleteDataset(dataset: any) {
  message.info(`删除数据集：${dataset.name}`)
}

function saveConfig() {
  message.success('训练配置已保存')
}

function loadConfig() {
  message.info('加载训练配置')
}

async function startTraining() {
  if (!selectedDataset.value) {
    message.warning('请先选择数据集')
    return
  }
  
  if (!trainingConfig.modelName) {
    message.warning('请输入模型名称')
    return
  }

  isTraining.value = true
  currentTraining.status = 'training'
  
  // 模拟训练过程
  message.success('训练已开始，请耐心等待...')
  
  // 模拟进度更新
  const interval = setInterval(() => {
    if (currentProgress.currentEpoch < trainingConfig.epochs) {
      currentProgress.currentEpoch++
      currentProgress.trainAccuracy = Math.min(95, 70 + currentProgress.currentEpoch * 0.5)
      currentProgress.valAccuracy = Math.min(93, 65 + currentProgress.currentEpoch * 0.45)
      
      const remaining = trainingConfig.epochs - currentProgress.currentEpoch
      const hours = Math.floor(remaining * 2 / 60)
      const minutes = remaining * 2 % 60
      currentProgress.estimatedTime = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:00`
    } else {
      clearInterval(interval)
      isTraining.value = false
      currentTraining.status = 'completed'
      message.success('模型训练完成！')
    }
  }, 2000)
}

function pauseTraining() {
  currentTraining.status = 'paused'
  message.info('训练已暂停')
}

function stopTraining() {
  isTraining.value = false
  currentTraining.status = 'idle'
  currentProgress.currentEpoch = 0
  message.warning('训练已停止')
}

function viewTrainingDetail(record: any) {
  message.info(`查看训练详情：${record.modelName}`)
}

function downloadModel(record: any) {
  message.success(`开始下载模型：${record.modelName}`)
}

function handleUploadDataset() {
  if (!newDataset.name) {
    message.warning('请输入数据集名称')
    return
  }
  
  message.success('数据集上传成功')
  uploadModalVisible.value = false
  
  // 重置表单
  newDataset.name = ''
  newDataset.description = ''
  newDataset.format = 'folder'
  uploadFileList.value = []
}
</script>

<style scoped>
.ai-training-page {
  padding: 0;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #722ed1 0%, #1890ff 100%);
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

.status-tag {
  font-size: 14px;
  font-weight: bold;
  padding: 8px 16px;
}

/* 训练模式选择 */
.mode-selection-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.training-mode {
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.training-mode:hover {
  border-color: #1890ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.training-mode.active {
  border-color: #722ed1;
  background: linear-gradient(135deg, #f9f0ff 0%, #fff0f6 100%);
}

.mode-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #722ed1, #eb2f96);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.training-mode h3 {
  font-size: 18px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 8px;
}

.training-mode p {
  color: #666;
  margin-bottom: 16px;
}

.mode-features {
  list-style: none;
  padding: 0;
  margin: 0;
}

.mode-features li {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

/* 数据集管理 */
.dataset-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.dataset-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.dataset-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.dataset-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.dataset-item:hover {
  background: #fafafa;
  border-color: #1890ff;
}

.dataset-item.selected {
  border-color: #722ed1;
  background: linear-gradient(135deg, #f9f0ff 0%, #fff0f6 100%);
}

.dataset-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.dataset-header h4 {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
}

.dataset-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  font-size: 12px;
  color: #999;
}

.dataset-description {
  font-size: 14px;
  color: #666;
}

.dataset-actions-mini {
  display: flex;
  gap: 8px;
}

/* 训练配置 */
.config-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.config-actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

/* 训练进度 */
.progress-card {
  border-radius: 12px;
  margin-bottom: 24px;
}

.current-training {
  margin-bottom: 32px;
}

.training-info h4 {
  font-size: 18px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 16px;
}

.progress-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.progress-item {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
}

.progress-chart {
  margin-bottom: 24px;
}

.loss-chart {
  margin-top: 16px;
  height: 200px;
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
  font-size: 32px;
}

.training-controls {
  display: flex;
  gap: 12px;
}

.training-history h4 {
  margin-bottom: 16px;
  color: #262626;
}

/* 上传模态框 */
.upload-area {
  text-align: center;
  padding: 32px;
}

.upload-area i {
  font-size: 48px;
  color: #1890ff;
  margin-bottom: 16px;
}

.upload-area p {
  margin-bottom: 8px;
  color: #262626;
}

.upload-hint {
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .dataset-actions {
    flex-direction: column;
  }
  
  .config-actions {
    flex-direction: column;
  }
  
  .training-controls {
    flex-direction: column;
  }
  
  .progress-details {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .page-header {
    padding: 20px;
  }
  
  .title-section h1 {
    font-size: 24px;
  }
  
  .training-mode {
    padding: 16px;
  }
  
  .dataset-item {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
}
</style>
