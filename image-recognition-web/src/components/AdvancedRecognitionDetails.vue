<template>
  <div v-if="data" :style="{ display: 'flex', flexDirection: 'column', gap: '16px' }">
    <!-- 基本信息 -->
    <a-card title="基本信息" size="small" :style="{ borderRadius: '6px' }">
      <a-descriptions :column="2" size="small">
        <a-descriptions-item label="分类">{{ data.category }}</a-descriptions-item>
        <a-descriptions-item label="名称">{{ data.name }}</a-descriptions-item>
        <a-descriptions-item label="置信度">{{ (data.confidence * 100).toFixed(1) }}%</a-descriptions-item>
        <a-descriptions-item v-if="data.basic_info?.color" label="颜色">{{ data.basic_info.color }}</a-descriptions-item>
        <a-descriptions-item v-if="data.basic_info?.shape" label="形状">{{ data.basic_info.shape }}</a-descriptions-item>
        <a-descriptions-item v-if="data.basic_info?.size" label="尺寸">{{ data.basic_info.size }}</a-descriptions-item>
        <a-descriptions-item v-if="data.basic_info?.material" label="材质">{{ data.basic_info.material }}</a-descriptions-item>
      </a-descriptions>
    </a-card>

    <!-- 详细信息 - 动物类 -->
    <a-card v-if="isAnimal" title="动物详细信息" size="small" :style="{ borderRadius: '6px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :md="12">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item v-if="data.detailed_info?.species" label="品种/种类">
              {{ data.detailed_info.species }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.habitat" label="栖息环境">
              {{ data.detailed_info.habitat }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.lifespan" label="寿命">
              {{ data.detailed_info.lifespan }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.temperament" label="性格特点">
              {{ data.detailed_info.temperament }}
            </a-descriptions-item>
          </a-descriptions>
        </a-col>
        <a-col :xs="24" :md="12">
          <div v-if="data.detailed_info?.characteristics?.length" :style="{ marginBottom: '16px' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">特征描述</h4>
            <div :style="{ display: 'flex', flexWrap: 'wrap', gap: '6px' }">
              <a-tag v-for="char in data.detailed_info.characteristics" :key="char" color="blue">
                {{ char }}
              </a-tag>
            </div>
          </div>
          <div v-if="data.detailed_info?.diet?.length" :style="{ marginBottom: '16px' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">主要食物</h4>
            <div :style="{ display: 'flex', flexWrap: 'wrap', gap: '6px' }">
              <a-tag v-for="food in data.detailed_info.diet" :key="food" color="green">
                {{ food }}
              </a-tag>
            </div>
          </div>
          <div v-if="data.detailed_info?.care_tips?.length">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">饲养要点</h4>
            <ul :style="{ margin: 0, paddingLeft: '20px', fontSize: '13px' }">
              <li v-for="tip in data.detailed_info.care_tips" :key="tip">{{ tip }}</li>
            </ul>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 详细信息 - 植物类 -->
    <a-card v-if="isPlant" title="植物详细信息" size="small" :style="{ borderRadius: '6px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :md="12">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item v-if="data.detailed_info?.botanical_name" label="学名">
              {{ data.detailed_info.botanical_name }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.growth_conditions" label="生长条件">
              {{ data.detailed_info.growth_conditions }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.flowering_season" label="花期">
              {{ data.detailed_info.flowering_season }}
            </a-descriptions-item>
          </a-descriptions>
        </a-col>
        <a-col :xs="24" :md="12">
          <div v-if="data.detailed_info?.care_instructions?.length" :style="{ marginBottom: '16px' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">养护方法</h4>
            <ul :style="{ margin: 0, paddingLeft: '20px', fontSize: '13px' }">
              <li v-for="instruction in data.detailed_info.care_instructions" :key="instruction">{{ instruction }}</li>
            </ul>
          </div>
          <div v-if="data.detailed_info?.uses?.length">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">用途</h4>
            <div :style="{ display: 'flex', flexWrap: 'wrap', gap: '6px' }">
              <a-tag v-for="use in data.detailed_info.uses" :key="use" color="purple">
                {{ use }}
              </a-tag>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 详细信息 - 交通工具类 -->
    <a-card v-if="isVehicle" title="交通工具详细信息" size="small" :style="{ borderRadius: '6px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :md="12">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item v-if="data.detailed_info?.brand" label="品牌">
              {{ data.detailed_info.brand }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.model" label="型号">
              {{ data.detailed_info.model }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.price_range" label="价格区间">
              {{ data.detailed_info.price_range }}
            </a-descriptions-item>
          </a-descriptions>
          <div v-if="data.detailed_info?.specifications" :style="{ marginTop: '16px' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">规格参数</h4>
            <a-descriptions :column="1" size="small">
              <a-descriptions-item v-if="data.detailed_info.specifications.engine" label="发动机">
                {{ data.detailed_info.specifications.engine }}
              </a-descriptions-item>
              <a-descriptions-item v-if="data.detailed_info.specifications.fuel_type" label="燃料类型">
                {{ data.detailed_info.specifications.fuel_type }}
              </a-descriptions-item>
              <a-descriptions-item v-if="data.detailed_info.specifications.capacity" label="容量">
                {{ data.detailed_info.specifications.capacity }}
              </a-descriptions-item>
            </a-descriptions>
          </div>
        </a-col>
        <a-col :xs="24" :md="12">
          <div v-if="data.detailed_info?.features?.length">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">特色功能</h4>
            <div :style="{ display: 'flex', flexWrap: 'wrap', gap: '6px' }">
              <a-tag v-for="feature in data.detailed_info.features" :key="feature" color="orange">
                {{ feature }}
              </a-tag>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 详细信息 - 食物类 -->
    <a-card v-if="isFood" title="食物详细信息" size="small" :style="{ borderRadius: '6px' }">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :md="12">
          <a-descriptions :column="1" size="small">
            <a-descriptions-item v-if="data.detailed_info?.cuisine_type" label="菜系类型">
              {{ data.detailed_info.cuisine_type }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.cooking_method" label="制作方法">
              {{ data.detailed_info.cooking_method }}
            </a-descriptions-item>
            <a-descriptions-item v-if="data.detailed_info?.origin" label="起源地">
              {{ data.detailed_info.origin }}
            </a-descriptions-item>
          </a-descriptions>
          <div v-if="data.detailed_info?.ingredients?.length" :style="{ marginTop: '16px' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">主要食材</h4>
            <div :style="{ display: 'flex', flexWrap: 'wrap', gap: '6px' }">
              <a-tag v-for="ingredient in data.detailed_info.ingredients" :key="ingredient" color="cyan">
                {{ ingredient }}
              </a-tag>
            </div>
          </div>
        </a-col>
        <a-col :xs="24" :md="12">
          <div v-if="data.detailed_info?.nutrition" :style="{ marginBottom: '16px' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">营养信息</h4>
            <a-descriptions :column="1" size="small">
              <a-descriptions-item v-if="data.detailed_info.nutrition.calories" label="热量">
                {{ data.detailed_info.nutrition.calories }}
              </a-descriptions-item>
              <a-descriptions-item v-if="data.detailed_info.nutrition.protein" label="蛋白质">
                {{ data.detailed_info.nutrition.protein }}
              </a-descriptions-item>
            </a-descriptions>
            <div v-if="data.detailed_info.nutrition.vitamins?.length" :style="{ marginTop: '8px' }">
              <span :style="{ fontSize: '13px', fontWeight: '500', marginRight: '8px' }">维生素：</span>
              <a-tag v-for="vitamin in data.detailed_info.nutrition.vitamins" :key="vitamin" size="small" color="lime">
                {{ vitamin }}
              </a-tag>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 扩展功能分析结果 -->
    <a-card v-if="hasExtendedFeatures" title="扩展功能分析" size="small" :style="{ borderRadius: '6px' }">
      <a-row :gutter="[16, 16]">
        <!-- 文字识别 -->
        <a-col v-if="data.detailed_info?.text_content" :xs="24" :md="12">
          <div :style="{ padding: '12px', background: '#f6ffed', borderRadius: '6px', border: '1px solid #b7eb8f' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600', color: '#52c41a' }">
              <i class="fas fa-font" :style="{ marginRight: '6px' }"></i>
              文字识别
            </h4>
            <div :style="{ fontSize: '13px', lineHeight: '1.6' }">{{ data.detailed_info.text_content }}</div>
          </div>
        </a-col>

        <!-- 人脸分析 -->
        <a-col v-if="data.detailed_info?.face_analysis" :xs="24" :md="12">
          <div :style="{ padding: '12px', background: '#f6ffed', borderRadius: '6px', border: '1px solid #b7eb8f' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600', color: '#52c41a' }">
              <i class="fas fa-user" :style="{ marginRight: '6px' }"></i>
              人脸分析
            </h4>
            <div :style="{ fontSize: '13px', lineHeight: '1.6' }">{{ data.detailed_info.face_analysis }}</div>
          </div>
        </a-col>

        <!-- 情感分析 -->
        <a-col v-if="data.detailed_info?.emotion_analysis" :xs="24" :md="12">
          <div :style="{ padding: '12px', background: '#fff7e6', borderRadius: '6px', border: '1px solid #ffd591' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600', color: '#faad14' }">
              <i class="fas fa-smile" :style="{ marginRight: '6px' }"></i>
              情感分析
            </h4>
            <div :style="{ fontSize: '13px', lineHeight: '1.6' }">{{ data.detailed_info.emotion_analysis }}</div>
          </div>
        </a-col>

        <!-- 色彩分析 -->
        <a-col v-if="data.detailed_info?.color_analysis" :xs="24" :md="12">
          <div :style="{ padding: '12px', background: '#f9f0ff', borderRadius: '6px', border: '1px solid #d3adf7' }">
            <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600', color: '#722ed1' }">
              <i class="fas fa-palette" :style="{ marginRight: '6px' }"></i>
              色彩分析
            </h4>
            <div :style="{ fontSize: '13px', lineHeight: '1.6' }">{{ data.detailed_info.color_analysis }}</div>
          </div>
        </a-col>
      </a-row>
    </a-card>

    <!-- 环境信息 -->
    <a-card v-if="data.environment" title="环境信息" size="small" :style="{ borderRadius: '6px' }">
      <a-descriptions :column="2" size="small">
        <a-descriptions-item v-if="data.environment.background" label="背景">
          {{ data.environment.background }}
        </a-descriptions-item>
        <a-descriptions-item v-if="data.environment.lighting" label="光线">
          {{ data.environment.lighting }}
        </a-descriptions-item>
        <a-descriptions-item v-if="data.environment.setting" label="场景">
          {{ data.environment.setting }}
        </a-descriptions-item>
      </a-descriptions>
    </a-card>

    <!-- 技术分析 -->
    <a-card v-if="data.technical_analysis" title="技术分析" size="small" :style="{ borderRadius: '6px' }">
      <a-descriptions :column="2" size="small">
        <a-descriptions-item v-if="data.technical_analysis.image_quality" label="图像质量">
          {{ data.technical_analysis.image_quality }}
        </a-descriptions-item>
        <a-descriptions-item v-if="data.technical_analysis.composition" label="构图">
          {{ data.technical_analysis.composition }}
        </a-descriptions-item>
        <a-descriptions-item v-if="data.technical_analysis.perspective" label="视角">
          {{ data.technical_analysis.perspective }}
        </a-descriptions-item>
        <a-descriptions-item v-if="data.technical_analysis.focus" label="焦点">
          {{ data.technical_analysis.focus }}
        </a-descriptions-item>
      </a-descriptions>
    </a-card>

    <!-- 可能的其他结果 -->
    <a-card v-if="data.alternatives?.length" title="可能的其他结果" size="small" :style="{ borderRadius: '6px' }">
      <div :style="{ display: 'flex', flexDirection: 'column', gap: '8px' }">
        <div 
          v-for="alt in data.alternatives" 
          :key="alt.name"
          :style="{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 12px', background: '#fafafa', borderRadius: '4px' }"
        >
          <div>
            <span :style="{ fontWeight: '500' }">{{ alt.name }}</span>
            <span v-if="alt.reason" :style="{ fontSize: '12px', color: '#666', marginLeft: '8px' }">
              ({{ alt.reason }})
            </span>
          </div>
          <a-tag color="blue">{{ (alt.confidence * 100).toFixed(1) }}%</a-tag>
        </div>
      </div>
    </a-card>

    <!-- 标签和描述 -->
    <a-card v-if="data.tags?.length || data.description" title="总结信息" size="small" :style="{ borderRadius: '6px' }">
      <div v-if="data.tags?.length" :style="{ marginBottom: '16px' }">
        <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">相关标签</h4>
        <div :style="{ display: 'flex', flexWrap: 'wrap', gap: '6px' }">
          <a-tag v-for="tag in data.tags" :key="tag" color="geekblue">
            {{ tag }}
          </a-tag>
        </div>
      </div>
      <div v-if="data.description">
        <h4 :style="{ margin: '0 0 8px 0', fontSize: '14px', fontWeight: '600' }">详细描述</h4>
        <div :style="{ fontSize: '13px', lineHeight: '1.6', color: '#666' }">{{ data.description }}</div>
      </div>
    </a-card>
  </div>
  <div v-else :style="{ textAlign: 'center', padding: '40px 0', color: '#999' }">
    <i class="fas fa-exclamation-triangle" :style="{ fontSize: '48px', marginBottom: '16px' }"></i>
    <div>数据解析失败</div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  data: any
}

const props = defineProps<Props>()

// 判断对象类型
const isAnimal = computed(() => {
  if (!props.data?.category) return false
  const category = props.data.category.toLowerCase()
  return category.includes('动物') || category.includes('猫') || category.includes('狗') || 
         category.includes('鸟') || category.includes('鱼') || category.includes('兔') ||
         props.data.detailed_info?.species || props.data.detailed_info?.habitat
})

const isPlant = computed(() => {
  if (!props.data?.category) return false
  const category = props.data.category.toLowerCase()
  return category.includes('植物') || category.includes('花') || category.includes('树') ||
         props.data.detailed_info?.botanical_name || props.data.detailed_info?.flowering_season
})

const isVehicle = computed(() => {
  if (!props.data?.category) return false
  const category = props.data.category.toLowerCase()
  return category.includes('车') || category.includes('汽车') || category.includes('飞机') ||
         category.includes('船') || category.includes('交通') ||
         props.data.detailed_info?.brand || props.data.detailed_info?.specifications
})

const isFood = computed(() => {
  if (!props.data?.category) return false
  const category = props.data.category.toLowerCase()
  return category.includes('食物') || category.includes('菜') || category.includes('水果') ||
         category.includes('饮料') || category.includes('食品') ||
         props.data.detailed_info?.cuisine_type || props.data.detailed_info?.ingredients
})

const hasExtendedFeatures = computed(() => {
  return props.data?.detailed_info?.text_content ||
         props.data?.detailed_info?.face_analysis ||
         props.data?.detailed_info?.emotion_analysis ||
         props.data?.detailed_info?.color_analysis
})
</script>
