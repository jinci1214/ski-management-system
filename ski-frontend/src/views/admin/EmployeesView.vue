<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createEmployee,
  deleteEmployee,
  getEmployeePage,
  updateEmployee,
  updateEmployeeStatus
} from '../../api/admin'
import { useAuthStore } from '../../stores/auth'
import { formatDate } from '../../utils/format'

const auth = useAuthStore()
const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const editing = ref(false)
const formRef = ref()
const page = reactive({ pageNum: 1, pageSize: 10 })

const positions = ['系统管理员', '运营管理员', '票务管理员', '设备管理员', '安全管理员', '客服人员']
const currentAdminId = computed(() => Number(auth.admin?.id || 0))
const dialogTitle = computed(() => editing.value ? '编辑员工信息' : '新增员工')

const form = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  phone: '',
  position: '',
  status: 1
})

const rules = {
  username: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 2, max: 30, message: '账号长度应为 2—30 个字符', trigger: 'blur' }
  ],
  password: [{
    validator: (_rule, value, callback) => {
      if (!editing.value && !value) callback(new Error('请设置初始密码'))
      else if (value && (value.length < 3 || value.length > 40)) callback(new Error('密码长度应为 3—40 个字符'))
      else callback()
    },
    trigger: 'blur'
  }],
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { max: 30, message: '姓名不能超过 30 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入 11 位手机号码', trigger: 'blur' }
  ],
  position: [{ required: true, message: '请选择或填写职位', trigger: 'change' }]
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    username: '',
    password: '',
    name: '',
    phone: '',
    position: '',
    status: 1
  })
  formRef.value?.clearValidate()
}

const loadEmployees = async () => {
  loading.value = true
  try {
    const result = await getEmployeePage(page) || {}
    rows.value = Array.isArray(result.data) ? result.data : []
    total.value = Number(result.total || 0)
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  editing.value = false
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  editing.value = true
  Object.assign(form, {
    id: row.id,
    username: row.username || '',
    password: '',
    name: row.name || '',
    phone: row.phone || '',
    position: row.position || '',
    status: Number(row.status) === 0 ? 0 : 1
  })
  dialogVisible.value = true
}

const saveEmployee = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  const payload = {
    username: form.username.trim(),
    name: form.name.trim(),
    phone: form.phone.trim(),
    position: form.position.trim(),
    status: Number(form.status)
  }
  if (form.password) payload.password = form.password

  saving.value = true
  try {
    if (editing.value) {
      await updateEmployee(form.id, payload)
      ElMessage.success('员工信息已更新')
    } else {
      await createEmployee(payload)
      ElMessage.success('员工已创建')
    }
    dialogVisible.value = false
    await loadEmployees()
  } finally {
    saving.value = false
  }
}

const toggleStatus = async (row) => {
  const nextStatus = Number(row.status)
  try {
    await updateEmployeeStatus(row.id, nextStatus)
    ElMessage.success(nextStatus === 1 ? '员工账号已启用' : '员工账号已停用')
  } catch {
    row.status = nextStatus === 1 ? 0 : 1
  }
}

