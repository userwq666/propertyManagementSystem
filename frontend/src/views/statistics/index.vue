<template>
  <div class="statistics-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">总收费金额</div>
              <div class="card-value money">¥{{ overview.totalFeeMoney?.toFixed(2) || '0.00' }}</div>
            </div>
            <el-icon class="card-icon" style="color: #409eff"><Money /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">总欠费金额</div>
              <div class="card-value money warning">¥{{ overview.totalArrearsMoney?.toFixed(2) || '0.00' }}</div>
            </div>
            <el-icon class="card-icon" style="color: #e6a23c"><Warning /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">总报修数</div>
              <div class="card-value">{{ overview.totalRepairCount || 0 }}</div>
            </div>
            <el-icon class="card-icon" style="color: #67c23a"><Tools /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">待处理报修数</div>
              <div class="card-value danger">{{ overview.pendingRepairCount || 0 }}</div>
            </div>
            <el-icon class="card-icon" style="color: #f56c6c"><Clock /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>按月收费统计</span>
              <el-date-picker v-model="selectedYear" type="year" placeholder="选择年份" @change="fetchMonthlyStatistics" />
            </div>
          </template>
          <el-table :data="monthlyStatistics" border style="width: 100%">
            <el-table-column prop="month" label="月份" width="80">
              <template #default="{ row }">{{ row.month }}月</template>
            </el-table-column>
            <el-table-column prop="totalMoney" label="应收金额">
              <template #default="{ row }">¥{{ row.totalMoney?.toFixed(2) || '0.00' }}</template>
            </el-table-column>
            <el-table-column prop="paidMoney" label="已收金额">
              <template #default="{ row }">¥{{ row.paidMoney?.toFixed(2) || '0.00' }}</template>
            </el-table-column>
            <el-table-column prop="unpaidMoney" label="未收金额">
              <template #default="{ row }">
                <span :class="{ 'text-danger': row.unpaidMoney > 0 }">¥{{ row.unpaidMoney?.toFixed(2) || '0.00' }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>按项目收费统计</span>
          </template>
          <el-table :data="itemStatistics" border style="width: 100%">
            <el-table-column prop="itemName" label="收费项目" />
            <el-table-column prop="totalMoney" label="应收金额">
              <template #default="{ row }">¥{{ row.totalMoney?.toFixed(2) || '0.00' }}</template>
            </el-table-column>
            <el-table-column prop="paidMoney" label="已收金额">
              <template #default="{ row }">¥{{ row.paidMoney?.toFixed(2) || '0.00' }}</template>
            </el-table-column>
            <el-table-column prop="unpaidMoney" label="未收金额">
              <template #default="{ row }">
                <span :class="{ 'text-danger': row.unpaidMoney > 0 }">¥{{ row.unpaidMoney?.toFixed(2) || '0.00' }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>报修状态分布</span>
          </template>
          <el-table :data="repairOverview" border style="width: 100%">
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="数量" />
            <el-table-column label="占比">
              <template #default="{ row }">
                {{ getPercentage(row.count, repairTotal) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>按报修类型统计</span>
          </template>
          <el-table :data="repairByType" border style="width: 100%">
            <el-table-column prop="typeName" label="报修类型" />
            <el-table-column prop="count" label="数量" />
            <el-table-column label="占比">
              <template #default="{ row }">
                {{ getPercentage(row.count, repairTotal) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Money, Warning, Tools, Clock } from '@element-plus/icons-vue'
import {
  getStatisticsOverview,
  getFeeMonthlyStatistics,
  getFeeByItemStatistics,
  getRepairOverview,
  getRepairByTypeStatistics
} from '@/api/statistics'

const overview = ref({})
const monthlyStatistics = ref([])
const itemStatistics = ref([])
const repairOverview = ref([])
const repairByType = ref([])
const selectedYear = ref(new Date())

const repairTotal = computed(() => {
  return repairOverview.value.reduce((sum, item) => sum + (item.count || 0), 0)
})

const getPercentage = (value, total) => {
  if (total === 0) return '0%'
  return ((value / total) * 100).toFixed(1) + '%'
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger' }
  return types[status] || 'info'
}

const fetchOverview = async () => {
  try {
    const res = await getStatisticsOverview()
    overview.value = res.data || {}
  } catch (error) {
    ElMessage.error('获取总览数据失败')
  }
}

const fetchMonthlyStatistics = async () => {
  try {
    const year = selectedYear.value ? selectedYear.value.getFullYear() : new Date().getFullYear()
    const res = await getFeeMonthlyStatistics(year)
    monthlyStatistics.value = res.data || []
  } catch (error) {
    ElMessage.error('获取月度统计失败')
  }
}

const fetchItemStatistics = async () => {
  try {
    const res = await getFeeByItemStatistics()
    itemStatistics.value = res.data || []
  } catch (error) {
    ElMessage.error('获取项目统计失败')
  }
}

const fetchRepairOverview = async () => {
  try {
    const res = await getRepairOverview()
    repairOverview.value = res.data || []
  } catch (error) {
    ElMessage.error('获取报修概览失败')
  }
}

const fetchRepairByType = async () => {
  try {
    const res = await getRepairByTypeStatistics()
    repairByType.value = res.data || []
  } catch (error) {
    ElMessage.error('获取报修类型统计失败')
  }
}

onMounted(() => {
  fetchOverview()
  fetchMonthlyStatistics()
  fetchItemStatistics()
  fetchRepairOverview()
  fetchRepairByType()
})
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.card-value.money {
  color: #409eff;
}

.card-value.money.warning {
  color: #e6a23c;
}

.card-value.danger {
  color: #f56c6c;
}

.card-icon {
  font-size: 48px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.el-table {
  margin-top: 10px;
}
</style>
