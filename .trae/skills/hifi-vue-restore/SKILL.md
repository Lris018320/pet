---
name: "hifi-vue-restore"
description: "严格按高保真设计图还原/优化 Vue3+Element Plus 页面（本宠物平台工作区专属）。当用户提供设计稿或原型截图要求'按图还原/严格按高保真优化'、或指定 Hero 大图、扫描框浮卡、全屏铺满等效果时调用。涵盖区块拆解、AI 配图、演示数据兜底+真实 API 渐进增强、clamp 铺满、构建与浏览器自检规范。"
---

# 高保真页面对照还原（宠物平台工作区）

适用于本工作区 `pet-frontend`（Vue3 `<script setup>` + Vite 5 + Element Plus + scoped CSS，品牌紫蓝 `#6d5ce0/#667eea`）。用户提供高保真截图时，目标是**对照式还原**，不是主观美化。

## 一、工作流（按顺序执行）

1. **拆区块清单**：把设计图从上到下拆成固定区块（如 Hero / 主区多栏 / 底部多卡），写成约束清单后再动手，逐项落地，不新增图上没有的元素。
2. **读现状再写**：先 Read 目标 .vue 全文 + 相关 API 文件（`src/api/*.js`）+ 后端 VO（`pet-backend/.../vo/`），确认真实字段；前端展示字段多于后端时，用前端资料库（常量映射）补全，未知项优雅降级为"待补充"。
3. **整页重写优先**：页面结构变化大时直接 Write 重写整个 SFC（模板+脚本+样式），保留原有全部真实功能（接口调用、弹窗、跳转）。
4. **构建**：`npx vite build`（PowerShell，cwd 必须为 `pet-frontend`），必须看到 `✓ built in`。
5. **浏览器自检**：见第五节。全部通过后再汇报。

## 二、视觉规范（与已完成页面统一）

- **Hero 大图卡**：圆角 22px、`linear-gradient(125deg,#eef3ff,#e6ecff,#dfe8ff)` 浅蓝紫底；左文右图 flex 布局；图片用 AI 生成（见第三节）。
- **扫描框**：定位层内放 `<i class="tl/tr/bl/br">` 四个 18px 白色 L 形角（相邻两边 3px 白边 + drop-shadow），不要画完整矩形。
- **识别浮卡**：白底 96% + 圆角 12px + 柔和阴影；圆形渐变图标底（紫 `#7c8df6→#6d5ce0` / 绿 `#36d9a5→#12b886`）+ 加粗品种名 + 紫色百分比。
- **装饰**：底部双层波浪用 SVG path（`#dbe6ff`/`#c4d5fb`，`preserveAspectRatio="none"` 绝对定位贴底）；手写标注用 `Comic Sans MS/STKaiti` + `rotate(-8deg)`；爪印/星点低透明度点缀。
- **卡片**：`#fff` 底、`1px solid #edf0f8`、圆角 16px、`box-shadow:0 10px 28px rgba(94,110,190,.07)`。
- **主按钮**：渐变紫胶囊（圆角 23-24px、高 46-48px、紫色投影）；次按钮白底 `1.6px solid #dcdffa` 紫字。
- **分段 Tab**：灰底 `#f2f4fa` 胶囊容器 + 选中项白底浮起；图标一律**内联 SVG**（24 viewBox、currentColor），不用 emoji 做主视觉。
- **状态态**：空态（浅紫圆底放大镜）、加载（爪印呼吸 + 进度条）、失败（红圆感叹号）都要设计，不用 el-empty 默认图。

## 三、AI 配图规范（禁止占位图）

所有装饰/实物图必须使用：
```
https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=<encodeURIComponent(英文提示词)>&image_size=<square_hd|square|portrait_4_3|portrait_16_9|landscape_4_3|landscape_16_9>
```
- 提示词写英文，包含主体 + 背景 + "professional pet photography/illustration"。
- 同一形象跨页面复用同一 URL（先 Grep `text_to_image` 看 Home.vue 已有的 dog/cat/bichon 等常量，保持一致）。
- 后端返回的图片路径用 `resolveImg()`：`http` 开头原样返回，否则拼 `import.meta.env.VITE_API_BASE || '/api'`。

## 四、数据模式：演示兜底 + 真实 API 渐进增强

```js
const records = ref([...演示数据...])   // 先保证高保真呈现
onMounted(async () => {
  try {
    const res = await myRecords({ page: 1, size: 4 })
    const list = res.data?.list || []
    if (list.length) records.value = list.map(...)   // 有真实数据才覆盖
  } catch (e) { /* 未登录/失败保留演示数据 */ }
})
```
- 时间格式化：`String(t).replace('T',' ').substring(0,16)`。
- 上传识别用 `el-upload` 的 `:http-request` 自定义接管（`uploadImage(file)` → `recognize(imageUrl)`），上传成功与识别合并为一次操作，识别中在预览图上叠扫描线动画。
- 示例图点击：直接展示预置品种结果（标记 `demo: true`），不调接口。
- 保存类按钮：先存记录再弹建档表单；接口失败给 `ElMessage.error('保存失败，请先登录后重试')`，页面不崩。

## 五、自检规范

- **构建产物核验**：scoped CSS 压缩后选择器带 `[data-v-xxxx]`，PowerShell 验证时用 `Contains("关键片段")` 或 `IndexOf+Substring` 看原文，**不要** `-replace '\s',''` 后匹配。
- **浏览器自检**：后端常未启动，路由守卫只看 token。先打开任意页面执行
  `localStorage.setItem('token','test-token'); localStorage.setItem('userInfo', JSON.stringify({id:1,nickname:'测试用户',role:'user'}))`
  再导航目标页；AxiosError/500 属预期，只关注 Vue 渲染错误。
- 交互逐项点验（示例切换/重置/Tab/弹窗），并查溢出：`document.documentElement.scrollWidth <= window.innerWidth`。

## 六、铺满规范（沿用全站）

- 页面容器：`width:100%; max-width:none; margin:0; padding:18px clamp(16px,2vw,32px) 40px`，不套 1200px 居中。
- 多栏用 CSS Grid `minmax(0, 1fr)` 防溢出；断点 1280（三栏→两栏）/1020（单栏）/760（图文纵叠）。
- 例外：文章正文保留 `max-width:900px; margin:0 auto` 阅读列；登录卡保留窄卡片居中。

## 七、本工作区踩过的坑（必须遵守）

1. **同一文件的多个 Edit 严禁放在同一条消息并行**——会互相覆盖丢失修改。串行编辑，每次改完 Grep/Read 复核关键行。
2. 不要对对象数组直接 `reduce` 求字符码（`tagStyle` 曾因此白屏）；字符运算先 `String(x||'')`。
3. 路由跳转以 `src/router/index.js` 实际路径为准（如品种列表是 `/breed` 不是 `/breeds`）。
4. 摄像头：`getUserMedia` 失败必须 try/catch 显示友好提示，切走 Tab/卸载组件时 `getTracks().stop()`。
5. 窄屏导航/长文本加 `min-width:0`、`flex-wrap`，横向滚动容器隐藏滚动条。
