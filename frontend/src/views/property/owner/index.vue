<template>
  <div class="app-container">
    <div class="page-header">
      <h1>业主管理</h1>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增业主</el-button>
    </div>
    <el-card>
      <el-form :model="query" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="姓名"><el-input v-model="query.name" placeholder="业主姓名" clearable style="width:150px" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="query.phone" placeholder="手机号" clearable style="width:150px" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery"><el-icon><RefreshRight /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">{{ ownerTypeLabel(row.ownerType) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status===0?'danger':'success'" size="small">{{ row.status===0?'禁用':'正常' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInTime" label="入住时间" width="160" />
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
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="姓名" prop="name"><el-input v-model="dialog.form.name" placeholder="业主姓名" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="dialog.form.phone" placeholder="手机号" /></el-form-item>
        <el-form-item label="身份证号"><el-input v-model="dialog.form.idCard" placeholder="身份证号" /></el-form-item>
        <el-form-item label="业主类型" prop="ownerType">
          <el-select v-model="dialog.form.ownerType" style="width:100%">
            <el-option v-for="t in ownerTypeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dialog.form.status"><el-radio :value="1">正常</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="入住时间">
          <el-date-picker v-model="dialog.form.checkInTime" type="datetime" placeholder="选择入住时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
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
import { getOwnerList,getOwnerInfo,addOwner,updateOwner,deleteOwner } from '@/api/community/owner'
const loading=ref(false);const tableData=ref([]);const total=ref(0);const formRef=ref(null)
const query=reactive({ pageNum:1,pageSize:10,name:'',phone:'' })
const dialog=reactive({ visible:false,title:'',loading:false,form:{ id:null,name:'',phone:'',idCard:'',ownerType:1,status:1,checkInTime:'',remark:'' } })
const ownerTypeOptions=[{ value:1,label:'本人' },{ value:2,label:'家属' },{ value:3,label:'租客' }]
const rules={ name:[{ required:true,message:'请输入姓名',trigger:'blur' }], phone:[{ pattern:/^1[3-9]\d{9}$/,message:'手机号格式不正确',trigger:'blur' }] }
const ownerTypeLabel=(t)=>ownerTypeOptions.find(o=>o.value===(typeof t==='number'?t:t?.value))?.label||''
const loadData=async()=>{ loading.value=true; try{ const res=await getOwnerList(query); tableData.value=res.data?.records||[]; total.value=res.data?.total||0 }catch{ tableData.value=[] }; loading.value=false }
const handleQuery=()=>{ query.pageNum=1; loadData() }
const resetQuery=()=>{ Object.assign(query,{ pageNum:1,pageSize:10,name:'',phone:'' }); loadData() }
const resetForm=()=>{ dialog.form={ id:null,name:'',phone:'',idCard:'',ownerType:1,status:1,checkInTime:'',remark:'' }; formRef.value?.resetFields() }
const handleAdd=()=>{ dialog.title='新增业主'; resetForm(); dialog.visible=true }
const handleEdit=async(row)=>{ dialog.title='编辑业主'; try{ const res=await getOwnerInfo(row.id); const d=res.data; dialog.form={ id:d.id,name:d.name,phone:d.phone||'',idCard:d.idCard||'',ownerType:typeof d.ownerType==='number'?d.ownerType:(d.ownerType?.value??1),status:typeof d.status==='number'?d.status:(d.status?.value??1),checkInTime:d.checkInTime||'',remark:d.remark||'' }; dialog.visible=true }catch{} }
const submitForm=async()=>{ try{ await formRef.value.validate() }catch{ return }; dialog.loading=true; try{ if(dialog.form.id){ await updateOwner(dialog.form) }else{ await addOwner(dialog.form) }; ElMessage.success(dialog.form.id?'修改成功':'新增成功'); dialog.visible=false; loadData() }catch{}; dialog.loading=false }
const handleDelete=async(row)=>{ try{ await ElMessageBox.confirm(`确定删除 "${row.name}" 吗？`,'提示',{ type:'warning' }); await deleteOwner(row.id); ElMessage.success('删除成功'); loadData() }catch{} }
onMounted(loadData)
</script>
<style lang="scss" scoped>
.app-container{padding:20px}.page-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px;h1{font-size:20px;font-weight:600;color:#303133;margin:0}}.search-form{margin-bottom:16px}.pagination-container{display:flex;justify-content:flex-end;margin-top:16px}
</style>