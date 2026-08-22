<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getFacilities, getResortDetail, getResorts, getSkiRuns, getWeather } from '../../api/public'
import DefaultCover from '../../components/DefaultCover.vue'
import PageBanner from '../../components/PageBanner.vue'
import { formatDate } from '../../utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const resorts = ref([])
const skiRuns = ref([])
const facilities = ref([])
const weather = ref([])
const keyword = ref('')
const city = ref('')
const status = ref('all')
const drawerOpen = ref(false)
const detailLoading = ref(false)
const current = ref(null)

const cities = computed(() => [...new Set(resorts.value.map((item) => item.city).filter(Boolean))])
const filteredResorts = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return resorts.value.filter((item) => {
    const matchKey = !key || [item.name, item.city, item.address, item.introduction].some((value) => String(value || '').toLowerCase().includes(key))
    const matchCity = !city.value || item.city === city.value
    const matchStatus = status.value === 'all' || Number(item.status) === Number(status.value)
    return matchKey && matchCity && matchStatus
  })
})
const currentRuns = computed(() => skiRuns.value.filter((item) => Number(item.resortId) === Number(current.value?.id)))
const currentFacilities = computed(() => facilities.value.filter((item) => Number(item.resortId) === Number(current.value?.id)))
const currentWeather = computed(() => weather.value
  .filter((item) => Number(item.resortId) === Number(current.value?.id))
  .sort((a, b) => String(b.date || '').localeCompare(String(a.date || '')))
  .slice(0, 5))

const levelType = (level) => ({ 初级: 'success', 中级: 'primary', 高级: 'warning', 专家级: 'danger' }[level] || 'info')
const formatTime = (value) => value ? String(value).slice(0, 5) : '待公布'

const loadData = async () => {
  loading.value = true
  const results = await Promise.allSettled([getResorts(), getSkiRuns(), getFacilities(), getWeather()])
  const targets = [resorts, skiRuns, facilities, weather]
  results.forEach((result, index) => {
    targets[index].value = result.status === 'fulfilled' && Array.isArray(result.value) ? result.value : []
  })
  loading.value = false
  if (route.query.id) openDetail(route.query.id, false)
}

const openDetail = async (id, updateRoute = true) => {
  drawerOpen.value = true
  detailLoading.value = true
  current.value = resorts.value.find((item) => Number(item.id) === Number(id)) || null
  try {
    const detail = await getResortDetail(id)
    if (detail) current.value = detail
  } finally {
    detailLoading.value = false
  }
  if (updateRoute && String(route.query.id || '') !== String(id)) {
    router.replace({ query: { ...route.query, id } })
  }
}

const clearDetailQuery = () => {
  current.value = null
  if (route.query.id) {
    const query = { ...route.query }
    delete query.id
    router.replace({ query })
  }
}

watch(() => route.query.id, (id) => {
  if (id && !drawerOpen.value && !loading.value) openDetail(id, false)
})
onMounted(loadData)
</script>

