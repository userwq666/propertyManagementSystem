<template>
  <div class="statistics-dashboard">
    <!-- 顶部概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="12" :sm="8" :md="4" v-for="card in overviewCards" :key="card.key">
        <el-card shadow="hover" class="overview-card" :body-style="{ padding: '20px' }">
          <div class="card-content">
            <div class="card-info">
              <div class="card-label">{{ card.label }}</div>
              <div class="card-value" :style="{ color: card.color }">{{ card.value }}</div>
            </div>
            <div class="card-icon" :style="{ backgroundColor: card.bgColor }">
              <el-icon :style="{ color: card.color }">
                <component :is="card.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 时间范围选择器 -->
    <div class="time-range-selector">
      <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
        <el-radio-button
          v-for="option in timeRangeOptions"
          :key="option.value"
          :label="option.value"
        >
          {{ option.label }}
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 费用统计模块 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">月度收费趋势</span>
          </template>
          <div ref="feeTrendChartRef" class="chart-container" v-loading="feeTrendLoading"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">收费项目占比</span>
          </template>
          <div ref="feeItemRatioChartRef" class="chart-container" v-loading="feeItemRatioLoading"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 报修统计模块 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">月度报修趋势</span>
          </template>
          <div ref="repairTrendChartRef" class="chart-container" v-loading="repairTrendLoading"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">报修类型占比</span>
          </template>
          <div ref="repairTypeRatioChartRef" class="chart-container" v-loading="repairTypeRatioLoading"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 设备统计模块 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">设备状态分布</span>
          </template>
          <div ref="deviceStatusChartRef" class="chart-container" v-loading="deviceStatusLoading"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">维保到期预警</span>
          </template>
          <div class="table-container" v-loading="maintenanceWarningLoading">
            <el-table :data="maintenanceWarnings" border style="width: 100%" max-height="300">
              <el-table-column prop="deviceName" label="设备名称" show-overflow-tooltip />
              <el-table-column prop="deviceCode" label="设备编号" width="120" />
              <el-table-column prop="location" label="位置" show-overflow-tooltip />
              <el-table-column prop="maintenanceDate" label="维保日期" width="120" />
              <el-table-column prop="daysRemaining" label="剩余天数" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getDaysRemainingType(row.daysRemaining)" size="small">
                    {{ row.daysRemaining }}天
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 满意度统计模块 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">投诉满意度评分趋势</span>
          </template>
          <div ref="satisfactionTrendChartRef" class="chart-container" v-loading="satisfactionTrendLoading"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">投诉类型分布</span>
          </template>
          <div ref="complaintTypeChartRef" class="chart-container" v-loading="complaintTypeLoading"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 巡检统计模块 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">巡检完成率趋势</span>
          </template>
          <div ref="inspectionCompletionChartRef" class="chart-container" v-loading="inspectionCompletionLoading"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="card-title">异常率统计</span>
          </template>
          <div ref="inspectionAbnormalChartRef" class="chart-container" v-loading="inspectionAbnormalLoading"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { getOverview, getMonthlyFeeStatistics, getFeeByItem, getRepairOverview, getRepairByType, getFeeTrend, getRepairTrend, getRepairTypeRatio, getDeviceStatus, getMaintenanceWarning, getSatisfactionTrend, getComplaintTypeRatio, getInspectionCompletion, getInspectionAbnormal } from '@/api/statistics/dashboard'

const timeRange = ref('30d')
const timeRangeOptions = TIME_RANGE_OPTIONS

const overviewCards = ref([
  { key: 'buildings', label: '总楼栋数', value: 0, icon: OfficeBuilding, color: '#409EFF', bgColor: '#ecf5ff' },
  { key: 'houses', label: '总房屋数', value: 0, icon: House, color: '#67C23A', bgColor: '#f0f9eb' },
  { key: 'owners', label: '总业主数', value: 0, icon: User, color: '#E6A23C', bgColor: '#fdf6ec' },
  { key: 'feeAmount', label: '总收费金额', value: '¥0', icon: Money, color: '#F56C6C', bgColor: '#fef0f0' },
  { key: 'devices', label: '总设备数', value: 0, icon: Cpu, color: '#909399', bgColor: '#f4f4f5' },
  { key: 'monthlyRepairs', label: '本月报修数', value: 0, icon: Warning, color: '#626AEA', bgColor: '#f0f0ff' }
])

const feeTrendChartRef = ref(null)
const feeItemRatioChartRef = ref(null)
const repairTrendChartRef = ref(null)
const repairTypeRatioChartRef = ref(null)
const deviceStatusChartRef = ref(null)
const satisfactionTrendChartRef = ref(null)
const complaintTypeChartRef = ref(null)
const inspectionCompletionChartRef = ref(null)
const inspectionAbnormalChartRef = ref(null)

