<script setup>
import { computed, onMounted, ref } from 'vue'
import { getAnnouncements, getResorts, getSkiRuns, getWeather } from '../../api/public'
import DefaultCover from '../../components/DefaultCover.vue'
import { formatDate } from '../../utils/format'

const loading = ref(true)
const loadError = ref(false)
const resorts = ref([])
const skiRuns = ref([])
const weather = ref([])
const announcements = ref([])

const featuredResorts = computed(() => resorts.value.filter((item) => Number(item.status) === 1).slice(0, 3))
const latestWeather = computed(() => [...weather.value]
  .sort((a, b) => String(b.date || '').localeCompare(String(a.date || '')))
  .slice(0, 4))
const latestAnnouncements = computed(() => [...announcements.value]
  .sort((a, b) => new Date(b.publishTime || b.createTime || 0) - new Date(a.publishTime || a.createTime || 0))
  .slice(0, 4))
const resortName = (id) => resorts.value.find((item) => Number(item.id) === Number(id))?.name || '黑龙江雪场'

const loadData = async () => {
  loading.value = true
  loadError.value = false
  const results = await Promise.allSettled([getResorts(), getSkiRuns(), getWeather(), getAnnouncements()])
  const targets = [resorts, skiRuns, weather, announcements]
  results.forEach((result, index) => {
    if (result.status === 'fulfilled') targets[index].value = Array.isArray(result.value) ? result.value : []
  })
  loadError.value = results.every((result) => result.status === 'rejected')
  loading.value = false
}

onMounted(loadData)
</script>

