<template>
  <div>
    <el-form :inline="true" class="search-form">
      <el-form-item label="选择设备">
        <el-select v-model="equipmentId" placeholder="请选择设备" filterable style="width:240px" @change="loadEquipment">
          <el-option v-for="e in equipments.filter(i => i.id != null)" :key="e.id" :label="e.equipmentName" :value="e.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadEquipment">查询</el-button>
        <el-button @click="resetEquipment">重置</el-button>
      </el-form-item>
    </el-form>

    <el-card v-if="equipment" shadow="never" class="equip-info">
      <template #header>
        <div class="equip-info-header">
          <span>设备信息</span>
          <el-button size="small" :loading="refreshing" @click="loadEquipment">刷新</el-button>
        </div>
      </template>
      <div class="equip-info-row">
        <span><b>{{ equipment.equipmentName }}</b></span>
        <el-tag :type="statusType(equipment.status)">{{ statusText(equipment.status) }}</el-tag>
      </div>
      <div class="equip-info-row">
        <span>编号：{{ equipment.equipmentNo || '-' }}</span>
        <span>分类：{{ equipment.categoryName || '-' }}</span>
        <span>型号：{{ equipment.model || '-' }}</span>
        <span>位置：{{ equipment.location || '-' }}</span>
      </div>
    </el-card>
    <el-empty v-else description="请选择设备查看记录" />

    <el-tabs v-if="equipment" v-model="activeTab">
      <el-tab-pane label="巡查计划" name="plans">
        <el-table :data="plans" border stripe v-loading="loadingPlans">
          <el-table-column prop="planName" label="计划名称" min-width="140" />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">{{ planTypeText(row.planType) }}</template>
          </el-table-column>
          <el-table-column label="频率" width="100">
            <template #default="{ row }">{{ freqText(row.frequencyType) }}</template>
          </el-table-column>
          <el-table-column prop="startDate" label="开始日期" width="110" />
          <el-table-column prop="endDate" label="结束日期" width="110" />
          <el-table-column label="巡检员" min-width="110">
            <template #default="{ row }">{{ (row.inspectorNames || []).join('、') || '-' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="巡查记录" name="records">
        <el-table :data="inspectionRecords" border stripe v-loading="loadingRecords">
          <el-table-column prop="planName" label="计划" min-width="120" />
          <el-table-column prop="inspectionTime" label="巡检日期" width="180" />
          <el-table-column label="结果" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'">
                {{ resultText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="abnormalDesc" label="异常描述" show-overflow-tooltip />
          <el-table-column prop="inspectorName" label="巡检人" width="90" />
          <el-table-column prop="fillerName" label="填写人" width="90" />
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="报修记录" name="repairs">
        <el-table :data="repairs" border stripe v-loading="loadingRepairs">
          <el-table-column prop="repairNo" label="报修单号" width="150" />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">{{ repairTypeText(row.repairType) }}</template>
          </el-table-column>
          <el-table-column prop="repairContent" label="报修内容" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="repairStatusTag(row.status)">{{ repairStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="handleContent" label="维修内容" show-overflow-tooltip />
          <el-table-column prop="handleTime" label="完成时间" width="160" />
          <el-table-column prop="handlerName" label="处理人" width="90" />
          <el-table-column prop="createTime" label="报修时间" width="160" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getEquipmentRecordOptions, getEquipmentRecordSummary } from '@/api/equipment/record'

const equipments = ref([])
const equipmentId = ref('')
const equipment = ref(null)
const activeTab = ref('plans')
const plans = ref([])
const inspectionRecords = ref([])
const repairs = ref([])
const loadingPlans = ref(false)
const loadingRecords = ref(false)
const loadingRepairs = ref(false)
const refreshing = ref(false)

const statusType = (s) => ({ 1: 'success', 2: 'danger', 3: 'warning', 4: 'info', 5: 'info' }[s] || 'info')
const statusText = (s) => ({ 1: '正常', 2: '故障', 3: '维修中', 4: '停用', 5: '报废' }[s] || '未知')
const planTypeText = (t) => ({ 1: '日常巡检', 2: '专项巡检', 3: '季节性巡检', 4: '临时巡检' }[t] || '')
const freqText = (f) => ({ 1: '每天', 2: '每周', 3: '每月', 4: '每季度', 5: '每半年', 6: '每年', 7: '一次性' }[f] || '')
const resultText = (s) => ({ 1: '正常', 2: '异常', 3: '未巡检' }[s] || '')
const repairTypeText = (t) => ({ 水电: '水电', 门窗: '门窗', 家电: '家电', 公共设施: '公共设施', 其他: '其他' }[t] || t || '')
const repairStatusText = (s) => ({ 0: '待派单', 1: '处理中', 2: '待确认', 3: '已完成', 4: '已取消' }[s] || '')
const repairStatusTag = (s) => ({ 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }[s] || 'info')

onMounted(async () => {
  try {
    const e = await getEquipmentRecordOptions()
    equipments.value = e.data || []
  } catch (e) { /* 忽略 */ }
})

async function loadEquipment() {
  if (!equipmentId.value) return
  refreshing.value = true
  loadingPlans.value = true
  loadingRecords.value = true
  loadingRepairs.value = true
  try {
    const s = await getEquipmentRecordSummary(equipmentId.value)
    const data = s.data || {}
    equipment.value = data.equipment || null
    plans.value = data.plans || []
    inspectionRecords.value = data.records || []
    repairs.value = data.repairs || []
  } catch (e) {
    equipment.value = null
    plans.value = []
    inspectionRecords.value = []
    repairs.value = []
  } finally {
    refreshing.value = false
    loadingPlans.value = false
    loadingRecords.value = false
    loadingRepairs.value = false
  }
}

function resetEquipment() {
  equipmentId.value = ''
  equipment.value = null
  plans.value = []
  inspectionRecords.value = []
  repairs.value = []
}
</script>

<style scoped>
.equip-info { margin-bottom: 16px; border-radius: 8px; }
.equip-info-header { display: flex; justify-content: space-between; align-items: center; }
.equip-info-row { display: flex; align-items: center; gap: 20px; margin: 6px 0; font-size: 14px; color: #606266; }
</style>
