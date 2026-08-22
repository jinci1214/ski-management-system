<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getAnnouncementDetail, getAnnouncements } from '../../api/public'
import PageBanner from '../../components/PageBanner.vue'
import { formatDate } from '../../utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const announcements = ref([])
const keyword = ref('')
const category = ref('全部')
const page = ref(1)
const pageSize = 8
const drawerOpen = ref(false)
const detailLoading = ref(false)
const current = ref(null)

const categories = computed(() => ['全部', ...new Set(announcements.value.map((item) => item.category).filter(Boolean))])
const filtered = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return [...announcements.value]
    .filter((item) => (category.value === '全部' || item.category === category.value) && (!key || `${item.title || ''} ${item.content || ''}`.toLowerCase().includes(key)))
    .sort((a, b) => new Date(b.publishTime || b.createTime || 0) - new Date(a.publishTime || a.createTime || 0))
})
const paged = computed(() => filtered.value.slice((page.value - 1) * pageSize, page.value * pageSize))
const featured = computed(() => filtered.value[0] || announcements.value[0] || null)

const loadData = async () => {
  loading.value = true
  try {
    const data = await getAnnouncements()
    announcements.value = Array.isArray(data) ? data : []
  } finally {
    loading.value = false
  }
  if (route.query.id) openDetail(route.query.id, false)
}

const openDetail = async (id, updateRoute = true) => {
  drawerOpen.value = true
  detailLoading.value = true
  current.value = announcements.value.find((item) => Number(item.id) === Number(id)) || null
  try {
    const detail = await getAnnouncementDetail(id)
    if (detail) current.value = detail
  } finally {
    detailLoading.value = false
  }
  if (updateRoute && String(route.query.id || '') !== String(id)) router.replace({ query: { ...route.query, id } })
}

const clearDetailQuery = () => {
  current.value = null
  if (route.query.id) {
    const query = { ...route.query }
    delete query.id
    router.replace({ query })
  }
}

watch([keyword, category], () => { page.value = 1 })
watch(() => route.query.id, (id) => {
  if (id && !drawerOpen.value && !loading.value) openDetail(id, false)
})
onMounted(loadData)
</script>

