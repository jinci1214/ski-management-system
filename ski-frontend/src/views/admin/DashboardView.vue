<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getDashboardStatistics } from '../../api/admin'
import { formatMoney } from '../../utils/format'

const loading = ref(false)
const statistics = ref({})
const orderChartRef = ref()
const revenueChartRef = ref()
let orderChart
let revenueChart

const summaryCards = computed(() => [
  { label: '今日营业额', value: formatMoney(statistics.value.todayRevenue), note: '已支付及已完成订单', icon: 'Wallet', tone: 'blue' },
  { label: '今日订单', value: Number(statistics.value.todayOrderCount || 0), note: `累计订单 ${Number(statistics.value.orderCount || 0)} 笔`, icon: 'Tickets', tone: 'cyan' },
  { label: '注册游客', value: Number(statistics.value.userCount || 0), note: '系统累计注册人数', icon: 'UserFilled', tone: 'violet' },
  { label: '待支付订单', value: Number(statistics.value.pendingOrderCount || 0), note: '需要持续关注', icon: 'Timer', tone: 'orange' },
  { label: '运营雪场', value: Number(statistics.value.resortCount || 0), note: '已纳入系统管理', icon: 'Place', tone: 'green' },
  { label: '正常设施', value: Number(statistics.value.normalFacilityCount || 0), note: '当前运行状态正常', icon: 'SetUp', tone: 'indigo' },
  { label: '游客评分', value: Number(statistics.value.averageRating || 0).toFixed(1), note: '综合反馈评分 / 5', icon: 'StarFilled', tone: 'yellow' }
])

const getIconComponent = (name) => name

const baseTextStyle = {
  color: '#6d8294',
  fontFamily: 'Inter, PingFang SC, Microsoft YaHei, sans-serif'
}

const renderOrderChart = () => {
  if (!orderChartRef.value) return
  orderChart ||= echarts.init(orderChartRef.value)
  const source = Array.isArray(statistics.value.orderTypeDistribution)
    ? statistics.value.orderTypeDistribution.map((item) => ({
      name: item.orderType || '未分类',
      value: Number(item.count || 0)
    }))
    : []

  const hasData = source.some((item) => item.value > 0)
  orderChart.setOption({
    color: ['#1479b8', '#28b9cf', '#ff974f', '#7d75d8', '#55b78b'],
    tooltip: hasData ? { trigger: 'item', formatter: '{b}<br/>{c} 笔（{d}%）' } : { show: false },
    legend: {
      show: hasData,
      orient: 'vertical',
      right: 12,
      top: 'center',
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 16,
      textStyle: baseTextStyle
    },
    graphic: hasData ? [] : [{
      type: 'text',
      left: 'center',
      top: 'middle',
      style: { text: '暂无订单类型数据', fill: '#9aabb8', fontSize: 13 }
    }],
    series: [{
      type: 'pie',
      radius: ['48%', '70%'],
      center: hasData ? ['36%', '52%'] : ['50%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: { borderColor: '#fff', borderWidth: 4, borderRadius: 6 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 700 } },
      data: source
    }]
  }, true)
}

const renderRevenueChart = () => {
  if (!revenueChartRef.value) return
  revenueChart ||= echarts.init(revenueChartRef.value)
  const source = Array.isArray(statistics.value.recentSevenDayRevenue)
    ? statistics.value.recentSevenDayRevenue
    : []
  const dates = source.map((item) => String(item.date || '').slice(5).replace('-', '/'))
  const values = source.map((item) => Number(item.revenue || 0))
  const hasData = source.length > 0

  revenueChart.setOption({
    tooltip: {
      show: hasData,
      trigger: 'axis',
      backgroundColor: '#102f49',
      borderWidth: 0,
      textStyle: { color: '#fff' },
      formatter: (items) => `${items[0]?.axisValueLabel || ''}<br/>营业额：${formatMoney(items[0]?.value)}`
    },
    grid: { left: 18, right: 22, top: 30, bottom: 10, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: { lineStyle: { color: '#dce8ef' } },
      axisTick: { show: false },
      axisLabel: baseTextStyle
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitNumber: 4,
      axisLabel: { ...baseTextStyle, formatter: (value) => value >= 1000 ? `${value / 1000}k` : value },
      splitLine: { lineStyle: { color: '#e8f0f4', type: 'dashed' } }
    },
    graphic: hasData ? [] : [{
      type: 'text',
      left: 'center',
      top: 'middle',
      style: { text: '暂无近七日营收数据', fill: '#9aabb8', fontSize: 13 }
    }],
    series: [{
      name: '营业额',
      type: 'line',
      data: values,
      smooth: 0.32,
      symbol: 'circle',
      symbolSize: 7,
      showSymbol: hasData,
      lineStyle: { width: 3, color: '#1479b8' },
      itemStyle: { color: '#fff', borderColor: '#1479b8', borderWidth: 3 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(20, 121, 184, 0.28)' },
          { offset: 1, color: 'rgba(20, 121, 184, 0.02)' }
        ])
      }
    }]
  }, true)
}