<template>
  <div class="home-page">
    <section class="hero">
      <div class="hero-sky" aria-hidden="true"><span v-for="n in 12" :key="n" :class="`snow-${n}`">•</span></div>
      <div class="hero-ridge ridge-back" aria-hidden="true" />
      <div class="hero-ridge ridge-front" aria-hidden="true" />
      <div class="page-container hero-inner">
        <div class="hero-copy">
          <span class="hero-tag"><i /> 黑龙江冰雪旅游服务平台</span>
          <h1>奔赴北国雪境<br /><em>遇见一场好雪</em></h1>
          <p>汇聚省内滑雪场、雪道雪况、票务租赁与教学课程，为游客提供清晰、便捷的一站式滑雪服务。</p>
          <div class="hero-actions">
            <router-link to="/resorts"><el-button type="primary" size="large" round>探索滑雪场 <el-icon class="el-icon--right"><ArrowRight /></el-icon></el-button></router-link>
            <router-link to="/services"><el-button size="large" round plain>立即预订服务</el-button></router-link>
          </div>
          <div class="hero-note"><el-icon><CircleCheck /></el-icon> 数据来自雪场管理端实时维护</div>
        </div>

        <div class="weather-glance glass-card">
          <div class="glance-title">
            <div><small>TODAY'S SNOW</small><strong>近期雪况</strong></div>
            <span class="live-dot">实时</span>
          </div>
          <template v-if="loading">
            <el-skeleton :rows="4" animated />
          </template>
          <div v-else-if="latestWeather.length" class="weather-list">
            <div v-for="item in latestWeather.slice(0, 3)" :key="item.id" class="weather-row">
              <span class="weather-icon">{{ item.weather === '雪' ? '❄' : item.weather === '晴' ? '☀' : '☁' }}</span>
              <div><strong>{{ resortName(item.resortId) }}</strong><small>{{ formatDate(item.date) }} · {{ item.snow || '雪况待更新' }}</small></div>
              <b>{{ item.temperatureMin ?? '—' }}° / {{ item.temperatureMax ?? '—' }}°</b>
            </div>
          </div>
          <el-empty v-else :image-size="60" description="今日雪况正在更新" />
          <router-link to="/resorts" class="glance-link">查看全部雪场信息 <el-icon><ArrowRight /></el-icon></router-link>
        </div>
      </div>
    </section>

    <section class="quick-stats">
      <div class="page-container stats-grid">
        <div><strong>{{ resorts.length }}</strong><span>收录滑雪场</span></div>
        <div><strong>{{ skiRuns.length }}</strong><span>开放雪道信息</span></div>
        <div><strong>{{ weather.length }}</strong><span>天气雪况记录</span></div>
        <div><strong>一站式</strong><span>购票租赁与课程</span></div>
      </div>
    </section>

    <section class="section-space resorts-section">
      <div class="page-container">
        <div class="section-heading">
          <div><span class="section-kicker">FEATURED RESORTS</span><h2 class="page-title">发现龙江好雪场</h2><p class="page-subtitle">从城市周边到林海雪原，选择适合你的冰雪目的地。</p></div>
          <router-link to="/resorts" class="text-link">全部雪场 <el-icon><ArrowRight /></el-icon></router-link>
        </div>

        <div v-if="loading" class="resort-grid"><el-skeleton v-for="n in 3" :key="n" animated><template #template><el-skeleton-item variant="image" style="height: 230px" /><el-skeleton-item variant="h3" style="margin-top: 18px; width: 55%" /><el-skeleton-item variant="text" /></template></el-skeleton></div>
        <div v-else-if="featuredResorts.length" class="resort-grid">
          <article v-for="(item, index) in featuredResorts" :key="item.id" class="resort-card">
            <DefaultCover :image="item.image" :title="item.name" :subtitle="item.city || '黑龙江'" :tone="index === 1 ? 'cyan' : index === 2 ? 'navy' : 'blue'" height="238px" />
            <div class="resort-body">
              <div class="resort-meta"><span><el-icon><Location /></el-icon>{{ item.city || '黑龙江' }}</span><el-tag type="success" effect="light" round>营业中</el-tag></div>
              <h3>{{ item.name }}</h3>
              <p>{{ item.introduction || '雪场介绍正在完善，更多开放信息请进入雪场导览查看。' }}</p>
              <router-link :to="{ path: '/resorts', query: { id: item.id } }">了解雪场 <el-icon><Right /></el-icon></router-link>
            </div>
          </article>
        </div>
        <el-empty v-else :description="loadError ? '暂时无法获取雪场数据' : '暂无营业中的滑雪场'">
          <el-button v-if="loadError" type="primary" @click="loadData">重新加载</el-button>
        </el-empty>
      </div>
    </section>

    <section class="service-section section-space">
      <div class="page-container service-layout">
        <div class="service-intro">
          <span class="section-kicker light">ONE-STOP SERVICE</span>
          <h2>从出发前，到滑雪后<br />服务都在这里</h2>
          <p>不仅是票务系统，更覆盖雪具租赁、教学报名、订单处理、设施信息和游客意见反馈。</p>
          <router-link to="/services"><el-button size="large" round color="#ffffff">浏览全部服务</el-button></router-link>
        </div>
        <div class="service-grid">
          <router-link to="/services" class="service-card"><span>01</span><i>🎫</i><strong>雪票购买</strong><small>浏览票种与有效时长，在线创建订单</small></router-link>
          <router-link to="/services" class="service-card"><span>02</span><i>🎿</i><strong>雪具租赁</strong><small>查看库存与租金，到场轻装上阵</small></router-link>
          <router-link to="/services" class="service-card"><span>03</span><i>🏂</i><strong>课程报名</strong><small>按难度选择课程，跟随教练进阶</small></router-link>
          <router-link to="/account" class="service-card"><span>04</span><i>💬</i><strong>游客反馈</strong><small>提交意见建议，在线查看管理回复</small></router-link>
        </div>
      </div>
    </section>

    <section class="section-space news-section">
      <div class="page-container news-layout">
        <div class="news-heading">
          <span class="section-kicker">LATEST NEWS</span>
          <h2 class="page-title">雪场公告资讯</h2>
          <p class="page-subtitle">及时了解开放安排、雪道调整、天气提醒和活动信息。</p>
          <router-link to="/announcements" class="text-link">查看全部公告 <el-icon><ArrowRight /></el-icon></router-link>
        </div>
        <div v-if="loading" class="news-list"><el-skeleton :rows="5" animated /></div>
        <div v-else-if="latestAnnouncements.length" class="news-list">
          <router-link v-for="item in latestAnnouncements" :key="item.id" :to="{ path: '/announcements', query: { id: item.id } }" class="news-item">
            <time><strong>{{ formatDate(item.publishTime || item.createTime, 'DD') }}</strong><span>{{ formatDate(item.publishTime || item.createTime, 'YYYY.MM') }}</span></time>
            <div><el-tag size="small" effect="plain">{{ item.category || '雪场资讯' }}</el-tag><h3>{{ item.title }}</h3><p>{{ item.content || '点击查看公告详情' }}</p></div>
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <el-empty v-else description="暂无公告资讯" />
      </div>
    </section>
  </div>
</template>

