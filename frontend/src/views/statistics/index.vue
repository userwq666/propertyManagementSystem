<template>
  <div class="statistics-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overview.totalOwner || 0 }}</div>
          <div class="stat-label">总业主数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overview.totalHouse || 0 }}</div>
          <div class="stat-label">总房屋数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overview.monthlyFee || 0 }}</div>
          <div class="stat-label">本月收费(元)</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overview.pendingRepair || 0 }}</div>
          <div class="stat-label">待处理报修</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 年份选择器 -->
    <div class="year-selector">
      <span class="year-label">年度选择：</span>
      <el-input-number v-model="selectedYear" :min="2020" :max="2099" @change="loadMonthlyFee" />
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>月度收费趋势</template>
          <v-chart :option="monthlyFeeOption" style="height: 350px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>收费项目占比</template>
          <v-chart :option="feeByItemOption" style="height: 350px" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>报修类型统计</template>
          <v-chart :option="repairByTypeOption" style="height: 350px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>报修概览</template>
          <div class="repair-overview">
            <el-row :gutter="16">
              <el-col :span="8">
                <div class="ro-card">
                  <div class="ro-value">{{ repairOverview.total || 0 }}</div>
                  <div class="ro-label">总报修</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="ro-card">
                  <div class="ro-value" style="color: #67c23a;">{{ repairOverview.done || 0 }}</div>
                  <div class="ro-label">已完成</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="ro-card">
                  <div class="ro-value" style="color: #f56c6c;">{{ repairOverview.pending || 0 }}</div>
                  <div class="ro-label">待处理</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>

    <!-- 设备 & 巡检 -->
    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>设备状态分布</template>
          <v-chart :option="equipmentStatusOption" style="height: 300px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>维保到期预警（未来30天）</template>
          <div class="warning-list" v-if="maintenanceWarnings.length">
            <div v-for="w in maintenanceWarnings" :key="w.id" class="warning-item">
              <el-tag type="warning" size="small">{{ w.equipmentName }}</el-tag>
              <span>到期: {{ w.nextMaintenanceDate }}</span>
            </div>
          </div>
          <el-empty v-else description="无即将到期维保" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="chart-card">
          <template #header>巡检概览</template>
          <v-chart :option="inspectionOption" style="height: 300px" autoresize />
        </el-card>
      </el-col>
    </el-row>

    <!-- 投诉 & 满意 -->
    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>投诉类型占比</template>
          <v-chart :option="complaintTypeOption" style="height: 300px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>报修满意度趋势</template>
          <v-chart :option="satisfactionOption" style="height: 300px" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { BarChart, PieChart, LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from 'echarts/components'
import { getOverview, getMonthlyFee, getFeeByItem, getRepairByType, getRepairOverview, getEquipmentStatus, getMaintenanceWarning, getSatisfactionTrend, getComplaintTypeRatio, getInspectionCompletion, getInspectionAbnormal } from '@/api/statistics/index'

use([CanvasRenderer, BarChart, PieChart, LineChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent])

const selectedYear = ref(new Date().getFullYear())
const overview = reactive({})
const repairOverview = reactive({})

const monthlyFeeOption = ref({})
const feeByItemOption = ref({})
const repairByTypeOption = ref({})
const equipmentStatusOption = ref({})
const maintenanceWarnings = ref([])
const inspectionOption = ref({})
const complaintTypeOption = ref({})
const satisfactionOption = ref({})

onMounted(() => {
  loadOverview()
  loadMonthlyFee()
  loadFeeByItem()
  loadRepairByType()
  loadRepairOverview()
  loadEquipmentStatus()
  loadMaintenanceWarning()
  loadInspection()
  loadComplaintType()
  loadSatisfaction()
  loadOverview()
  loadMonthlyFee()
  loadFeeByItem()
  loadRepairByType()
  loadRepairOverview()
})

async function loadOverview() {
  try {
    const res = await getOverview()
    Object.assign(overview, res.data || {})
  } catch (e) { /* ignore */ }
}

async function loadMonthlyFee() {
  try {
    const res = await getMonthlyFee(selectedYear.value)
    const data = res.data || []
    monthlyFeeOption.value = {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.map(d => d.month + '月') },
      yAxis: { type: 'value', name: '金额(元)' },
      series: [{ type: 'bar', data: data.map(d => d.amount), itemStyle: { color: '#409eff' } }]
    }
  } catch (e) { /* ignore */ }
}

