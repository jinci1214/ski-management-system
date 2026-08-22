<script setup>
defineProps({
  compact: { type: Boolean, default: false },
  inverse: { type: Boolean, default: false }
})
</script>

<template>
  <span class="snow-logo" :class="{ compact, inverse }" aria-label="黑龙江滑雪场管理系统">
    <span class="logo-mark" aria-hidden="true">
      <svg viewBox="0 0 48 48" role="img">
        <path class="mark-bg" d="M24 2.7 42.4 13.3v21.4L24 45.3 5.6 34.7V13.3L24 2.7Z" />
        <path class="mountain-back" d="m8.8 31.8 9.8-13.4 5.2 7 5.4-8.1 10 14.5H8.8Z" />
        <path class="mountain-front" d="m9.7 34.3 8.9-10.6 5.6 6.7 4-4.4 10.1 8.3H9.7Z" />
        <path class="snow-line" d="M13 36.7c6.2-2.8 15.7-2.8 22 0" />
        <path class="flake" d="M32.8 9.1v7.2m-3.1-5.4 6.2 3.6m0-3.6-6.2 3.6" />
      </svg>
    </span>
    <span v-if="!compact" class="logo-copy">
      <strong>龙江雪境</strong>
      <small>SKI HEILONGJIANG</small>
    </span>
  </span>
</template>

<style scoped>
.snow-logo {
  display: inline-flex;
  align-items: center;
  gap: 11px;
  color: #0b2744;
  line-height: 1;
}

.logo-mark {
  display: inline-flex;
  width: 44px;
  height: 44px;
  flex: 0 0 auto;
}

.compact .logo-mark {
  width: 38px;
  height: 38px;
}

svg {
  width: 100%;
  height: 100%;
}

.mark-bg { fill: #1479b8; }
.mountain-back { fill: #7ed7e6; }
.mountain-front { fill: #fff; }
.snow-line {
  fill: none;
  stroke: #fff;
  stroke-linecap: round;
  stroke-width: 1.8;
}
.flake {
  fill: none;
  stroke: #fff;
  stroke-linecap: round;
  stroke-width: 1.25;
}

.logo-copy {
  display: grid;
  gap: 4px;
}

.logo-copy strong {
  font-size: 19px;
  font-weight: 800;
  letter-spacing: 2px;
  white-space: nowrap;
}

.logo-copy small {
  color: #6d8294;
  font-size: 8px;
  font-weight: 700;
  letter-spacing: 1.7px;
  white-space: nowrap;
}

.inverse,
.inverse .logo-copy small {
  color: #fff;
}
</style>
