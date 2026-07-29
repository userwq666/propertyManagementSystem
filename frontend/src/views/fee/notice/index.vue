<template>
  <div class="app-container">
    <div class="page-header">
      <h1>缴费通知</h1>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新建通知</el-button>
    </div>
    <el-card>
      <el-form :model="query" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="标题"><el-input v-model="query.noticeTitle" placeholder="通知标题" clearable style="width:180px" /></el-form-item>
        <el-form-item label="发送状态">
          <el-select v-model="query.sendStatus" placeholder="全部" clearable style="width:120px">
            <el-option label="待发送" :value="0" /><el-option label="已发送" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery"><el-icon><RefreshRight /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="noticeTitle" label="标题" width="200" show-overflow-tooltip />
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">{{ row.noticeType===1?'催缴':'公告' }}</template>
        </el-table-column>
        <el-table-column label="范围" width="80" align="center">
          <template #default="{ row }">{{ row.sendScope===1?'全部':'指定' }}</template>
        </el-table-column>
        <el-table-column label="发送状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.sendStatus===1?'success':'info'" size="small">{{ row.sendStatus===1?'已发送':'待发送' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="160" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.sendStatus!==1" type="success" size="small" link @click="handleSend(row)">发送</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" :total="total" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="通知标题" prop="noticeTitle"><el-input v-model="dialog.form.noticeTitle" /></el-form-item>
        <el-form-item label="通知类型">
          <el-select v-model="dialog.form.noticeType" style="width:100%">
            <el-option label="催缴通知" :value="1" /><el-option label="公告通知" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送范围">
          <el-select v-model="dialog.form.sendScope" style="width:100%">
            <el-option label="全部业主" :value="1" /><el-option label="指定业主" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dialog.form.sendScope===2" label="业主ID">
          <el-input v-model="dialog.form.ownerIds" placeholder="多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="通知内容" prop="noticeContent">
          <el-input v-model="dialog.form.noticeContent" type="textarea" :rows="5" placeholder="通知内容" />
        </el-form-item>
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
import { getNoticeList,addNotice,updateNotice,deleteNotice,publishNotice } from '@/api/fee/notice'
const loading=ref(false);const tableData=ref([]);const total=ref(0);const formRef=ref(null)
const query=reactive({ pageNum:1,pageSize:10,noticeTitle:'',sendStatus:null })
const dialog=reactive({ visible:false,title:'',loading:false,form:{ id:null,noticeTitle:'',noticeContent:'',noticeType:1,sendScope:1,ownerIds:'',buildingIds:'' } })
const rules={ noticeTitle:[{required:true,message:'请输入通知标题',trigger:'blur'}] }
const loadData=async()=>{ loading.value=true; try{ const res=await getNoticeList(query); tableData.value=res.data?.records||[]; total.value=res.data?.total||0 }catch{ tableData.value=[] }; loading.value=false }
const handleQuery=()=>{ query.pageNum=1; loadData() }
const resetQuery=()=>{ Object.assign(query,{ pageNum:1,pageSize:10,noticeTitle:'',sendStatus:null }); loadData() }
const resetForm=()=>{ dialog.form={ id:null,noticeTitle:'',noticeContent:'',noticeType:1,sendScope:1,ownerIds:'',buildingIds:'' }; formRef.value?.resetFields() }
const handleAdd=()=>{ dialog.title='新建通知'; resetForm(); dialog.visible=true }
const handleEdit=async(row)=>{ dialog.title='编辑通知'; try{ const d=row; dialog.form={ id:d.id,noticeTitle:d.noticeTitle,noticeContent:d.noticeContent||'',noticeType:d.noticeType||1,sendScope:d.sendScope||1,ownerIds:d.ownerIds||'',buildingIds:d.buildingIds||'' }; dialog.visible=true }catch{} }
const submitForm=async()=>{ try{ await formRef.value.validate() }catch{ return }; dialog.loading=true; try{ if(dialog.form.id){ await updateNotice(dialog.form) }else{ await addNotice(dialog.form) }; ElMessage.success(dialog.form.id?'修改成功':'新增成功'); dialog.visible=false; loadData() }catch{}; dialog.loading=false }
const handleDelete=async(row)=>{ try{ await ElMessageBox.confirm(`确定删除 "${row.noticeTitle}" 吗？`,'提示',{ type:'warning' }); await deleteNotice(row.id); ElMessage.success('删除成功'); loadData() }catch{} }
const handleSend=async(row)=>{ try{ await ElMessageBox.confirm('确定发送此通知吗？','提示',{ type:'info' }); await publishNotice(row.id); ElMessage.success('发送成功'); loadData() }catch{} }
onMounted(loadData)
</script>
<style lang="scss" scoped>
.app-container{padding:20px}.page-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px;h1{font-size:20px;font-weight:600;color:#303133;margin:0}}.search-form{margin-bottom:16px}.pagination-container{display:flex;justify-content:flex-end;margin-top:16px}
</style>