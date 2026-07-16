<template>
  <div class="dashboard-container">
    <page-header title="数据概览" :breadcrumbs="breadcrumbs">
      <template #actions>
        <el-button type="primary" @click="refreshData" :loading="loading">
          <el-icon><refresh /></el-icon>
          刷新数据
        </el-button>
      </template>
    </page-header>

    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="24" :sm="12" :md="6" :lg="3" v-for="card in statCards" :key="card.key">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">{{ card.label }}</p>
              <p class="stat-value">{{ card.value }}</p>
              <p class="stat-trend" :class="card.trend > 0 ? 'up' : 'down'">
                <el-icon :class="card.trend > 0 ? 'trend-up' : 'trend-down'">
                  <component :is="card.trend > 0 ? 'ArrowUp' : 'ArrowDown'" />
                </el-icon>
                <span>{{ Math.abs(card.trend) }}%</span>
                <span>较上月</span>
              </p>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: card.color }">
              <el-icon><component :is="card.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>收费趋势</span>
              <el-select v-model="feeChartType" placeholder="选择时间" style="width: 120px" clearable @change="loadFeeChart">
                <el-option label="近7天" value="7" />
                <el-option label="近30天" value="30" />
                <el-option label="近90天" value="90" />
              </el-select>
            </div>
          </template>
          <div class="chart-container" ref="feeChartRef" style="height: 300px" />
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>费用结构</span>
          </template>
          <div class="chart-container" ref="feeStructureRef" style="height: 300px" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="bottom-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="card-header">
              <span>报修工单状态</span>
              <el-select v-model="repairStatus" placeholder="状态" style="width: 100px" clearable @change="loadRepairChart">
                <el-option label="全部" value="" />
                <el-option label="待派单" value="0" />
                <el-option label="处理中" value="1" />
                <el-option label="已完成" value="2" />
                <el-option label="已取消" value="3" />
              </el-select>
            </div>
          </template>
          <div class="chart-container" ref="repairChartRef" style="height: 280px" />
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span>近期公告</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item v-for="notice in recentNotices" :key="notice.id" :label="notice.title">
              <div class="notice-item">
                <p class="notice-content">{{ notice.content }}</p>
                <p class="notice-meta">
                  <span>{{ formatDate(notice.createTime) }}</span>
                  <el-tag :type="getNoticeType(notice.type)" size="small">
                    {{ getNoticeTypeLabel(notice.type) }}
                  </el-tag>
                </p>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="tables-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待办事项</span>
              <el-button type="link" size="small" @click="goToRepair">更多</el-button>
            </div>
          </template>
          <el-table :data="pendingTasks" border style="width: 100%" size="small">
            <el-table-column prop="type" label="类型" width="80">
              <template #default="scope">
                <el-tag :type="getTaskTypeColor(scope.row.type)" size="small">
                  {{ getTaskTypeLabel(scope.row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column prop="createTime" label="创建时间" width="160" />
            <el-table-column label="操作" width="80">
              <template #default="scope">
                <el-button size="small" type="primary" link @click="handleTask(scope.row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>房屋入住率</span>
              <el-button type="link" size="small" @click="goToProperty">更多</el-button>
            </div>
          </template>
          <el-table :data="occupancyData" border style="width: 100%" size="small">
            <el-table-column prop="buildingName" label="楼栋" />
            <el-table-column prop="totalRooms" label="总房间" width="100" />
            <el-table-column prop="occupiedRooms" label="已入住" width="100" />
            <el-table-column prop="occupancyRate" label="入住率" width="120">
              <template #default="scope">
                <el-progress :percentage="scope.row.occupancyRate" :stroke-width="12" :show-text="true" :format="p => `${p}%`" />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ArrowUp, ArrowDown, Refresh, House, Coin, Tickets, UserFilled, WarningFilled } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader/index.vue'

const router = useRouter()

const statCards = ref([
  { key: 'fee', label: '本月收费', value: '¥1,258,000', trend: 12.5, icon: 'Coin', color: '#e6f7ff' },
  { key: 'repair', label: '报修工单', value: '234', trend: -5.2, icon: 'Tickets', color: '#fff1f0' },
  { key: 'occupancy', label: '入住率', value: '92.5%', trend: 2.1, icon: 'House', color: '#f6ffed' },
  { key: 'complaint', label: '投诉处理', value: '18', trend: -15.3, icon: 'WarningFilled', color: '#fffbe6' }
])

const feeChartType = ref('30')
const repairStatus = ref('')

const feeChartRef = ref(null)
const feeStructureRef = ref(null)
const repairChartRef = ref(null)

let feeChart = null
let feeStructureChart = null
let repairChart = null

const recentNotices = ref([
  { id: 1, title: '关于春节期间物业服务安排的通知', content: '春节期间物业服务正常运行，24小时值班电话...', createTime: '2024-01-20', type: '1' },
  { id: 2, title: '小区停车位调整通告', content: '为优化停车资源，自2月1日起调整部分停车位归属...', createTime: '2024-01-18', type: '2' },
  { id: 3, title: '电梯年度检测安排', content: '1号楼、2号楼电梯将于本周六进行年度安全检测...', createTime: '2024-01-15', type: '3' },
  { id: 4, title: '物业费缴纳优惠活动', content: '提前缴纳全年物业费享9折优惠，详情咨询物业前台...', createTime: '2024-01-10', type: '3' }
])

const pendingTasks = ref([
  { id: 1, type: 'repair', title: '101室漏水报修', createTime: '2024-01-20 09:30' },
  { id: 2, type: 'complaint', title: '关于噪音扰民投诉', createTime: '2024-01-20 10:15' },
  { id: 3, type: 'fee', title: '202室物业费催缴', createTime: '2024-01-20 11:00' },
  { id: 4, type: 'repair', title: '电梯故障紧急抢修', createTime: '2024-01-20 14:20' },
  { id: 5, type: 'complaint', title: '公共区域卫生问题', createTime: '2024-01-20 15:45' }
])

const occupancyData = ref([
  { buildingName: '1号楼', totalRooms: 120, occupiedRooms: 115, occupancyRate: 95.8 },
  { buildingName: '2号楼', totalRooms: 120, occupiedRooms: 108, occupancyRate: 90.0 },
  { buildingName: '3号楼', totalRooms: 80, occupiedRooms: 72, occupancyRate: 90.0 },
  { buildingName: '4号楼', totalRooms: 80, occupiedRooms: 76, occupancyRate: 95.0 },
  { buildingName: '5号楼', totalRooms: 60, occupiedRooms: 52, occupancyRate: 86.7 }
])

const loading = ref(false)

const breadcrumbs = computed(() => [
  { path: '/', name: '首页' },
  { path: '/dashboard', name: '数据概览' }
])

const initCharts = async () => {
  await nextTick()
  initFeeChart()
  initFeeStructureChart()
  initRepairChart()
}

const initFeeChart = () => {
  if (!feeChartRef.value) return
  feeChart = echarts.init(feeChartRef.value)
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['1/14', '1/15', '1/16', '1/17', '1/18', '1/19', '1/20'], axisLine: { lineStyle: { color: '#ebeef5' } } },
    yAxis: { type: 'value', axisLine: { lineStyle: { color: '#ebeef5' } }, splitLine: { lineStyle: { color: '#ebeef5' } } },
    series: [
      { name: '应收', type: 'bar', data: [320, 302, 301, 334, 390, 330, 320], itemStyle: { color: '#409eff' } },
      { name: '实收', type: 'bar', data: [280, 290, 285, 310, 350, 310, 300], itemStyle: { color: '#67c23a' } }
    ]
  }
  feeChart.setOption(option)
  window.addEventListener('resize', resizeCharts)
}

