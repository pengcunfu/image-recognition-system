<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题 -->
    <a-card :style="{ marginBottom: '24px', borderRadius: '8px' }">
      <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }">
        <div>
          <h1 :style="{ margin: '0 0 8px 0', fontSize: '28px', fontWeight: '600' }">
            AI模型训练
          </h1>
          <p :style="{ margin: '0', fontSize: '14px', opacity: 0.65 }">VIP专享的自定义AI模型训练服务</p>
        </div>
        <a-tag :color="getStatusColor(currentTraining.status)" :style="{ fontSize: '14px', padding: '4px 12px' }">
          <i class="fas fa-circle" :style="{ fontSize: '10px', marginRight: '6px' }"></i>
          {{ getStatusText(currentTraining.status) }}
        </a-tag>
      </div>
    </a-card>

    <!-- 训练模式选择 -->
    <a-card title="选择训练模式" :style="{ marginBottom: '16px', borderRadius: '8px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :md="8">
          <a-card 
            hoverable
            :style="{ 
              borderRadius: '8px',
              border: selectedMode === 'custom' ? '2px solid #1890ff' : '1px solid #d9d9d9',
              background: selectedMode === 'custom' ? '#e6f7ff' : 'white',
              cursor: 'pointer'
            }"
            @click="selectMode('custom')"
          >
            <div :style="{ textAlign: 'center' }">
              <div :style="{ width: '56px', height: '56px', margin: '0 auto 12px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '50%', background: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)', fontSize: '24px', color: '#fff' }">
                <i class="fas fa-cogs"></i>
              </div>
              <h3 :style="{ margin: '0 0 8px 0', fontSize: '16px', fontWeight: '600' }">自定义训练</h3>
              <p :style="{ margin: '0 0 12px 0', fontSize: '13px', opacity: 0.65 }">使用您的专有数据集训练专属模型</p>
              <div :style="{ textAlign: 'left', fontSize: '12px', opacity: 0.75 }">
                <div :style="{ marginBottom: '4px' }">• 完全自主控制</div>
                <div :style="{ marginBottom: '4px' }">• 高度定制化</div>
                <div>• 最佳性能优化</div>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :xs="24" :md="8">
          <a-card 
            hoverable
            :style="{ 
              borderRadius: '8px',
              border: selectedMode === 'transfer' ? '2px solid #1890ff' : '1px solid #d9d9d9',
              background: selectedMode === 'transfer' ? '#e6f7ff' : 'white',
              cursor: 'pointer'
            }"
            @click="selectMode('transfer')"
          >
            <div :style="{ textAlign: 'center' }">
              <div :style="{ width: '56px', height: '56px', margin: '0 auto 12px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '50%', background: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)', fontSize: '24px', color: '#fff' }">
                <i class="fas fa-share-alt"></i>
              </div>
              <h3 :style="{ margin: '0 0 8px 0', fontSize: '16px', fontWeight: '600' }">迁移学习</h3>
              <p :style="{ margin: '0 0 12px 0', fontSize: '13px', opacity: 0.65 }">基于预训练模型进行快速优化</p>
              <div :style="{ textAlign: 'left', fontSize: '12px', opacity: 0.75 }">
                <div :style="{ marginBottom: '4px' }">• 训练速度快</div>
                <div :style="{ marginBottom: '4px' }">• 数据需求少</div>
                <div>• 成本效益高</div>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :xs="24" :md="8">
          <a-card 
            hoverable
            :style="{ 
              borderRadius: '8px',
              border: selectedMode === 'finetune' ? '2px solid #1890ff' : '1px solid #d9d9d9',
              background: selectedMode === 'finetune' ? '#e6f7ff' : 'white',
              cursor: 'pointer'
            }"
            @click="selectMode('finetune')"
          >
            <div :style="{ textAlign: 'center' }">
              <div :style="{ width: '56px', height: '56px', margin: '0 auto 12px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '50%', background: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)', fontSize: '24px', color: '#fff' }">
                <i class="fas fa-sliders-h"></i>
              </div>
              <h3 :style="{ margin: '0 0 8px 0', fontSize: '16px', fontWeight: '600' }">模型微调</h3>
              <p :style="{ margin: '0 0 12px 0', fontSize: '13px', opacity: 0.65 }">在现有模型基础上进行精细调整</p>
              <div :style="{ textAlign: 'left', fontSize: '12px', opacity: 0.75 }">
                <div :style="{ marginBottom: '4px' }">• 保持稳定性</div>
                <div :style="{ marginBottom: '4px' }">• 精确优化</div>
                <div>• 风险可控</div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-card>

    <!-- 数据集管理 -->
    <a-card title="数据集管理" :style="{ marginBottom: '16px', borderRadius: '8px' }">
      <template #extra>
        <a-space>
          <a-button type="primary" @click="showUploadModal">
            <i class="fas fa-upload"></i>
            上传数据集
          </a-button>
          <a-button @click="createDataset">
            <i class="fas fa-plus"></i>
            创建数据集
          </a-button>
        </a-space>
      </template>

      <div :style="{ display: 'flex', flexDirection: 'column', gap: '12px' }">
        <div 
          v-for="dataset in datasets" 
          :key="dataset.id"
          :style="{ 
            padding: '16px',
            borderRadius: '8px',
            border: selectedDataset?.id === dataset.id ? '2px solid #1890ff' : '1px solid #d9d9d9',
            background: selectedDataset?.id === dataset.id ? '#e6f7ff' : '#fafafa',
            cursor: 'pointer',
            transition: 'all 0.3s'
          }"
          @click="selectDataset(dataset)"
        >
          <div :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }">
            <div :style="{ flex: 1 }">
              <div :style="{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }">
                <h4 :style="{ margin: 0, fontSize: '15px', fontWeight: '600' }">{{ dataset.name }}</h4>
                <a-tag :color="getDatasetStatusColor(dataset.status)" size="small">
                  {{ dataset.status }}
                </a-tag>
              </div>
              <div :style="{ fontSize: '13px', opacity: 0.65, marginBottom: '8px' }">
                <span :style="{ marginRight: '16px' }">
                  <i class="fas fa-images" :style="{ marginRight: '4px' }"></i>
                  {{ dataset.imageCount }} 张图片
                </span>
                <span :style="{ marginRight: '16px' }">
                  <i class="fas fa-tags" :style="{ marginRight: '4px' }"></i>
                  {{ dataset.categories }} 个类别
                </span>
                <span>
                  <i class="fas fa-database" :style="{ marginRight: '4px' }"></i>
                  {{ dataset.size }}
                </span>
              </div>
              <p :style="{ margin: 0, fontSize: '13px', opacity: 0.75 }">{{ dataset.description }}</p>
            </div>
            <a-space>
              <a-button type="text" @click.stop="editDataset(dataset)" :style="{ padding: '4px 8px' }">
                <i class="fas fa-edit"></i>
              </a-button>
              <a-button type="text" danger @click.stop="deleteDataset(dataset)" :style="{ padding: '4px 8px' }">
                <i class="fas fa-trash"></i>
              </a-button>
            </a-space>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 训练配置 -->
    <a-card v-if="selectedDataset" title="训练配置" :style="{ marginBottom: '16px', borderRadius: '8px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :lg="12">
          <a-form layout="vertical" :style="{ marginBottom: 0 }">
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
          <a-form layout="vertical" :style="{ marginBottom: 0 }">
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

      <div :style="{ marginTop: '16px', display: 'flex', gap: '8px', flexWrap: 'wrap' }">
        <a-button type="primary" size="large" @click="startTraining" :loading="isTraining">
          <i class="fas fa-play"></i>
          开始训练
        </a-button>
        <a-button size="large" @click="saveConfig">
          <i class="fas fa-save"></i>
          保存配置
        </a-button>
        <a-button size="large" @click="loadConfig">
          <i class="fas fa-folder-open"></i>
          加载配置
        </a-button>
      </div>
    </a-card>

    <!-- 训练进度 -->
    <a-card v-if="isTraining || trainingHistory.length > 0" title="训练进度" :style="{ marginBottom: '16px', borderRadius: '8px' }">
      <div v-if="isTraining" :style="{ padding: '16px', marginBottom: '16px', borderRadius: '8px', background: '#f0f2f5' }">
        <div :style="{ marginBottom: '16px' }">
          <h4 :style="{ margin: '0 0 12px 0', fontSize: '16px', fontWeight: '600' }">{{ trainingConfig.modelName }}</h4>
          <a-row :gutter="[16, 16]">
            <a-col :xs="12" :sm="6">
              <div :style="{ fontSize: '13px', opacity: 0.65 }">当前轮次</div>
              <div :style="{ fontSize: '16px', fontWeight: '600' }">{{ currentProgress.currentEpoch }} / {{ trainingConfig.epochs }}</div>
            </a-col>
            <a-col :xs="12" :sm="6">
              <div :style="{ fontSize: '13px', opacity: 0.65 }">训练准确率</div>
              <div :style="{ fontSize: '16px', fontWeight: '600', color: '#52c41a' }">{{ currentProgress.trainAccuracy.toFixed(1) }}%</div>
            </a-col>
            <a-col :xs="12" :sm="6">
              <div :style="{ fontSize: '13px', opacity: 0.65 }">验证准确率</div>
              <div :style="{ fontSize: '16px', fontWeight: '600', color: '#1890ff' }">{{ currentProgress.valAccuracy.toFixed(1) }}%</div>
            </a-col>
            <a-col :xs="12" :sm="6">
              <div :style="{ fontSize: '13px', opacity: 0.65 }">预计剩余时间</div>
              <div :style="{ fontSize: '16px', fontWeight: '600' }">{{ currentProgress.estimatedTime }}</div>
            </a-col>
          </a-row>
        </div>
        
        <a-progress 
          :percent="Math.round((currentProgress.currentEpoch / trainingConfig.epochs) * 100)"
          :stroke-color="{ '0%': '#1890ff', '100%': '#52c41a' }"
          stroke-linecap="round"
          :style="{ marginBottom: '16px' }"
        />

        <div :style="{ display: 'flex', gap: '8px', flexWrap: 'wrap' }">
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
      <div v-if="trainingHistory.length > 0">
        <h4 :style="{ margin: '0 0 12px 0', fontSize: '15px', fontWeight: '600' }">训练历史</h4>
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
            <div :style="{ padding: '24px', textAlign: 'center' }">
              <div :style="{ fontSize: '48px', color: '#1890ff', marginBottom: '12px' }">
                <i class="fas fa-cloud-upload-alt"></i>
              </div>
              <p :style="{ margin: '0 0 8px 0', fontSize: '14px' }">点击或拖拽文件到此区域上传</p>
              <p :style="{ margin: 0, fontSize: '12px', opacity: 0.65 }">支持 ZIP、TAR、GZ 格式，最大 2GB</p>
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
    createTime: '2025-03-20 10:30'
  },
  {
    id: 2,
    modelName: '植物分类模型v1.0',
    dataset: '植物分类数据集',
    accuracy: '94.2%',
    trainTime: '1小时45分',
    status: '已完成',
    createTime: '2025-03-19 14:20'
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
