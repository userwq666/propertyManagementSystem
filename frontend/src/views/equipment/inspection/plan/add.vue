<template>
  <div class="inspection-plan-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑计划' : '新增计划' }}</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="form.planName" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="计划类型" prop="planType">
          <el-radio-group v-model="form.planType">
            <el-radio :value="0">手动创建</el-radio>
            <el-radio :value="1">周期生成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.planType === 0" label="计划日期" prop="planDate">
          <el-date-picker v-model="form.planDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item v-if="form.planType === 1" label="周期类型" prop="cycleType">
          <el-select v-model="form.cycleType" placeholder="请选择周期">
            <el-option label="每天" :value="0" />
            <el-option label="每周" :value="1" />
            <el-option label="每月" :value="2" />
            <el-option label="自定义" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.planType === 1 && form.cycleType === 3" label="周期值">
          <el-input-number v-model="form.cycleValue" :min="1" />
          <span style="margin-left: 10px">天</span>
        </el-form-item>
        <el-form-item v-if="form.planType === 1" label="开始日期" prop="startDate">
          <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item v-if="form.planType === 1" label="结束日期">
          <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="选择设备" prop="equipmentIds">
          <el-select v-model="selectedEquipmentIds" multiple placeholder="请选择设备" style="width: 100%">
            <el-option v-for="item in equipmentList" :key="item.id" :label="item.equipmentName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检人员" prop="inspectorIds">
          <el-select v-model="selectedInspectorIds" multiple placeholder="请选择巡检人员" style="width: 100%">
            <el-option v-for="item in inspectorList" :key="item.id" :label="item.realName || item.username" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { pageEquipment } from '../../../../api/equipment'
import { addInspectionPlan, updateInspectionPlan, getInspectionPlan } from '../../../../api/inspectionPlan'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const isEdit = ref(false)

const equipmentList = ref([])
const inspectorList = ref([])
const selectedEquipmentIds = ref([])
const selectedInspectorIds = ref([])

const form = ref({
  planName: '',
  planType: 0,
  cycleType: null,
  cycleValue: null,
  planDate: null,
  startDate: null,
  endDate: null,
  equipmentIds: '',
  inspectorIds: ''
})

const rules = {
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  planType: [{ required: true, message: '请选择计划类型', trigger: 'change' }]
}

const handleCancel = () => router.back()

const handleSubmit = async () => {
  await formRef.value.validate()
  form.value.equipmentIds = selectedEquipmentIds.value.join(',')
  form.value.inspectorIds = selectedInspectorIds.value.join(',')
  
  if (isEdit.value) {
    await updateInspectionPlan({ id: route.query.id, ...form.value })
    ElMessage.success('更新成功')
  } else {
    await addInspectionPlan(form.value)
    ElMessage.success('新增成功')
  }
  router.back()
}

const loadEquipment = async () => {
  const res = await pageEquipment({ pageNum: 1, pageSize: 1000 })
  equipmentList.value = res.data.records
}

const loadInspectors = async () => {
  inspectorList.value = []
}

onMounted(async () => {
  await loadEquipment()
  await loadInspectors()
  
  if (route.query.id) {
    isEdit.value = true
    const res = await getInspectionPlan(route.query.id)
    form.value = res.data
    selectedEquipmentIds.value = res.data.equipmentIds ? res.data.equipmentIds.split(',').map(Number) : []
    selectedInspectorIds.value = res.data.inspectorIds ? res.data.inspectorIds.split(',').map(Number) : []
  }
})
</script>

<style scoped>
.inspection-plan-add { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>