<template>
  <div class="fee-arrears">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>欠费统计</span>
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
      
      <el-row :gutter="20" class="statistics-row">
        <el-col :span="8">
          <el-statistic title="欠费户数" :value="statistics.count" />
        </el-col>
        <el-col :span="8">
          <el-statistic title="欠费总金额" :value="statistics.totalArrears" :precision="2" />
        </el-col>
      </el-row>
      
      <el-table :data="statistics.arrearsList" border style="width: 100%">
        <el-table-column prop="id" label="账单ID" width="80" />
        <el-table-column prop="ownerId" label="业主ID" />
        <el-table-column prop="houseId" label="房屋ID" />
        <el-table-column prop="totalMoney" label="欠费金额" />
        <el-table-column prop="billCycle" label="账单周期" />
        <el-table-column prop="createTime" label="生成时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '@/api/feeRecord'

const statistics = ref({
  count: 0,
  totalArrears: 0,
  arrearsList: []
})

const searchForm = ref({
  ownerId: '',
  houseId: ''
})

const fetchData = async () => {
  const params = {}
  if (searchForm.value.ownerId) params.ownerId = searchForm.value.ownerId
  if (searchForm.value.houseId) params.houseId = searchForm.value.houseId
  const res = await getStatistics(params)
  statistics.value = res.data
}

const handleSearch = () => { fetchData() }
const handleReset = () => { searchForm.value = { ownerId: '', houseId: '' }; fetchData() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.fee-arrears { padding: 20px; }
.search-form { margin-bottom: 20px; }
.statistics-row { margin-bottom: 20px; }
</style>