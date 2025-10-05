<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">产品管理</h1>
      <a-button type="primary" @click="addProduct">
        <template #icon>
          <i class="fas fa-plus"></i>
        </template>
        添加产品
      </a-button>
    </div>
    
    <a-card class="table-card">
      <a-table 
        :columns="productColumns" 
        :data-source="products"
        :pagination="{ pageSize: 10 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag 
              :color="record.stock > 0 ? 'success' : 'error'"
              class="status-tag"
            >
              {{ record.stock > 0 ? '上架' : '缺货' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="editProduct(record)">
                编辑
              </a-button>
              <a-button danger size="small" @click="deleteProduct(record)">
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'

// 表格列定义
const productColumns = [
  { title: '产品名称', dataIndex: 'name', key: 'name' },
  { title: '分类', dataIndex: 'category', key: 'category' },
  { title: '价格', dataIndex: 'price', key: 'price' },
  { title: '库存', dataIndex: 'stock', key: 'stock' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action' }
]

// 产品数据
const products = ref([
  { name: '智能图像识别摄像头', category: '硬件设备', price: '¥8,999', stock: 156 },
  { name: 'AI视觉分析模块', category: '硬件模块', price: '¥12,999', stock: 89 },
  { name: '图像处理服务包', category: '软件服务', price: '¥4,599', stock: 0 },
  { name: '深度学习训练平台', category: '云服务', price: '¥15,999', stock: 25 },
  { name: '边缘计算设备', category: '硬件设备', price: '¥6,888', stock: 78 }
])

// 方法
function addProduct() {
  message.info('添加产品功能开发中')
}

function editProduct(record: any) {
  message.info(`编辑产品：${record.name}`)
}

function deleteProduct(record: any) {
  message.info(`删除产品：${record.name}`)
}
</script>

<style scoped>
.page {
  width: 100%;
}

.page-title {
  margin-bottom: 24px;
  color: #262626;
  font-size: 24px;
  font-weight: 600;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.table-card {
  margin-bottom: 24px;
}

.status-tag {
  font-size: 12px;
  font-weight: 500;
}

/* Responsive */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
}
</style>
