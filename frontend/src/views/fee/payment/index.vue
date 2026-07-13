<template>
  <div class="fee-payment">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>缴费记录</span>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="业主ID">
          <el-input v-model="searchForm.ownerId" placeholder="请输入业主ID" clearable />
        </el-form-item>
        <el-form-item label="房屋ID">
          <el-input v-model="searchForm.houseId" placeholder="请输入房屋ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
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
        <el-table-column prop="payTime" label="缴费时间" />
        <el-table-column prop="payWay" label="支付方式" />
      </el-table>
      
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { pageFeeRecord } from '@/api/feeRecord'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  ownerId: '',
  houseId: ''
})

const fetchData = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    payStatus: 1
  }
  if (searchForm.value.ownerId) params.ownerId = searchForm.value.ownerId
  if (searchForm.value.houseId) params.houseId = searchForm.value.houseId
  const res = await pageFeeRecord(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => { currentPage.value = 1; fetchData() }
const handleReset = () => { searchForm.value = { ownerId: '', houseId: '' }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => { fetchData() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.fee-payment { padding: 20px; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>