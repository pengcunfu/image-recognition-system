<template>
  <div :style="{ padding: '24px' }">
    <!-- 页面标题 -->
    <a-card :style="{ marginBottom: '16px', borderRadius: '8px', textAlign: 'center' }">
      <h1 :style="{ fontSize: '24px', fontWeight: '600', margin: '0 0 8px 0' }">
        <CrownOutlined :style="{ color: '#ffd700', marginRight: '8px' }" />
        成为VIP会员
      </h1>
      <p :style="{ fontSize: '14px', opacity: 0.65, margin: 0 }">解锁更多高级功能，享受专属特权</p>
    </a-card>

    <!-- 用户余额显示 -->
    <a-card :style="{ maxWidth: '1200px', margin: '0 auto 16px', textAlign: 'center', background: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)', borderRadius: '8px' }">
      <div :style="{ color: 'white' }">
        <p :style="{ fontSize: '13px', marginBottom: '6px', opacity: 0.9 }">我的余额</p>
        <p :style="{ fontSize: '28px', fontWeight: 'bold', margin: 0 }">
          ¥ {{ userBalance.toFixed(2) }}
        </p>
        <a-button type="text" size="small" :style="{ color: 'white', marginTop: '8px', textDecoration: 'underline' }" @click="showRechargeInfo">
          如何充值？
        </a-button>
      </div>
    </a-card>

    <!-- VIP套餐卡片 -->
    <div :style="{ maxWidth: '1200px', margin: '0 auto' }">
      <a-row :gutter="16">
        <!-- 7天体验卡 -->
        <a-col :span="8">
          <a-card 
            hoverable
            :style="{ 
              borderRadius: '8px', 
              boxShadow: '0 4px 12px rgba(0,0,0,0.1)', 
              transition: 'all 0.3s',
              border: selectedPlan === 0 ? '2px solid #1890ff' : '1px solid #d9d9d9',
              height: '100%',
              display: 'flex',
              flexDirection: 'column'
            }"
            :bodyStyle="{ flex: 1, display: 'flex', flexDirection: 'column' }"
            @click="selectPlan(0)"
          >
            <div :style="{ textAlign: 'center', display: 'flex', flexDirection: 'column', height: '100%' }">
              <div :style="{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff', marginBottom: '16px' }">
                体验套餐
              </div>
              <div :style="{ fontSize: '48px', fontWeight: 'bold', color: '#333', marginBottom: '8px' }">
                ¥9.9
              </div>
              <div :style="{ fontSize: '14px', color: '#999', marginBottom: '24px' }">7天VIP</div>
              
              <a-divider />
              
              <div :style="{ textAlign: 'left', padding: '0 12px', flex: 1 }">
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>无限次图像识别</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>高级识别功能</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>批量处理</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>数据分析报告</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>AI模型训练</span>
                </div>
              </div>
              
              <a-button 
                type="primary" 
                size="large" 
                block 
                :style="{ height: '48px', fontSize: '16px', fontWeight: 'bold' }"
                @click="handlePurchase(0)"
                :loading="purchasing && selectedPlan === 0"
              >
                立即开通
              </a-button>
            </div>
          </a-card>
        </a-col>

        <!-- 月度会员 -->
        <a-col :span="8">
          <a-card 
            hoverable
            :style="{ 
              borderRadius: '8px', 
              boxShadow: selectedPlan === 1 ? '0 8px 24px rgba(24,144,255,0.3)' : '0 4px 12px rgba(0,0,0,0.1)', 
              transition: 'all 0.3s',
              border: selectedPlan === 1 ? '2px solid #1890ff' : '1px solid #d9d9d9',
              position: 'relative',
              height: '100%',
              display: 'flex',
              flexDirection: 'column'
            }"
            :bodyStyle="{ flex: 1, display: 'flex', flexDirection: 'column' }"
            @click="selectPlan(1)"
          >
            <div :style="{ position: 'absolute', top: '-12px', right: '20px', background: '#ff4d4f', color: 'white', padding: '4px 16px', borderRadius: '8px', fontSize: '12px', fontWeight: 'bold' }">
              推荐
            </div>
            <div :style="{ textAlign: 'center', display: 'flex', flexDirection: 'column', height: '100%' }">
              <div :style="{ fontSize: '24px', fontWeight: 'bold', color: '#1890ff', marginBottom: '16px' }">
                月度会员
              </div>
              <div :style="{ fontSize: '48px', fontWeight: 'bold', color: '#333', marginBottom: '8px' }">
                ¥29.9
              </div>
              <div :style="{ fontSize: '14px', color: '#999', marginBottom: '24px' }">30天VIP</div>
              
              <a-divider />
              
              <div :style="{ textAlign: 'left', padding: '0 12px', flex: 1 }">
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>无限次图像识别</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>高级识别功能</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>批量处理</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>数据分析报告</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>AI模型训练</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>优先客服支持</span>
                </div>
              </div>
              
              <a-button 
                type="primary" 
                size="large" 
                block 
                :style="{ height: '48px', fontSize: '16px', fontWeight: 'bold' }"
                @click="handlePurchase(1)"
                :loading="purchasing && selectedPlan === 1"
              >
                立即开通
              </a-button>
            </div>
          </a-card>
        </a-col>

        <!-- 年度会员 -->
        <a-col :span="8">
          <a-card 
            hoverable
            :style="{ 
              borderRadius: '8px', 
              boxShadow: '0 4px 12px rgba(0,0,0,0.1)', 
              transition: 'all 0.3s',
              border: selectedPlan === 2 ? '2px solid #1890ff' : '1px solid #d9d9d9',
              position: 'relative',
              height: '100%',
              display: 'flex',
              flexDirection: 'column'
            }"
            :bodyStyle="{ flex: 1, display: 'flex', flexDirection: 'column' }"
            @click="selectPlan(2)"
          >
            <div :style="{ position: 'absolute', top: '-12px', right: '20px', background: '#ffd700', color: '#333', padding: '4px 16px', borderRadius: '8px', fontSize: '12px', fontWeight: 'bold' }">
              最划算
            </div>
            <div :style="{ textAlign: 'center', display: 'flex', flexDirection: 'column', height: '100%' }">
              <div :style="{ fontSize: '24px', fontWeight: 'bold', color: '#ffd700', marginBottom: '16px' }">
                年度会员
              </div>
              <div :style="{ fontSize: '48px', fontWeight: 'bold', color: '#333', marginBottom: '8px' }">
                ¥199
              </div>
              <div :style="{ fontSize: '14px', color: '#999', marginBottom: '4px' }">365天VIP</div>
              <div :style="{ fontSize: '12px', color: '#52c41a', marginBottom: '20px' }">
                <TagOutlined /> 相当于每月仅需 ¥16.6
              </div>
              
              <a-divider />
              
              <div :style="{ textAlign: 'left', padding: '0 12px', flex: 1 }">
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>无限次图像识别</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>高级识别功能</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>批量处理</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>数据分析报告</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>AI模型训练</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>优先客服支持</span>
                </div>
                <div :style="{ marginBottom: '12px', display: 'flex', alignItems: 'center' }">
                  <CheckOutlined :style="{ color: '#52c41a', marginRight: '8px' }" />
                  <span>专属数据存储</span>
                </div>
              </div>
              
              <a-button 
                type="primary" 
                size="large" 
                block 
                :style="{ height: '48px', fontSize: '16px', fontWeight: 'bold' }"
                @click="handlePurchase(2)"
                :loading="purchasing && selectedPlan === 2"
              >
                立即开通
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- VIP特权说明 -->
    <div :style="{ maxWidth: '1200px', margin: '48px auto 0' }">
      <a-card :style="{ borderRadius: '8px' }">
        <h2 :style="{ fontSize: '24px', fontWeight: 'bold', marginBottom: '24px', textAlign: 'center' }">
          VIP会员特权
        </h2>
        <a-row :gutter="24">
          <a-col :span="8">
            <div :style="{ textAlign: 'center', padding: '24px' }">
              <ThunderboltOutlined :style="{ fontSize: '48px', color: '#1890ff', marginBottom: '16px' }" />
              <h3 :style="{ fontSize: '18px', fontWeight: 'bold', marginBottom: '12px' }">无限次识别</h3>
              <p :style="{ color: '#666', fontSize: '14px' }">突破识别次数限制，随时随地进行图像识别，满足您的各种需求</p>
            </div>
          </a-col>
          <a-col :span="8">
            <div :style="{ textAlign: 'center', padding: '24px' }">
              <RobotOutlined :style="{ fontSize: '48px', color: '#52c41a', marginBottom: '16px' }" />
              <h3 :style="{ fontSize: '18px', fontWeight: 'bold', marginBottom: '12px' }">AI模型训练</h3>
              <p :style="{ color: '#666', fontSize: '14px' }">使用您的数据训练专属AI模型，提升识别准确度</p>
            </div>
          </a-col>
          <a-col :span="8">
            <div :style="{ textAlign: 'center', padding: '24px' }">
              <LineChartOutlined :style="{ fontSize: '48px', color: '#ffd700', marginBottom: '16px' }" />
              <h3 :style="{ fontSize: '18px', fontWeight: 'bold', marginBottom: '12px' }">数据分析</h3>
              <p :style="{ color: '#666', fontSize: '14px' }">获取详细的数据分析报告，洞察识别趋势和模式</p>
            </div>
          </a-col>
        </a-row>
      </a-card>
    </div>

    <!-- 充值说明模态框 -->
    <a-modal 
      v-model:open="rechargeInfoVisible" 
      title="如何充值余额" 
      :footer="null"
      :width="600"
    >
      <div :style="{ padding: '16px' }">
        <a-alert 
          message="余额充值说明" 
          description="当前系统余额需要通过管理员充值。请联系系统管理员为您的账户充值。"
          type="info" 
          show-icon 
          :style="{ marginBottom: '16px' }"
        />
        <div :style="{ background: '#f5f5f5', padding: '16px', borderRadius: '8px' }">
          <p :style="{ margin: 0, fontWeight: 'bold', marginBottom: '12px' }">联系方式：</p>
          <p :style="{ margin: 0, marginBottom: '8px' }">📧 邮箱: admin@example.com</p>
          <p :style="{ margin: 0, marginBottom: '8px' }">📞 电话: 400-123-4567</p>
          <p :style="{ margin: 0 }">💬 在线客服: 工作日 9:00-18:00</p>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { 
  CrownOutlined, 
  CheckOutlined, 
  TagOutlined, 
  ThunderboltOutlined, 
  RobotOutlined, 
  LineChartOutlined 
} from '@ant-design/icons-vue'
import { UserAPI } from '@/api/user'
import VipAPI from '@/api/vip'

