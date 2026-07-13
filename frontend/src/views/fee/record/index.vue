<template>
  <div class="fee-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>账单管理</span>
          <el-button type="primary" @click="handleGenerate">批量生成账单</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="账单ID" width="80" />
        <el-table-column prop="ownerId" label="业主ID" />
        <el-table-column prop="houseId" label="房屋ID" />
        <el-table-column prop="totalMoney" label="应付金额" />
        <el-table-column prop="billCycle" label="账单周期" />
        <el-table-column prop="payStatus" label="缴费状态">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 0 ? 'warning' : row.payStatus === 1 ? 'success' : 'danger'">
              {{ row.payStatus === 0 ? '未缴费' : row.payStatus === 1 ? '已缴费' : '欠费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-if="row.payStatus === 0" size="small" type="success" @click="handlePay(row)">确认缴费</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
    
    <el-dialog v-model="generateDialogVisible" title="批量生成账单" width="600px">
      <el-form :model="generateForm" label-width="100px">
        <el-form-item label="收费项目">
          <el-select v-model="generateForm.itemId" placeholder="请选择收费项目">
            <el-option v-for="item in feeItems" :key="item.id" :label="item.itemName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主ID">
          <el-input v-model="generateForm.ownerId" placeholder="请输入业主ID" />
        </el-form-item>
        <el-form-item label="房屋ID">
          <el-input v-model="generateForm.houseId" placeholder="请输入房屋ID" />
        </el-form-item>
        <el-form-item label="账单周期">
          <el-input v-model="generateForm.billCycle" placeholder="请输入账单周期（如：2024-01）" />
        </el-form-item>
        <el-form-item label="应付金额">
          <el-input-number v-model="generateForm.totalMoney" :min="0.01" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGenerateSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pageFeeRecord, generateBills, confirmPay } from '@/api/feeRecord'
import { listFeeItem } from '@/api/feeItem'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const generateDialogVisible = ref(false)
const feeItems = ref([])

const generateForm = ref({
  itemId: null,
  ownerId: '',
  houseId: '',
  billCycle: '',
  totalMoney: 0
})

const fetchData = async () => {
  const res = await pageFeeRecord({ pageNum: currentPage.value, pageSize: pageSize.value })
  tableData.value = res.data.records
  total.value = res.data.total
}

const fetchFeeItems = async () => {
  const res = await listFeeItem()
  feeItems.value = res.data
}

const handleGenerate = () => {
  generateForm.value = { itemId: null, ownerId: '', houseId: '', billCycle: '', totalMoney: 0 }
  generateDialogVisible.value = true
}

const handleGenerateSubmit = async () => {
  await generateBills([generateForm.value])
  ElMessage.success('账单生成成功')
  generateDialogVisible.value = false
  fetchData()
}

const handlePay = async (row) => {
  await confirmPay(row.id, '线下缴费')
  ElMessage.success('缴费成功')
  fetchData()
}

const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => { fetchData() }

onMounted(() => { fetchData(); fetchFeeItems() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.fee-record { padding: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>