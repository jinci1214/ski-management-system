<template>
  <section v-if="config" class="resource-page">
    <header class="resource-header">
      <div>
        <p class="resource-eyebrow">业务资源管理</p>
        <h1>{{ config.title }}</h1>
        <p>{{ config.subtitle }}</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openCreate">
        {{ config.createText }}
      </el-button>
    </header>

    <el-card class="search-card" shadow="never">
      <el-form class="search-form" :model="query" label-position="top" @submit.prevent="handleSearch">
        <el-form-item
          v-for="field in config.searchFields"
          :key="field.prop"
          :label="field.label"
        >
          <el-select
            v-if="field.type === 'select'"
            v-model="query[field.prop]"
            clearable
            filterable
            :placeholder="`全部${field.label}`"
          >
            <el-option
              v-for="option in fieldOptions(field)"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
          <el-input
            v-else
            v-model.trim="query[field.prop]"
            clearable
            :placeholder="field.placeholder || `请输入${field.label}`"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <div class="search-actions">
          <el-button type="primary" :icon="Search" native-type="submit">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <div>
          <strong>数据列表</strong>
          <span>共 {{ filteredRows.length }} 条</span>
        </div>
        <el-button text type="primary" :icon="Refresh" :loading="loading" @click="loadData">
          刷新
        </el-button>
      </div>

      <el-alert
        v-if="serverTotal > rows.length"
        class="limit-alert"
        type="warning"
        :closable="false"
        show-icon
        :title="`当前数据较多，已加载前 ${rows.length} 条记录用于查询和分页。`"
      />

      <el-table
        v-loading="loading"
        :data="pageRows"
        row-key="id"
        stripe
        :empty-text="emptyText"
      >
        <el-table-column
          v-for="column in config.columns"
          :key="column.prop"
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :min-width="column.minWidth"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <el-image
              v-if="column.format === 'image' && row[column.prop]"
              class="table-image"
              :src="row[column.prop]"
              :preview-src-list="[row[column.prop]]"
              preview-teleported
              fit="cover"
            >
              <template #error>
                <div class="image-placeholder">暂无</div>
              </template>
            </el-image>
            <span v-else-if="column.format === 'image'" class="muted-text">未上传</span>
            <el-tag
              v-else-if="column.tag && displayValue(row, column) !== '—'"
              :type="tagType(row, column)"
              effect="light"
            >
              {{ displayValue(row, column) }}
            </el-tag>
            <span v-else>{{ displayValue(row, column) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="154" fixed="right" align="center">
          <template #default="{ row }">
            <el-button text type="primary" :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button text type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="filteredRows.length" class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="filteredRows.length"
          layout="total, sizes, prev, pager, next"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      class="resource-dialog"
      :title="dialogMode === 'create' ? config.createText : `编辑${config.entityName}`"
      width="760px"
      destroy-on-close
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <div v-loading="dialogLoading">
        <el-form
          ref="formRef"
          :model="form"
          :rules="formRules"
          label-position="top"
          status-icon
        >
          <div class="form-grid">
            <el-form-item
              v-for="field in config.formFields"
              :key="field.prop"
              :prop="field.prop"
              :label="field.label"
              :class="{ 'form-item-wide': field.wide }"
            >
              <template v-if="field.type === 'select'">
                <el-select
                  v-model="form[field.prop]"
                  filterable
                  clearable
                  :disabled="isFieldDisabled(field)"
                  :placeholder="`请选择${field.label}`"
                >
                  <el-option
                    v-for="option in fieldOptions(field, true)"
                    :key="option.value"
                    :label="option.label"
                    :value="option.value"
                  />
                </el-select>
              </template>

              <el-input-number
                v-else-if="field.type === 'number'"
                v-model="form[field.prop]"
                class="full-width"
                controls-position="right"
                :disabled="isFieldDisabled(field)"
                :min="field.min"
                :max="field.max"
                :precision="field.precision"
              />

              <el-date-picker
                v-else-if="field.type === 'datetime'"
                v-model="form[field.prop]"
                class="full-width"
                type="datetime"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DDTHH:mm:ss"
                :disabled="isFieldDisabled(field)"
                :placeholder="`请选择${field.label}`"
              />

              <el-date-picker
                v-else-if="field.type === 'date'"
                v-model="form[field.prop]"
                class="full-width"
                type="date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled="isFieldDisabled(field)"
                :placeholder="`请选择${field.label}`"
              />

              <el-time-picker
                v-else-if="field.type === 'time'"
                v-model="form[field.prop]"
                class="full-width"
                format="HH:mm"
                value-format="HH:mm:ss"
                :disabled="isFieldDisabled(field)"
                :placeholder="`请选择${field.label}`"
              />

              <div v-else-if="field.type === 'file'" class="upload-field">
                <el-upload
                  v-model:file-list="uploadFiles"
                  :auto-upload="false"
                  :limit="1"
                  :accept="field.accept"
                  :on-change="handleFileChange"
                  :on-remove="handleFileRemove"
                  :on-exceed="handleFileExceed"
                >
                  <el-button :icon="Upload">选择图片</el-button>
                  <template #tip>
                    <div class="el-upload__tip">支持 JPG、PNG、WEBP，单次选择一张图片。</div>
                  </template>
                </el-upload>
                <el-image
                  v-if="dialogMode === 'edit' && form.image && !form.imageFile"
                  class="current-image"
                  :src="form.image"
                  :preview-src-list="[form.image]"
                  preview-teleported
                  fit="cover"
                />
              </div>

              <el-input
                v-else
                v-model.trim="form[field.prop]"
                :type="field.type === 'textarea' ? 'textarea' : 'text'"
                :rows="field.rows || 4"
                :maxlength="field.maxlength"
                :show-word-limit="Boolean(field.maxlength && field.type === 'textarea')"
                :disabled="isFieldDisabled(field)"
                :placeholder="field.placeholder || `请输入${field.label}`"
              />

              <div v-if="field.tip" class="field-tip">{{ field.tip }}</div>
            </el-form-item>
          </div>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" :disabled="dialogLoading" @click="handleSubmit">
          {{ submitting ? '保存中' : '保存' }}
        </el-button>
      </template>
    </el-dialog>
  </section>

  <el-result
    v-else
    icon="warning"
    title="无法识别的管理资源"
    sub-title="请从左侧管理菜单重新进入该页面。"
  />
</template>

<script setup>
import { computed, nextTick, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Refresh, Search, Upload } from '@element-plus/icons-vue'
import {
  createResource,
  deleteResource,
  getEmployeePage,
  getResourceDetail,
  getResourcePage,
  updateResource,
  uploadImage
} from '../../api/admin'
import {
  getCoaches,
  getFacilities,
  getResorts,
  getSkiRuns
} from '../../api/public'
import { resourceConfigs } from './resourceConfigs'

const DATA_FETCH_LIMIT = 1000

const route = useRoute()
const resourceKey = computed(() => String(route.meta.resource || ''))
const config = computed(() => resourceConfigs[resourceKey.value])

const loading = ref(false)
const dialogLoading = ref(false)
const submitting = ref(false)
const rows = ref([])
const serverTotal = ref(0)
const query = reactive({})
const pagination = reactive({ pageNum: 1, pageSize: 10 })

const dialogVisible = ref(false)
const dialogMode = ref('create')
const editingId = ref(null)
const formRef = ref()
const form = reactive({})
const uploadFiles = ref([])

const lookupData = reactive({
  resorts: [],
  skiRuns: [],
  facilities: [],
  coaches: [],
  employees: []
})

const lookupLoaders = {
  resorts: getResorts,
  skiRuns: getSkiRuns,
  facilities: getFacilities,
  coaches: getCoaches,
  employees: () => getEmployeePage({ pageNum: 1, pageSize: DATA_FETCH_LIMIT })
}

const hasActiveQuery = computed(() =>
  config.value?.searchFields.some((field) => {
    const value = query[field.prop]
    return value !== '' && value !== null && typeof value !== 'undefined'
  })
)

const filteredRows = computed(() => {
  if (!config.value) return []

  return rows.value.filter((row) => config.value.searchFields.every((field) => {
    const expected = query[field.prop]
    if (expected === '' || expected === null || typeof expected === 'undefined') return true

    if (field.type === 'keyword') {
      const keyword = String(expected).trim().toLowerCase()
      if (!keyword) return true
      return field.keys.some((key) => String(row[key] ?? '').toLowerCase().includes(keyword))
    }

    if (field.type === 'input') {
      return String(row[field.prop] ?? '').toLowerCase().includes(String(expected).toLowerCase())
    }

    return String(row[field.prop] ?? '') === String(expected)
  }))
})

const pageRows = computed(() => {
  const start = (pagination.pageNum - 1) * pagination.pageSize
  return filteredRows.value.slice(start, start + pagination.pageSize)
})

const emptyText = computed(() => hasActiveQuery.value ? '没有符合查询条件的数据' : '暂无数据，可以点击右上角新增')

const formRules = computed(() => {
  const rules = {}
  for (const field of config.value?.formFields || []) {
    const fieldRules = []
    if (field.required) {
      fieldRules.push({
        required: true,
        message: field.type === 'select' || ['date', 'datetime', 'time'].includes(field.type)
          ? `请选择${field.label}`
          : `请输入${field.label}`,
        trigger: field.type === 'input' || field.type === 'textarea' || !field.type ? 'blur' : 'change'
      })
    }
    if (field.type === 'number' && (typeof field.min === 'number' || typeof field.max === 'number')) {
      fieldRules.push({
        type: 'number',
        min: field.min,
        max: field.max,
        message: numberRangeMessage(field),
        trigger: 'change'
      })
    }
    if (fieldRules.length) rules[field.prop] = fieldRules
  }
  return rules
})

watch(resourceKey, async () => {
  dialogVisible.value = false
  pagination.pageNum = 1
  initializeQuery()
  await Promise.all([loadData(), loadLookups()])
}, { immediate: true })

watch([() => filteredRows.value.length, () => pagination.pageSize], () => {
  const maxPage = Math.max(1, Math.ceil(filteredRows.value.length / pagination.pageSize))
  if (pagination.pageNum > maxPage) pagination.pageNum = maxPage
})

function normalizeCollection(result) {
  if (Array.isArray(result)) return result
  if (Array.isArray(result?.data)) return result.data
  if (Array.isArray(result?.records)) return result.records
  return []
}

async function loadData() {
  if (!config.value) return
  loading.value = true
  try {
    const result = await getResourcePage(resourceKey.value, {
      pageNum: 1,
      pageSize: DATA_FETCH_LIMIT
    })
    rows.value = normalizeCollection(result)
    serverTotal.value = Number(result?.total ?? rows.value.length)
  } catch {
    rows.value = []
    serverTotal.value = 0
  } finally {
    loading.value = false
  }
}

async function loadLookups() {
  const names = config.value?.lookups || []
  await Promise.all(names.map(async (name) => {
    const loader = lookupLoaders[name]
    if (!loader) return
    try {
      lookupData[name] = normalizeCollection(await loader())
    } catch {
      lookupData[name] = []
    }
  }))
}

function initializeQuery() {
  for (const key of Object.keys(query)) delete query[key]
  for (const field of config.value?.searchFields || []) query[field.prop] = ''
}

function initializeForm(record = {}) {
  for (const key of Object.keys(form)) delete form[key]

  const defaults = config.value?.defaults || {}
  for (const field of config.value?.formFields || []) {
    if (field.type === 'file') {
      form[field.prop] = null
      continue
    }

    if (record[field.prop] !== null && typeof record[field.prop] !== 'undefined') {
      form[field.prop] = record[field.prop]
    } else if (Object.prototype.hasOwnProperty.call(defaults, field.prop)) {
      form[field.prop] = defaults[field.prop]
    } else if (field.defaultNow) {
      form[field.prop] = formatDateTime(new Date())
    } else if (field.defaultToday) {
      form[field.prop] = formatDate(new Date())
    } else {
      form[field.prop] = field.type === 'input' || field.type === 'textarea' || !field.type ? '' : null
    }
  }

  form.id = record.id ?? null
  form.image = record.image || ''
  uploadFiles.value = []
}

function handleSearch() {
  pagination.pageNum = 1
}

function handleReset() {
  initializeQuery()
  pagination.pageNum = 1
}

function fieldOptions(field, includeCurrentValue = false) {
  let options = field.options || []
  if (field.lookup) {
    options = (lookupData[field.lookup] || []).map((item) => ({
      label: item[field.lookupLabel || 'name'] || `ID ${item.id}`,
      value: item.id
    }))
  }

  if (includeCurrentValue && field.lookup) {
    const current = form[field.prop]
    const exists = options.some((option) => String(option.value) === String(current))
    if (current !== null && current !== '' && typeof current !== 'undefined' && !exists) {
      options = [...options, { label: `原关联记录（ID ${current}）`, value: current }]
    }
  }
  return options
}

function displayValue(row, column) {
  const value = row[column.prop]
  if ((value === null || typeof value === 'undefined' || value === '') && column.fallbackProp) {
    return row[column.fallbackProp] || '—'
  }
  if (value === null || typeof value === 'undefined' || value === '') return '—'

  if (column.lookup) {
    const item = lookupData[column.lookup]?.find((candidate) => String(candidate.id) === String(value))
    return item?.name || `ID ${value}`
  }

  if (column.options) {
    return column.options.find((option) => String(option.value) === String(value))?.label ?? value
  }

  if (column.format === 'money') {
    const number = Number(value)
    return Number.isFinite(number) ? `￥${number.toFixed(2)}` : value
  }
  if (column.format === 'datetime') return formatDisplayDateTime(value)
  if (column.format === 'date') return String(value).slice(0, 10)
  if (column.format === 'time') return String(value).slice(0, 5)
  return `${value}${column.suffix || ''}`
}

function tagType(row, column) {
  const value = row[column.prop]
  if (column.tagTypes && Object.prototype.hasOwnProperty.call(column.tagTypes, value)) {
    return column.tagTypes[value]
  }
  return 'primary'
}

function openCreate() {
  dialogMode.value = 'create'
  editingId.value = null
  dialogLoading.value = false
  initializeForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function openEdit(row) {
  dialogMode.value = 'edit'
  editingId.value = row.id
  initializeForm(row)
  dialogVisible.value = true
  dialogLoading.value = true
  await nextTick()
  formRef.value?.clearValidate()

  try {
    const detail = await getResourceDetail(resourceKey.value, row.id)
    initializeForm(detail || row)
  } catch {
    initializeForm(row)
  } finally {
    dialogLoading.value = false
    nextTick(() => formRef.value?.clearValidate())
  }
}

function isFieldDisabled(field) {
  return dialogMode.value === 'edit' && field.editDisabled
}

function handleFileChange(uploadFile) {
  if (!uploadFile.raw?.type?.startsWith('image/')) {
    ElMessage.warning('只能选择图片文件')
    uploadFiles.value = []
    return
  }
  if (uploadFile.raw.size > 5 * 1024 * 1024) {
    ElMessage.warning('图片不能超过 5MB')
    uploadFiles.value = []
    return
  }
  form.imageFile = uploadFile.raw || null
  uploadFiles.value = uploadFile.raw ? [uploadFile] : []
}

function handleFileRemove() {
  form.imageFile = null
  uploadFiles.value = []
}

function handleFileExceed() {
  ElMessage.warning('每次只能选择一张图片，请先移除已选图片')
}

async function handleSubmit() {
  if (!formRef.value || submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid || !runBusinessValidation()) return

  submitting.value = true
  try {
    if (resourceKey.value === 'resort' && form.imageFile) {
      const uploadResult = await uploadImage(form.imageFile)
      form.image = uploadResult.url
    }
    const payload = buildPayload()
    if (dialogMode.value === 'create') {
      await createResource(resourceKey.value, payload)
    } else {
      await updateResource(resourceKey.value, editingId.value, payload)
    }
    ElMessage.success(`${dialogMode.value === 'create' ? '新增' : '修改'}${config.value.entityName}成功`)
    dialogVisible.value = false
    await loadData()
  } finally {
    submitting.value = false
  }
}

function buildPayload() {
  if (resourceKey.value === 'skiRun') {
    const data = new FormData()
    for (const field of config.value.formFields) {
      if (field.type === 'file') continue
      const value = form[field.prop]
      if (value !== null && value !== '' && typeof value !== 'undefined') data.append(field.prop, value)
    }
    if (form.imageFile) data.append('image', form.imageFile)
    return data
  }

  const data = {}
  for (const field of config.value.formFields) {
    if (field.type !== 'file') data[field.prop] = form[field.prop]
  }

  if (resourceKey.value === 'resort') data.image = form.image || null

  if (resourceKey.value === 'course') {
    const coach = lookupData.coaches.find((item) => String(item.id) === String(form.coachId))
    data.teacher = coach?.name || null
  }
  return data
}

function runBusinessValidation() {
  if (resourceKey.value === 'weather' && Number(form.temperatureMin) > Number(form.temperatureMax)) {
    ElMessage.warning('最低温度不能高于最高温度')
    return false
  }
  if (resourceKey.value === 'equipment' && Number(form.remaining) > Number(form.total)) {
    ElMessage.warning('剩余可租数量不能大于总库存')
    return false
  }
  if (resourceKey.value === 'resort' && form.openTime && form.closeTime && form.openTime >= form.closeTime) {
    ElMessage.warning('闭场时间必须晚于开放时间')
    return false
  }
  return true
}

async function handleDelete(row) {
  const rowName = row.name || row.title || `ID ${row.id}`
  try {
    await ElMessageBox.confirm(
      `确定删除${config.value.entityName}“${rowName}”吗？删除后无法恢复。`,
      '删除确认',
      {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await deleteResource(resourceKey.value, row.id)
    ElMessage.success('删除成功')
    await loadData()
  } catch (error) {
    // 取消删除不需要提示；请求异常已经由全局响应拦截器统一说明。
    if (error === 'cancel' || error === 'close') return
  }
}

function handleDialogClosed() {
  formRef.value?.resetFields()
  uploadFiles.value = []
  editingId.value = null
}

function numberRangeMessage(field) {
  if (typeof field.min === 'number' && typeof field.max === 'number') {
    return `${field.label}应在 ${field.min} 到 ${field.max} 之间`
  }
  if (typeof field.min === 'number') return `${field.label}不能小于 ${field.min}`
  return `${field.label}不能大于 ${field.max}`
}

function formatDate(date) {
  const pad = (value) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

function formatDateTime(date) {
  const pad = (value) => String(value).padStart(2, '0')
  return `${formatDate(date)}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

function formatDisplayDateTime(value) {
  return String(value).replace('T', ' ').slice(0, 16)
}
</script>

<style scoped>
.resource-page {
  display: grid;
  gap: 18px;
}

.resource-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding: 2px 2px 4px;
}

.resource-header h1 {
  margin: 5px 0 8px;
  color: var(--ski-navy);
  font-size: 28px;
  line-height: 1.25;
}

.resource-header p {
  margin: 0;
  color: var(--ski-muted);
  line-height: 1.7;
}

.resource-eyebrow {
  color: var(--ski-blue) !important;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 1px;
}

.search-card,
.table-card {
  border: 1px solid var(--ski-border);
}

.search-form {
  display: flex;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 12px 16px;
}

.search-form :deep(.el-form-item) {
  min-width: 190px;
  margin: 0;
}

.search-form :deep(.el-form-item__label) {
  color: var(--ski-muted);
  font-size: 13px;
  line-height: 22px;
}

.search-form :deep(.el-input),
.search-form :deep(.el-select) {
  width: 210px;
}

.search-actions {
  display: flex;
  gap: 8px;
  padding-bottom: 1px;
}

.table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 15px;
}

.table-toolbar strong {
  color: var(--ski-navy);
  font-size: 17px;
}

.table-toolbar span {
  margin-left: 10px;
  color: var(--ski-muted);
  font-size: 13px;
}

.limit-alert {
  margin-bottom: 14px;
}

.table-image {
  width: 52px;
  height: 36px;
  border-radius: 7px;
  vertical-align: middle;
}

.image-placeholder {
  display: grid;
  width: 100%;
  height: 100%;
  place-items: center;
  color: #94a7b5;
  background: #eef4f7;
  font-size: 12px;
}

.muted-text {
  color: #93a4b1;
  font-size: 13px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  overflow-x: auto;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 18px;
}

.form-item-wide {
  grid-column: 1 / -1;
}

.full-width,
.form-grid :deep(.el-select),
.form-grid :deep(.el-date-editor) {
  width: 100%;
}

.field-tip {
  width: 100%;
  margin-top: 5px;
  color: var(--ski-muted);
  font-size: 12px;
  line-height: 1.6;
}

.upload-field {
  display: flex;
  align-items: flex-start;
  gap: 18px;
  width: 100%;
}

.current-image {
  flex: 0 0 auto;
  width: 112px;
  height: 72px;
  border: 1px solid var(--ski-border);
  border-radius: 9px;
}

:deep(.el-table) {
  --el-table-header-bg-color: #f4f9fc;
  --el-table-row-hover-bg-color: #f2f9fc;
  --el-table-border-color: #e8f0f4;
}

:deep(.el-table th.el-table__cell) {
  color: #48677e;
  font-weight: 700;
}

@media (max-width: 780px) {
  .resource-header {
    align-items: stretch;
    flex-direction: column;
  }

  .resource-header .el-button {
    align-self: flex-start;
  }

  .search-form,
  .search-form :deep(.el-form-item),
  .search-form :deep(.el-input),
  .search-form :deep(.el-select) {
    width: 100%;
  }

  .search-actions {
    width: 100%;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-item-wide {
    grid-column: auto;
  }

  .resource-dialog {
    width: calc(100vw - 28px) !important;
  }
}
</style>
