import { get } from '@/utils/request'

/**
 * VIP统计分析数据
 */
export interface VipAnalytics {
  totalRecognitions: number
  averageAccuracy: number
  avgProcessTime: number
  costSaved: number
  growthRate: number
  accuracyImprovement: number
  speedImprovement: number
  daysAnalyzed: number
}

/**
 * VIP趋势数据点
 */
export interface VipTrendPoint {
  date: string
  recognitions: number
  accuracy: number
  avgTime: number
}

/**
 * VIP识别趋势
 */
export interface VipTrends {
  dailyTrends: VipTrendPoint[]
  dates: string[]
  recognitionCounts: number[]
  accuracyTrends: number[]
  timeTrends: number[]
}

/**
 * VIP分类分析项
 */
export interface VipCategoryItem {
  category: string
  count: number
  avgAccuracy: number
  avgTime: number
  percentage: number
}

/**
 * VIP分类分析
 */
export interface VipCategoryAnalysis {
  categories: VipCategoryItem[]
  topCategory: string
  mostAccurateCategory: string
  fastestCategory: string
}

/**
 * VIP性能数据点
 */
export interface VipPerformancePoint {
  date: string
  accuracy: number
  avgTime: number
  count: number
}

/**
 * VIP性能分析
 */
export interface VipPerformanceAnalysis {
  currentAccuracy: number
  previousAccuracy: number
  currentAvgTime: number
  previousAvgTime: number
  accuracyTrend: number
  speedTrend: number
  performanceHistory: VipPerformancePoint[]
}

/**
 * VIP智能建议项
 */
export interface VipSuggestionItem {
  id: string
  type: string
  title: string
  description: string
  impact: string
  icon: string
  priority: number
  applicable: boolean
}

/**
 * VIP智能建议
 */
export interface VipSuggestions {
  suggestions: VipSuggestionItem[]
  totalSuggestions: number
  highPrioritySuggestions: number
}

/**
 * 统计API模块
 */
export class StatsAPI {
  /**
   * 获取VIP用户统计分析数据
   */
  static getVipAnalytics(days: number = 30) {
    return get<VipAnalytics>('/api/stats/vip/analytics', { days })
  }

  /**
   * 获取VIP用户识别趋势数据
   */
  static getVipTrends(days: number = 30) {
    return get<VipTrends>('/api/stats/vip/trends', { days })
  }

  /**
   * 获取VIP用户分类分析数据
   */
  static getVipCategoryAnalysis(days: number = 30) {
    return get<VipCategoryAnalysis>('/api/stats/vip/categories', { days })
  }

  /**
   * 获取VIP用户性能分析数据
   */
  static getVipPerformanceAnalysis(days: number = 30) {
    return get<VipPerformanceAnalysis>('/api/stats/vip/performance', { days })
  }

  /**
   * 获取VIP用户智能建议
   */
  static getVipSuggestions() {
    return get<VipSuggestions>('/api/stats/vip/suggestions')
  }
}

// 导出默认实例
export default StatsAPI
