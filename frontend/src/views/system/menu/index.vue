<template>
  <div class="app-container">
    <div class="page-header">
      <h1>菜单管理</h1>
      <el-button type="primary" @click="handleAdd(null)"><el-icon><Plus /></el-icon> 新增菜单</el-button>
    </div>

    <el-card>
      <el-table v-loading="loading" :data="tableData" row-key="id" border stripe style="width:100%" default-expand-all :tree-props="{ children:'children', hasChildren:'hasChildren' }">
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="menuType" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="menuTypeColor(row.menuType)" size="small">{{ menuTypeLabel(row.menuType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="160" />
        <el-table-column prop="component" label="组件路径" width="200" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" width="180" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch :model-value="isEnabled(row.status)" @change="(v) => handleStatusChange(row, v)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleAdd(row)">新增子级</el-button>
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="dialog.form" :rules="formRules" label-width="90px">
        <el-form-item label="上级菜单">
          <el-tree-select v-model="dialog.form.parentId" :data="treeSelectData" :props="{ label:'menuName', value:'id', children:'children' }" placeholder="无(顶级菜单)" clearable check-strictly style="width:100%" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="dialog.form.menuType" placeholder="选择菜单类型" style="width:100%" @change="onTypeChange">
            <el-option label="目录" :value="0" />
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="dialog.form.menuName" placeholder="菜单名称" />
        </el-form-item>
        <el-form-item v-if="dialog.form.menuType!==2" label="路由路径">
          <el-input v-model="dialog.form.path" placeholder="路由路径" />
        </el-form-item>
        <el-form-item v-if="dialog.form.menuType===1" label="组件路径">
          <el-input v-model="dialog.form.component" placeholder="组件路径" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="dialog.form.perms" placeholder="权限标识" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dialog.form.sort" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dialog.form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { Plus } from '@element-plus/icons-vue'
import { getMenuList, getMenuTree, getMenuInfo, addMenu, updateMenu, deleteMenu } from '@/api/system/menu'

const loading = ref(false)
const tableData = ref([])
const treeSelectData = ref([])
const formRef = ref(null)

const dialog = reactive({
  visible:false, title:'', loading:false,
  form: { id:null, parentId:null, menuName:'', path:'', component:'', perms:'', menuType:1, sort:0, status:1 }
})

const formRules = {
  menuName: [{ required:true, message:'请输入菜单名称', trigger:'blur' }],
  menuType: [{ required:true, message:'请选择菜单类型', trigger:'change' }]
}

const menuTypeColor = (t) => ({0:'', 1:'success', 2:'warning'})[t]||''
const menuTypeLabel = (t) => ({0:'目录', 1:'菜单', 2:'按钮'})[t]||'未知'
const isEnabled = (s) => (typeof s==='number'?s===1:s?.value===1)

const onTypeChange = () => { dialog.form.path=''; dialog.form.component=''; dialog.form.perms='' }

const loadData = async () => {
  loading.value = true
  try { const res = await getMenuList({}); tableData.value = res.data||[]; treeSelectData.value = res.data||[] } catch { tableData.value = [] }
  loading.value = false
}

const resetForm = () => {
  dialog.form = { id:null, parentId:null, menuName:'', path:'', component:'', perms:'', menuType:1, sort:0, status:1 }
  formRef.value?.resetFields()
}

const handleAdd = (parent) => {
  dialog.title = parent ? `新增子级 (${parent.menuName})` : '新增菜单'
  resetForm()
  if (parent) dialog.form.parentId = parent.id
  dialog.visible = true
}

const handleEdit = async (row) => {
  dialog.title = '编辑菜单'
  try {
    const res = await getMenuInfo(row.id)
    const m = res.data
    dialog.form = {
      id: m.id, parentId: m.parentId||null,
      menuName: m.menuName, path: m.path||'', component: m.component||'',
      perms: m.perms||'',
      menuType: typeof m.menuType==='number'?m.menuType:(m.menuType?.value??1),
      sort: m.sort||0,
      status: typeof m.status==='number'?m.status:(m.status?.value??1)
    }
    dialog.visible = true
  } catch {}
}

const submitForm = async () => {
  try { await formRef.value.validate() } catch { return }
  dialog.loading = true
  try {
    if (dialog.form.id) { await updateMenu(dialog.form) } else { await addMenu(dialog.form) }
    ElMessage.success(dialog.form.id?'修改成功':'新增成功')
    dialog.visible = false; loadData()
  } catch {}
  dialog.loading = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除菜单 "${row.menuName}" 吗？`, '提示', { type:'warning' })
    await deleteMenu(row.id)
    ElMessage.success('删除成功'); loadData()
  } catch {}
}

const handleStatusChange = async (row, v) => {
  try { await updateMenu({ id:row.id, status:v?1:0 }); ElMessage.success(v?'已启用':'已禁用'); loadData() } catch {}
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.app-container { padding:20px; }
.page-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;
  h1 { font-size:20px; font-weight:600; color:#303133; margin:0; }
}
</style>