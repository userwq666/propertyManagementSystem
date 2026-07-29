<template>
  <div class="app-container">
    <div class="page-header">
      <h1>报修工单管理</h1>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">待派单</div>
            <div class="stat-value warning">{{ statistics.pendingDispatchCount }}</div>
          </div>
          <el-icon class="stat-icon warning"><warning /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">处理中</div>
            <div class="stat-value primary">{{ statistics.processingCount }}</div>
          </div>
          <el-icon class="stat-icon primary"><monitor /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">待确认</div>
            <div class="stat-value warning">{{ statistics.pendingConfirmCount }}</div>
          </div>
          <el-icon class="stat-icon warning"><question-mark-circle /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">已完成</div>
            <div class="stat-value success">{{ statistics.completedCount }}</div>
          </div>
          <el-icon class="stat-icon success"><check-circle /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="4" :xl="4">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">超时工单</div>
            <div class="stat-value danger">{{ statistics.timeoutCount }}</div>
          </div>
          <el-icon class="stat-icon danger"><clock /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>工单列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['repair:order:add']">
              <plus /> 新增工单
            </el-button>
            <el-button type="info" @click="handleRefresh">
              <refresh /> 刷新
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 标签页切换 -->
      <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick" class="mb-4">
        <el-tab-pane v-for="tab in tabs" :key="tab.key" :label="`${tab.label} ${tab.status ? '(' + getStatusCount(tab.status) + ')' : ''}`" :name="tab.key" />
      </el-tabs>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px" @keyup.enter="handleQuery">
        <el-form-item label="工单编号" prop="orderNo">
          <el-input v-model="queryParams.orderNo" placeholder="请输入工单编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入报修标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="queryParams.repairType" placeholder="请选择报修类型" clearable style="width: 180px">
            <el-option v-for="item in repairTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="queryParams.priority" placeholder="请选择优先级" clearable style="width: 180px">
            <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择工单状态" clearable style="width: 180px">
            <el-option v-for="item in orderStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修人" prop="reporterName">
          <el-input v-model="queryParams.reporterName" placeholder="请输入报修人" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="报修时间" prop="beginTime">
          <el-date-picker
            v-model="queryParams.beginTime"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 320px"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="处理人" prop="handlerName">
          <el-input v-model="queryParams.handlerName" placeholder="请输入处理人" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <search /> 查询
          </el-button>
          <el-button @click="resetQuery">
            <refresh /> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 工单表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="orderId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
        default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="orderNo" label="工单编号" width="160" align="center" show-overflow-tooltip />
        <el-table-column prop="title" label="报修标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="repairType" label="报修类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="repairTypeColorMap[scope.row.repairType] || ''" effect="dark">
              {{ getDictLabel(repairTypeOptions, scope.row.repairType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100" align="center">
          <template #default="scope">
            <el-tag :type="priorityColorMap[scope.row.priority] || ''" effect="dark">
              {{ getDictLabel(priorityOptions, scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="报修位置" min-width="150" show-overflow-tooltip />
        <el-table-column prop="reporterName" label="报修人" width="100" align="center" />
        <el-table-column prop="reporterPhone" label="报修电话" width="130" align="center" />
        <el-table-column prop="createTime" label="报修时间" width="180" align="center" />
        <el-table-column prop="handlerName" label="处理人" width="100" align="center" />
        <el-table-column prop="status" label="工单状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="orderStatusColorMap[scope.row.status] || ''" effect="dark">
              {{ getDictLabel(orderStatusOptions, scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="处理进度" width="120" align="center">
          <template #default="scope">
            <el-progress :percentage="scope.row.progress" :stroke-width="12" :show-text="true" :format="progressFormat" />
          </template>
        </el-table-column>
        <el-table-column prop="estimatedFinishTime" label="预计完成时间" width="180" align="center" />
        <el-table-column label="操作" align="center" width="320" fixed="right" class-name="small-padding">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleDetail(scope.row)" v-permission="['repair:order:detail']">详情</el-button>
            <el-button size="small" type="success" link @click="handleDispatch(scope.row)" v-if="canDispatch(scope.row)" v-permission="['repair:order:dispatch']">派单</el-button>
            <el-button size="small" type="warning" link @click="handleProcess(scope.row)" v-if="canProcess(scope.row)" v-permission="['repair:order:process']">处理</el-button>
            <el-button size="small" type="info" link @click="handleFinish(scope.row)" v-if="canFinish(scope.row)" v-permission="['repair:order:confirm']">完工</el-button>
            <el-button size="small" type="danger" link @click="handleEvaluate(scope.row)" v-if="canEvaluate(scope.row)" v-permission="['repair:order:evaluate']">评价</el-button>
            <el-button size="small" type="danger" link @click="handleCancel(scope.row)" v-if="canCancel(scope.row)" v-permission="['repair:order:cancel']">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="getList"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="detailTitle"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-descriptions :column="1" border class="detail-descriptions" v-if="detailData">
        <el-descriptions-item label="工单编号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="报修标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">
          <el-tag :type="repairTypeColorMap[detailData.repairType] || ''" effect="dark">
            {{ getDictLabel(repairTypeOptions, detailData.repairType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="priorityColorMap[detailData.priority] || ''" effect="dark">
            {{ getDictLabel(priorityOptions, detailData.priority) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修位置">{{ detailData.location }}</el-descriptions-item>
        <el-descriptions-item label="报修描述">{{ detailData.description }}</el-descriptions-item>
        <el-descriptions-item label="报修图片" v-if="detailData.images && detailData.images.length > 0">
          <el-image-viewer :url-list="detailData.images" :initial-index="0" />
        </el-descriptions-item>
        <el-descriptions-item label="报修人">{{ detailData.reporterName }}</el-descriptions-item>
        <el-descriptions-item label="报修电话">{{ detailData.reporterPhone }}</el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="所属房屋">{{ detailData.houseName }} - {{ detailData.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工单状态">
          <el-tag :type="orderStatusColorMap[detailData.status] || ''" effect="dark">
            {{ getDictLabel(orderStatusOptions, detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理进度">
          <el-progress :percentage="detailData.progress" :stroke-width="14" :show-text="true" :format="progressFormat" />
        </el-descriptions-item>
        <el-descriptions-item label="预计完成时间">{{ detailData.estimatedFinishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际完成时间">{{ detailData.actualFinishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="派单时间">{{ detailData.dispatchTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="派单备注">{{ detailData.dispatchRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="取消原因" v-if="detailData.status === '6'">{{ detailData.cancelReason }}</el-descriptions-item>
        <el-descriptions-item label="取消时间" v-if="detailData.status === '6'">{{ detailData.cancelTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 处理进度时间线 -->
      <div class="progress-timeline" v-if="detailData.progressLogs && detailData.progressLogs.length > 0">
        <h4>处理进度</h4>
        <el-timeline>
          <el-timeline-item
            v-for="(log, index) in detailData.progressLogs"
            :key="index"
            :timestamp="log.createTime"
            :type="index === 0 ? 'primary' : ''"
            :hollow="index !== 0"
          >
            <div class="timeline-content">
              <div class="timeline-title">{{ log.handlerName }} 更新了进度</div>
              <div class="timeline-progress">
                <el-progress :percentage="log.progress" :stroke-width="10" :show-text="true" :format="progressFormat" />
              </div>
              <div class="timeline-remark" v-if="log.processRemark">{{ log.processRemark }}</div>
              <div class="timeline-images" v-if="log.processImages && log.processImages.length > 0">
                <el-image
                  v-for="(img, imgIndex) in log.processImages"
                  :key="imgIndex"
                  :src="img"
                  :preview-src-list="log.processImages"
                  :initial-index="imgIndex"
                  class="timeline-image"
                />
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增/编辑工单弹窗 -->
    <el-dialog
      v-model="addDialogVisible"
      :title="addDialogTitle"
      width="700px"
      :close-on-click-modal="false"
      :before-close="closeAddDialog"
      destroy-on-close
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px" class="dialog-form">
        <el-form-item label="报修标题" prop="title" :rules="[{ required: true, message: '请输入报修标题', trigger: 'blur' }]">
          <el-input v-model="addForm.title" placeholder="请输入报修标题" />
        </el-form-item>
        <el-form-item label="报修类型" prop="repairType" :rules="[{ required: true, message: '请选择报修类型', trigger: 'change' }]">
          <el-select v-model="addForm.repairType" placeholder="请选择报修类型" style="width: 100%">
            <el-option v-for="item in repairTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority" :rules="[{ required: true, message: '请选择优先级', trigger: 'change' }]">
          <el-select v-model="addForm.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修位置" prop="location" :rules="[{ required: true, message: '请选择报修位置', trigger: 'change' }]">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="8">
              <el-select v-model="addForm.buildingId" placeholder="请选择楼栋" style="width: 100%" @change="handleBuildingChange">
                <el-option v-for="item in buildingList" :key="item.buildingId" :label="item.buildingName" :value="item.buildingId" />
              </el-select>
            </el-col>
            <el-col :span="16">
              <el-tree-select
                v-model="addForm.houseId"
                :props="houseTreeProps"
                :data="filteredHouseTreeData"
                placeholder="请选择房屋"
                style="width: 100%"
                check-strictly
                @change="handleHouseChange"
              />
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="报修描述" prop="description" :rules="[{ required: true, message: '请输入报修描述', trigger: 'blur' }]">
          <el-input v-model="addForm.description" type="textarea" placeholder="请详细描述故障情况" :rows="4" />
        </el-form-item>
        <el-form-item label="报修图片" prop="images">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
            :on-preview="handleImagePreview"
            :file-list="addForm.images"
            list-type="picture-card"
            :limit="5"
            :on-exceed="handleExceed"
            accept="image/*"
          >
            <el-icon><plus /></el-icon>
          </el-upload>
          <el-form-item :error="addForm.images.length === 0 ? '请至少上传一张图片' : ''" />
        </el-form-item>
        <el-form-item label="联系电话" prop="reporterPhone" :rules="[{ required: true, message: '请输入联系电话', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]">
          <el-input v-model="addForm.reporterPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="所属房屋" prop="houseId">
          <el-tree-select
            v-model="addForm.houseId"
            :props="houseTreeProps"
            :data="houseTreeData"
            placeholder="请选择房屋"
            style="width: 100%"
            check-strictly
            show-checkbox
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAddDialog">取消</el-button>
          <el-button type="primary" @click="submitAddForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 派单弹窗 -->
    <el-dialog
      v-model="dispatchDialogVisible"
      title="派单"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="dispatchFormRef" :model="dispatchForm" :rules="dispatchRules" label-width="100px" class="dialog-form">
        <el-form-item label="工单编号" prop="orderNo">
          <el-input v-model="dispatchForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="dispatchForm.title" disabled />
        </el-form-item>
        <el-form-item label="处理人" prop="handlerId" :rules="[{ required: true, message: '请选择处理人', trigger: 'change' }]">
          <el-select v-model="dispatchForm.handlerId" placeholder="请选择处理人" style="width: 100%" filterable>
            <el-option v-for="worker in workerList" :key="worker.workerId" :label="worker.workerName" :value="worker.workerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="预计完成时间" prop="estimatedFinishTime" :rules="[{ required: true, message: '请选择预计完成时间', trigger: 'change' }]">
          <el-date-picker v-model="dispatchForm.estimatedFinishTime" type="datetime" placeholder="请选择预计完成时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="派单备注" prop="dispatchRemark">
          <el-input v-model="dispatchForm.dispatchRemark" type="textarea" placeholder="请输入派单备注" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dispatchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDispatchForm">确定派单</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 处理进度更新弹窗 -->
    <el-dialog
      v-model="processDialogVisible"
      title="处理进度更新"
      width="700px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="processFormRef" :model="processForm" :rules="processRules" label-width="100px" class="dialog-form">
        <el-form-item label="工单编号" prop="orderNo">
          <el-input v-model="processForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="processForm.title" disabled />
        </el-form-item>
        <el-form-item label="当前进度" prop="progress" :rules="[{ required: true, message: '请选择处理进度', trigger: 'change' }]">
          <el-select v-model="processForm.progress" placeholder="请选择处理进度" style="width: 100%">
            <el-option v-for="item in progressOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注" prop="processRemark" :rules="[{ required: true, message: '请输入处理备注', trigger: 'blur' }]">
          <el-input v-model="processForm.processRemark" type="textarea" placeholder="请输入处理备注" :rows="3" />
        </el-form-item>
        <el-form-item label="进度图片" prop="processImages">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleProcessImageChange"
            :on-remove="handleProcessImageRemove"
            :on-preview="handleImagePreview"
            :file-list="processForm.processImages"
            list-type="picture-card"
            :limit="5"
            :on-exceed="handleExceed"
            accept="image/*"
          >
            <el-icon><plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="processDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitProcessForm">确定提交</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 完工确认弹窗 -->
    <el-dialog
      v-model="finishDialogVisible"
      title="完工确认"
      width="700px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="finishFormRef" :model="finishForm" :rules="finishRules" label-width="100px" class="dialog-form">
        <el-form-item label="工单编号" prop="orderNo">
          <el-input v-model="finishForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="finishForm.title" disabled />
        </el-form-item>
        <el-form-item label="完成描述" prop="finishDescription" :rules="[{ required: true, message: '请输入完成描述', trigger: 'blur' }]">
          <el-input v-model="finishForm.finishDescription" type="textarea" placeholder="请输入完成情况描述" :rows="3" />
        </el-form-item>
        <el-form-item label="完成图片" prop="finishImages" :rules="[{ required: true, message: '请上传完成图片', trigger: 'change' }]">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleFinishImageChange"
            :on-remove="handleFinishImageRemove"
            :on-preview="handleImagePreview"
            :file-list="finishForm.finishImages"
            list-type="picture-card"
            :limit="5"
            :on-exceed="handleExceed"
            accept="image/*"
          >
            <el-icon><plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="业主签名" prop="ownerSignature">
          <el-input v-model="finishForm.ownerSignature" type="textarea" placeholder="请输入业主签名/确认信息" :rows="2" />
        </el-form-item>
        <el-form-item label="业主评分" prop="ownerScore">
          <el-rate v-model="finishForm.ownerScore" :max="5" :show-text="true" :texts="['极差', '失望', '一般', '满意', '惊喜']" />
        </el-form-item>
        <el-form-item label="业主评价" prop="ownerEvaluate">
          <el-input v-model="finishForm.ownerEvaluate" type="textarea" placeholder="请输入业主评价内容" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="finishDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFinishForm">确定完工</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 评价回复弹窗 -->
    <el-dialog
      v-model="evaluateDialogVisible"
      title="评价回复"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="evaluateFormRef" :model="evaluateForm" label-width="100px" class="dialog-form">
        <el-form-item label="工单编号" prop="orderNo">
          <el-input v-model="evaluateForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input v-model="evaluateForm.content" disabled type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="评价图片" v-if="evaluateForm.images && evaluateForm.images.length > 0">
          <el-image-viewer :url-list="evaluateForm.images" :initial-index="0" />
        </el-form-item>
        <el-form-item label="回复内容" prop="reply" :rules="[{ required: true, message: '请输入回复内容', trigger: 'blur' }]">
          <el-input v-model="evaluateForm.reply" type="textarea" placeholder="请输入回复内容" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="evaluateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEvaluateReply">提交回复</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 取消工单弹窗 -->
    <el-dialog
      v-model="cancelDialogVisible"
      title="取消工单"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="cancelFormRef" :model="cancelForm" :rules="cancelRules" label-width="80px" class="dialog-form">
        <el-form-item label="工单编号" prop="orderNo">
          <el-input v-model="cancelForm.orderNo" disabled />
        </el-form-item>
        <el-form-item label="取消原因" prop="reason" :rules="[{ required: true, message: '请输入取消原因', trigger: 'blur' }]">
          <el-input v-model="cancelForm.reason" type="textarea" placeholder="请输入取消原因" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCancelForm">确定取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { Plus, Download, Refresh, Search, Warning, Monitor, QuestionFilled, CircleCheck, Clock, Edit, SwitchButton, Delete, Document, Upload, Picture, ZoomIn } from '@element-plus/icons-vue'
import {
  getRepairOrderList,
  getRepairOrderInfo,
  addRepairOrder,
  updateRepairOrder,
  deleteRepairOrder,
  getRepairStatistics,
  getRepairWorkerList, updateRepairOrderStatus, } from '@/api/repair/order'
import { getBuildingList } from '@/api/community/building'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()

// 根据楼栋过滤房屋树数据
const filteredHouseTreeData = computed(() => {
  if (!selectedBuildingId.value) {
    return houseTreeData.value
  }
  // 这里可以根据楼栋ID过滤房屋树数据
  // 假设房屋树数据中已经包含了楼栋信息
  return houseTreeData.value
})

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  title: '',
  repairType: '',
  priority: '',
  status: '',
  reporterName: '',
  handlerName: '',
  beginTime: '',
  endTime: ''
})

// 统计数据
const statistics = reactive({
  pendingDispatchCount: 0,
  processingCount: 0,
  pendingConfirmCount: 0,
  completedCount: 0,
  timeoutCount: 0
})

// 标签页
const tabs = ref(TabTypes)
const activeTab = ref('mine')

// 字典选项
const repairTypeOptions = ref(RepairTypeOptions)
const priorityOptions = ref(PriorityOptions)
const orderStatusOptions = ref(OrderStatusOptions)
const progressOptions = ref(ProgressOptions)

// 颜色映射
const orderStatusColorMap = OrderStatusColorMap
const priorityColorMap = PriorityColorMap
const repairTypeColorMap = RepairTypeColorMap

// 房屋树
const houseTreeData = ref([])
const houseTreeProps = ref({
  label: 'label',
  value: 'id',
  children: 'children'
})

// 维修人员列表
const workerList = ref([])

// 楼栋列表
const buildingList = ref([])
const selectedBuildingId = ref('')

// 详情弹窗
const detailDialogVisible = ref(false)
const detailTitle = ref('工单详情')
const detailData = ref(null)

// 新增/编辑弹窗
const addDialogVisible = ref(false)
const addDialogTitle = ref('新增工单')
const isAdd = ref(true)
const addFormRef = ref(null)

const addForm = reactive({
  title: '',
  repairType: '',
  priority: '2',
  location: '',
  description: '',
  images: [],
  reporterPhone: '',
  houseId: '',
  buildingId: ''
})

const addRules = reactive({
  title: [{ required: true, message: '请输入报修标题', trigger: 'blur' }],
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  location: [{ required: true, message: '请输入报修位置', trigger: 'blur' }],
  description: [{ required: true, message: '请输入报修描述', trigger: 'blur' }],
  reporterPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
})

// 派单弹窗
const dispatchDialogVisible = ref(false)
const dispatchFormRef = ref(null)

const dispatchForm = reactive({
  dispatchId: undefined,
  orderId: 0,
  orderNo: '',
  title: '',
  handlerId: '',
  estimatedFinishTime: '',
  dispatchRemark: ''
})

const dispatchRules = reactive({
  handlerId: [{ required: true, message: '请选择处理人', trigger: 'change' }],
  estimatedFinishTime: [{ required: true, message: '请选择预计完成时间', trigger: 'change' }]
})

// 处理进度弹窗
const processDialogVisible = ref(false)
const processFormRef = ref(null)

const processForm = reactive({
  orderId: 0,
  orderNo: '',
  title: '',
  progress: 0,
  processRemark: '',
  processImages: []
})

const processRules = reactive({
  progress: [{ required: true, message: '请选择处理进度', trigger: 'change' }],
  processRemark: [{ required: true, message: '请输入处理备注', trigger: 'blur' }]
})

// 完工确认弹窗
const finishDialogVisible = ref(false)
const finishFormRef = ref(null)

const finishForm = reactive({
  orderId: 0,
  orderNo: '',
  title: '',
  finishDescription: '',
  finishImages: [],
  ownerSignature: '',
  ownerScore: 5,
  ownerEvaluate: ''
})

const finishRules = reactive({
  finishDescription: [{ required: true, message: '请输入完成描述', trigger: 'blur' }],
  finishImages: [{ required: true, message: '请上传完成图片', trigger: 'change' }]
})

// 评价回复弹窗
const evaluateDialogVisible = ref(false)
const evaluateFormRef = ref(null)

const evaluateForm = reactive({
  evaluateId: 0,
  orderNo: '',
  content: '',
  images: [],
  reply: ''
})

// 取消工单弹窗
const cancelDialogVisible = ref(false)
const cancelFormRef = ref(null)

const cancelForm = reactive({
  orderId: 0,
  orderNo: '',
  reason: ''
})

const cancelRules = reactive({
  reason: [{ required: true, message: '请输入取消原因', trigger: 'blur' }]
})

// 进度条格式化
const progressFormat = (percentage) => {
  return `${percentage}%`
}

// 获取字典标签
const getDictLabel = (options, value) => {
  if (!options || !value) return ''
  const item = options.find(d => d.value === value)
  return item ? item.label : ''
}

// 获取状态计数
const getStatusCount = (status) => {
  switch (status) {
    case '1': return statistics.pendingDispatchCount
    case '2':
    case '3': return statistics.processingCount
    case '4': return statistics.pendingConfirmCount
    case '5': return statistics.completedCount
    default: return 0
  }
}

// 权限判断
const canDispatch = (row) => row.status === '1'
const canProcess = (row) => row.status === '2' || row.status === '3'
const canFinish = (row) => row.status === '4'
const canEvaluate = (row) => row.status === '5' && row.evaluateId
const canCancel = (row) => row.status !== '5' && row.status !== '6'

// 初始化
onMounted(async () => {
  await getBuildingListData()
  await getHouseTreeData()
  await getWorkerListData()
  await getStatistics()
  getList()
})

// 获取房屋树
const getHouseTreeData = async () => {
  try {
    const res = await getRepairOrderList({})
    houseTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取房屋树失败:', error)
  }
}

// 获取楼栋列表
const getBuildingListData = async () => {
  try {
    const res = await getBuildingList({ status: '0' })
    buildingList.value = res.rows || res.data?.rows || res.data || []
  } catch (error) {
    console.error('获取楼栋列表失败:', error)
  }
}

// 楼栋选择变化
const handleBuildingChange = (value) => {
  selectedBuildingId.value = value
  addForm.houseId = ''
  addForm.location = ''
}

// 房屋选择变化
const handleHouseChange = (value) => {
  if (value) {
    const building = buildingList.value.find(b => b.buildingId === Number(selectedBuildingId.value))
    const buildingName = building ? building.buildingName : ''
    addForm.location = `${buildingName} - ${value}`
  }
}

// 获取维修人员列表
const getWorkerListData = async () => {
  try {
    const res = await getRepairWorkerList({ status: '0' })
    workerList.value = res.rows || res.data?.rows || res.data || []
  } catch (error) {
    console.error('获取维修人员列表失败:', error)
  }
}

// 获取统计数据
const getStatistics = async () => {
  try {
    const res = await getRepairStatistics()
    const data = res.data || res
    statistics.pendingDispatchCount = data.pendingDispatchCount || 0
    statistics.processingCount = data.processingCount || 0
    statistics.pendingConfirmCount = data.pendingConfirmCount || 0
    statistics.completedCount = data.completedCount || 0
    statistics.timeoutCount = data.timeoutCount || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取工单列表
const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (activeTab.value !== 'mine' && activeTab.value !== 'all') {
      const tab = tabs.value.find(t => t.key === activeTab.value)
      if (tab) params.status = tab.status
    }
    const res = await getRepairOrderList(params)
    const response = res
    tableData.value = response.rows || response.data?.rows || []
    total.value = response.total || response.data?.total || 0
  } catch (error) {
    console.error('获取工单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 标签页切换
const handleTabClick = (tab) => {
  queryParams.pageNum = 1
  if (tab.name === 'mine') {
    queryParams.status = ''
  } else if (tab.name !== 'all') {
    queryParams.status = tab.props?.status || ''
  } else {
    queryParams.status = ''
  }
  getList()
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.orderNo = ''
  queryParams.title = ''
  queryParams.repairType = ''
  queryParams.priority = ''
  queryParams.status = ''
  queryParams.reporterName = ''
  queryParams.handlerName = ''
  queryParams.beginTime = ''
  queryParams.endTime = ''
  handleQuery()
}

// 日期范围变化
const handleDateChange = (value) => {
  if (value && value.length === 2) {
    queryParams.beginTime = value[0]
    queryParams.endTime = value[1]
  } else {
    queryParams.beginTime = ''
    queryParams.endTime = ''
  }
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.orderId)
}

// 刷新
const handleRefresh = () => {
  getStatistics()
  getList()
  ElMessage.success('刷新成功')
}

// 新增工单
const handleAdd = () => {
  isAdd.value = true
  addDialogTitle.value = '新增工单'
  resetAddForm()
  addDialogVisible.value = true
}

// 重置新增表单
const resetAddForm = () => {
  addForm.title = ''
  addForm.repairType = ''
  addForm.priority = '2'
  addForm.location = ''
  addForm.description = ''
  addForm.images = []
  addForm.reporterPhone = ''
  addForm.houseId = ''
  addForm.buildingId = ''
  selectedBuildingId.value = ''
  nextTick(() => {
    if (addFormRef.value) {
      addFormRef.value.clearValidate()
    }
  })
}

// 关闭新增弹窗
const closeAddDialog = (done) => {
  resetAddForm()
  done()
}

// 图片上传处理
const handleImageChange = (file, fileList) => {
  addForm.images = fileList.map(f => ({
    name: f.name,
    url: URL.createObjectURL(f.raw),
    raw: f.raw
  }))
}

const handleImageRemove = (file, fileList) => {
  addForm.images = fileList.map(f => ({
    name: f.name,
    url: f.url,
    raw: f.raw
  }))
}

const handleImagePreview = (file) => {
  // 预览由 el-image-viewer 自动处理
}

const handleExceed = (files, fileList) => {
  ElMessage.warning(`最多只能上传 5 张图片，当前已选择 ${fileList.length} 张`)
}

// 派单图片
const handleProcessImageChange = (file, fileList) => {
  processForm.processImages = fileList.map(f => ({
    name: f.name,
    url: URL.createObjectURL(f.raw),
    raw: f.raw
  }))
}

const handleProcessImageRemove = (file, fileList) => {
  processForm.processImages = fileList.map(f => ({
    name: f.name,
    url: f.url,
    raw: f.raw
  }))
}

// 完工图片
const handleFinishImageChange = (file, fileList) => {
  finishForm.finishImages = fileList.map(f => ({
    name: f.name,
    url: URL.createObjectURL(f.raw),
    raw: f.raw
  }))
}

const handleFinishImageRemove = (file, fileList) => {
  finishForm.finishImages = fileList.map(f => ({
    name: f.name,
    url: f.url,
    raw: f.raw
  }))
}

// 提交新增表单
const submitAddForm = async () => {
  if (!addFormRef.value) return
  try {
    await addFormRef.value.validate()
    if (addForm.images.length === 0) {
      ElMessage.warning('请至少上传一张报修图片')
      return
    }
    // 上传图片
    const imageUrls = await uploadImages(addForm.images.map(f => f.raw))
    const formData = {
      ...addForm,
      images: imageUrls,
      houseId: Number(addForm.houseId) || null
    }
    if (isAdd.value) {
      await addRepairOrder(formData)
      ElMessage.success('新增工单成功')
    } else {
      await updateRepairOrder(formData)
      ElMessage.success('修改工单成功')
    }
    addDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 上传图片
const uploadImages = async (files) => {
  const urls = []
  for (const file of files) {
    const formData = new FormData()
    formData.append('file', file)
    try {
      const res = await addRepairOrder(formData)
      urls.push(res.data?.url || res.url || res.data)
    } catch (error) {
      console.error('图片上传失败:', error)
      ElMessage.error('图片上传失败')
    }
  }
  return urls
}

// 详情
const handleDetail = async (row) => {
  try {
    const res = await getRepairOrderInfo(row.orderId)
    detailData.value = res.data || res
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 派单
const handleDispatch = (row) => {
  dispatchForm.dispatchId = undefined
  dispatchForm.orderId = row.orderId
  dispatchForm.orderNo = row.orderNo
  dispatchForm.title = row.title
  dispatchForm.handlerId = ''
  dispatchForm.estimatedFinishTime = ''
  dispatchForm.dispatchRemark = ''
  nextTick(() => {
    if (dispatchFormRef.value) {
      dispatchFormRef.value.clearValidate()
    }
  })
  dispatchDialogVisible.value = true
}

// 提交派单
const submitDispatchForm = async () => {
  if (!dispatchFormRef.value) return
  try {
    await dispatchFormRef.value.validate()
    await updateRepairOrderStatus({
      orderId: dispatchForm.orderId,
      handlerId: Number(dispatchForm.handlerId),
      estimatedFinishTime: dispatchForm.estimatedFinishTime,
      dispatchRemark: dispatchForm.dispatchRemark
    })
    ElMessage.success('派单成功')
    dispatchDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('派单失败:', error)
  }
}

// 处理进度
const handleProcess = (row) => {
  processForm.orderId = row.orderId
  processForm.orderNo = row.orderNo
  processForm.title = row.title
  processForm.progress = row.progress
  processForm.processRemark = ''
  processForm.processImages = []
  nextTick(() => {
    if (processFormRef.value) {
      processFormRef.value.clearValidate()
    }
  })
  processDialogVisible.value = true
}

// 提交处理进度
const submitProcessForm = async () => {
  if (!processFormRef.value) return
  try {
    await processFormRef.value.validate()
    const imageUrls = await uploadImages(processForm.processImages.map(f => f.raw))
    await updateRepairOrderStatus({
      orderId: processForm.orderId,
      progress: Number(processForm.progress),
      processRemark: processForm.processRemark,
      processImages: imageUrls
    })
    ElMessage.success('进度更新成功')
    processDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('进度更新失败:', error)
  }
}

// 完工确认
const handleFinish = (row) => {
  finishForm.orderId = row.orderId
  finishForm.orderNo = row.orderNo
  finishForm.title = row.title
  finishForm.finishDescription = ''
  finishForm.finishImages = []
  finishForm.ownerSignature = ''
  finishForm.ownerScore = 5
  finishForm.ownerEvaluate = ''
  nextTick(() => {
    if (finishFormRef.value) {
      finishFormRef.value.clearValidate()
    }
  })
  finishDialogVisible.value = true
}

// 提交完工
const submitFinishForm = async () => {
  if (!finishFormRef.value) return
  try {
    await finishFormRef.value.validate()
    const imageUrls = await uploadImages(finishForm.finishImages.map(f => f.raw))
    await updateRepairOrderStatus({
      orderId: finishForm.orderId,
      finishDescription: finishForm.finishDescription,
      finishImages: imageUrls,
      ownerSignature: finishForm.ownerSignature,
      ownerScore: finishForm.ownerScore,
      ownerEvaluate: finishForm.ownerEvaluate
    })
    ElMessage.success('完工确认成功')
    finishDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('完工确认失败:', error)
  }
}

// 评价回复
const handleEvaluate = async (row) => {
  try {
    const res = await getRepairOrderInfo(row.orderId)
    const data = res.data || res
    evaluateForm.evaluateId = data.evaluateId
    evaluateForm.orderNo = data.orderNo
    evaluateForm.content = data.evaluateContent
    evaluateForm.images = data.evaluateImages || []
    evaluateForm.reply = data.evaluateReply || ''
    evaluateDialogVisible.value = true
  } catch (error) {
    console.error('获取评价详情失败:', error)
  }
}

// 提交评价回复
const submitEvaluateReply = async () => {
  if (!evaluateFormRef.value) return
  try {
    await evaluateFormRef.value.validate()
    await updateRepairOrderStatus(evaluateForm.evaluateId, evaluateForm.reply)
    ElMessage.success('回复成功')
    evaluateDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('回复失败:', error)
  }
}

// 取消工单
const handleCancel = (row) => {
  cancelForm.orderId = row.orderId
  cancelForm.orderNo = row.orderNo
  cancelForm.reason = ''
  nextTick(() => {
    if (cancelFormRef.value) {
      cancelFormRef.value.clearValidate()
    }
  })
  cancelDialogVisible.value = true
}

// 提交取消
const submitCancelForm = async () => {
  if (!cancelFormRef.value) return
  try {
    await cancelFormRef.value.validate()
    await updateRepairOrderStatus(cancelForm.orderId, cancelForm.reason)
    ElMessage.success('取消成功')
    cancelDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('取消失败:', error)
  }
}


</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  h1 {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
}

.mb-4 {
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  .stat-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .stat-label {
      font-size: 14px;
      color: #909399;
    }
    .stat-value {
      font-size: 28px;
      font-weight: 600;
      &.warning { color: #E6A23C; }
      &.primary { color: #409EFF; }
      &.success { color: #67C23A; }
      &.danger { color: #F56C6C; }
    }
  }
  .stat-icon {
    font-size: 24px;
    &.warning { color: #E6A23C; }
    &.primary { color: #409EFF; }
    &.success { color: #67C23A; }
    &.danger { color: #F56C6C; }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-form {
  padding: 10px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-descriptions {
  :deep(.el-descriptions__row) {
    &:nth-child(odd) {
      :deep(.el-descriptions__cell) {
        background-color: #fafafa;
      }
    }
  }
  :deep(.el-descriptions__label) {
    font-weight: 600;
    color: #606266;
  }
}

.upload-demo {
  :deep(.el-upload-list__item) {
    width: 100px;
    height: 100px;
    :deep(.el-upload-list__item-thumbnail) {
      width: 100px;
      height: 100px;
    }
  }
  :deep(.el-upload--picture-card) {
    width: 100px;
    height: 100px;
    :deep(.el-upload__input) {
      width: 100px;
      height: 100px;
    }
  }
}

.small-padding {
  :deep(.cell) { display: flex; align-items: center; gap: 4px; flex-wrap: wrap; }
  :deep(.el-table__cell) {
    padding: 5px 10px;
  }
}

.fixed-width {
  width: 320px;
}

.progress-timeline {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  
  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20px;
  }
  
  .timeline-content {
    .timeline-title {
      font-size: 14px;
      color: #303133;
      margin-bottom: 8px;
    }
    
    .timeline-progress {
      margin-bottom: 8px;
    }
    
    .timeline-remark {
      font-size: 13px;
      color: #606266;
      margin-bottom: 8px;
    }
    
    .timeline-images {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      .timeline-image {
        width: 80px;
        height: 80px;
        border-radius: 4px;
        object-fit: cover;
      }
    }
  }
}
</style>
