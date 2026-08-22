<script setup>
defineProps({
  eyebrow: { type: String, default: 'SKI HEILONGJIANG' },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  icon: { type: String, default: '❄' }
})
</script>

<template>
  <section class="page-banner">
    <div class="banner-grid" aria-hidden="true" />
    <div class="orb orb-one" aria-hidden="true" />
    <div class="orb orb-two" aria-hidden="true" />
    <div class="page-container banner-inner">
      <div class="banner-copy">
        <span class="eyebrow">{{ eyebrow }}</span>
        <h1>{{ title }}</h1>
        <p v-if="description">{{ description }}</p>
        <div v-if="$slots.actions" class="actions"><slot name="actions" /></div>
      </div>
      <div class="banner-emblem" aria-hidden="true">
        <span>{{ icon }}</span>
        <small>北纬 45° 好雪之境</small>
      </div>
    </div>
  </section>
</template>

<style scoped>
.page-banner {
  position: relative;
  overflow: hidden;
  min-height: 330px;
  background: linear-gradient(125deg, #092947 0%, #0b659a 56%, #37b9d1 100%);
  color: #fff;
}

.banner-grid {
  position: absolute;
  inset: 0;
  opacity: 0.13;
  background-image: linear-gradient(rgba(255,255,255,.22) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,.22) 1px, transparent 1px);
  background-size: 42px 42px;
  transform: perspective(450px) rotateX(58deg) scale(1.35) translateY(48%);
}

.orb {
  position: absolute;
  border: 1px solid rgba(255,255,255,.22);
  border-radius: 50%;
  background: rgba(255,255,255,.07);
}

.orb-one { top: -110px; right: 9%; width: 290px; height: 290px; }
.orb-two { bottom: -130px; left: 42%; width: 250px; height: 250px; }

.banner-inner {
  position: relative;
  z-index: 1;
  display: flex;
  min-height: 330px;
  align-items: center;
  justify-content: space-between;
  gap: 50px;
  padding-top: 44px;
  padding-bottom: 44px;
}

.banner-copy { max-width: 690px; }
.eyebrow {
  display: inline-flex;
  margin-bottom: 15px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 3px;
  opacity: .78;
}

h1 {
  margin: 0;
  font-size: clamp(34px, 5vw, 56px);
  line-height: 1.13;
  letter-spacing: -1.5px;
}

p {
  max-width: 650px;
  margin: 18px 0 0;
  color: rgba(255,255,255,.83);
  font-size: 16px;
  line-height: 1.85;
}

.actions { margin-top: 25px; }

.banner-emblem {
  display: grid;
  width: 180px;
  height: 180px;
  flex: 0 0 auto;
  place-content: center;
  justify-items: center;
  gap: 12px;
  border: 1px solid rgba(255,255,255,.28);
  border-radius: 50%;
  background: rgba(255,255,255,.09);
  box-shadow: inset 0 0 0 13px rgba(255,255,255,.035);
  backdrop-filter: blur(8px);
}

.banner-emblem span { font-size: 54px; line-height: 1; }
.banner-emblem small { font-size: 11px; letter-spacing: 2px; opacity: .82; }

@media (max-width: 720px) {
  .page-banner,
  .banner-inner { min-height: 290px; }
  .banner-emblem { display: none; }
  .banner-inner { padding-top: 38px; padding-bottom: 38px; }
}
</style>
