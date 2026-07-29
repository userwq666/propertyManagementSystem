<template>
  <div class="app-container">
    <div class="page-header">
      <h1>房屋信息</h1>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增房屋</el-button>
    </div>
    <el-card>
      <el-form :model="query" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="房间号"><el-input v-model="query.roomNo" placeholder="房间号" clearable style="width:150px" /></el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="query.buildingId" placeholder="全部" clearable style="width:160px">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingNo" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.houseStatus" placeholder="全部" clearable style="width:120px">
            <el-option v-for="s in houseStatusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery"><el-icon><RefreshRight /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column label="楼栋" width="100">
          <template #default="{ row }">{{ getBuildingNo(row.buildingId) }}</template>
        </el-table-column>
        <el-table-column prop="area" label="面积(m²)" width="90" align="right" />
        <el-table-column prop="houseType" label="户型" width="80" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="houseStatusColor(row.houseStatus)" size="small">{{ houseStatusLabel(row.houseStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerId" label="业主ID" width="80" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
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

    <el-dialog v-model="dialog.visible" :title="dialog.title" width="460px" @close="resetForm">
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="dialog.form.buildingId" placeholder="选择楼栋" style="width:100%">
            <el-option v-for="b in buildings" :key="b.id" :label="b.buildingNo" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="dialog.form.roomNo" placeholder="如 101" />
        </el-form-item>
        <el-form-item label="面积(m²)" prop="area">
          <el-input-number v-model="dialog.form.area" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="户型">
          <el-input v-model="dialog.form.houseType" placeholder="如 2室1厅" />
        </el-form-item>
        <el-form-item label="房屋状态" prop="houseStatus">
          <el-select v-model="dialog.form.houseStatus" placeholder="选择状态" style="width:100%">
            <el-option v-for="s in houseStatusOptions" :key="s.value" :label="s.label" :value="s.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主ID">
          <el-input v-model="dialog.form.ownerId" placeholder="业主ID" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dialog.form.remark" type="textarea" :rows="2" placeholder="备注" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { getHouseList, getHouseInfo, addHouse, updateHouse, deleteHouse } from '@/api/community/house'
import { getBuildingList } from '@/api/community/building'

const loading = ref(false); const tableData = ref([]); const total = ref(0); const buildings = ref([]); const formRef = ref(null)
const query = reactive({ pageNum:1, pageSize:10, roomNo:'', buildingId:null, houseStatus:null })
const dialog = reactive({ visible:false, title:'', loading:false, form:{ id:null, buildingId:null, roomNo:'', area:null, houseType:'', houseStatus:0, ownerId:null, remark:'' } })
const houseStatusOptions = [{ value:0, label:'空置' }, { value:1, label:'已入住' }, { value:2, label:'出租' }]
const rules = { roomNo:[{ required:true, message:'请输入房间号', trigger:'blur' }], buildingId:[{ required:true, message:'请选择楼栋', trigger:'change' }] }

const houseStatusColor = (s) => ({0:'info',1:'success',2:'warning'})[s]||''
const houseStatusLabel = (s) => houseStatusOptions.find(o=>o.value===(typeof s==='number'?s:s?.value))?.label||''
const getBuildingNo = (bid) => buildings.value.find(b=>b.id===bid)?.buildingNo||''

const loadBuildings = async () => {
  try { const res = await getBuildingList({ pageNum:1, pageSize:999 }); buildings.value = res.data?.records||[] } catch {}
}
const loadData = async () => {
  loading.value = true
  try { const res = await getHouseList(query); tableData.value = res.data?.records||[]; total.value = res.data?.total||0 } catch { tableData.value = [] }
  loading.value = false
}
const handleQuery = () => { query.pageNum=1; loadData() }
const resetQuery = () => { Object.assign(query,{ pageNum:1,pageSize:10,roomNo:'',buildingId:null,houseStatus:null }); loadData() }
const resetForm = () => { dialog.form = { id:null, buildingId:null, roomNo:'', area:null, houseType:'', houseStatus:0, ownerId:null, remark:'' }; formRef.value?.resetFields() }
const handleAdd = () => { dialog.title='新增房屋'; resetForm(); dialog.visible=true }
const handleEdit = async (row) => {
  dialog.title='编辑房屋'
  try { const res = await getHouseInfo(row.id); const d = res.data; dialog.form = { id:d.id, buildingId:d.buildingId, roomNo:d.roomNo, area:d.area, houseType:d.houseType||'', houseStatus:typeof d.houseStatus==='number'?d.houseStatus:(d.houseStatus?.value??0), ownerId:d.ownerId, remark:d.remark||'' }; dialog.visible=true } catch {}
}
const submitForm = async () => {
  try { await formRef.value.validate() } catch { return }
  dialog.loading = true
  try { if (dialog.form.id) { await updateHouse(dialog.form) } else { await addHouse(dialog.form) }; ElMessage.success(dialog.form.id?'修改成功':'新增成功'); dialog.visible=false; loadData() } catch {}
  dialog.loading = false
}
const handleDelete = async (row) => {
  try { await ElMessageBox.confirm(`确定删除 "${row.roomNo}" 吗？`,'提示',{ type:'warning' }); await deleteHouse(row.id); ElMessage.success('删除成功'); loadData() } catch {}
}
onMounted(() => { loadBuildings(); loadData() })
</script>

<style lang="scss" scoped>
.app-container { padding:20px; }
.page-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px; h1 { font-size:20px; font-weight:600; color:#303133; margin:0; } }
.search-form { margin-bottom:16px; }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px; }
</style>