const initFeeStructureChart = () => {
  if (!feeStructureRef.value) return
  feeStructureChart = echarts.init(feeStructureRef.value)
  const option = {
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', data: ['物业费', '车位费', '水电费', '其他费用'] },
    series: [
      {
        name: '费用结构', type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: '16', fontWeight: 'bold' } },
        labelLine: { show: false },
        data: [
          { value: 65, name: '物业费' },
          { value: 20, name: '车位费' },
          { value: 10, name: '水电费' },
          { value: 5, name: '其他费用' }
        ]
      }
    ],
    color: ['#409eff', '#67c23a', '#e6a23c', '#909399']
  }
  feeStructureChart.setOption(option)
  window.addEventListener('resize', resizeCharts)
}

const initRepairChart = () => {
  if (!repairChartRef.value) return
  repairChart = echarts.init(repairChartRef.value)
  const option = {
    tooltip: { trigger: 'item', formatter: '{a} <br/>{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', data: ['待派单', '处理中', '已完成', '已取消'] },
    series: [
      {
        name: '工单状态', type: 'pie', radius: ['40%', '70%'], avoidLabelOverlap: false,
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
        label: { show: true, formatter: '{b}: {c} ({d}%)' },
        emphasis: { label: { show: true, fontSize: '14', fontWeight: 'bold' } },
        data: [
          { value: 45, name: '待派单' },
          { value: 120, name: '处理中' },
          { value: 200, name: '已完成' },
          { value: 15, name: '已取消' }
        ]
      }
    ],
    color: ['#e6a23c', '#409eff', '#67c23a', '#909399']
  }
  repairChart.setOption(option)
  window.addEventListener('resize', resizeCharts)
}