<template>
  <div>
    <PageBanner title="公告与冰雪资讯" description="关注雪道开放、票务调整、课程安排和天气提醒，让每一次滑雪出行都有准备、有保障。" icon="📣" />

    <section class="section-space announcement-page">
      <div class="page-container">
        <div class="filter-panel glass-card">
          <div class="category-list">
            <button v-for="item in categories" :key="item" type="button" :class="{ active: category === item }" @click="category = item">{{ item }}</button>
          </div>
          <el-input v-model="keyword" clearable placeholder="搜索公告标题或内容" prefix-icon="Search" />
        </div>

        <div v-if="loading" class="content-layout"><el-skeleton class="featured-skeleton" animated><template #template><el-skeleton-item variant="image" style="height: 360px" /></template></el-skeleton><el-skeleton :rows="8" animated /></div>
        <template v-else-if="filtered.length">
          <div class="content-layout">
            <article v-if="featured" class="featured-card" tabindex="0" @click="openDetail(featured.id)" @keydown.enter="openDetail(featured.id)">
              <div class="feature-art"><span>❄</span><small>HEILONGJIANG<br />SKI NEWS</small></div>
              <div class="feature-shade" />
              <div class="feature-copy"><el-tag effect="dark" color="#1682b3">{{ featured.category || '雪场资讯' }}</el-tag><h2>{{ featured.title }}</h2><p>{{ featured.content || '点击查看公告详情。' }}</p><time>{{ formatDate(featured.publishTime || featured.createTime, 'YYYY年MM月DD日 HH:mm') }}</time></div>
            </article>

            <div class="announcement-list">
              <article v-for="item in paged" :key="item.id" tabindex="0" @click="openDetail(item.id)" @keydown.enter="openDetail(item.id)">
                <time><strong>{{ formatDate(item.publishTime || item.createTime, 'DD') }}</strong><span>{{ formatDate(item.publishTime || item.createTime, 'YYYY.MM') }}</span></time>
                <div class="list-copy"><span>{{ item.category || '雪场资讯' }}</span><h3>{{ item.title }}</h3><p>{{ item.content || '点击查看公告详情。' }}</p></div>
                <button type="button" aria-label="查看公告详情"><el-icon><ArrowRight /></el-icon></button>
              </article>
            </div>
          </div>
          <div v-if="filtered.length > pageSize" class="pagination"><el-pagination v-model:current-page="page" background layout="prev, pager, next" :page-size="pageSize" :total="filtered.length" /></div>
        </template>
        <el-empty v-else description="没有找到符合条件的公告"><el-button @click="keyword = ''; category = '全部'">查看全部公告</el-button></el-empty>
      </div>
    </section>

    <el-drawer v-model="drawerOpen" size="min(680px, 94vw)" :with-header="false" @closed="clearDetailQuery">
      <div v-loading="detailLoading" class="detail-drawer">
        <button class="detail-close" type="button" aria-label="关闭公告" @click="drawerOpen = false"><el-icon><Close /></el-icon></button>
        <template v-if="current">
          <div class="detail-header"><span>SKI RESORT NOTICE</span><el-tag effect="dark" color="#1479b8">{{ current.category || '雪场资讯' }}</el-tag><h1>{{ current.title }}</h1><div><el-icon><Calendar /></el-icon>发布于 {{ formatDate(current.publishTime || current.createTime, 'YYYY年MM月DD日 HH:mm') }}</div></div>
          <div class="detail-content">{{ current.content || '暂无详细内容。' }}</div>
          <div class="detail-reminder"><span>❄</span><p><strong>温馨提示</strong>雪场开放与天气情况可能临时调整，出发前建议再次查看最新公告或致电雪场确认。</p></div>
          <div class="detail-footer"><span>黑龙江滑雪场管理系统</span><router-link to="/resorts">查看雪场信息 <el-icon><ArrowRight /></el-icon></router-link></div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.announcement-page{background:linear-gradient(180deg,#f4f8fb,#fff)}.filter-panel{position:relative;z-index:3;display:flex;align-items:center;justify-content:space-between;gap:24px;margin-top:-96px;margin-bottom:52px;padding:17px 19px}.category-list{display:flex;align-items:center;gap:5px;overflow-x:auto}.category-list button{padding:10px 15px;border:0;border-radius:10px;background:transparent;color:#647e90;font-size:13px;white-space:nowrap;cursor:pointer}.category-list button:hover,.category-list button.active{background:#e5f4f9;color:#126f9e;font-weight:700}.filter-panel>.el-input{width:270px;flex:0 0 auto}.content-layout{display:grid;grid-template-columns:.8fr 1.2fr;gap:35px}.featured-card{position:sticky;top:105px;height:420px;overflow:hidden;border-radius:20px;background:linear-gradient(145deg,#0a385d,#1484ad 65%,#65ced9);box-shadow:0 14px 35px rgba(10,66,99,.15);color:#fff;cursor:pointer;outline:0}.feature-art{position:absolute;inset:0;overflow:hidden}.feature-art::before,.feature-art::after{position:absolute;border:1px solid rgba(255,255,255,.2);border-radius:50%;content:''}.feature-art::before{top:-80px;right:-70px;width:300px;height:300px}.feature-art::after{right:80px;bottom:-140px;width:360px;height:360px}.feature-art>span{position:absolute;top:55px;right:60px;font-size:70px;opacity:.85}.feature-art small{position:absolute;top:35px;left:30px;font-size:9px;font-weight:800;line-height:1.7;letter-spacing:2px;opacity:.7}.feature-shade{position:absolute;inset:0;background:linear-gradient(180deg,transparent 25%,rgba(4,28,48,.85))}.feature-copy{position:absolute;right:30px;bottom:30px;left:30px}.feature-copy h2{display:-webkit-box;margin:15px 0 10px;font-size:27px;line-height:1.35;-webkit-box-orient:vertical;-webkit-line-clamp:2;overflow:hidden}.feature-copy p{display:-webkit-box;margin:0;color:rgba(255,255,255,.72);font-size:12px;line-height:1.8;-webkit-box-orient:vertical;-webkit-line-clamp:2;overflow:hidden}.feature-copy time{display:block;margin-top:15px;color:rgba(255,255,255,.57);font-size:10px}.announcement-list{display:grid}.announcement-list article{display:grid;grid-template-columns:68px 1fr 38px;align-items:center;gap:19px;min-height:116px;padding:18px 8px;border-bottom:1px solid #dfe9ee;cursor:pointer;outline:0;transition:.2s}.announcement-list article:hover,.announcement-list article:focus-visible{padding-right:2px;padding-left:15px;background:#f6fafc}.announcement-list time{display:grid;justify-items:center;padding-right:17px;border-right:1px solid #dbe6ec}.announcement-list time strong{color:#126f9e;font-size:29px}.announcement-list time span{margin-top:3px;color:#8698a4;font-size:10px}.list-copy{min-width:0}.list-copy>span{color:#1781ad;font-size:10px}.list-copy h3{overflow:hidden;margin:7px 0;color:#1b3a51;font-size:16px;text-overflow:ellipsis;white-space:nowrap}.list-copy p{overflow:hidden;margin:0;color:#7d8f9c;font-size:11px;text-overflow:ellipsis;white-space:nowrap}.announcement-list article>button{display:grid;width:34px;height:34px;place-items:center;border:0;border-radius:50%;background:#edf6fa;color:#1479b8;cursor:pointer}.pagination{display:flex;justify-content:flex-end;margin-top:32px}.detail-drawer{position:relative;min-height:100%;padding:50px 48px}.detail-close{position:absolute;top:20px;right:22px;display:grid;width:36px;height:36px;place-items:center;border:0;border-radius:50%;background:#edf5f8;color:#527084;cursor:pointer}.detail-header{padding-bottom:27px;border-bottom:1px solid #dce8ef}.detail-header>span{display:block;margin-bottom:18px;color:#1680ac;font-size:10px;font-weight:800;letter-spacing:3px}.detail-header h1{margin:18px 0;color:#12334b;font-size:31px;line-height:1.45}.detail-header div{display:flex;align-items:center;gap:6px;color:#8495a1;font-size:11px}.detail-content{min-height:220px;padding:32px 0;color:#405f74;font-size:15px;line-height:2.05;white-space:pre-wrap;word-break:break-word}.detail-reminder{display:flex;align-items:flex-start;gap:13px;padding:18px;border-radius:13px;background:#edf7fa}.detail-reminder>span{font-size:23px}.detail-reminder p{display:grid;gap:5px;margin:0;color:#728795;font-size:11px;line-height:1.7}.detail-reminder strong{color:#2a526a;font-size:13px}.detail-footer{display:flex;align-items:center;justify-content:space-between;margin-top:35px;padding-top:18px;border-top:1px solid #e2ebef;color:#8899a5;font-size:11px}.detail-footer a{display:flex;align-items:center;gap:5px;color:#1479b8;font-weight:700}
@media(max-width:850px){.filter-panel{align-items:stretch;flex-direction:column}.filter-panel>.el-input{width:100%}.content-layout{grid-template-columns:1fr}.featured-card{position:relative;top:auto;height:350px}.featured-skeleton{height:350px}}
@media(max-width:600px){.filter-panel{margin-top:-77px}.category-list{padding-bottom:4px}.featured-card{height:320px}.feature-copy{right:22px;bottom:23px;left:22px}.feature-copy h2{font-size:23px}.announcement-list article{grid-template-columns:55px 1fr;gap:12px}.announcement-list article>button{display:none}.announcement-list time{padding-right:11px}.detail-drawer{padding:55px 22px 35px}.detail-header h1{font-size:25px}}
</style>
