<template>
  <div class="page">
    <h1 class="page-title">数据分析</h1>
    
    <!-- Analytics Stats -->
    <a-row :gutter="[24, 24]" class="stats-grid">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="本月收入"
            :value="2345678"
            prefix="¥"
            :value-style="{ color: '#1890ff' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+25.3%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="本月订单"
            :value="15432"
            suffix="单"
            :value-style="{ color: '#52c41a' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+18.7%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="客户满意度"
            :value="89.5"
            suffix="%"
            :value-style="{ color: '#faad14' }"
          />
          <div class="stat-trend trend-up">
            <i class="fas fa-arrow-up"></i>
            <span>+3.2%</span>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="转化率"
            :value="67.8"
            suffix="%"
            :value-style="{ color: '#f5222d' }"
          />
          <div class="stat-trend trend-down">
            <i class="fas fa-arrow-down"></i>
            <span>-1.5%</span>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :lg="12">
        <a-card title="销售分析" class="chart-card">
          <div class="chart-placeholder">
            <i class="fas fa-chart-pie"></i>
            图表区域 - 销售分析图表
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="用户增长趋势" class="chart-card">
          <div class="chart-placeholder">
            <i class="fas fa-chart-area"></i>
            图表区域 - 用户增长趋势图
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :lg="12">
        <a-card title="产品销量排行" class="chart-card">
          <div class="chart-placeholder">
            <i class="fas fa-chart-bar"></i>
            图表区域 - 产品销量排行榜
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card title="地域分布" class="chart-card">
          <div class="chart-placeholder">
            <i class="fas fa-map-marked-alt"></i>
            图表区域 - 用户地域分布图
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
// 这个页面主要是展示数据分析图表
// 实际项目中会集成 ECharts 或其他图表库
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

/* Stats Grid */
.stats-grid {
  margin-bottom: 24px;
}

.stat-card {
  /* 移除左边框 */
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  font-size: 12px;
}

.trend-up {
  color: #52c41a;
}

.trend-down {
  color: #ff4d4f;
}

/* Charts */
.chart-card {
  margin-bottom: 24px;
}

.chart-placeholder {
  height: 300px;
  background: #f5f5f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-size: 16px;
  gap: 8px;
}
</style>