async function loadFeeByItem() {
  try {
    const res = await getFeeByItem()
    const data = res.data || []
    feeByItemOption.value = {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '45%'],
        data: data.map(d => ({ name: d.itemName, value: d.amount })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
      }]
    }
  } catch (e) { /* ignore */ }
}

async function loadRepairByType() {
  try {
    const res = await getRepairByType()
    const data = res.data || []
    repairByTypeOption.value = {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.map(d => d.repairTypeName) },
      yAxis: { type: 'value', name: '数量' },
      series: [{ type: 'bar', data: data.map(d => d.count), itemStyle: { color: '#e6a23c' } }]
    }
  } catch (e) { /* ignore */ }
}

async function loadRepairOverview() {
  try {
    const res = await getRepairOverview()
    Object.assign(repairOverview, res.data || {})
  } catch (e) { /* ignore */ }
}
async function loadEquipmentStatus() {
  try {
    const res = await getEquipmentStatus()
    const data = res.data || []
    equipmentStatusOption.value = {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
        data: data.map(d => ({ name: d.statusName, value: d.count })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
      }]
    }
  } catch (e) { /* ignore */ }
}

async function loadMaintenanceWarning() {
  try {
    const res = await getMaintenanceWarning()
    maintenanceWarnings.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function loadInspection() {
  try {
    const [compRes, abnRes] = await Promise.all([
      getInspectionCompletion(),
      getInspectionAbnormal()
    ])
    inspectionOption.value = {
      tooltip: { trigger: 'axis' },
      legend: { data: ['完成率(%)', '异常率(%)'], bottom: 0 },
      xAxis: { type: 'category', data: (compRes.data || []).map(d => d.planName || d.name || '') },
      yAxis: { type: 'value', max: 100 },
      series: [
        { name: '完成率(%)', type: 'bar', data: (compRes.data || []).map(d => d.completionRate || d.rate || 0), itemStyle: { color: '#67c23a' } },
        { name: '异常率(%)', type: 'bar', data: (abnRes.data || []).map(d => d.abnormalRate || d.rate || 0), itemStyle: { color: '#f56c6c' } }
      ]
    }
  } catch (e) { /* ignore */ }
}

async function loadComplaintType() {
  try {
    const res = await getComplaintTypeRatio()
    const data = res.data || []
    complaintTypeOption.value = {
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
        data: data.map(d => ({ name: d.typeName, value: d.count })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
      }]
    }
  } catch (e) { /* ignore */ }
}

async function loadSatisfaction() {
  try {
    const res = await getSatisfactionTrend()
    const data = res.data || []
    satisfactionOption.value = {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.map(d => d.month || d.period || '') },
      yAxis: { type: 'value', name: '评分', min: 0, max: 5 },
      series: [{ type: 'line', data: data.map(d => d.avgScore || d.score || 0), smooth: true, itemStyle: { color: '#409eff' } }]
    }
  } catch (e) { /* ignore */ }
}
</script>

<style scoped>
.statistics-container {
  padding: 0;
}

.stat-cards {
  margin-bottom: 16px;
}

.stat-card {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.year-selector {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.year-label {
  font-size: 14px;
  color: #606266;
}

.chart-card {
  margin-bottom: 0;
}

.repair-overview {
  padding: 20px 0;
}

.ro-card {
  text-align: center;
  padding: 20px 0;
}

.ro-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.warning-list { padding: 10px 0; max-height: 260px; overflow-y: auto; }
.warning-item { display: flex; align-items: center; gap: 8px; padding: 8px 0; border-bottom: 1px solid #ebeef5; }
.warning-item:last-child { border-bottom: none; }
.warning-item span { font-size: 13px; color: #606266; }

.ro-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
</style>