<style scoped>
.hero { position: relative; min-height: 680px; overflow: hidden; background: linear-gradient(122deg, #071f37 0%, #0d5c8d 51%, #42bed1 100%); color: #fff; }
.hero::before { position: absolute; inset: 0; background: radial-gradient(circle at 73% 20%, rgba(255,255,255,.2), transparent 24%), linear-gradient(90deg, rgba(3,24,43,.2), transparent 60%); content: ''; }
.hero-sky { position: absolute; inset: 0; opacity: .48; }
.hero-sky span { position: absolute; color: #fff; font-size: 22px; }
.snow-1{left:8%;top:15%}.snow-2{left:22%;top:45%;font-size:9px!important}.snow-3{left:42%;top:12%}.snow-4{left:58%;top:35%;font-size:12px!important}.snow-5{left:72%;top:11%}.snow-6{left:88%;top:42%}.snow-7{left:94%;top:18%;font-size:10px!important}.snow-8{left:51%;top:63%}.snow-9{left:31%;top:70%;font-size:11px!important}.snow-10{left:15%;top:78%}.snow-11{left:80%;top:71%;font-size:12px!important}.snow-12{left:66%;top:82%}
.hero-ridge { position: absolute; right: -12%; bottom: -36%; width: 73%; height: 82%; border-radius: 48% 48% 0 0; transform: rotate(-9deg); }
.ridge-back { right: 10%; bottom: -54%; background: rgba(191,239,248,.32); }
.ridge-front { background: linear-gradient(155deg, rgba(247,253,255,.9), rgba(186,231,243,.64)); box-shadow: -30px -12px 80px rgba(255,255,255,.15); }
.hero-inner { position: relative; z-index: 2; display: grid; min-height: 680px; grid-template-columns: minmax(0, 1.25fr) minmax(330px, .75fr); align-items: center; gap: 70px; padding-top: 60px; padding-bottom: 72px; }
.hero-tag { display: inline-flex; align-items: center; gap: 9px; margin-bottom: 23px; font-size: 13px; font-weight: 600; letter-spacing: 1px; }
.hero-tag i { width: 28px; height: 2px; background: #79e1ed; }
.hero h1 { margin: 0; font-size: clamp(48px, 6vw, 76px); line-height: 1.12; letter-spacing: -3px; }
.hero h1 em { color: #b7f0f6; font-style: normal; }
.hero-copy > p { max-width: 620px; margin: 25px 0 0; color: rgba(255,255,255,.78); font-size: 17px; line-height: 1.9; }
.hero-actions { display: flex; gap: 13px; margin-top: 34px; }
.hero-actions :deep(.el-button.is-plain) { border-color: rgba(255,255,255,.52); background: rgba(255,255,255,.1); color: #fff; }
.hero-note { display: flex; align-items: center; gap: 7px; margin-top: 24px; color: rgba(255,255,255,.6); font-size: 12px; }
.weather-glance { align-self: center; padding: 25px; color: #17344a; }
.glance-title { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.glance-title div { display: grid; gap: 4px; }.glance-title small { color: #7b93a5; font-size: 9px; letter-spacing: 2px; }.glance-title strong { font-size: 20px; }
.live-dot { padding: 4px 9px; border-radius: 20px; background: #e3f7ec; color: #1d8b57; font-size: 11px; }
.weather-list { display: grid; }.weather-row { display: grid; grid-template-columns: 38px 1fr auto; align-items: center; gap: 10px; padding: 14px 0; border-bottom: 1px solid #e5eef3; }.weather-icon { font-size: 24px; }.weather-row div { display: grid; gap: 5px; }.weather-row strong { overflow: hidden; font-size: 13px; text-overflow: ellipsis; white-space: nowrap; }.weather-row small { color: #8295a4; font-size: 11px; }.weather-row b { color: #135f8e; font-size: 12px; font-weight: 700; white-space: nowrap; }
.glance-link { display: flex; align-items: center; justify-content: space-between; margin-top: 18px; color: #1479b8; font-size: 13px; font-weight: 700; }
.quick-stats { position: relative; z-index: 4; margin-top: -32px; }.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); overflow: hidden; border: 1px solid rgba(220,232,239,.8); border-radius: 18px; background: #fff; box-shadow: 0 15px 40px rgba(12,60,90,.12); }.stats-grid div { display: grid; justify-items: center; gap: 7px; padding: 28px 15px; border-right: 1px solid #e4edf2; }.stats-grid div:last-child{border-right:0}.stats-grid strong{color:#0d6398;font-size:27px}.stats-grid span{color:#728696;font-size:13px}
.section-heading { display: flex; align-items: end; justify-content: space-between; gap: 30px; margin-bottom: 34px; }.section-kicker { display: inline-block; margin-bottom: 12px; color: #1787ba; font-size: 11px; font-weight: 800; letter-spacing: 3px; }.section-kicker.light{color:#87e4ed}.text-link{display:inline-flex;align-items:center;gap:7px;color:#1479b8;font-size:14px;font-weight:700;white-space:nowrap}
.resort-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 24px; }.resort-card { overflow: hidden; border: 1px solid #dce8ef; border-radius: 19px; background: #fff; box-shadow: 0 9px 28px rgba(17,70,105,.07); transition: transform .25s ease, box-shadow .25s ease; }.resort-card:hover{transform:translateY(-6px);box-shadow:0 18px 40px rgba(17,70,105,.13)}.resort-body{padding:21px}.resort-meta{display:flex;align-items:center;justify-content:space-between;color:#718899;font-size:12px}.resort-meta span{display:flex;align-items:center;gap:4px}.resort-body h3{margin:13px 0 9px;color:#102f47;font-size:21px}.resort-body p{display:-webkit-box;min-height:47px;margin:0;color:#728595;font-size:13px;line-height:1.8;-webkit-box-orient:vertical;-webkit-line-clamp:2;overflow:hidden}.resort-body>a{display:inline-flex;align-items:center;gap:5px;margin-top:17px;color:#1479b8;font-size:13px;font-weight:700}
.service-section { background: linear-gradient(125deg, #082743, #0b668f 72%, #1c91a4); color:#fff; }.service-layout{display:grid;grid-template-columns:.8fr 1.2fr;align-items:center;gap:70px}.service-intro h2{margin:0;font-size:clamp(34px,4vw,48px);line-height:1.3}.service-intro p{margin:20px 0 30px;color:rgba(255,255,255,.7);line-height:1.9}.service-intro :deep(.el-button){color:#0e648f}.service-grid{display:grid;grid-template-columns:1fr 1fr;gap:16px}.service-card{position:relative;display:grid;min-height:190px;padding:24px;border:1px solid rgba(255,255,255,.15);border-radius:18px;background:rgba(255,255,255,.09);backdrop-filter:blur(9px);transition:.25s}.service-card:hover{transform:translateY(-4px);background:rgba(255,255,255,.15)}.service-card>span{position:absolute;top:17px;right:19px;color:rgba(255,255,255,.35);font-size:12px}.service-card i{font-size:31px;font-style:normal}.service-card strong{align-self:end;margin-top:18px;font-size:18px}.service-card small{margin-top:8px;color:rgba(255,255,255,.62);font-size:12px;line-height:1.65}
.news-layout{display:grid;grid-template-columns:.65fr 1.35fr;gap:75px}.news-heading .text-link{margin-top:25px}.news-list{display:grid}.news-item{display:grid;grid-template-columns:66px 1fr auto;align-items:center;gap:22px;padding:19px 10px;border-bottom:1px solid #dce8ef;transition:.2s}.news-item:hover{padding-right:4px;padding-left:16px;background:#f7fbfd}.news-item time{display:grid;justify-items:center;padding-right:15px;border-right:1px solid #dbe7ed}.news-item time strong{color:#126f9f;font-size:28px}.news-item time span{color:#8395a3;font-size:10px}.news-item>div{min-width:0}.news-item h3{overflow:hidden;margin:7px 0 4px;color:#17354b;font-size:16px;text-overflow:ellipsis;white-space:nowrap}.news-item p{overflow:hidden;margin:0;color:#7a8d9b;font-size:12px;text-overflow:ellipsis;white-space:nowrap}.news-item>.el-icon{color:#6e899b}
@media(max-width:980px){.hero-inner{grid-template-columns:1fr;gap:34px;padding-top:90px}.hero{min-height:auto}.weather-glance{max-width:600px}.resort-grid{grid-template-columns:1fr 1fr}.service-layout,.news-layout{grid-template-columns:1fr}.service-layout{gap:40px}.news-layout{gap:35px}.footer-grid{grid-template-columns:1fr 1fr}}
@media(max-width:680px){.hero-inner{padding-top:65px;padding-bottom:70px}.hero h1{font-size:43px;letter-spacing:-2px}.hero-copy>p{font-size:15px}.hero-actions{align-items:stretch;flex-direction:column}.hero-actions a,.hero-actions :deep(.el-button){width:100%}.weather-glance{padding:19px}.quick-stats{margin-top:-20px}.stats-grid{grid-template-columns:1fr 1fr}.stats-grid div:nth-child(2){border-right:0}.stats-grid div{padding:20px 8px;border-bottom:1px solid #e4edf2}.stats-grid div:nth-child(3),.stats-grid div:nth-child(4){border-bottom:0}.stats-grid strong{font-size:22px}.section-heading{align-items:start;flex-direction:column}.resort-grid,.service-grid{grid-template-columns:1fr}.service-card{min-height:165px}.news-item{grid-template-columns:54px 1fr;gap:12px}.news-item>.el-icon{display:none}.news-heading .text-link{margin-top:18px}}
</style>
