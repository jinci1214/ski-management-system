<script setup>
import { reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteFeedback, getFeedbackPage, replyFeedback } from '../../api/admin'
import { formatDate } from '../../utils/format'

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const total = ref(0)
const replyVisible = ref(false)
const activeFeedback = ref(null)
const page = reactive({ pageNum: 1, pageSize: 10 })
const replyText = ref('')

const loadFeedback = async () => {
  loading.value = true
  try {
    const result = await getFeedbackPage(page) || {}
    rows.value = Array.isArray(result.data) ? result.data : []
    total.value = Number(result.total || 0)
  } finally {
    loading.value = false
  }
}

const openReply = (row) => {
  activeFeedback.value = row
  replyText.value = row.reply || ''
  replyVisible.value = true
}

const submitReply = async () => {
  const content = replyText.value.trim()
  if (!content) {
    ElMessage.warning('请填写回复内容')
    return
  }
  saving.value = true
  try {
    await replyFeedback(activeFeedback.value.id, content)
    ElMessage.success(activeFeedback.value.reply ? '回复已更新' : '反馈已回复')
    replyVisible.value = false
    await loadFeedback()
  } finally {
    saving.value = false
  }
}

const removeFeedback = async (row) => {
  try {
    await ElMessageBox.confirm(
      '删除后将无法恢复这条游客反馈，是否继续？',
      '删除反馈',
      { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }
  await deleteFeedback(row.id)
  ElMessage.success('反馈已删除')
  if (rows.value.length === 1 && page.pageNum > 1) page.pageNum -= 1
  await loadFeedback()
}

const handlePageSize = (size) => {
  page.pageSize = size
  page.pageNum = 1
  loadFeedback()
}

const feedbackTag = (type) => ({
  服务评价: 'primary',
  设施建议: 'success',
  安全问题: 'danger',
  其他: 'info'
}[type] || 'info')

loadFeedback()
</script>

<template>
  <div class="feedback-page">
    <div class="page-heading">
      <div>
        <p>VISITOR VOICE</p>
        <h2>游客反馈</h2>
        <span>及时查看游客评价和意见，记录工作人员的正式回复</span>
      </div>
      <el-button :loading="loading" @click="loadFeedback"><el-icon><Refresh /></el-icon>刷新</el-button>
    </div>

    <div class="summary-strip">
      <div class="summary-icon"><el-icon><ChatDotRound /></el-icon></div>
      <div><strong>{{ total }}</strong><span>累计反馈</span></div>
      <p>回复内容会保存在系统中，便于后续服务质量复盘。</p>
    </div>

    <el-card class="table-card" shadow="never">
      <div class="table-heading">
        <div><strong>反馈记录</strong><span>按提交时间倒序展示</span></div>
        <el-tag effect="plain" round>第 {{ page.pageNum }} 页</el-tag>
      </div>

      <el-table v-loading="loading" :data="rows" stripe table-layout="fixed" empty-text="暂时还没有游客反馈">
        <el-table-column prop="id" label="编号" width="72" align="center" />
        <el-table-column prop="userId" label="游客 ID" width="90" align="center" />
        <el-table-column prop="type" label="反馈类型" width="115" align="center">
          <template #default="{ row }"><el-tag :type="feedbackTag(row.type)" effect="light">{{ row.type || '其他' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="content" label="反馈内容" min-width="250">
          <template #default="{ row }">
            <el-tooltip :content="row.content || '—'" placement="top" :show-after="500">
              <span class="content-text">{{ row.content || '—' }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="145" align="center">
          <template #default="{ row }">
            <el-rate v-if="row.rating" :model-value="Number(row.rating)" disabled size="small" />
            <span v-else class="muted">未评分</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态" width="105" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '已回复' ? 'success' : 'warning'" effect="plain" round>{{ row.status || '待处理' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" min-width="155">
          <template #default="{ row }">{{ formatDate(row.createTime, 'YYYY-MM-DD HH:mm') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="145" fixed="right" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openReply(row)">{{ row.reply ? '查看回复' : '回复' }}</el-button>
            <el-button link type="danger" @click="removeFeedback(row)">删除</el-button>
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
          @current-change="loadFeedback"
          @size-change="handlePageSize"
        />
      </div>
    </el-card>

    <el-dialog v-model="replyVisible" :title="activeFeedback?.reply ? '查看并修改回复' : '回复游客反馈'" width="min(92vw, 620px)">
      <template v-if="activeFeedback">
        <div class="feedback-detail">
          <div class="feedback-meta">
            <el-tag :type="feedbackTag(activeFeedback.type)" effect="light">{{ activeFeedback.type || '其他' }}</el-tag>
            <span>游客 ID：{{ activeFeedback.userId }}</span>
            <span>{{ formatDate(activeFeedback.createTime, 'YYYY-MM-DD HH:mm') }}</span>
          </div>
          <p>{{ activeFeedback.content || '无反馈内容' }}</p>
          <div v-if="activeFeedback.rating" class="rating-line">
            游客评分 <el-rate :model-value="Number(activeFeedback.rating)" disabled size="small" />
          </div>
        </div>
        <el-form label-position="top">
          <el-form-item label="工作人员回复" required>
            <el-input
              v-model="replyText"
              type="textarea"
              :rows="5"
              maxlength="500"
              show-word-limit
              placeholder="请以礼貌、明确的语言回复游客"
            />
          </el-form-item>
        </el-form>
        <div v-if="activeFeedback.replyTime" class="reply-time">上次回复：{{ formatDate(activeFeedback.replyTime, 'YYYY-MM-DD HH:mm') }}</div>
      </template>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitReply">保存回复</el-button>
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

.summary-strip {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 16px;
  padding: 18px 22px;
  border: 1px solid #dceaf1;
  border-radius: 15px;
  color: #fff;
  background: linear-gradient(112deg, #0c3a5d, #1479b8 63%, #23a7c3);
  box-shadow: 0 10px 28px rgba(18, 93, 139, 0.15);
}

.summary-icon {
  display: grid;
  width: 42px;
  height: 42px;
  flex: 0 0 auto;
  place-items: center;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.13);
  font-size: 21px;
}

.summary-strip strong,
.summary-strip span {
  display: block;
}

.summary-strip strong {
  font-size: 22px;
}

.summary-strip span {
  color: #a9d7e8;
  font-size: 11px;
}

.summary-strip p {
  margin: 0 0 0 auto;
  color: #c1e1ec;
  font-size: 12px;
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

.content-text {
  display: block;
  overflow: hidden;
  color: #3e586b;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.muted,
.reply-time {
  color: #94a5b1;
  font-size: 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  overflow-x: auto;
}

.feedback-detail {
  margin-bottom: 20px;
  padding: 18px;
  border-left: 4px solid #1a86b9;
  border-radius: 10px;
  background: #f1f7fa;
}

.feedback-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px 15px;
  color: #7c909f;
  font-size: 12px;
}

.feedback-detail p {
  margin: 15px 0 0;
  color: #28475e;
  line-height: 1.8;
  white-space: pre-wrap;
}

.rating-line {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 13px;
  color: #7c909f;
  font-size: 12px;
}

.reply-time {
  margin-top: -10px;
}

@media (max-width: 680px) {
  .page-heading span,
  .summary-strip p {
    display: none;
  }

  .table-card :deep(.el-card__body) {
    padding: 0 10px 14px;
  }

  .pagination-wrap {
    justify-content: flex-start;
  }
}
</style>