<template>
  <div>
    <PageBanner title="雪场导览" description="汇集黑龙江省内滑雪场、开放时间、雪道等级、服务设施和近期雪况，帮助你在出发前做好选择。" icon="🏔">
      <template #actions><router-link to="/services"><el-button type="primary" size="large" round color="#ffffff">查看滑雪服务</el-button></router-link></template>
    </PageBanner>

    <section class="section-space">
      <div class="page-container">
        <div class="filter-bar glass-card">
          <div class="search-box"><el-icon><Search /></el-icon><input v-model="keyword" placeholder="搜索雪场名称、城市或地址" aria-label="搜索雪场" /></div>
          <el-select v-model="city" clearable placeholder="全部城市" class="filter-select"><el-option v-for="item in cities" :key="item" :label="item" :value="item" /></el-select>
          <el-segmented v-model="status" :options="[{ label: '全部', value: 'all' }, { label: '营业中', value: '1' }, { label: '暂停营业', value: '0' }]" />
        </div>

        <div class="result-head">
          <div><h2>龙江滑雪目的地</h2><p>共找到 {{ filteredResorts.length }} 家雪场</p></div>
          <span>信息由雪场管理端维护</span>
        </div>

        <div v-if="loading" class="resort-grid">
          <el-card v-for="n in 6" :key="n" shadow="never"><el-skeleton animated><template #template><el-skeleton-item variant="image" style="height: 220px" /><el-skeleton-item variant="h3" style="width: 50%; margin-top: 20px" /><el-skeleton-item variant="text" /><el-skeleton-item variant="text" style="width: 75%" /></template></el-skeleton></el-card>
        </div>
        <div v-else-if="filteredResorts.length" class="resort-grid">
          <article v-for="(item, index) in filteredResorts" :key="item.id" class="resort-card" @click="openDetail(item.id)" @keydown.enter="openDetail(item.id)" tabindex="0">
            <DefaultCover :image="item.image" :title="item.name" :subtitle="item.city || '黑龙江'" :tone="['blue', 'cyan', 'navy'][index % 3]" height="220px" />
            <div class="card-content">
              <div class="title-row"><h3>{{ item.name }}</h3><el-tag :type="Number(item.status) === 1 ? 'success' : 'info'" round>{{ Number(item.status) === 1 ? '营业中' : '暂停营业' }}</el-tag></div>
              <p class="address"><el-icon><Location /></el-icon>{{ item.city || '黑龙江' }} · {{ item.address || '地址待完善' }}</p>
              <p class="introduction">{{ item.introduction || '雪场简介正在完善，可查看详情了解开放雪道和配套设施。' }}</p>
              <div class="card-footer"><span><el-icon><Clock /></el-icon>{{ formatTime(item.openTime) }}—{{ formatTime(item.closeTime) }}</span><b>查看详情 <el-icon><ArrowRight /></el-icon></b></div>
            </div>
          </article>
        </div>
        <el-empty v-else description="没有符合条件的滑雪场"><el-button @click="keyword = ''; city = ''; status = 'all'">清除筛选</el-button></el-empty>
      </div>
    </section>

    <el-drawer v-model="drawerOpen" size="min(720px, 94vw)" :with-header="false" @closed="clearDetailQuery">
      <div v-loading="detailLoading" class="detail-panel">
        <template v-if="current">
          <DefaultCover :image="current.image" :title="current.name" :subtitle="current.city || '黑龙江'" tone="navy" height="270px" />
          <div class="detail-content">
            <button class="drawer-close" type="button" aria-label="关闭详情" @click="drawerOpen = false"><el-icon><Close /></el-icon></button>
            <div class="detail-title"><div><span>{{ current.city || '黑龙江' }} · SKI RESORT</span><h2>{{ current.name }}</h2></div><el-tag :type="Number(current.status) === 1 ? 'success' : 'info'" size="large" round>{{ Number(current.status) === 1 ? '营业中' : '暂停营业' }}</el-tag></div>
            <p class="detail-intro">{{ current.introduction || '该雪场的详细介绍正在持续完善。' }}</p>
            <div class="contact-grid">
              <div><el-icon><Location /></el-icon><span>雪场地址<small>{{ current.address || '待完善' }}</small></span></div>
              <div><el-icon><Phone /></el-icon><span>联系电话<small>{{ current.phone || '待公布' }}</small></span></div>
              <div><el-icon><Clock /></el-icon><span>开放时间<small>{{ formatTime(current.openTime) }}—{{ formatTime(current.closeTime) }}</small></span></div>
            </div>

            <section class="detail-section">
              <div class="detail-section-title"><h3>开放雪道</h3><span>{{ currentRuns.length }} 条记录</span></div>
              <div v-if="currentRuns.length" class="run-list">
                <div v-for="run in currentRuns" :key="run.id"><el-tag :type="levelType(run.level)" effect="dark" round>{{ run.level || '未分级' }}</el-tag><span><strong>{{ run.name }}</strong><small>{{ run.length || '—' }} 米 · 落差 {{ run.height || '—' }} 米</small></span><el-image v-if="run.image" class="run-image" :src="run.image" :preview-src-list="[run.image]" preview-teleported fit="cover"><template #placeholder><span>加载中</span></template><template #error><span>图片加载失败</span></template></el-image><div v-else class="run-image-empty">暂无照片</div></div>
              </div>
              <el-empty v-else :image-size="65" description="暂无雪道记录" />
            </section>

            <section class="detail-section">
              <div class="detail-section-title"><h3>近期天气与雪况</h3><span>出发前请及时确认</span></div>
              <div v-if="currentWeather.length" class="weather-grid">
                <div v-for="item in currentWeather" :key="item.id"><time>{{ formatDate(item.date, 'MM/DD') }}</time><b>{{ item.weather || '—' }}</b><span>{{ item.temperatureMin ?? '—' }}° ~ {{ item.temperatureMax ?? '—' }}°</span><small>{{ item.snow || '雪况待更新' }} · 风速 {{ item.wind ?? '—' }}m/s</small></div>
              </div>
              <el-empty v-else :image-size="65" description="暂无天气雪况记录" />
            </section>

            <section class="detail-section">
              <div class="detail-section-title"><h3>配套设施</h3><span>{{ currentFacilities.length }} 项</span></div>
              <div v-if="currentFacilities.length" class="facility-list">
                <div v-for="item in currentFacilities" :key="item.id"><span class="facility-icon">{{ item.type === '缆车' ? '🚡' : item.type === '造雪机' ? '❄' : item.type === '救援设施' ? '⛑' : '🏠' }}</span><div><strong>{{ item.name }}</strong><small>{{ item.location || item.description || item.type }}</small></div><el-tag :type="item.status === '正常' ? 'success' : item.status === '维护中' ? 'warning' : 'info'" size="small">{{ item.status || '待确认' }}</el-tag></div>
              </div>
              <el-empty v-else :image-size="65" description="暂无设施记录" />
            </section>

            <router-link to="/services" class="booking-link"><el-button type="primary" size="large">去选择雪票与服务 <el-icon class="el-icon--right"><ArrowRight /></el-icon></el-button></router-link>
          </div>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.filter-bar{display:flex;align-items:center;gap:14px;margin-top:-96px;margin-bottom:54px;padding:18px;position:relative;z-index:3}.search-box{display:flex;min-width:260px;flex:1;align-items:center;gap:10px;padding:0 15px;border:1px solid #d8e6ed;border-radius:12px;background:#fff;color:#7d92a1}.search-box input{width:100%;height:42px;border:0;outline:0;background:transparent;color:#17374f}.filter-select{width:170px}.result-head{display:flex;align-items:end;justify-content:space-between;margin-bottom:26px}.result-head h2{margin:0;color:#102f47;font-size:29px}.result-head p{margin:8px 0 0;color:#8193a0;font-size:13px}.result-head>span{color:#8b9aa5;font-size:12px}.resort-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:24px}.resort-card{overflow:hidden;border:1px solid #dce8ef;border-radius:19px;background:#fff;box-shadow:0 8px 27px rgba(15,67,99,.07);cursor:pointer;outline:0;transition:.25s}.resort-card:hover,.resort-card:focus-visible{transform:translateY(-5px);box-shadow:0 17px 40px rgba(15,67,99,.14)}.card-content{padding:20px}.title-row{display:flex;align-items:center;justify-content:space-between;gap:12px}.title-row h3{overflow:hidden;margin:0;color:#15354c;font-size:20px;text-overflow:ellipsis;white-space:nowrap}.address{display:flex;align-items:center;gap:5px;margin:13px 0 0;color:#6f8798;font-size:12px}.introduction{display:-webkit-box;min-height:47px;margin:13px 0 0;color:#728695;font-size:13px;line-height:1.8;-webkit-box-orient:vertical;-webkit-line-clamp:2;overflow:hidden}.card-footer{display:flex;align-items:center;justify-content:space-between;margin-top:18px;padding-top:15px;border-top:1px solid #e6eef3;color:#7a8e9c;font-size:12px}.card-footer span,.card-footer b{display:flex;align-items:center;gap:5px}.card-footer b{color:#1479b8}
.detail-panel{min-height:100%}.detail-content{position:relative;padding:30px 34px 50px}.drawer-close{position:absolute;top:-250px;right:20px;display:grid;width:38px;height:38px;place-items:center;border:1px solid rgba(255,255,255,.5);border-radius:50%;background:rgba(9,39,66,.35);color:#fff;cursor:pointer;backdrop-filter:blur(5px)}.detail-title{display:flex;align-items:center;justify-content:space-between;gap:20px}.detail-title span{color:#1780b0;font-size:10px;font-weight:700;letter-spacing:2px}.detail-title h2{margin:8px 0 0;color:#102f47;font-size:31px}.detail-intro{margin:22px 0;color:#60798b;line-height:1.9}.contact-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:10px}.contact-grid>div{display:flex;align-items:flex-start;gap:10px;padding:16px;border-radius:13px;background:#eff7fa;color:#1380b3}.contact-grid span{display:grid;gap:6px;color:#6f8595;font-size:11px}.contact-grid small{color:#1a3c53;font-size:12px;font-weight:600;line-height:1.5}.detail-section{margin-top:34px}.detail-section-title{display:flex;align-items:center;justify-content:space-between;margin-bottom:15px}.detail-section-title h3{margin:0;color:#17364d;font-size:19px}.detail-section-title span{color:#8698a4;font-size:11px}.run-list,.facility-list{display:grid;gap:9px}.run-list>div{display:grid;grid-template-columns:auto minmax(0,1fr) 132px;align-items:center;gap:14px;padding:10px 12px 10px 14px;border:1px solid #e0ebf0;border-radius:12px}.run-list span{display:grid;min-width:0;gap:5px}.run-list strong{overflow:hidden;color:#284a61;font-size:13px;text-overflow:ellipsis;white-space:nowrap}.run-list small{color:#8294a0;font-size:11px}.run-image,.run-image-empty{width:132px;height:74px;overflow:hidden;border-radius:8px;background:#edf5f8}.run-image{cursor:zoom-in}.run-image :deep(.el-image__inner){transition:transform .2s}.run-image:hover :deep(.el-image__inner){transform:scale(1.05)}.run-image :deep(.el-image__placeholder),.run-image :deep(.el-image__error){display:grid;height:100%;place-items:center;color:#8094a1;font-size:10px}.run-image-empty{display:grid;place-items:center;border:1px dashed #ccdce4;color:#95a6b1;font-size:10px}.weather-grid{display:grid;grid-template-columns:repeat(5,1fr);gap:8px}.weather-grid>div{display:grid;justify-items:center;gap:7px;padding:14px 7px;border-radius:12px;background:linear-gradient(180deg,#edf8fc,#fff);text-align:center}.weather-grid time{color:#718b9b;font-size:11px}.weather-grid b{color:#126d9d}.weather-grid span{font-size:11px}.weather-grid small{color:#8395a1;font-size:10px;line-height:1.5}.facility-list>div{display:grid;grid-template-columns:38px 1fr auto;align-items:center;gap:11px;padding:13px 15px;border:1px solid #e0ebf0;border-radius:12px}.facility-icon{display:grid;width:36px;height:36px;place-items:center;border-radius:10px;background:#e8f5fa}.facility-list div div{display:grid;gap:4px}.facility-list strong{font-size:13px}.facility-list small{color:#8194a1;font-size:11px}.booking-link{display:block;margin-top:34px}.booking-link :deep(.el-button){width:100%}
@media(max-width:920px){.resort-grid{grid-template-columns:1fr 1fr}.filter-bar{flex-wrap:wrap}.search-box{flex-basis:100%}.filter-select{flex:1}.weather-grid{grid-template-columns:repeat(3,1fr)}}
@media(max-width:640px){.filter-bar{align-items:stretch;flex-direction:column;margin-top:-74px}.filter-select{width:100%}.filter-bar :deep(.el-segmented){width:100%}.filter-bar :deep(.el-segmented__group){display:grid;grid-template-columns:repeat(3,1fr)}.result-head>span{display:none}.resort-grid{grid-template-columns:1fr}.detail-content{padding:25px 20px 40px}.detail-title{align-items:flex-start}.contact-grid{grid-template-columns:1fr}.run-list>div{grid-template-columns:auto minmax(0,1fr) 100px;gap:9px;padding:8px}.run-image,.run-image-empty{width:100px;height:62px}.weather-grid{grid-template-columns:1fr 1fr}.drawer-close{top:-250px}.detail-title h2{font-size:26px}}
</style>