const removeEmployee = async (row) => {
  if (Number(row.id) === currentAdminId.value) {
    ElMessage.warning('不能删除当前登录账号')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定删除员工“${row.name || row.username}”吗？删除后无法恢复。`,
      '删除员工',
      { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }

  await deleteEmployee(row.id)
  ElMessage.success('员工已删除')
  if (rows.value.length === 1 && page.pageNum > 1) page.pageNum -= 1
  await loadEmployees()
}

const handlePageSize = (size) => {
  page.pageSize = size
  page.pageNum = 1
  loadEmployees()
}

loadEmployees()
</script>

<template>
  <div class="employees-page">
    <div class="page-heading">
      <div>
        <p>STAFF ACCOUNTS</p>
        <h2>员工管理</h2>
        <span>维护管理端员工资料、岗位和账号启用状态</span>
      </div>
      <el-button type="primary" @click="openCreate"><el-icon><Plus /></el-icon>新增员工</el-button>
    </div>

    <div class="notice-bar">
      <el-icon><InfoFilled /></el-icon>
      <span>停用账号后，该员工将无法登录管理端；当前登录账号不能被停用或删除。</span>
      <el-button link type="primary" :loading="loading" @click="loadEmployees">刷新列表</el-button>
    </div>

    <el-card class="table-card" shadow="never">
      <div class="table-heading">
        <div><strong>员工账号</strong><span>共 {{ total }} 名员工</span></div>
        <el-tag type="success" effect="plain" round>在职账号管理</el-tag>
      </div>

      <el-table v-loading="loading" :data="rows" stripe table-layout="fixed" empty-text="暂无员工数据">
        <el-table-column label="员工" min-width="170">
          <template #default="{ row }">
            <div class="employee-cell">
              <el-avatar :size="35">{{ (row.name || row.username || '员').slice(0, 1) }}</el-avatar>
              <div><strong>{{ row.name || '未填写姓名' }}</strong><span>@{{ row.username || '—' }}</span></div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="position" label="职位" min-width="125">
          <template #default="{ row }"><el-tag effect="light">{{ row.position || '未分配' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="130">
          <template #default="{ row }">{{ row.phone || '—' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="155">
          <template #default="{ row }">{{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm') }}</template>
        </el-table-column>
        <el-table-column label="账号状态" width="125" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              inline-prompt
              active-text="启用"
              inactive-text="停用"
              :disabled="Number(row.id) === currentAdminId"
              @change="toggleStatus(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="135" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" :disabled="Number(row.id) === currentAdminId" @click="removeEmployee(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page.pageNum"
          v-model:page-size="page.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadEmployees"
          @size-change="handlePageSize"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="min(92vw, 600px)"
      destroy-on-close
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="登录账号" prop="username">
            <el-input v-model.trim="form.username" maxlength="30" placeholder="例如：admin_li" autocomplete="off" />
          </el-form-item>
          <el-form-item :label="editing ? '重置密码（选填）' : '初始密码'" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              autocomplete="new-password"
              :placeholder="editing ? '不修改请留空' : '请输入初始密码'"
            />
          </el-form-item>
          <el-form-item label="员工姓名" prop="name">
            <el-input v-model.trim="form.name" maxlength="30" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model.trim="form.phone" maxlength="11" placeholder="请输入手机号码" />
          </el-form-item>
          <el-form-item label="职位" prop="position">
            <el-select v-model="form.position" filterable allow-create default-first-option placeholder="选择或输入职位" style="width: 100%">
              <el-option v-for="item in positions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="账号状态" prop="status">
            <el-radio-group v-model="form.status" :disabled="editing && Number(form.id) === currentAdminId">
              <el-radio-button :value="1">启用</el-radio-button>
              <el-radio-button :value="0">停用</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveEmployee">{{ editing ? '保存修改' : '创建员工' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 20px;
}

.page-heading p {
  margin: 0 0 4px;
  color: #1683b8;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1.5px;
}

.page-heading h2 {
  margin: 0;
  color: #102f49;
  font-size: 25px;
}

.page-heading span {
  display: block;
  margin-top: 7px;
  color: #7b8e9d;
  font-size: 13px;
}

.notice-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  padding: 13px 17px;
  border: 1px solid #cee6f0;
  border-radius: 11px;
  color: #56788e;
  background: #ebf7fb;
  font-size: 12px;
}

.notice-bar > .el-icon {
  flex: 0 0 auto;
  color: #1786b6;
  font-size: 17px;
}

.notice-bar .el-button {
  margin-left: auto;
}

.table-card {
  border-color: #e0eaf0;
  box-shadow: 0 8px 24px rgba(21, 72, 103, 0.045);
}

.table-card :deep(.el-card__body) {
  padding: 0 18px 18px;
}

.table-heading {
  display: flex;
  min-height: 66px;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.table-heading strong {
  color: #19374e;
  font-size: 16px;
}

.table-heading span {
  margin-left: 10px;
  color: #8a9ba8;
  font-size: 12px;
}

.employee-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.employee-cell :deep(.el-avatar) {
  flex: 0 0 auto;
  color: #fff;
  background: linear-gradient(135deg, #1479b8, #27b9cf);
  font-size: 13px;
}

.employee-cell strong,
.employee-cell span {
  display: block;
}

.employee-cell strong {
  color: #29485e;
  font-size: 13px;
}

.employee-cell span {
  margin-top: 3px;
  color: #91a1ad;
  font-size: 11px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  overflow-x: auto;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
}

@media (max-width: 680px) {
  .page-heading span,
  .notice-bar span {
    display: none;
  }

  .table-card :deep(.el-card__body) {
    padding: 0 10px 14px;
  }

  .pagination-wrap {
    justify-content: flex-start;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
