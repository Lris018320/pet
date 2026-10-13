<template>
  <section class="page-banner" :class="`theme-${theme}`">
    <div class="banner-deco deco-1"></div>
    <div class="banner-deco deco-2"></div>
    <div class="banner-inner">
      <div class="banner-text">
        <div class="banner-icon">
          <span>{{ icon }}</span>
        </div>
        <div class="banner-heading">
          <h1>{{ title }}</h1>
          <p v-if="subtitle">{{ subtitle }}</p>
        </div>
      </div>
      <div v-if="$slots.default || $slots.actions" class="banner-extra">
        <slot name="actions"></slot>
        <slot></slot>
      </div>
    </div>
  </section>
</template>

<script setup>
defineProps({
  icon: { type: String, default: '🐾' },
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  // 主题：purple(默认紫) / orange(暖橙) / green(青绿) / blue(蓝) / dark(深灰)
  theme: { type: String, default: 'purple' }
})
</script>

<style scoped>
.page-banner {
  position: relative;
  overflow: hidden;
  padding: 44px clamp(16px, 2vw, 32px);
  color: #fff;
}
.theme-purple {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.theme-orange {
  background: linear-gradient(135deg, #f6a96b 0%, #ee7b6b 100%);
}
.theme-green {
  background: linear-gradient(135deg, #43e97b 0%, #2fb7a5 100%);
}
.theme-blue {
  background: linear-gradient(135deg, #4facfe 0%, #3b82d4 100%);
}
.theme-dark {
  background: linear-gradient(135deg, #2c3e50 0%, #4a4a6a 100%);
}

.banner-deco {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  pointer-events: none;
}
.deco-1 {
  width: 320px;
  height: 320px;
  top: -160px;
  right: -80px;
}
.deco-2 {
  width: 220px;
  height: 220px;
  bottom: -130px;
  left: 8%;
  background: rgba(255, 255, 255, 0.06);
}

.banner-inner {
  position: relative;
  width: 100%;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}
.banner-text {
  display: flex;
  align-items: center;
  gap: 18px;
}
.banner-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.banner-icon span {
  font-size: 32px;
  line-height: 1;
}
.banner-heading h1 {
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 4px;
  letter-spacing: 0.3px;
}
.banner-heading p {
  font-size: 14px;
  opacity: 0.9;
}
.banner-extra {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}
/* 横幅内按钮适配 */
.banner-extra :deep(.el-button--default) {
  background: rgba(255, 255, 255, 0.15);
  border-color: rgba(255, 255, 255, 0.4);
  color: #fff;
}
.banner-extra :deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 0.28);
  border-color: #fff;
  color: #fff;
}
.banner-extra :deep(.el-button--primary) {
  background: #fff;
  border-color: #fff;
  color: #5a54b8;
  font-weight: 600;
}
.banner-extra :deep(.el-button--primary:hover) {
  background: #f5f5f5;
  border-color: #f5f5f5;
  color: #5a54b8;
}

@media (max-width: 600px) {
  .page-banner {
    padding: 32px 16px;
  }
  .banner-heading h1 {
    font-size: 20px;
  }
  .banner-icon {
    width: 48px;
    height: 48px;
  }
  .banner-icon span {
    font-size: 26px;
  }
}
</style>
