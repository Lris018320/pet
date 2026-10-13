---
name: "vue-element-page-style"
description: "Build/beautify Vue3 + Element Plus pages with a reusable UI kit (PageBanner + page-shell/content-card classes). Invoke when creating new front-end pages or when user says pages look flat/ugly/plain and need visual depth or unification."
---

# Vue3 + Element Plus 页面美化规范（本工作区专用）

适用于本宠物平台（pet-frontend，Vue3 + Vite + Element Plus + scoped CSS）。
**目标**：让页面摆脱"扁平、无层级"观感，统一为「彩色渐变页头 + 灰底白卡 + 柔和阴影」的成熟产品风格。

## 一、何时使用
- 新增任何前台功能页（列表页 / 详情页 / 表单页 / 个人中心）。
- 用户反馈页面"扁平、不好看、杂乱、不像正常网页"时。
- 批量统一多个页面视觉风格时。

## 二、核心原则（来自踩坑经验）
1. **先复用现有体系，勿自创骨架**：本项目已有 `PageBanner.vue` 组件和 `src/styles/global.css` 中的页面类，新页面直接套用，不要另写一套 page-header/container。
2. **设计令牌优先，少硬编码**：颜色用品牌色 `#667eea` 或 `--el-color-primary`；页面类已在 global.css 定义，优先加 class 而非写内联/重复样式。
3. **样式收敛在 scoped**：页面专属样式写在 `<style scoped>`；覆盖 Element Plus 内部结构用 `根类 + :deep(...)`，避免全局污染。
4. **先定 DOM 骨架再写样式**：结构（横幅→page-body→卡片）确定后一次性写样式；删除旧 class 时同步删除其样式，不留死代码。
5. **每次迭代只动一类问题**（版式 → 密度 → 响应式），不要反复来回改。

## 三、标准页面骨架

```vue
<template>
  <div class="page-shell xxx-page">
    <PageBanner
      icon="🐾"
      title="页面标题"
      subtitle="一句副标题说明页面用途"
      theme="purple"
    >
      <template #actions>
        <el-button type="primary" @click="...">主操作</el-button>
      </template>
    </PageBanner>

    <div class="page-body">
      <!-- 筛选条（可选） -->
      <el-card class="filter-card" :body-style="{ padding: '14px 18px' }"> ... </el-card>

      <!-- 内容卡片 -->
      <el-card class="content-card"> ... </el-card>

      <!-- 可点击卡片网格：content-card + hover-card，配合 :body-style="{ padding:'0' }" 做封面卡 -->
    </div>
  </div>
</template>

<script setup>
import PageBanner from '@/components/PageBanner.vue'
</script>
```

详情页用 `<div class="page-body narrow">`（max-width 900px）。

## 四、PageBanner 组件
路径：`src/components/PageBanner.vue`（已存在）。
Props：
- `icon`：emoji 字符串
- `title` / `subtitle`
- `theme`：`purple`(默认) / `orange` / `green` / `blue` / `dark`
插槽：`#actions` 放横幅右侧按钮（白/透明样式已内置适配）。

主题分配约定：识别=purple，档案=orange，资讯=blue，记录=green，个人中心/品种=dark。

## 五、global.css 可复用类（已存在，勿重复定义）
| class | 用途 |
| --- | --- |
| `.page-shell` | 页面最外层，渐变灰底 + 底部留白 |
| `.page-body` / `.page-body.narrow` | 内容容器，1200 / 900 居中 |
| `.content-card` | 白色卡片：圆角14 + 柔和阴影 + hover 加深；搭配 `<el-card>` 使用 |
| `.filter-card` | 筛选/搜索条卡片 |
| `.hover-card` | 可点击卡片：hover 上浮4px + 深阴影 |
| `.section-heading` | 区块小标题（左侧紫色竖条） |

品牌色已注入 Element Plus 变量（`--el-color-primary: #667eea`），按钮/链接默认即品牌色。

## 六、常见页面模板要点
- **列表页**：PageBanner → filter-card（radio-group/input）→ 卡片网格（`content-card hover-card`，封面图 `:body-style="{padding:'0'}"`，封面 hover `scale(1.05)`，角标用绝对定位 tag）→ 分页。
- **详情页**：page-body narrow + 返回按钮 + 一张大 `content-card`；资讯类可用彩色渐变 `.article-header`（标题白字压在渐变色块上）拉开层级。
- **表单/个人中心**：左右两栏，左侧账号卡可用 `.account-cover` 渐变条 + 头像负 margin 上浮（`margin-top:-40px`）+ 白边头像制造层次。
- **空状态**：用大 emoji + `<el-empty>`，避免纯文字。
- **图片兜底**：无封面时用渐变背景块 + emoji + 分类名（参考 InfoList 的 palette 数组按 id 取色）。

## 七、响应式
- 卡片网格用 `<el-row :gutter="20">` + `<el-col :xs="24" :sm="12" :md="8">`。
- `@media (max-width:768px)` 隐藏/折叠横幅装饰、两栏改单栏。

## 八、验收
- 运行 `npx vite build`（用 cwd 参数切到 pet-frontend，PowerShell 5.1 不支持 `&&`）确认无编译错误；stderr 中的 chunk-size 警告可忽略。
- 视觉自检：页面是否有「彩色横幅 → 灰底 → 白卡阴影」三级层次；可点元素是否有 hover 反馈。