const renderCharts = async () => {
  await nextTick()
  renderOrderChart()
  renderRevenueChart()
}

const loadStatistics = async (showSuccess = false) => {
  loading.value = true
  try {
    statistics.value = await getDashboardStatistics() || {}
    await renderCharts()
    if (showSuccess) ElMessage.success('看板数据已更新')
  } finally {
    loading.value = false
  }
}

const resizeCharts = () => {
  orderChart?.resize()
  revenueChart?.resize()
}

onMounted(() => {
  loadStatistics()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  orderChart?.dispose()
  revenueChart?.dispose()
})
</script>

<template>
  <div class="dashboard-page" v-loading="loading">
    <div class="page-heading">
      <div>
        <p>OPERATIONS OVERVIEW</p>
        <h2>今日运营概览</h2>
        <span>汇总雪场经营、服务与设施运行情况</span>
      </div>
      <el-button :loading="loading" @click="loadStatistics(true)">
        <el-icon><Refresh /></el-icon>刷新数据
      </el-button>
    </div>

    <section class="metric-grid">
      <article v-for="card in summaryCards" :key="card.label" class="metric-card">
        <div class="metric-icon" :class="`tone-${card.tone}`">
          <el-icon><component :is="getIconComponent(card.icon)" /></el-icon>
        </div>
        <div class="metric-copy">
          <span>{{ card.label }}</span>
          <strong>{{ card.value }}</strong>
          <small>{{ card.note }}</small>
        </div>
      </article>
    </section>

    <section class="chart-grid">
      <article class="chart-card revenue-card">
        <div class="card-title">
          <div><span>REVENUE TREND</span><h3>近七日营业额</h3></div>
          <el-tag effect="plain" round>最近 7 天</el-tag>
        </div>
        <div ref="revenueChartRef" class="chart"></div>
      </article>

      <article class="chart-card">
        <div class="card-title">
          <div><span>ORDER STRUCTURE</span><h3>订单类型分布</h3></div>
          <el-icon class="card-title-icon"><PieChart /></el-icon>
        </div>
        <div ref="orderChartRef" class="chart"></div>
      </article>
    </section>
  </div>
</template>

<style scoped>
.dashboard-page {
  min-height: 420px;
}

.page-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 22px;
}

.page-heading p,
.card-title span {
  margin: 0 0 5px;
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

.page-heading > div > span {
  display: block;
  margin-top: 7px;
  color: #7b8e9d;
  font-size: 13px;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 15px;
}

.metric-card {
  display: flex;
  min-width: 0;
  align-items: flex-start;
  gap: 14px;
  padding: 20px;
  border: 1px solid #e0eaf0;
  border-radius: 15px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(21, 72, 103, 0.055);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 32px rgba(21, 72, 103, 0.1);
}

.metric-icon {
  display: grid;
  width: 43px;
  height: 43px;
  flex: 0 0 auto;
  place-items: center;
  border-radius: 12px;
  font-size: 20px;
}

.tone-blue { color: #1479b8; background: #e7f3fb; }
.tone-cyan { color: #129ab3; background: #e3f8fa; }
.tone-violet { color: #7668cc; background: #f0edfd; }
.tone-orange { color: #e77836; background: #fff0e7; }
.tone-green { color: #36966e; background: #e7f6ef; }
.tone-indigo { color: #4e73c9; background: #eaf0fc; }
.tone-yellow { color: #d39a27; background: #fff6dc; }

.metric-copy {
  min-width: 0;
}

.metric-copy span,
.metric-copy small {
  display: block;
  overflow: hidden;
  color: #7b8f9e;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.metric-copy span {
  font-size: 12px;
}

.metric-copy strong {
  display: block;
  margin: 5px 0 4px;
  overflow: hidden;
  color: #17364f;
  font-size: 24px;
  line-height: 1.15;
  text-overflow: ellipsis;
}

.metric-copy small {
  font-size: 10px;
}

.chart-grid {
  display: grid;
  margin-top: 17px;
  grid-template-columns: minmax(0, 1.65fr) minmax(330px, 1fr);
  gap: 17px;
}

.chart-card {
  min-width: 0;
  padding: 22px 22px 12px;
  border: 1px solid #e0eaf0;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(21, 72, 103, 0.055);
}

.card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.card-title h3 {
  margin: 0;
  color: #17364f;
  font-size: 17px;
}

.card-title-icon {
  color: #7993a5;
  font-size: 24px;
}

.chart {
  width: 100%;
  height: 330px;
}

@media (max-width: 1280px) {
  .metric-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 1060px) {
  .metric-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .chart-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .page-heading {
    align-items: flex-start;
  }

  .page-heading h2 {
    font-size: 22px;
  }

  .page-heading > div > span {
    display: none;
  }

  .metric-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .metric-card {
    padding: 16px;
  }

  .chart-card {
    padding: 18px 12px 8px;
  }

  .chart {
    height: 290px;
  }
}
</style>