const feeTrendLoading = ref(false)
const feeItemRatioLoading = ref(false)
const repairTrendLoading = ref(false)
const repairTypeRatioLoading = ref(false)
const deviceStatusLoading = ref(false)
const maintenanceWarningLoading = ref(false)
const satisfactionTrendLoading = ref(false)
const complaintTypeLoading = ref(false)
const inspectionCompletionLoading = ref(false)
const inspectionAbnormalLoading = ref(false)

const maintenanceWarnings = ref([])

let charts = []

const initChart = (domRef, option) => {
  if (!domRef) return null
  const chart = echarts.init(domRef)
  chart.setOption(option)
  charts.push(chart)
  return chart
}

const handleResize = () => {
  charts.forEach(chart => chart?.resize())
}

const handleTimeRangeChange = () => {
  loadAllData()
}

const loadOverview = async () => {
  try {
    const res = await getDashboardOverview({ timeRange: timeRange.value })
    const data = res.data || res
    overviewCards.value[0].value = data.totalBuildings || 0
    overviewCards.value[1].value = data.totalHouses || 0
    overviewCards.value[2].value = data.totalOwners || 0
    overviewCards.value[3].value = `¥${(data.totalFeeAmount || 0).toLocaleString()}`
    overviewCards.value[4].value = data.totalDevices || 0
    overviewCards.value[5].value = data.monthlyRepairCount || 0
  } catch (error) {
    console.error('加载概览数据失败:', error)
  }
}

const loadFeeTrend = async () => {
  feeTrendLoading.value = true
  try {
    const res = await getFeeTrend({ timeRange: timeRange.value })
    const data = res.data || res || []
    const dates = data.map(item => item.date)
    const amounts = data.map(item => item.amount)
    if (feeTrendChartRef.value) {
      initChart(feeTrendChartRef.value, {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
        yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
        series: [{
          name: '收费金额',
          type: 'line',
          data: amounts,
          smooth: true,
          itemStyle: { color: '#409EFF' },
          areaStyle: { color: 'rgba(64, 158, 255, 0.1)' }
        }]
      })
    }
  } catch (error) {
    console.error('加载收费趋势失败:', error)
  } finally {
    feeTrendLoading.value = false
  }
}

const loadFeeItemRatio = async () => {
  feeItemRatioLoading.value = true
  try {
    const res = await getFeeByItem({ timeRange: timeRange.value })
    const data = res.data || res || []
    if (feeItemRatioChartRef.value) {
      initChart(feeItemRatioChartRef.value, {
        tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          name: '收费项目',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
          labelLine: { show: false },
          data: data.map((item, index) => ({
            ...item,
            itemStyle: { color: CHART_COLORS[index % CHART_COLORS.length] }
          }))
        }]
      })
    }
  } catch (error) {
    console.error('加载收费项目占比失败:', error)
  } finally {
    feeItemRatioLoading.value = false
  }
}

const loadRepairTrend = async () => {
  repairTrendLoading.value = true
  try {
    const res = await getRepairTrend({ timeRange: timeRange.value })
    const data = res.data || res || []
    const dates = data.map(item => item.date)
    const counts = data.map(item => item.count)
    if (repairTrendChartRef.value) {
      initChart(repairTrendChartRef.value, {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
        yAxis: { type: 'value' },
        series: [{
          name: '报修数量',
          type: 'line',
          data: counts,
          smooth: true,
          itemStyle: { color: '#E6A23C' },
          areaStyle: { color: 'rgba(230, 162, 60, 0.1)' }
        }]
      })
    }
  } catch (error) {
    console.error('加载报修趋势失败:', error)
  } finally {
    repairTrendLoading.value = false
  }
}

const loadRepairTypeRatio = async () => {
  repairTypeRatioLoading.value = true
  try {
    const res = await getRepairTypeRatio({ timeRange: timeRange.value })
    const data = res.data || res || []
    if (repairTypeRatioChartRef.value) {
      initChart(repairTypeRatioChartRef.value, {
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          name: '报修类型',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
          labelLine: { show: false },
          data: data.map((item, index) => ({
            ...item,
            itemStyle: { color: CHART_COLORS[index % CHART_COLORS.length] }
          }))
        }]
      })
    }
  } catch (error) {
    console.error('加载报修类型占比失败:', error)
  } finally {
    repairTypeRatioLoading.value = false
  }
}

const loadDeviceStatus = async () => {
  deviceStatusLoading.value = true
  try {
    const res = await getDeviceStatus({ timeRange: timeRange.value })
    const data = res.data || res || []
    if (deviceStatusChartRef.value) {
      initChart(deviceStatusChartRef.value, {
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          name: '设备状态',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
          labelLine: { show: false },
          data: data.map(item => {
            const statusOption = DEVICE_STATUS_OPTIONS.find(s => s.value === item.name)
            return {
              name: statusOption?.label || item.name,
              value: item.value,
              itemStyle: { color: statusOption?.color || '#909399' }
            }
          })
        }]
      })
    }
  } catch (error) {
    console.error('加载设备状态分布失败:', error)
  } finally {
    deviceStatusLoading.value = false
  }
}

