<template>
  <div class="app-container">
    <div class="page-header">
      <h1>车位管理</h1>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增车位</el-button>
    </div>
    <el-card>
      <el-form :model="query" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="车位编号"><el-input v-model="query.parkingNo" placeholder="车位编号" clearable style="width:160px" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:120px">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery"><el-icon><RefreshRight /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="parkingNo" label="车位编号" width="120" />
        <el-table-column label="类型" width="70" align="center">
          <template #default="{ row }">{{ row.parkingType===1?'地上':'地下' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="parkingStatusColor(row.status)" size="small">{{ parkingStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerId" label="业主ID" width="80" />
        <el-table-column prop="rentPrice" label="月租金" width="90" align="right">
          <template #default="{ row }">{{ row.rentPrice ? '¥'+Number(row.rentPrice).toFixed(2) : '--' }}</template>
        </el-table-column>
        <el-table-column prop="sellPrice" label="售价" width="110" align="right">
          <template #default="{ row }">{{ row.sellPrice ? '¥'+Number(row.sellPrice).toFixed(2) : '--' }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" :total="total" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="420px" @close="resetForm">
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="车位编号" prop="parkingNo"><el-input v-model="dialog.form.parkingNo" placeholder="如 P001" /></el-form-item>
        <el-form-item label="车位类型" prop="parkingType">
          <el-select v-model="dialog.form.parkingType" style="width:100%">
            <el-option label="地上" :value="1" /><el-option label="地下" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="dialog.form.status" style="width:100%">
            <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主ID"><el-input v-model="dialog.form.ownerId" placeholder="绑定业主ID" /></el-form-item>
        <el-form-item label="月租金"><el-input-number v-model="dialog.form.rentPrice" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="售价"><el-input-number v-model="dialog.form.sellPrice" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="dialog.form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" :loading="dialog.loading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref,reactive,onMounted } from 'vue'; import { ElMessage,ElMessageBox } from 'element-plus'; import { Plus,Search,RefreshRight } from '@element-plus/icons-vue'
import { getParkingList,getParkingInfo,addParking,updateParking,deleteParking } from '@/api/community/parking'
const loading=ref(false);const tableData=ref([]);const total=ref(0);const formRef=ref(null)
const query=reactive({ pageNum:1,pageSize:10,parkingNo:'',status:null })
const dialog=reactive({ visible:false,title:'',loading:false,form:{ id:null,parkingNo:'',parkingType:1,status:0,ownerId:null,rentPrice:null,sellPrice:null,remark:'' } })
const statusOptions=[{ value:0,label:'空闲' },{ value:1,label:'已租' },{ value:2,label:'已售' },{ value:3,label:'维修中' }]
const parkingStatusColor=(s)=>({0:'success',1:'warning',2:'info',3:'danger'})[s]||''
const parkingStatusLabel=(s)=>statusOptions.find(o=>o.value===(typeof s==='number'?s:s?.value))?.label||''
const rules={ parkingNo:[{ required:true,message:'请输入车位编号',trigger:'blur' }] }
const loadData=async()=>{ loading.value=true; try{ const res=await getParkingList(query); tableData.value=res.data?.records||[]; total.value=res.data?.total||0 }catch{ tableData.value=[] }; loading.value=false }
const handleQuery=()=>{ query.pageNum=1; loadData() }
const resetQuery=()=>{ Object.assign(query,{ pageNum:1,pageSize:10,parkingNo:'',status:null }); loadData() }
const resetForm=()=>{ dialog.form={ id:null,parkingNo:'',parkingType:1,status:0,ownerId:null,rentPrice:null,sellPrice:null,remark:'' }; formRef.value?.resetFields() }
const handleAdd=()=>{ dialog.title='新增车位'; resetForm(); dialog.visible=true }
const handleEdit=async(row)=>{ dialog.title='编辑车位'; try{ const res=await getParkingInfo(row.id); const d=res.data; dialog.form={ id:d.id,parkingNo:d.parkingNo,parkingType:typeof d.parkingType==='number'?d.parkingType:(d.parkingType?.value??1),status:typeof d.status==='number'?d.status:(d.status?.value??0),ownerId:d.ownerId,rentPrice:d.rentPrice,sellPrice:d.sellPrice,remark:d.remark||'' }; dialog.visible=true }catch{} }
const submitForm=async()=>{ try{ await formRef.value.validate() }catch{ return }; dialog.loading=true; try{ if(dialog.form.id){ await updateParking(dialog.form) }else{ await addParking(dialog.form) }; ElMessage.success(dialog.form.id?'修改成功':'新增成功'); dialog.visible=false; loadData() }catch{}; dialog.loading=false }
const handleDelete=async(row)=>{ try{ await ElMessageBox.confirm(`确定删除 "${row.parkingNo}" 吗？`,'提示',{ type:'warning' }); await deleteParking(row.id); ElMessage.success('删除成功'); loadData() }catch{} }
onMounted(loadData)
</script>
<style lang="scss" scoped>
.app-container{padding:20px}.page-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px;h1{font-size:20px;font-weight:600;color:#303133;margin:0}}.search-form{margin-bottom:16px}.pagination-container{display:flex;justify-content:flex-end;margin-top:16px}
</style>