const resizeCharts = () => {
  feeChart?.resize()
  feeStructureChart?.resize()
  repairChart?.resize()
}

const loadFeeChart = () => {
  // 根据类型重新加载数据
  initFeeChart()
}

const loadRepairChart = () => {
  initRepairChart()
}

const refreshData = async () => {
  loading.value = true
  await new Promise(r => setTimeout(r, 1000))
  initCharts()
  loading.value = false
}

const formatDate = (dateStr) => {
  return dateStr ? new Date(dateStr).toLocaleDateString('zh-CN') : ''
}

const getNoticeType = (type) => {
  return type === '1' ? 'danger' : type === '2' ? 'warning' : 'info'
}

const getNoticeTypeLabel = (type) => {
  return type === '1' ? '紧急' : type === '2' ? '重要' : '普通'
}

const getTaskTypeColor = (type) => {
  return type === 'repair' ? 'primary' : type === 'complaint' ? 'danger' : 'warning'
}

const getTaskTypeLabel = (type) => {
  return type === 'repair' ? '报修' : type === 'complaint' ? '投诉' : '费用'
}

const handleTask = (task) => {
  switch (task.type) {
    case 'repair': router.push('/repair/order'); break
    case 'complaint': router.push('/complaint/list'); break
    case 'fee': router.push('/fee/bill'); break
  }
}

const goToRepair = () => router.push('/repair/order')
const goToProperty = () => router.push('/property/house')

onMounted(() => {
  initCharts()
})

onBeforeUnmount(() => {
  feeChart?.dispose()
  feeStructureChart?.dispose()
  repairChart?.dispose()
  window.removeEventListener('resize', resizeCharts)
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 0;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  height: 100%;
  transition: transform 0.3s, box-shadow 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 8px 0;
}

.stat-info {
  flex: 1;

  .stat-label {
    margin: 0 0 4px;
    font-size: 14px;
    color: #909399;
  }

  .stat-value {
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    line-height: 1.2;
  }

  .stat-trend {
    margin: 0;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 4px;

    &.up { color: #67c23a; }
    &.down { color: #f56c6c; }

    .trend-up { color: #67c23a; }
    .trend-down { color: #f56c6c; }
  }
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  .el-icon {
    font-size: 20px;
    color: #fff;
  }
}

.charts-row, .bottom-row, .tables-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
  }
}

.chart-container {
  width: 100%;
  height: 100%;
}

.notice-item {
  .notice-content {
    margin: 0 0 8px;
    font-size: 13px;
    color: #606266;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .notice-meta {
    margin: 0;
    font-size: 12px;
    color: #909399;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

/* 面包屑样式覆盖 */
:deep(.el-breadcrumb) {
  font-size: 14px;
}

:deep(.el-breadcrumb__inner) {
  color: #606266;

  &:hover {
    color: #409eff;
  }
}

:deep(.el-breadcrumb__separator) {
  color: #c0c4cc;
}
</style>