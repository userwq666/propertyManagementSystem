<template>
  <div class="inspection-record-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑记录' : '新增记录' }}</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="关联计划">
          <el-select v-model="form.planId" placeholder="请选择计划（可选）" clearable>
            <el-option v-for="item in planList" :key="item.id" :label="item.planName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择设备" prop="equipmentId">
          <el-select v-model="form.equipmentId" placeholder="请选择设备">
            <el-option v-for="item in equipmentList" :key="item.id" :label="item.equipmentName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检结果" prop="result">
          <el-radio-group v-model="form.result">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">一般异常</el-radio>
            <el-radio :value="2">严重异常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="异常描述">
          <el-input v-model="form.faultDesc" type="textarea" rows="3" placeholder="请输入异常描述" />
        </el-form-item>
        <el-form-item label="维修建议">
          <el-input v-model="form.repairSuggestion" type="textarea" rows="3" placeholder="请输入维修建议" />
        </el-form-item>
        <el-form-item label="预估费用">
          <el-input-number v-model="form.budget" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="预估工时">
          <el-input v-model="form.duration" placeholder="如：2小时、1天" />
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
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { pageEquipment } from '../../../../api/equipment'
import { pageInspectionPlan } from '../../../../api/inspectionPlan'
import {
  addInspectionRecord,
  updateInspectionRecord,
  getInspectionRecord
} from '../../../../api/inspectionRecord'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const isEdit = ref(false)

const equipmentList = ref([])
const planList = ref([])

const form = ref({
  planId: null,
  equipmentId: null,
  result: null,
  faultDesc: '',
  repairSuggestion: '',
  budget: null,
  duration: ''
})

const rules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  result: [{ required: true, message: '请选择巡检结果', trigger: 'change' }]
}

const handleCancel = () => router.back()

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateInspectionRecord({ id: route.query.id, ...form.value })
    ElMessage.success('更新成功')
  } else {
    await addInspectionRecord(form.value)
    ElMessage.success('新增成功')
  }
  router.back()
}

const loadEquipment = async () => {
  const res = await pageEquipment({ pageNum: 1, pageSize: 1000 })
  equipmentList.value = res.data.records
}

const loadPlans = async () => {
  const res = await pageInspectionPlan({ pageNum: 1, pageSize: 1000 })
  planList.value = res.data.records
}

onMounted(async () => {
  await loadEquipment()
  await loadPlans()
  
  if (route.query.id) {
    isEdit.value = true
    const res = await getInspectionRecord(route.query.id)
    form.value = res.data
  }
})
</script>

<style scoped>
.inspection-record-add { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>