<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  image: { type: String, default: '' },
  title: { type: String, default: '黑龙江冰雪之旅' },
  subtitle: { type: String, default: '遇见北国好雪' },
  tone: { type: String, default: 'blue' },
  height: { type: [String, Number], default: 220 }
})

const failed = ref(false)
watch(() => props.image, () => { failed.value = false })

const imageUrl = computed(() => {
  const value = props.image?.trim()
  if (!value) return ''
  if (/^(https?:|data:|blob:)/i.test(value) || value.startsWith('/api/')) return value
  return value.startsWith('/') ? `/api${value}` : value
})

const coverHeight = computed(() => typeof props.height === 'number' ? `${props.height}px` : props.height)
</script>

<template>
  <div class="default-cover" :class="[`tone-${tone}`, { 'has-image': imageUrl && !failed }]" :style="{ height: coverHeight }">
    <img v-if="imageUrl && !failed" :src="imageUrl" :alt="title" @error="failed = true" />
    <div v-else class="cover-art" aria-hidden="true">
      <span class="sun" />
      <span class="mountain mountain-one" />
      <span class="mountain mountain-two" />
      <span class="snowbank snowbank-one" />
      <span class="snowbank snowbank-two" />
      <span class="flake flake-one">❄</span>
      <span class="flake flake-two">✦</span>
    </div>
    <div class="shade" />
    <div class="cover-copy">
      <span>{{ subtitle }}</span>
      <strong>{{ title }}</strong>
    </div>
  </div>
</template>

<style scoped>
.default-cover {
  position: relative;
  min-height: 150px;
  overflow: hidden;
  background: linear-gradient(145deg, #0870ad 0%, #4fc5da 58%, #eafaff 100%);
  color: #fff;
}

.tone-cyan { background: linear-gradient(145deg, #087d9e, #54c9cf 58%, #edffff); }
.tone-navy { background: linear-gradient(145deg, #0b2744, #236a98 60%, #c8edfa); }
.tone-purple { background: linear-gradient(145deg, #344c92, #808fce 58%, #eef2ff); }

img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.45s ease;
}

.default-cover:hover img { transform: scale(1.035); }

.cover-art,
.shade {
  position: absolute;
  inset: 0;
}

.cover-art { overflow: hidden; }
.shade { background: linear-gradient(180deg, transparent 34%, rgba(2, 26, 48, 0.68) 100%); }
.has-image .shade { background: linear-gradient(180deg, rgba(6, 35, 58, 0.02), rgba(6, 35, 58, 0.74)); }

.sun {
  position: absolute;
  top: 15%;
  right: 14%;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 0 32px rgba(255, 255, 255, 0.42);
}

.mountain {
  position: absolute;
  bottom: 16%;
  width: 0;
  height: 0;
  border-style: solid;
  filter: drop-shadow(0 7px 10px rgba(9, 60, 91, 0.12));
}

.mountain-one {
  left: -5%;
  border-width: 0 115px 128px;
  border-color: transparent transparent rgba(218, 248, 255, 0.82);
}

.mountain-two {
  right: -8%;
  bottom: 12%;
  border-width: 0 145px 155px;
  border-color: transparent transparent rgba(239, 252, 255, 0.93);
}

.snowbank {
  position: absolute;
  bottom: -40%;
  width: 85%;
  height: 65%;
  border-radius: 50% 50% 0 0;
  background: #f7fdff;
}

.snowbank-one { left: -15%; transform: rotate(5deg); }
.snowbank-two { right: -20%; transform: rotate(-7deg); }

.flake { position: absolute; color: rgba(255, 255, 255, 0.74); }
.flake-one { top: 16%; left: 12%; font-size: 24px; }
.flake-two { top: 40%; right: 28%; font-size: 18px; }

.cover-copy {
  position: absolute;
  right: 20px;
  bottom: 17px;
  left: 20px;
  z-index: 2;
  display: grid;
  gap: 5px;
  text-shadow: 0 2px 12px rgba(0, 31, 57, 0.24);
}

.cover-copy span {
  font-size: 12px;
  letter-spacing: 2px;
  opacity: 0.9;
}

.cover-copy strong {
  overflow: hidden;
  font-size: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
