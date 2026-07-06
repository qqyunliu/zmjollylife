<template>
  <div class="dashboard">
    <h1 class="page-title">视频数据</h1>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card stat-users">
        <div class="stat-header">
          <div>
            <span class="stat-title">用户数</span>
          </div>
          <span class="stat-change">{{ userStats.change }}</span>
        </div>
        <div class="stat-value">{{ userStats.value }}</div>
      </div>

      <div class="stat-card stat-play">
        <div class="stat-header">
          <div>
            <span class="stat-title">播放</span>
          </div>
          <span class="stat-change">{{ playStats.change }}</span>
        </div>
        <div class="stat-value"
             style="color: #2196F3;">{{ playStats.value }}</div>
      </div>

      <div class="stat-card stat-like">
        <div class="stat-header">
          <div>
            <span class="stat-title">点赞</span>
          </div>
          <span class="stat-change">{{ likeStats.change }}</span>
        </div>
        <div class="stat-value"
             style="color: #4CAF50;">{{ likeStats.value }}</div>
      </div>

      <div class="stat-card stat-comment">
        <div class="stat-header">
          <div>
            <span class="stat-title">评论</span>
          </div>
          <span class="stat-change">{{ commentStats.change }}</span>
        </div>
        <div class="stat-value"
             style="color: #9E9E9E;">{{ commentStats.value }}</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-section">
      <div class="chart-header">
        <h2 class="chart-title">近7天新增用户数</h2>
        <div class="chart-controls">
          <div class="chart-legend">
            <div class="legend-dot"></div>
            <span>新增用户数</span>
          </div>
        </div>
      </div>
      <div class="chart-container">
        <canvas ref="chartCanvas"></canvas>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from 'vue'
import Chart from 'chart.js/auto'

const { proxy } = getCurrentInstance()

const chartCanvas = ref(null)
let chart = null

const formatNumber = (value) => {
  const n = Number(value || 0)
  return Number.isFinite(n) ? n.toLocaleString() : '0'
}

const userStats = ref({ value: '0', change: '' })
const playStats = ref({ value: '0', change: '' })
const likeStats = ref({ value: '0', change: '' })
const commentStats = ref({ value: '0', change: '' })

const chartData = ref({
  labels: [],
  data: []
})

const fetchStatsData = async () => {
  const result = await proxy.request({
    url: proxy.Api.getActualTimaStatisticsInfo
  })
  if (!result) return

  const data = result.data || {}
  userStats.value = { value: formatNumber(data.userCount), change: '' }
  playStats.value = { value: formatNumber(data.playCount), change: '' }
  likeStats.value = { value: formatNumber(data.likeCount), change: '' }
  commentStats.value = { value: formatNumber(data.commentCount), change: '' }
}

const fetchChartData = async () => {
  const result = await proxy.request({
    url: proxy.Api.getWeekStatisticsInfo
  })
  if (!result) return

  chartData.value = {
    labels: result.data?.labels || [],
    data: result.data?.data || []
  }
}

// 初始化图表
const initChart = () => {
  const ctx = chartCanvas.value.getContext('2d')
  chart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: chartData.value.labels,
      datasets: [{
        label: '新增用户数',
        data: chartData.value.data,
        borderColor: '#e91e63',
        backgroundColor: 'rgba(233, 30, 99, 0.1)',
        borderWidth: 2,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#e91e63',
        pointBorderColor: '#e91e63',
        pointRadius: 4,
        pointHoverRadius: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          grid: {
            display: false
          },
          ticks: {
            maxTicksLimit: 7
          }
        },
        y: {
          beginAtZero: true,
          ticks: {
            precision: 0
          },
          grid: {
            color: 'rgba(0, 0, 0, 0.1)'
          }
        }
      },
      interaction: {
        intersect: false,
        mode: 'index'
      }
    }
  })

}

// 更新图表数据
const updateChart = () => {
  if (chart) {
    chart.data.labels = chartData.value.labels
    chart.data.datasets[0].data = chartData.value.data
    chart.update()
  }
}

// 组件挂载时执行
onMounted(async () => {
  await fetchStatsData()
  await fetchChartData()
  initChart()
})

// 暴露方法供父组件调用（可选）
defineExpose({
  fetchStatsData,
  fetchChartData
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 20px;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 4px;
  background: #e3e5e7;
}

.stat-card.stat-users::before { background: #e91e63; }
.stat-card.stat-play::before { background: #2196F3; }
.stat-card.stat-like::before { background: #4CAF50; }
.stat-card.stat-comment::before { background: #9E9E9E; }

.stat-card .stat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat-card .stat-title {
  font-size: 14px;
  font-weight: normal;
  opacity: 0.8;
}

.stat-card .stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 4px;
}

.stat-card .stat-change {
  font-size: 12px;
  opacity: 0.7;
}

.chart-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.chart-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chart-legend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #e91e63;
}

.chart-container {
  height: 300px;
  position: relative;
}

/* 图标样式 */
.icon-users::before {
  content: '👥';
}
.icon-play::before {
  content: '▶️';
}
.icon-thumbs::before {
  content: '👍';
}
.icon-detail::before {
  content: '📊';
}
.icon-star::before {
  content: '⭐';
}
.icon-comment::before {
  content: '💬';
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