const router = useRouter()

// 用户余额
const userBalance = ref(0)

// 选中的套餐
const selectedPlan = ref<number | null>(null)

// 购买中状态
const purchasing = ref(false)

// 充值说明弹窗
const rechargeInfoVisible = ref(false)

// 套餐配置
const plans = [
  { type: 0, name: '体验套餐', days: 7, price: 9.9 },
  { type: 1, name: '月度会员', days: 30, price: 29.9 },
  { type: 2, name: '年度会员', days: 365, price: 199 }
]

// 加载用户信息
async function loadUserInfo() {
  try {
    const profile = await UserAPI.getProfile()
    userBalance.value = profile.balance || 0
  } catch (error) {
    console.error('加载用户信息失败:', error)
    message.error('加载用户信息失败')
  }
}

// 选择套餐
function selectPlan(planType: number) {
  selectedPlan.value = planType
}

// 显示充值说明
function showRechargeInfo() {
  rechargeInfoVisible.value = true
}

// 购买VIP
async function handlePurchase(planType: number) {
  const plan = plans[planType]
  
  // 检查余额
  if (userBalance.value < plan.price) {
    message.warning('余额不足，请先充值')
    rechargeInfoVisible.value = true
    return
  }

  try {
    purchasing.value = true
    selectedPlan.value = planType

    // 创建订单
    const order = await VipAPI.createOrder({ planType })
    
    // 支付订单
    await VipAPI.payOrder(order.orderNo)
    
    message.success('VIP开通成功！')
    
    // 刷新用户信息（包括余额和角色）
    await loadUserInfo()
    
    // 更新localStorage中的用户角色，以便立即生效
    localStorage.setItem('userRole', '1') // 1 = VIP
    
    // 延迟跳转到用户中心
    setTimeout(() => {
      router.push('/user/dashboard')
    }, 1500)
  } catch (error: any) {
    console.error('购买VIP失败:', error)
    message.error(error.message || '购买失败，请稍后重试')
  } finally {
    purchasing.value = false
  }
}

// 页面加载时获取用户信息
onMounted(() => {
  loadUserInfo()
})
</script>

