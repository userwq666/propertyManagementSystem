<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="计划名称">
        <el-input v-model="searchForm.planName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="启用" :value="0" /><el-option label="停用" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left"><el-button type="primary" @click="handleAdd" v-permission="'inspection:plan:add'">新增计划</el-button></div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="planName" label="计划名称" />
        <el-table-column prop="planType" label="类型" width="100" />
        <el-table-column label="巡检频率" width="130">
          <template #default="{ row }">{{ freqText(row.frequencyType) }} {{ row.frequencyValue||'' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status==='ENABLED'?'success':'info'">{{ row.status==='ENABLED'?'启用':'停用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
        <el-table-column prop="creatorName" label="创建人" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'inspection:plan:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'inspection:plan:delete'">删除</el-button>
            <el-button size="small" @click="handleStatus(row)" v-permission="'inspection:plan:edit'">{{ row.status==='ENABLED'?'停用':'启用' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="650px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="计划名称" prop="planName"><el-input v-model="form.planName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="计划类型" prop="planType"><el-select v-model="form.planType" style="width:100%"><el-option label="日常巡检" value="DAILY" /><el-option label="专项巡检" value="SPECIAL" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="巡检频率"><el-select v-model="form.frequencyType" style="width:100%"><el-option label="每天" value="daily" /><el-option label="每周" value="weekly" /><el-option label="每月" value="monthly" /><el-option label="自定义" value="custom" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item v-if="form.frequencyType==='weekly'" label="星期"><el-select v-model="form.frequencyValue" multiple style="width:100%"><el-option v-for="(d,i) in 7" :key="i" :label="'周'+(i+1)" :value="i+1" /></el-select></el-form-item>
          <el-form-item v-if="form.frequencyType==='monthly'" label="日期"><el-select v-model="form.frequencyValue" style="width:100%"><el-option v-for="d in 28" :key="d" :label="d+'号'" :value="d" /></el-select></el-form-item>
          <el-form-item v-if="form.frequencyType==='custom'" label="间隔天数"><el-input-number v-model="form.frequencyValue" :min="1" :max="365" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="关联设备"><el-select v-model="form.equipmentIds" multiple filterable placeholder="请选择"><el-option v-for="e in equipments" :key="e.id" :label="e.equipmentName" :value="e.id" /></el-select></el-form-item>
        <el-form-item label="巡检人员"><el-select v-model="form.inspectorIds" multiple filterable placeholder="请选择"><el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addPlan, updatePlan, deletePlan, getPlanPage, updatePlanStatus } from '@/api/inspection/plan'
import { getEquipmentPage } from '@/api/equipment/equipment'
import { getUserPage } from '@/api/system/user'

const loading = ref(false); const tableData = ref([]); const total = ref(0)
const dialogVisible = ref(false); const formRef = ref(null); const isEdit = ref(false)
const equipments = ref([]); const users = ref([])
const searchForm = reactive({ pageNum: 1, pageSize: 10, planName: '', status: '' })
const form = reactive({ id: null, planName: '', planType: 'DAILY', frequencyType: '', frequencyValue: '', startDate: '', endDate: '', remark: '', equipmentIds: [], inspectorIds: [] })
const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑计划' : '新增计划')
const freqText = (f) => ({ daily:'每天', weekly:'每周', monthly:'每月', custom:'每' }[f]||'')
const rules = { planName: [{ required: true, message: '请输入', trigger: 'blur' }] }

onMounted(async () => {
  fetchData()
  const e = await getEquipmentPage({ pageNum: 1, pageSize: 200 }); equipments.value = e.data.records
  const u = await getUserPage({ pageNum: 1, pageSize: 200 }); users.value = u.data.records
})
async function fetchData() { loading.value=true; try{const r=await getPlanPage({...searchForm});tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false} }
function handleSearch(){searchForm.pageNum=1;fetchData()}
function resetSearch(){searchForm.planName='';searchForm.status='';handleSearch()}
function handleAdd(){isEdit.value=false;resetForm();dialogVisible.value=true}
function handleEdit(row){isEdit.value=true;Object.assign(form,{...row,equipmentIds:row.equipmentIds||[],inspectorIds:row.inspectorIds||[]});dialogVisible.value=true}
function resetForm(){formRef.value?.resetFields();Object.assign(form,{id:null,planName:'',planType:'DAILY',frequencyType:'',frequencyValue:'',startDate:'',endDate:'',remark:'',equipmentIds:[],inspectorIds:[]})}
async function handleSubmit(){const v=await formRef.value.validate().catch(()=>false);if(!v)return;try{if(isEdit.value)await updatePlan(form);else await addPlan(form);ElMessage.success(isEdit.value?'编辑成功':'新增成功');dialogVisible.value=false;fetchData()}catch(e){}}
async function handleDelete(row){await ElMessageBox.confirm('确定删除？','提示',{type:'warning'});try{await deletePlan(row.id);ElMessage.success('删除成功');fetchData()}catch(e){}}
async function handleStatus(row){const s=row.status==='ENABLED'?1:0;try{await updatePlanStatus({id:row.id,status:s});ElMessage.success('操作成功');fetchData()}catch(e){}}
</script>
