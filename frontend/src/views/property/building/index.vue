<template>
  <div class="app-container">
    <div class="page-header">
      <h1>楼栋管理</h1>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增楼栋</el-button>
    </div>
    <el-card>
      <el-form :model="query" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="楼栋编号"><el-input v-model="query.buildingNo" placeholder="楼栋编号" clearable style="width:160px" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery"><el-icon><RefreshRight /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="buildingNo" label="楼栋编号" width="120" />
        <el-table-column prop="floorCount" label="楼层数" width="80" align="center" />
        <el-table-column prop="totalHouse" label="房屋总数" width="80" align="center" />
        <el-table-column prop="buildYear" label="建成年份" width="80" align="center" />
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="400px">
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="楼栋编号" prop="buildingNo"><el-input v-model="dialog.form.buildingNo" placeholder="如 A栋" /></el-form-item>
        <el-form-item label="楼层数"><el-input-number v-model="dialog.form.floorCount" :min="1" style="width:100%" /></el-form-item>
        <el-form-item label="房屋总数"><el-input-number v-model="dialog.form.totalHouse" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="建成年份"><el-input-number v-model="dialog.form.buildYear" :min="1990" :max="2099" style="width:100%" /></el-form-item>
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
import { getBuildingList,getBuildingInfo,addBuilding,updateBuilding,deleteBuilding } from '@/api/community/building'
const loading=ref(false);const tableData=ref([]);const total=ref(0);const formRef=ref(null)
const query=reactive({ pageNum:1,pageSize:10,buildingNo:'' })
const dialog=reactive({ visible:false,title:'',loading:false,form:{ id:null,buildingNo:'',floorCount:null,totalHouse:null,buildYear:null,remark:'' } })
const rules={ buildingNo:[{ required:true,message:'请输入楼栋编号',trigger:'blur' }] }
const loadData=async()=>{ loading.value=true; try{ const res=await getBuildingList(query); tableData.value=res.data?.records||[]; total.value=res.data?.total||0 }catch{ tableData.value=[] }; loading.value=false }
const handleQuery=()=>{ query.pageNum=1; loadData() }
const resetQuery=()=>{ Object.assign(query,{ pageNum:1,pageSize:10,buildingNo:'' }); loadData() }
const resetForm=()=>{ dialog.form={ id:null,buildingNo:'',floorCount:null,totalHouse:null,buildYear:null,remark:'' }; formRef.value?.resetFields() }
const handleAdd=()=>{ dialog.title='新增楼栋'; resetForm(); dialog.visible=true }
const handleEdit=async(row)=>{ dialog.title='编辑楼栋'; try{ const res=await getBuildingInfo(row.id); const d=res.data; dialog.form={ id:d.id,buildingNo:d.buildingNo,floorCount:d.floorCount,totalHouse:d.totalHouse,buildYear:d.buildYear,remark:d.remark||'' }; dialog.visible=true }catch{} }
const submitForm=async()=>{ try{ await formRef.value.validate() }catch{ return }; dialog.loading=true; try{ if(dialog.form.id){ await updateBuilding(dialog.form) }else{ await addBuilding(dialog.form) }; ElMessage.success(dialog.form.id?'修改成功':'新增成功'); dialog.visible=false; loadData() }catch{}; dialog.loading=false }
const handleDelete=async(row)=>{ try{ await ElMessageBox.confirm(`确定删除 "${row.buildingNo}" 吗？`,'提示',{ type:'warning' }); await deleteBuilding(row.id); ElMessage.success('删除成功'); loadData() }catch{} }
onMounted(loadData)
</script>
<style lang="scss" scoped>
.app-container{padding:20px}.page-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px;h1{font-size:20px;font-weight:600;color:#303133;margin:0}}.search-form{margin-bottom:16px}.pagination-container{display:flex;justify-content:flex-end;margin-top:16px}
</style>