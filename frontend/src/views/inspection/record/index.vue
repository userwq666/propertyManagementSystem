<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="巡检计划">
        <el-select v-model="searchForm.planId" placeholder="请选择" clearable filterable>
          <el-option v-for="p in plans" :key="p.id" :label="p.planName" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备">
        <el-select v-model="searchForm.equipmentId" placeholder="请选择" clearable filterable>
          <el-option v-for="e in equipments" :key="e.id" :label="e.equipmentName" :value="e.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left"><el-button type="primary" @click="handleAdd" v-permission="'inspection:record:add'">新增记录</el-button></div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="planName" label="计划名称" width="140" />
        <el-table-column prop="equipmentName" label="设备名称" width="120" />
        <el-table-column prop="inspectorName" label="巡检人" width="100" />
        <el-table-column prop="inspectionTime" label="巡检时间" width="180" />
        <el-table-column label="巡检结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status==='NORMAL'?'success':row.status==='ABNORMAL'?'danger':'warning'">
              {{ row.status==='NORMAL'?'正常':row.status==='ABNORMAL'?'异常':'待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="abnormalDesc" label="异常描述" show-overflow-tooltip />
        <el-table-column label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.handleStatus==='HANDLED'?'success':'info'">{{ row.handleStatus==='HANDLED'?'已处理':'未处理' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'inspection:record:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'inspection:record:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="巡检计划"><el-select v-model="form.planId" filterable placeholder="请选择"><el-option v-for="p in plans" :key="p.id" :label="p.planName" :value="p.id" /></el-select></el-form-item>
        <el-form-item label="设备" prop="equipmentId"><el-select v-model="form.equipmentId" filterable placeholder="请选择"><el-option v-for="e in equipments" :key="e.id" :label="e.equipmentName" :value="e.id" /></el-select></el-form-item>
        <el-form-item label="巡检人员" prop="inspectorUserId"><el-select v-model="form.inspectorUserId" filterable placeholder="请选择"><el-option v-for="u in users" :key="u.id" :label="u.realName" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="巡检时间"><el-date-picker v-model="form.inspectionTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="巡检结果" prop="status"><el-select v-model="form.status"><el-option label="正常" value="NORMAL" /><el-option label="异常" value="ABNORMAL" /></el-select></el-form-item>
        <el-form-item v-if="form.status==='ABNORMAL'" label="异常描述"><el-input v-model="form.abnormalDesc" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="处理状态"><el-select v-model="form.handleStatus"><el-option label="未处理" value="UNHANDLED" /><el-option label="已处理" value="HANDLED" /></el-select></el-form-item>
        <el-form-item label="处理内容"><el-input v-model="form.handleContent" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addRecord, updateRecord, deleteRecord, getRecordPage } from '@/api/inspection/record'
import { getPlanPage } from '@/api/inspection/plan'
import { getEquipmentPage } from '@/api/equipment/equipment'
import { getUserPage } from '@/api/system/user'

const loading = ref(false); const tableData = ref([]); const total = ref(0)
const dialogVisible = ref(false); const formRef = ref(null); const isEdit = ref(false)
const plans = ref([]); const equipments = ref([]); const users = ref([])
const searchForm = reactive({ pageNum: 1, pageSize: 10, planId: '', equipmentId: '' })
const form = reactive({ id: null, planId: null, equipmentId: null, inspectorUserId: null, inspectionTime: '', status: 1, abnormalDesc: '', handleStatus: 0, handleContent: '', remark: '' })
const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑记录' : '新增记录')
const rules = { equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }], inspectorUserId: [{ required: true, message: '请选择巡检人员', trigger: 'change' }] }

onMounted(async () => {
  fetchData()
  const p = await getPlanPage({ pageNum: 1, pageSize: 200 }); plans.value = p.data.records
  const e = await getEquipmentPage({ pageNum: 1, pageSize: 200 }); equipments.value = e.data.records
  const u = await getUserPage({ pageNum: 1, pageSize: 200 }); users.value = u.data.records
})
async function fetchData() { loading.value=true; try{const r=await getRecordPage({...searchForm});tableData.value=r.data.records;total.value=r.data.total}finally{loading.value=false} }
function handleSearch(){searchForm.pageNum=1;fetchData()}
function resetSearch(){searchForm.planId='';searchForm.equipmentId='';handleSearch()}
function handleAdd(){isEdit.value=false;resetForm();dialogVisible.value=true}
function handleEdit(row){isEdit.value=true;Object.assign(form,row);dialogVisible.value=true}
function resetForm(){formRef.value?.resetFields();Object.assign(form,{id:null,planId:null,equipmentId:null,inspectorUserId:null,inspectionTime:'',status:'NORMAL',abnormalDesc:'',handleStatus:'UNHANDLED',handleContent:'',remark:''})}
async function handleSubmit(){const v=await formRef.value.validate().catch(()=>false);if(!v)return;try{if(isEdit.value)await updateRecord(form);else await addRecord(form);ElMessage.success(isEdit.value?'编辑成功':'新增成功');dialogVisible.value=false;fetchData()}catch(e){}}
async function handleDelete(row){await ElMessageBox.confirm('确定删除？','提示',{type:'warning'});try{await deleteRecord(row.id);ElMessage.success('删除成功');fetchData()}catch(e){}}
</script>