const loadMaintenanceWarning = async () => {
  maintenanceWarningLoading.value = true
  try {
    const res = await getMaintenanceWarning({ timeRange: timeRange.value })
    maintenanceWarnings.value = res.data || res || []
  } catch (error) {
    console.error('加载维保预警失败:', error)
  } finally {
    maintenanceWarningLoading.value = false
  }
}

const loadSatisfactionTrend = async () => {
  satisfactionTrendLoading.value = true
  try {
    const res = await getSatisfactionTrend({ timeRange: timeRange.value })
    const data = res.data || res || []
    const dates = data.map(item => item.date)
    const scores = data.map(item => item.score)
    if (satisfactionTrendChartRef.value) {
      initChart(satisfactionTrendChartRef.value, {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
        yAxis: { type: 'value', min: 0, max: 5, axisLabel: { formatter: '{value}分' } },
        series: [{
          name: '满意度评分',
          type: 'line',
          data: scores,
          smooth: true,
          itemStyle: { color: '#67C23A' },
          areaStyle: { color: 'rgba(103, 194, 58, 0.1)' }
        }]
      })
    }
  } catch (error) {
    console.error('加载满意度趋势失败:', error)
  } finally {
    satisfactionTrendLoading.value = false
  }
}

const loadComplaintType = async () => {
  complaintTypeLoading.value = true
  try {
    const res = await getComplaintTypeRatio({ timeRange: timeRange.value })
    const data = res.data || res || []
    if (complaintTypeChartRef.value) {
      initChart(complaintTypeChartRef.value, {
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          name: '投诉类型',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
          labelLine: { show: false },
          data: data.map((item, index) => ({
            ...item,
            itemStyle: { color: CHART_COLORS[index % CHART_COLORS.length] }
          }))
        }]
      })
    }
  } catch (error) {
    console.error('加载投诉类型分布失败:', error)
  } finally {
    complaintTypeLoading.value = false
  }
}

const loadInspectionCompletion = async () => {
  inspectionCompletionLoading.value = true
  try {
    const res = await getInspectionCompletion({ timeRange: timeRange.value })
    const data = res.data || res || []
    const dates = data.map(item => item.date)
    const rates = data.map(item => item.rate)
    if (inspectionCompletionChartRef.value) {
      initChart(inspectionCompletionChartRef.value, {
        tooltip: { trigger: 'axis', formatter: '{b}: {c}%' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
        yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
        series: [{
          name: '完成率',
          type: 'line',
          data: rates,
          smooth: true,
          itemStyle: { color: '#626AEA' },
          areaStyle: { color: 'rgba(98, 106, 234, 0.1)' }
        }]
      })
    }
  } catch (error) {
    console.error('加载巡检完成率失败:', error)
  } finally {
    inspectionCompletionLoading.value = false
  }
}

const loadInspectionAbnormal = async () => {
  inspectionAbnormalLoading.value = true
  try {
    const res = await getInspectionAbnormal({ timeRange: timeRange.value })
    const data = res.data || res || []
    const dates = data.map(item => item.date)
    const rates = data.map(item => item.rate)
    if (inspectionAbnormalChartRef.value) {
      initChart(inspectionAbnormalChartRef.value, {
        tooltip: { trigger: 'axis', formatter: '{b}: {c}%' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: dates, axisLabel: { rotate: 45 } },
        yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
        series: [{
          name: '异常率',
          type: 'bar',
          data: rates,
          itemStyle: { color: '#F56C6C' }
        }]
      })
    }
  } catch (error) {
    console.error('加载异常率统计失败:', error)
  } finally {
    inspectionAbnormalLoading.value = false
  }
}

const loadAllData = async () => {
  await Promise.all([
    loadOverview(),
    loadFeeTrend(),
    loadFeeItemRatio(),
    loadRepairTrend(),
    loadRepairTypeRatio(),
    loadDeviceStatus(),
    loadMaintenanceWarning(),
    loadSatisfactionTrend(),
    loadComplaintType(),
    loadInspectionCompletion(),
    loadInspectionAbnormal()
  ])
}

const getDaysRemainingType = (days) => {
  if (days <= 7) return 'danger'
  if (days <= 14) return 'warning'
  return 'success'
}

onMounted(async () => {
  await nextTick()
  await loadAllData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(chart => chart?.dispose())
  charts = []
})
</script>

<style lang="scss" scoped>
.statistics-dashboard {
  padding: 20px;
}

.overview-cards {
  margin-bottom: 20px;
}

.overview-card {
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .card-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .card-info {
    flex: 1;
    
    .card-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 8px;
    }
    
    .card-value {
      font-size: 24px;
      font-weight: 600;
    }
  }
  
  .card-icon {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      font-size: 24px;
    }
  }
}

.time-range-selector {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;
  
  .card-title {
    font-weight: 600;
    font-size: 16px;
  }
}

.chart-container {
  height: 350px;
  width: 100%;
}

.table-container {
  min-height: 350px;
}
</style>
