<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="姓名">
        <el-input v-model="searchForm.name" placeholder="请输入姓�? clearable />
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="searchForm.phone" placeholder="请输入电�? clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd" v-permission="'community:owner:add'">新增业主</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="idCard" label="身份证号" width="200" />
        <el-table-column label="业主类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.ownerType === 0 ? '' : 'warning'">{{ row.ownerType === 0 ? '个人' : '单位' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状�? width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="160" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'community:owner:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'community:owner:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="searchForm.pageNum"
        v-model:page-size="searchForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="关联用户" prop="userId">
          <el-input-number v-model="form.userId" :min="0" placeholder="请输入用户ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓�? />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电�? />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证�? />
        </el-form-item>
        <el-form-item label="身份证正�? prop="idCardFront">
          <el-input v-model="form.idCardFront" placeholder="请输入身份证正面图片URL" />
        </el-form-item>
        <el-form-item label="身份证反�? prop="idCardBack">
          <el-input v-model="form.idCardBack" placeholder="请输入身份证反面图片URL" />
        </el-form-item>
        <el-form-item label="业主类型" prop="ownerType">
          <el-radio-group v-model="form.ownerType">
            <el-radio :value="0">个人</el-radio>
            <el-radio :value="1">单位</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状�? prop="status">
          <el-radio-group v-model="form.status"><el-radio :value="1">正常</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备�? />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addOwner, updateOwner, deleteOwner, getOwnerPage } from '@/api/community/owner'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const searchForm = reactive({ pageNum: 1, pageSize: 10, name: '', phone: '' })
const form = reactive({ id: null, userId: null, name: '', phone: '', idCard: '', idCardFront: '', idCardBack: '', ownerType: 1, status: 1, remark: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑业主' : '新增业主')

const rules = {
  name: [{ required: true, message: '请输入姓�?, trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电�?, trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证�?, trigger: 'blur' }]
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getOwnerPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.name = ''; searchForm.phone = ''; handleSearch() }

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null; form.ownerType = 0; form.status = 0 }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) { await updateOwner(form) }
    else { await addOwner(form) }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该业主吗？', '提示', { type: 'warning' })
  try {
    await deleteOwner(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
