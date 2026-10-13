<template>
  <div class="recog-page">
    <!-- ============ Hero 横幅 ============ -->
    <section class="hero-card">
      <div class="hero-inner">
        <div class="hero-left">
          <span class="hero-badge">
            <svg viewBox="0 0 24 24" class="ic"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg>
            AI 智能识别
          </span>
          <h1>一张照片，认识你的宠物</h1>
          <p class="hero-sub">基于深度学习的AI图像识别技术，快速识别宠物品种、年龄、性别、特征等信息</p>
          <div class="hero-feats">
            <div class="feat">
              <span class="feat-ic">
                <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="7.4" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="2.6" fill="currentColor"/><path d="M12 1.8v3M12 19.2v3M1.8 12h3M19.2 12h3M4.7 4.7l2.1 2.1M17.2 17.2l2.1 2.1M19.3 4.7l-2.1 2.1M6.8 17.2l-2.1 2.1" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>
              </span>
              <div class="feat-txt"><b>精准识别</b><small>识别准确率高达98%+</small></div>
            </div>
            <div class="feat">
              <span class="feat-ic">
                <svg viewBox="0 0 24 24"><path d="M4 20V10h4v10H4zM10 20V4h4v16h-4zM16 20v-7h4v7h-4z" fill="currentColor"/></svg>
              </span>
              <div class="feat-txt"><b>多维分析</b><small>品种 / 年龄 / 性别 / 特征</small></div>
            </div>
            <div class="feat">
              <span class="feat-ic">
                <svg viewBox="0 0 24 24"><path d="M12 2.6l7.6 2.8v5.2c0 4.8-3.2 8.4-7.6 10.4-4.4-2-7.6-5.6-7.6-10.4V5.4L12 2.6z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M8.6 11.8l2.4 2.4 4.6-5" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </span>
              <div class="feat-txt"><b>安全隐私</b><small>图片仅用于识别分析</small></div>
            </div>
          </div>
        </div>

        <div class="hero-visual">
          <img :src="IMG.hero" alt="猫狗识别" class="hero-photo" />
          <!-- 扫描框：狗 -->
          <div class="scan scan-dog"><i class="tl"></i><i class="tr"></i><i class="bl"></i><i class="br"></i></div>
          <!-- 扫描框：猫 -->
          <div class="scan scan-cat"><i class="tl"></i><i class="tr"></i><i class="bl"></i><i class="br"></i></div>
          <!-- 浮卡 -->
          <div class="float-chip chip-dog">
            <span class="chip-ic chip-paw">
              <svg viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg>
            </span>
            <b>金毛寻回犬</b><em>98.7%</em>
          </div>
          <div class="float-chip chip-cat">
            <span class="chip-ic chip-heart">
              <svg viewBox="0 0 24 24"><path d="M12 20s-7-4.3-9-8.4C1.5 8.4 3.4 5 6.6 5c1.9 0 3.3 1 4.4 2.6h2C14.1 6 15.5 5 17.4 5c3.2 0 5.1 3.4 3.6 6.6-2 4.1-9 8.4-9 8.4z" fill="#fff"/></svg>
            </span>
            <b>中华田园猫</b><em>96.3%</em>
          </div>
          <!-- 手写标注 -->
          <div class="hero-note">让每一只宠物<br>都被温柔以待 ♡</div>
          <!-- 装饰爪印 / 星点 -->
          <svg class="deco-paw" viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg>
          <span class="deco-plus dp1">✦</span>
          <span class="deco-plus dp2">✚</span>
          <!-- 底部波浪 -->
          <svg class="hero-wave" viewBox="0 0 500 90" preserveAspectRatio="none">
            <path d="M0,50 C90,88 170,10 260,42 C340,70 410,60 500,30 L500,90 L0,90 Z" fill="#dbe6ff"/>
            <path d="M0,66 C100,90 180,40 280,60 C370,78 430,72 500,52 L500,90 L0,90 Z" fill="#c4d5fb"/>
          </svg>
        </div>
      </div>
    </section>

    <!-- ============ 主区：上传 + 结果 ============ -->
    <div class="main-grid">
      <!-- 左：上传面板 -->
      <section class="panel upload-panel">
        <div class="seg-tabs">
          <button class="seg" :class="{ on: activeTab === 'upload' }" @click="switchTab('upload')">
            <svg viewBox="0 0 24 24"><rect x="3" y="4.5" width="18" height="15" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="8.5" cy="9.5" r="1.6" fill="currentColor"/><path d="M4.5 16.5l4.2-4.2 3.4 3.4 2.6-2.6 4.8 4.8" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
            图片上传
          </button>
          <button class="seg" :class="{ on: activeTab === 'camera' }" @click="switchTab('camera')">
            <svg viewBox="0 0 24 24"><rect x="3" y="7" width="18" height="12.5" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M8 7l1.3-2.4h5.4L16 7" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><circle cx="12" cy="13.3" r="3.4" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
            摄像头拍摄
          </button>
          <button class="seg" :class="{ on: activeTab === 'phone' }" @click="switchTab('phone')">
            <svg viewBox="0 0 24 24"><rect x="7" y="3" width="10" height="18" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="17.4" r="1.1" fill="currentColor"/><path d="M10.5 6h3" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>
            手机拍摄
          </button>
        </div>

        <!-- 图片上传 -->
        <div v-show="activeTab === 'upload'" class="tab-body">
          <el-upload
            class="drop-upload"
            drag
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
            accept=".jpg,.jpeg,.png"
          >
            <div v-if="previewUrl" class="preview-box">
              <img :src="previewUrl" alt="预览" />
              <div v-if="recognizing" class="scan-mask">
                <span class="scan-line"></span>
                <div class="scan-tip">
                  <span class="scan-spin"></span>
                  AI 正在识别特征…
                </div>
              </div>
              <div class="reselect">↻ 重新选择图片</div>
            </div>
            <div v-else class="drop-box">
              <span class="cloud-ic">
                <svg viewBox="0 0 24 24"><path d="M7 18a4 4 0 0 1-.6-7.96A5.5 5.5 0 0 1 17.2 9.2 3.75 3.75 0 0 1 17 18H7z" fill="#a8b6f5"/><path d="M12 15.5v-6m0 0L9.4 12M12 9.5l2.6 2.5" fill="none" stroke="#fff" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </span>
              <div class="drop-title">拖拽图片到此处，或点击上传</div>
              <div class="drop-sub">支持 JPG、PNG、JPEG 格式，文件大小不超过 10MB</div>
              <span class="pick-btn">
                <svg viewBox="0 0 24 24"><path d="M4 6.5A2.5 2.5 0 0 1 6.5 4h2.2l1.2 1.8h6.4l-1.2-1.8h2.4A2.5 2.5 0 0 1 20 6.5v11A2.5 2.5 0 0 1 17.5 20h-11A2.5 2.5 0 0 1 4 17.5v-11z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/></svg>
                选择图片
              </span>
            </div>
          </el-upload>

          <!-- 示例图片 -->
          <div class="samples">
            <div class="block-title">
              <span class="bt-ic">
                <svg viewBox="0 0 24 24"><rect x="3" y="4.5" width="18" height="15" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="8.5" cy="9.5" r="1.6" fill="currentColor"/><path d="M4.5 16.5l4.2-4.2 3.4 3.4 2.6-2.6 4.8 4.8" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>
              </span>
              示例图片
            </div>
            <div class="sample-list">
              <div
                v-for="s in samples"
                :key="s.key"
                class="sample"
                @click="useSample(s)"
              >
                <div class="sample-img"><img :src="s.photo" :alt="s.name" /></div>
                <span>{{ s.name }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 摄像头拍摄 -->
        <div v-show="activeTab === 'camera'" class="tab-body camera-body">
          <div class="camera-box">
            <video ref="videoRef" v-show="camOn" autoplay playsinline muted></video>
            <div v-if="!camOn" class="cam-off" @click="startCamera">
              <span class="cam-ic">
                <svg viewBox="0 0 24 24"><rect x="3" y="7" width="18" height="12.5" rx="2.5" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M8 7l1.3-2.4h5.4L16 7" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><circle cx="12" cy="13.3" r="3.4" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>
              </span>
              <b>点击开启摄像头</b>
              <small>请正对宠物，保持面部清晰、光线充足</small>
            </div>
            <div v-if="camError" class="cam-error">无法访问摄像头，请检查浏览器权限后改用图片上传</div>
          </div>
          <div v-if="camOn" class="cam-actions">
            <button class="pick-btn" @click="capture">
              <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="6.5" fill="none" stroke="currentColor" stroke-width="1.8"/><circle cx="12" cy="12" r="2.6" fill="currentColor"/></svg>
              拍照并识别
            </button>
            <button class="ghost-btn" @click="stopCamera">取消</button>
          </div>
        </div>

        <!-- 手机扫码拍摄 -->
        <div v-show="activeTab === 'phone'" class="tab-body camera-body">
          <div class="phone-box">
            <div class="qr-wrap">
              <img v-if="qrImg" :src="qrImg" alt="扫码拍摄" class="qr-img" />
              <span v-else class="qr-loading">二维码生成中…</span>
              <div v-if="qrExpired" class="qr-mask">
                <b>二维码已过期</b>
                <button class="pick-btn" @click="openPhoneShoot">刷新二维码</button>
              </div>
            </div>
            <b class="phone-tip">打开手机相机 / 微信扫一扫</b>
            <small>手机与电脑连接同一网络，拍照后自动传输识别<br />若扫码无法打开，请改用局域网 IP 访问本页面</small>
          </div>
        </div>
      </section>

      <!-- 右：识别结果 -->
      <section class="panel result-panel">
        <div class="panel-head">
          <div class="ph-title">
            <span class="head-ic green">
              <svg viewBox="0 0 24 24"><rect x="4.5" y="8" width="15" height="11" rx="3" fill="#fff"/><circle cx="9" cy="13" r="1.4" fill="#12b886"/><circle cx="15" cy="13" r="1.4" fill="#12b886"/><path d="M9.5 16h5M12 4.5v3.6M8.5 5.4l1 1.4M15.5 5.4l-1 1.4" stroke="#fff" stroke-width="1.6" stroke-linecap="round"/><rect x="2.7" y="6.4" width="3.2" height="3.2" rx="1" fill="#fff"/></svg>
            </span>
            <b>识别结果</b>
          </div>
          <span v-if="recognizeTime" class="rec-time">识别时间：{{ recognizeTime }}</span>
        </div>

        <!-- 空态 -->
        <div v-if="!result && !recognizing" class="r-empty">
          <span class="empty-ic">
            <svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="6.5" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M16 16l4.2 4.2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><path d="M8.6 11h4.8M11 8.6v4.8" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg>
          </span>
          <b>等待识别</b>
          <small>上传或拍摄一张宠物照片<br>AI 识别结果将在这里展示</small>
        </div>

        <!-- 识别中 -->
        <div v-else-if="recognizing" class="r-loading">
          <span class="loading-paw">
            <svg viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z"/></svg>
          </span>
          <p>AI 正在分析宠物特征…</p>
          <div class="loading-bar"><i></i></div>
        </div>

        <!-- 失败 -->
        <div v-else-if="result && !result.success" class="r-fail">
          <span class="fail-ic">!</span>
          <b>识别失败</b>
          <p>{{ result.message || '请上传清晰的宠物正面照片重试' }}</p>
          <button class="pick-btn" @click="reset">重新上传</button>
        </div>

        <!-- 成功 -->
        <div v-else-if="result && result.success" class="r-body">
          <div class="r-main">
            <div class="r-photo">
              <img :src="result.photo" :alt="result.breed" />
              <span class="ok-badge">
                <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" fill="#fff"/><path d="M7.5 12.3l3 3 6-6.4" fill="none" stroke="#12b886" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                识别成功
              </span>
            </div>
            <div class="r-info">
              <div class="r-name-line">
                <h3>{{ result.breed }}</h3>
                <span class="conf-tag">{{ fmtConf(result.confidence) }}%</span>
              </div>
              <p class="r-desc">{{ result.desc }}</p>
              <ul class="r-meta">
                <li v-for="m in result.meta" :key="m.label">
                  <span class="meta-ic" v-html="ICONS[m.icon]"></span>
                  <span class="meta-label">{{ m.label }}</span>
                  <b>{{ m.value }}</b>
                </li>
              </ul>
            </div>
          </div>

          <div class="r-traits">
            <h4>
              <span class="spark-ic">
                <svg viewBox="0 0 24 24"><path d="M12 3l1.9 5.1L19 10l-5.1 1.9L12 17l-1.9-5.1L5 10l5.1-1.9L12 3z" fill="currentColor"/></svg>
              </span>
              AI 特征分析
            </h4>
            <div class="trait-tags">
              <span v-for="t in result.traits" :key="t" class="trait">
                <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" fill="#d7f3e8"/><path d="M8 12.2l2.6 2.6L16.2 9" fill="none" stroke="#12b886" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                {{ t }}
              </span>
              <span class="trait-more">›</span>
            </div>
          </div>

          <div class="r-actions">
            <button class="primary-btn" :disabled="saving || saved" @click="saveToArchive">
              <svg viewBox="0 0 24 24"><path d="M4 5.5A1.5 1.5 0 0 1 5.5 4h11.4L20 7.1V18.5a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 4 18.5v-13z" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/><rect x="8" y="4" width="8" height="5.5" rx="1" fill="currentColor" opacity=".35"/><path d="M8 14h8v5H8z" fill="currentColor" opacity=".35"/></svg>
              {{ saving ? '保存中…' : saved ? '✓ 已保存到我的档案' : '保存到我的档案' }}
            </button>
            <button class="outline-btn" @click="reset">
              <svg viewBox="0 0 24 24"><path d="M20 12a8 8 0 1 1-2.34-5.66M20 4v4h-4" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round" stroke-linejoin="round"/></svg>
              重新识别
            </button>
          </div>
        </div>
      </section>
    </div>

    <!-- ============ 底部三栏 ============ -->
    <div class="bottom-grid">
      <!-- AI 助手推广 -->
      <section class="assist-card" @click="router.push('/chat')">
        <img :src="IMG.robot" alt="AI助手" class="robot-img" />
        <div class="assist-txt">
          <b>不确定是什么品种？<br>试试我们的 AI 宠物助手吧！</b>
          <p>AI智能问答，为你解答养宠问题</p>
        </div>
        <span class="consult-btn">立即咨询</span>
      </section>

      <!-- 识别记录 -->
      <section class="panel sub-card">
        <div class="panel-head">
          <div class="ph-title">
            <span class="head-ic purple">
              <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="8.5" fill="none" stroke="#fff" stroke-width="1.8"/><path d="M12 7.5V12l3 2" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round"/></svg>
            </span>
            <b>识别记录</b>
          </div>
          <a class="more-link" @click="router.push('/my-records')">查看更多 ></a>
        </div>
        <div class="rec-list">
          <div v-for="(r, i) in records" :key="i" class="rec-item" @click="router.push('/my-records')">
            <img :src="r.photo" :alt="r.breed" />
            <div class="rec-meta">
              <b>{{ r.breed }}</b>
              <small>{{ r.time }}</small>
            </div>
          </div>
        </div>
      </section>

      <!-- 识别小贴士 -->
      <section class="panel sub-card">
        <div class="panel-head">
          <div class="ph-title">
            <span class="head-ic blue">
              <svg viewBox="0 0 24 24"><path d="M9 17.5h6M10 20h4M12 3a6 6 0 0 0-3.6 10.8c.7.6 1.1 1.3 1.1 2.2h5c0-.9.4-1.6 1.1-2.2A6 6 0 0 0 12 3z" fill="none" stroke="#fff" stroke-width="1.8" stroke-linejoin="round"/></svg>
            </span>
            <b>识别小贴士</b>
          </div>
          <a class="more-link" @click="router.push('/breed')">查看更多 ></a>
        </div>
        <ul class="tips">
          <li>
            <span class="tip-check"><svg viewBox="0 0 24 24"><path d="M6 12.4l3.8 3.8L18 8" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
            请确保宠物照片清晰、光线充足
          </li>
          <li>
            <span class="tip-check"><svg viewBox="0 0 24 24"><path d="M6 12.4l3.8 3.8L18 8" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
            避免遮挡宠物的面部特征
          </li>
          <li>
            <span class="tip-check"><svg viewBox="0 0 24 24"><path d="M6 12.4l3.8 3.8L18 8" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/></svg></span>
            建议使用正面或侧面全身照
          </li>
        </ul>
      </section>
    </div>

    <!-- ============ 一键建档弹窗 ============ -->
    <el-dialog
      v-model="archiveDialogVisible"
      title="保存到我的档案"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="archiveFormRef"
        :model="archiveForm"
        :rules="archiveRules"
        label-width="90px"
      >
        <el-form-item label="宠物名称" prop="petName">
          <el-input v-model="archiveForm.petName" placeholder="例如：豆豆" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="品种">
          <el-input v-model="archiveForm.breed" disabled />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="archiveForm.petType" disabled />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="archiveForm.gender">
            <el-radio value="公">公</el-radio>
            <el-radio value="母">母</el-radio>
            <el-radio value="未知">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model="archiveForm.age" placeholder="例如：2岁 / 8个月" maxlength="20" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="archiveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="archiving" @click="submitArchive">确认建档</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { uploadImage, recognize, saveRecord, myRecords, createUploadSession, getUploadSession } from '@/api/recognition'
import { createArchive } from '@/api/archive'
import QRCode from 'qrcode'

const router = useRouter()
const API_BASE = import.meta.env.VITE_API_BASE || '/api'

/* ========== 图片资源（AI 生成，与首页保持统一） ========== */
const IMG = {
  hero: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Happy golden retriever dog face close-up next to a grey white tabby cat with green eyes, soft light blue pastel studio background with gentle waves and paw prints, professional pet photography') +
    '&image_size=landscape_16_9',
  dog: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Happy golden retriever dog head portrait with tongue out smiling, soft cream studio background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  cat: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cute grey and white tabby Chinese domestic cat portrait with green eyes, soft neutral background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  husky: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Siberian husky dog head portrait with blue eyes, black and white fur, soft grey studio background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  corgi: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cute welsh corgi dog portrait smiling with big upright ears, orange and white fur, soft cream background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  bichon: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Adorable white bichon frise dog portrait smiling, soft light blue pastel background, professional pet photography, square avatar') +
    '&image_size=square_hd',
  robot: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=' +
    encodeURIComponent('Cute friendly white robot pet assistant mascot with glowing blue eyes and small antenna, rounded soft 3d style, light purple blue gradient background, professional illustration') +
    '&image_size=square_hd'
}

/* ========== 品种资料库（补充后端不返回的年龄/性别/体重/特征） ========== */
const ICONS = {
  paw: '<svg viewBox="0 0 24 24"><path d="M12 14.5c3.2 0 6 2.4 6 5 0 1-1.8 1.5-6 1.5s-6-.5-6-1.5c0-2.6 2.8-5 6-5zM7.2 11.6a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm9.6 0a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zM4.6 8.4a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4zm14.8 0a1.7 1.7 0 1 1 0-3.4 1.7 1.7 0 0 1 0 3.4z" fill="#5b7cf0"/></svg>',
  age: '<svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="8.2" fill="none" stroke="#5b7cf0" stroke-width="1.8"/><path d="M12 7.6V12l3 2" fill="none" stroke="#5b7cf0" stroke-width="1.8" stroke-linecap="round"/></svg>',
  gender: '<svg viewBox="0 0 24 24"><circle cx="10" cy="14" r="4.6" fill="none" stroke="#5b7cf0" stroke-width="1.8"/><path d="M13.4 10.6L19 5m0 0h-4m4 0v4" fill="none" stroke="#5b7cf0" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>',
  weight: '<svg viewBox="0 0 24 24"><path d="M12 7a5 5 0 1 0 0 10 5 5 0 0 0 0-10zm0 3.2a1.8 1.8 0 1 1 0 3.6 1.8 1.8 0 0 1 0-3.6z" fill="none" stroke="#5b7cf0" stroke-width="1.7"/><path d="M12 7V4.6m-2.4 0h4.8" fill="none" stroke="#5b7cf0" stroke-width="1.7" stroke-linecap="round"/></svg>',
  ruler: '<svg viewBox="0 0 24 24"><rect x="3.5" y="8" width="17" height="8" rx="1.6" transform="rotate(-24 12 12)" fill="none" stroke="#5b7cf0" stroke-width="1.7"/><path d="M8.4 10.6l1 1.8M11.2 9.4l1 1.8M14 8.2l1 1.8" stroke="#5b7cf0" stroke-width="1.4" stroke-linecap="round"/></svg>'
}

const PROFILES = {
  golden: {
    key: 'golden', name: '金毛寻回犬', petType: '狗', photo: IMG.dog, confidence: '98.7',
    desc: '金毛寻回犬是一种友善、聪明、忠诚的犬种，广泛用于家庭陪伴和导盲犬。',
    meta: [
      { icon: 'paw', label: '品种', value: '金毛寻回犬' },
      { icon: 'age', label: '年龄', value: '约2岁' },
      { icon: 'gender', label: '性别', value: '雄性' },
      { icon: 'weight', label: '体重', value: '约32.5kg' },
      { icon: 'ruler', label: '体长', value: '中型犬（体高约55–61cm）' }
    ],
    traits: ['金黄色被毛', '垂耳', '黑色鼻头', '中型犬', '友善的表情']
  },
  gardenCat: {
    key: 'gardenCat', name: '中华田园猫', petType: '猫', photo: IMG.cat, confidence: '96.3',
    desc: '中华田园猫性格活泼独立，体质强健、适应能力强，是十分常见的家养伴侣猫。',
    meta: [
      { icon: 'paw', label: '品种', value: '中华田园猫' },
      { icon: 'age', label: '年龄', value: '约1岁' },
      { icon: 'gender', label: '性别', value: '雌性' },
      { icon: 'weight', label: '体重', value: '约3.8kg' },
      { icon: 'ruler', label: '体长', value: '中小型（体高约25–30cm）' }
    ],
    traits: ['灰白被毛', '绿色眼睛', '三角立耳', '粉鼻头', '机敏灵动']
  },
  husky: {
    key: 'husky', name: '哈士奇', petType: '狗', photo: IMG.husky, confidence: '97.1',
    desc: '西伯利亚哈士奇外形酷似狼，精力充沛、热情友好，以标志性的蓝眼睛闻名。',
    meta: [
      { icon: 'paw', label: '品种', value: '西伯利亚哈士奇' },
      { icon: 'age', label: '年龄', value: '约3岁' },
      { icon: 'gender', label: '性别', value: '雄性' },
      { icon: 'weight', label: '体重', value: '约25kg' },
      { icon: 'ruler', label: '体长', value: '中型犬（体高约51–60cm）' }
    ],
    traits: ['黑白被毛', '蓝色眼睛', '三角形立耳', '双层毛发', '精力充沛']
  },
  corgi: {
    key: 'corgi', name: '柯基犬', petType: '狗', photo: IMG.corgi, confidence: '95.8',
    desc: '威尔士柯基犬身材矮小、腿短身长，性格开朗勇敢，大耳朵和“蜜桃臀”是它的标志。',
    meta: [
      { icon: 'paw', label: '品种', value: '威尔士柯基犬' },
      { icon: 'age', label: '年龄', value: '约2岁' },
      { icon: 'gender', label: '性别', value: '雌性' },
      { icon: 'weight', label: '体重', value: '约11kg' },
      { icon: 'ruler', label: '体长', value: '小型犬（体高约25–30cm）' }
    ],
    traits: ['黄白被毛', '大立耳', '短腿长身', '圆臀', '表情甜美']
  },
  bichon: {
    key: 'bichon', name: '比熊犬', petType: '狗', photo: IMG.bichon, confidence: '96.9',
    desc: '比熊犬有着蓬松的白色卷毛，体型娇小、性情温顺黏人，是优秀的家庭伴侣犬。',
    meta: [
      { icon: 'paw', label: '品种', value: '比熊犬' },
      { icon: 'age', label: '年龄', value: '约1岁半' },
      { icon: 'gender', label: '性别', value: '雄性' },
      { icon: 'weight', label: '体重', value: '约5.5kg' },
      { icon: 'ruler', label: '体长', value: '小型犬（体高约24–29cm）' }
    ],
    traits: ['白色卷毛', '圆脑袋', '黑色眼鼻', '蓬松尾巴', '温顺黏人']
  }
}

const samples = [
  PROFILES.golden,
  PROFILES.gardenCat,
  PROFILES.husky,
  PROFILES.corgi,
  PROFILES.bichon
]

/* ========== 状态 ========== */
const activeTab = ref('upload')
const previewUrl = ref(PROFILES.golden.photo)
const imageUrl = ref('')
const recognizing = ref(false)
const recognizeTime = ref('2025-06-08 14:32')
const result = ref({ ...PROFILES.golden, success: true, demo: true })
const saving = ref(false)
const saved = ref(false)
const records = ref([
  { breed: '金毛寻回犬', time: '2025-06-08 14:32', photo: IMG.dog },
  { breed: '中华田园猫', time: '2025-06-07 16:20', photo: IMG.cat },
  { breed: '哈士奇', time: '2025-06-06 11:17', photo: IMG.husky },
  { breed: '柯基犬', time: '2025-06-05 09:45', photo: IMG.corgi }
])

/* 摄像头 */
const videoRef = ref(null)
const camOn = ref(false)
const camError = ref(false)
let camStream = null

/* 建档 */
const archiveDialogVisible = ref(false)
const archiving = ref(false)
const archived = ref(false)
const archiveFormRef = ref(null)
const archiveForm = reactive({
  petName: '', breed: '', petType: '', gender: '未知', age: '', photoUrl: '', source: 'ai'
})
const archiveRules = {
  petName: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }]
}

/* ========== 工具 ========== */
function fmtConf(v) {
  const n = Number(v)
  return Number.isFinite(n) ? String(Math.round(n * 10) / 10) : v
}
function nowText() {
  const d = new Date()
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}
function resolveImg(url) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
function matchProfile(breed, petType) {
  const text = `${breed || ''}${petType || ''}`
  if (/金毛|寻回/.test(text)) return PROFILES.golden
  if (/田园|狸花|橘猫/.test(text)) return petType === '猫' ? PROFILES.gardenCat : null
  if (/哈士奇/.test(text)) return PROFILES.husky
  if (/柯基/.test(text)) return PROFILES.corgi
  if (/比熊/.test(text)) return PROFILES.bichon
  return null
}
function genericMeta(data) {
  const t = data.petType || '宠物'
  const size = t === '猫'
    ? { icon: 'ruler', label: '体长', value: '中小型宠物体型' }
    : { icon: 'ruler', label: '体长', value: '待补充' }
  return [
    { icon: 'paw', label: '品种', value: data.breed || '未知品种' },
    { icon: 'age', label: '年龄', value: '待补充' },
    { icon: 'gender', label: '性别', value: '待补充' },
    { icon: 'weight', label: '体重', value: '待补充' },
    size
  ]
}
function buildResult(data) {
  const profile = matchProfile(data.breed, data.petType)
  return {
    success: !!data.success,
    message: data.message,
    breed: data.breed || '未知品种',
    petType: data.petType,
    confidence: data.confidence,
    photo: previewUrl.value,
    desc: profile?.desc || data.knowledge?.description ||
      `已识别为${data.petType ? data.petType + '类' : ''}宠物：${data.breed}。更多品种资料持续完善中。`,
    meta: profile ? profile.meta : genericMeta(data),
    traits: profile?.traits || ['品种特征明显', '面部特征清晰', '毛发纹理正常']
  }
}

/* ========== 上传与识别 ========== */
function beforeUpload(file) {
  if (!['image/jpeg', 'image/jpg', 'image/png'].includes(file.type)) {
    ElMessage.error('仅支持 JPG、PNG、JPEG 格式')
    return false
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

async function handleUpload({ file }) {
  previewUrl.value = URL.createObjectURL(file)
  recognizing.value = true
  result.value = null
  recognizeTime.value = ''
  saved.value = false
  archived.value = false
  try {
    const up = await uploadImage(file)
    imageUrl.value = up.data.imageUrl
    const res = await recognize(up.data.imageUrl)
    result.value = buildResult(res.data)
    recognizeTime.value = nowText()
    if (res.data.success) ElMessage.success('识别成功')
    else ElMessage.warning(res.data.message || '识别失败，请换一张清晰照片')
  } catch (e) {
    ElMessage.error('图片上传失败，请重试')
    reset()
  } finally {
    recognizing.value = false
  }
}

/* 示例图片：直接展示对应品种的识别演示结果 */
function useSample(s) {
  activeTab.value = 'upload'
  previewUrl.value = s.photo
  imageUrl.value = s.photo
  recognizing.value = false
  saved.value = false
  archived.value = false
  result.value = { ...s, success: true, demo: true }
  recognizeTime.value = nowText()
}

function reset() {
  activeTab.value = 'upload'
  previewUrl.value = ''
  imageUrl.value = ''
  result.value = null
  recognizeTime.value = ''
  saved.value = false
  archived.value = false
}

/* ========== 摄像头 ========== */
async function startCamera() {
  camError.value = false
  try {
    camStream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' }, audio: false })
    camOn.value = true
    await nextTick()
    if (videoRef.value) videoRef.value.srcObject = camStream
  } catch (e) {
    camOn.value = false
    camError.value = true
  }
}
function stopCamera() {
  if (camStream) {
    camStream.getTracks().forEach(t => t.stop())
    camStream = null
  }
  camOn.value = false
}
function capture() {
  const video = videoRef.value
  if (!video || !video.videoWidth) return
  const canvas = document.createElement('canvas')
  canvas.width = video.videoWidth
  canvas.height = video.videoHeight
  canvas.getContext('2d').drawImage(video, 0, 0)
  canvas.toBlob((blob) => {
    if (!blob) return
    const file = new File([blob], `camera-${Date.now()}.jpg`, { type: 'image/jpeg' })
    stopCamera()
    handleUpload({ file })
  }, 'image/jpeg', 0.92)
}
function switchTab(t) {
  activeTab.value = t
  if (t !== 'camera') stopCamera()
  if (t === 'phone') openPhoneShoot()
  else stopPhonePoll()
}

/* ========== 手机扫码拍摄 ========== */
const qrImg = ref('')
const qrExpired = ref(false)
let phoneTimer = null

async function openPhoneShoot() {
  stopPhonePoll()
  qrExpired.value = false
  qrImg.value = ''
  try {
    const res = await createUploadSession()
    const sid = res.data.sessionId
    const link = `${location.origin}/mobile/capture?s=${sid}`
    qrImg.value = await QRCode.toDataURL(link, { width: 220, margin: 1, color: { dark: '#2c3e50' } })
    phoneTimer = setInterval(() => pollPhone(sid), 2000)
  } catch (e) {
    ElMessage.error('二维码生成失败，请稍后重试')
  }
}

function stopPhonePoll() {
  if (phoneTimer) {
    clearInterval(phoneTimer)
    phoneTimer = null
  }
}

async function pollPhone(sid) {
  try {
    const res = await getUploadSession(sid)
    if (res.data?.status === 'done' && res.data.imageUrl) {
      stopPhonePoll()
      recognizeRemote(res.data.imageUrl)
    }
  } catch (e) {
    // 会话过期/失效：停止轮询并提示刷新
    stopPhonePoll()
    qrExpired.value = true
  }
}

/* 手机照片已传至服务器：直接走识别流程（无需再上传） */
async function recognizeRemote(url) {
  activeTab.value = 'upload'
  previewUrl.value = resolveImg(url)
  imageUrl.value = url
  recognizing.value = true
  result.value = null
  recognizeTime.value = ''
  saved.value = false
  archived.value = false
  try {
    const res = await recognize(url)
    result.value = buildResult(res.data)
    recognizeTime.value = nowText()
    if (res.data.success) ElMessage.success('手机照片识别成功')
    else ElMessage.warning(res.data.message || '识别失败，请换一张清晰照片')
  } catch (e) {
    ElMessage.error('识别失败，请重试')
    reset()
  } finally {
    recognizing.value = false
  }
}

onBeforeUnmount(() => {
  stopCamera()
  stopPhonePoll()
})

/* ========== 保存记录 + 建档 ========== */
async function saveToArchive() {
  if (!result.value || !result.value.success) return
  saving.value = true
  try {
    if (!saved.value) {
      await saveRecord({
        imageUrl: imageUrl.value || result.value.photo,
        petType: result.value.petType,
        breed: result.value.breed,
        confidence: Number(result.value.confidence) || 0,
        success: 1
      })
      saved.value = true
      ElMessage.success('识别记录已保存')
    }
    archiveForm.petName = ''
    archiveForm.breed = result.value.breed || ''
    archiveForm.petType = result.value.petType || ''
    archiveForm.gender = '未知'
    archiveForm.age = ''
    archiveForm.photoUrl = imageUrl.value || result.value.photo || ''
    archiveForm.source = 'ai'
    archiveDialogVisible.value = true
  } catch (e) {
    ElMessage.error('保存失败，请先登录后重试')
  } finally {
    saving.value = false
  }
}

async function submitArchive() {
  if (!archiveFormRef.value) return
  await archiveFormRef.value.validate(async (valid) => {
    if (!valid) return
    archiving.value = true
    try {
      const res = await createArchive({ ...archiveForm })
      const archiveId = res.data
      archived.value = true
      archiveDialogVisible.value = false
      ElMessage.success('建档成功，正在跳转到档案详情…')
      setTimeout(() => router.push(`/archive/${archiveId}`), 800)
    } catch (e) {
      ElMessage.error('建档失败，请先登录后重试')
    } finally {
      archiving.value = false
    }
  })
}

/* ========== 识别记录（真实数据优先，演示兜底） ========== */
onMounted(async () => {
  try {
    const res = await myRecords({ page: 1, size: 4 })
    const page = res.data || {}
    const list = page.list || []
    if (Array.isArray(list) && list.length > 0) {
      records.value = list.map(r => ({
        breed: r.breed || '未知品种',
        time: r.createTime ? String(r.createTime).replace('T', ' ').substring(0, 16) : '',
        photo: resolveImg(r.imageUrl) || (r.petType === '猫' ? IMG.cat : IMG.dog)
      }))
    }
  } catch (e) {
    // 未登录 / 无数据时保留演示记录
  }
})
</script>

<style scoped>
/* ================= 页面骨架 ================= */
.recog-page {
  width: 100%;
  max-width: none;
  margin: 0;
  padding: 18px clamp(16px, 2vw, 32px) 40px;
}
.panel {
  background: #fff;
  border: 1px solid #edf0f8;
  border-radius: 16px;
  box-shadow: 0 10px 28px rgba(94, 110, 190, 0.07);
}

/* ================= Hero ================= */
.hero-card {
  position: relative;
  border-radius: 22px;
  overflow: hidden;
  background: linear-gradient(120deg, #eef3ff 0%, #e6ecff 55%, #dfe8ff 100%);
}
.hero-inner {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: stretch;
  min-height: 300px;
}
.hero-left {
  flex: 1.05;
  padding: 42px 12px 36px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  align-self: flex-start;
  padding: 6px 14px;
  border-radius: 999px;
  background: rgba(109, 92, 224, 0.1);
  color: #5b4bd6;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 16px;
}
.hero-badge .ic {
  width: 15px;
  height: 15px;
  fill: #6d5ce0;
}
.hero-left h1 {
  font-size: clamp(26px, 2.4vw, 36px);
  font-weight: 800;
  color: #1d2a55;
  line-height: 1.25;
  letter-spacing: 1px;
  margin: 0 0 12px;
}
.hero-sub {
  font-size: 14px;
  color: #5b6party;
  color: #5a6a96;
  line-height: 1.7;
  margin: 0 0 26px;
}
.hero-feats {
  display: flex;
  gap: clamp(16px, 2.4vw, 34px);
  flex-wrap: wrap;
}
.feat {
  display: flex;
  align-items: center;
  gap: 10px;
}
.feat-ic {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #e7ecff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  color: #6d5ce0;
}
.feat-ic svg {
  width: 21px;
  height: 21px;
}
.feat-txt {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.feat-txt b {
  font-size: 14px;
  color: #26314f;
  font-weight: 700;
}
.feat-txt small {
  font-size: 11.5px;
  color: #8a96b8;
  white-space: nowrap;
}

.hero-visual {
  position: relative;
  flex: 1;
  min-width: 42%;
  overflow: hidden;
}
.hero-photo {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}
.scan {
  position: absolute;
  border-radius: 8px;
  pointer-events: none;
}
.scan i {
  position: absolute;
  width: 18px;
  height: 18px;
  border: 3px solid rgba(255, 255, 255, 0.95);
  filter: drop-shadow(0 0 4px rgba(120, 150, 255, 0.8));
}
.scan .tl { top: -3px; left: -3px; border-right: none; border-bottom: none; border-radius: 8px 0 0 0; }
.scan .tr { top: -3px; right: -3px; border-left: none; border-bottom: none; border-radius: 0 8px 0 0; }
.scan .bl { bottom: -3px; left: -3px; border-right: none; border-top: none; border-radius: 0 0 0 8px; }
.scan .br { bottom: -3px; right: -3px; border-left: none; border-top: none; border-radius: 0 0 8px 0; }
.scan-dog { left: 7%; top: 7%; width: 47%; height: 76%; }
.scan-cat { left: 55%; top: 21%; width: 37%; height: 64%; }

.float-chip {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 12px;
  padding: 7px 14px 7px 8px;
  box-shadow: 0 8px 22px rgba(80, 100, 190, 0.22);
  white-space: nowrap;
}
.float-chip b {
  font-size: 14px;
  color: #26314f;
  font-weight: 700;
}
.float-chip em {
  font-style: normal;
  font-size: 14px;
  font-weight: 800;
  color: #5b4bd6;
}
.chip-ic {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.chip-paw {
  background: linear-gradient(135deg, #7c8df6, #6d5ce0);
}
.chip-paw svg {
  width: 16px;
  height: 16px;
  fill: #fff;
}
.chip-heart {
  background: linear-gradient(135deg, #36d9a5, #12b886);
}
.chip-heart svg {
  width: 16px;
  height: 16px;
}
.chip-dog { top: 34px; right: 12px; }
.chip-cat { top: 150px; right: 2px; }

.hero-note {
  position: absolute;
  top: 14px;
  right: 200px;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.5;
  color: #4a5fc1;
  transform: rotate(-8deg);
  font-family: 'Comic Sans MS', 'STKaiti', cursive;
  text-shadow: 0 1px 0 rgba(255,255,255,.6);
  pointer-events: none;
}
.deco-paw {
  position: absolute;
  width: 30px;
  height: 30px;
  right: 30px;
  bottom: 46px;
  fill: rgba(90, 120, 220, 0.28);
}
.deco-plus {
  position: absolute;
  color: rgba(110, 140, 235, 0.5);
  font-size: 15px;
  pointer-events: none;
}
.dp1 { right: 150px; top: 22px; }
.dp2 { right: 70px; top: 96px; font-size: 12px; }
.hero-wave {
  position: absolute;
  left: 0;
  right: 0;
  bottom: -2px;
  width: 100%;
  height: 70px;
  pointer-events: none;
}

/* ================= 主区布局 ================= */
.main-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.12fr) minmax(0, 0.88fr);
  gap: 16px;
  margin-top: 16px;
  align-items: start;
}

/* 分段 Tabs */
.seg-tabs {
  display: flex;
  gap: 8px;
  padding: 7px;
  margin: 16px 18px 0;
  background: #f2f4fa;
  border-radius: 999px;
}
.seg {
  flex: 1;
  height: 42px;
  border: none;
  background: transparent;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 600;
  color: #93a0bd;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.seg svg {
  width: 19px;
  height: 19px;
}
.seg.on {
  background: #fff;
  color: #6d5ce0;
  box-shadow: 0 4px 12px rgba(100, 110, 200, 0.16);
}
.tab-body {
  padding: 18px;
}

/* 拖拽上传 */
.drop-upload {
  width: 100%;
}
.drop-upload :deep(.el-upload),
.drop-upload :deep(.el-upload-dragger) {
  width: 100%;
  display: block;
}
.drop-upload :deep(.el-upload-dragger) {
  border: none;
  background: transparent;
  height: auto;
  padding: 0;
  border-radius: 14px;
}
.drop-upload :deep(.el-upload-dragger:hover) {
  border: none;
}
.drop-box {
  border: 1.6px dashed #c2cdf2;
  border-radius: 14px;
  background: linear-gradient(180deg, #fbfcff 0%, #f5f8ff 100%);
  min-height: 286px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 28px 20px;
  transition: border-color 0.2s, background 0.2s;
}
.drop-upload :deep(.el-upload-dragger:hover) .drop-box,
.drop-box:hover {
  border-color: #8ea2f2;
  background: #f3f6ff;
}
.cloud-ic {
  width: 66px;
  height: 66px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cloud-ic svg {
  width: 66px;
  height: 66px;
}
.drop-title {
  font-size: 16px;
  font-weight: 700;
  color: #2b365c;
}
.drop-sub {
  font-size: 12.5px;
  color: #98a3c0;
}
.pick-btn {
  margin-top: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 230px;
  height: 46px;
  padding: 0 28px;
  border: none;
  border-radius: 23px;
  background: linear-gradient(135deg, #6d5ce0, #7b6ef6);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(109, 92, 224, 0.28);
  transition: transform 0.15s, box-shadow 0.15s, opacity 0.15s;
}
.pick-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(109, 92, 224, 0.36);
}
.pick-btn svg {
  width: 18px;
  height: 18px;
}
.ghost-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 130px;
  height: 46px;
  padding: 0 24px;
  border-radius: 23px;
  border: 1.5px solid #d3daf5;
  background: #fff;
  color: #5c6a99;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
}
.ghost-btn:hover {
  border-color: #8ea2f2;
  color: #6d5ce0;
}

/* 预览 */
.preview-box {
  position: relative;
  width: 100%;
  height: 286px;
  border-radius: 14px;
  overflow: hidden;
  background: #eef1f8;
}
.preview-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.reselect {
  position: absolute;
  right: 12px;
  top: 12px;
  background: rgba(38, 48, 90, 0.62);
  color: #fff;
  font-size: 12.5px;
  padding: 6px 14px;
  border-radius: 999px;
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
}
.preview-box:hover .reselect {
  opacity: 1;
}
.scan-mask {
  position: absolute;
  inset: 0;
  background: rgba(30, 40, 90, 0.32);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.scan-line {
  position: absolute;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent, #5de1ff, transparent);
  box-shadow: 0 0 14px #5de1ff;
  animation: scanMove 1.8s ease-in-out infinite;
}
@keyframes scanMove {
  0% { top: 6%; }
  50% { top: 92%; }
  100% { top: 6%; }
}
.scan-tip {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.95);
  color: #4b55a8;
  font-size: 14px;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.scan-spin {
  width: 16px;
  height: 16px;
  border: 2.5px solid #cdd6ff;
  border-top-color: #6d5ce0;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 示例图片 */
.samples {
  margin-top: 18px;
}
.block-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14.5px;
  font-weight: 700;
  color: #2b365c;
  margin-bottom: 12px;
}
.bt-ic {
  width: 26px;
  height: 26px;
  border-radius: 8px;
  background: #eef1ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6d7bf0;
}
.bt-ic svg {
  width: 16px;
  height: 16px;
}
.sample-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}
.sample {
  text-align: center;
  cursor: pointer;
}
.sample-img {
  border-radius: 10px;
  overflow: hidden;
  aspect-ratio: 1;
  border: 1.5px solid #eef0f8;
  transition: transform 0.18s, box-shadow 0.18s, border-color 0.18s;
}
.sample img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.25s;
}
.sample:hover .sample-img {
  transform: translateY(-3px);
  border-color: #aebaf2;
  box-shadow: 0 8px 18px rgba(100, 115, 200, 0.2);
}
.sample:hover img {
  transform: scale(1.06);
}
.sample > span {
  display: block;
  margin-top: 7px;
  font-size: 12px;
  color: #5c6a8c;
}

/* 摄像头 */
.camera-body {
  display: flex;
  flex-direction: column;
}
/* 手机扫码拍摄 */
.phone-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 0 10px;
  text-align: center;
}
.qr-wrap {
  position: relative;
  width: 220px;
  height: 220px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #e6ebf5;
  box-shadow: 0 8px 20px rgba(90, 100, 180, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.qr-img {
  width: 200px;
  height: 200px;
  display: block;
}
.qr-loading {
  font-size: 13px;
  color: #8a93a6;
}
.qr-mask {
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
}
.qr-mask b {
  font-size: 14px;
  color: #f56c6c;
}
.phone-tip {
  font-size: 14px;
  color: #2c3e50;
}
.phone-box small {
  font-size: 12px;
  color: #8a93a6;
  line-height: 1.8;
}
.camera-box {
  position: relative;
  width: 100%;
  border-radius: 14px;
  overflow: hidden;
  background: #0f1730;
  min-height: 286px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.camera-box video {
  width: 100%;
  height: 286px;
  object-fit: cover;
  display: block;
}
.cam-off {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #aebadf;
  cursor: pointer;
  padding: 30px;
}
.cam-ic {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6px;
}
.cam-ic svg {
  width: 30px;
  height: 30px;
}
.cam-off b {
  font-size: 15px;
  color: #d6def8;
}
.cam-off small {
  font-size: 12.5px;
  color: #7d8ab4;
}
.cam-error {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #ffb4b4;
  font-size: 13.5px;
  padding: 24px;
  background: rgba(15, 23, 48, 0.9);
}
.cam-actions {
  display: flex;
  justify-content: center;
  gap: 14px;
  margin-top: 16px;
}

/* ================= 结果面板 ================= */
.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px 14px;
}
.ph-title {
  display: flex;
  align-items: center;
  gap: 9px;
}
.ph-title b {
  font-size: 16px;
  color: #26314f;
  font-weight: 700;
}
.head-ic {
  width: 30px;
  height: 30px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.head-ic svg {
  width: 20px;
  height: 20px;
}
.head-ic.green { background: linear-gradient(135deg, #36d9a5, #12b886); }
.head-ic.purple { background: linear-gradient(135deg, #8b7cf6, #6d5ce0); }
.head-ic.blue { background: linear-gradient(135deg, #6ea4ff, #4f7cf0); }
.rec-time {
  font-size: 12.5px;
  color: #9aa5c2;
}
.more-link {
  font-size: 12.5px;
  color: #7b8df0;
  cursor: pointer;
  font-weight: 500;
}
.more-link:hover {
  color: #5b4bd6;
}

/* 空 / 加载 / 失败 */
.r-empty,
.r-loading,
.r-fail {
  min-height: 420px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 30px;
  text-align: center;
}
.empty-ic {
  width: 84px;
  height: 84px;
  border-radius: 50%;
  background: #f2f5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a9b6e8;
  margin-bottom: 6px;
}
.empty-ic svg {
  width: 40px;
  height: 40px;
}
.r-empty b,
.r-fail b {
  font-size: 16px;
  color: #3a4670;
}
.r-empty small {
  font-size: 13px;
  color: #98a3c0;
  line-height: 1.7;
}
.loading-paw {
  width: 74px;
  height: 74px;
  border-radius: 50%;
  background: linear-gradient(135deg, #eeecff, #e3edff);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: breathe 1.4s ease-in-out infinite;
}
.loading-paw svg {
  width: 38px;
  height: 38px;
  fill: #6d5ce0;
}
@keyframes breathe {
  0%, 100% { transform: scale(1); box-shadow: 0 0 0 rgba(109, 92, 224, 0.2); }
  50% { transform: scale(1.08); box-shadow: 0 0 0 14px rgba(109, 92, 224, 0); }
}
.r-loading p {
  color: #5b6aa0;
  font-size: 14px;
  margin: 6px 0 0;
}
.loading-bar {
  width: 200px;
  height: 4px;
  border-radius: 2px;
  background: #e9edfb;
  overflow: hidden;
  margin-top: 6px;
}
.loading-bar i {
  display: block;
  width: 40%;
  height: 100%;
  border-radius: 2px;
  background: linear-gradient(90deg, #6d5ce0, #8b7cf6);
  animation: barMove 1.2s ease-in-out infinite;
}
@keyframes barMove {
  0% { margin-left: -40%; }
  100% { margin-left: 100%; }
}
.fail-ic {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  background: #fdeeee;
  color: #f56c6c;
  font-size: 38px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
}
.r-fail p {
  font-size: 13px;
  color: #98a3c0;
  margin: 0 0 8px;
}

/* 成功结果 */
.r-body {
  padding: 4px 20px 20px;
}
.r-main {
  display: flex;
  gap: 18px;
}
.r-photo {
  position: relative;
  width: 212px;
  height: 212px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: #eef1f8;
}
.r-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.ok-badge {
  position: absolute;
  left: 8px;
  bottom: 8px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: rgba(18, 184, 134, 0.92);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 8px;
}
.ok-badge svg {
  width: 14px;
  height: 14px;
}
.r-info {
  flex: 1;
  min-width: 0;
}
.r-name-line {
  display: flex;
  align-items: center;
  gap: 10px;
}
.r-name-line h3 {
  font-size: 22px;
  font-weight: 800;
  color: #1f2742;
  margin: 0;
}
.conf-tag {
  background: #eceeff;
  color: #6356e0;
  font-size: 13px;
  font-weight: 700;
  padding: 3px 9px;
  border-radius: 7px;
}
.r-desc {
  font-size: 12.5px;
  color: #8893b2;
  line-height: 1.7;
  margin: 8px 0 12px;
}
.r-meta {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  grid-template-columns: 1fr;
  gap: 2px;
}
.r-meta li {
  display: flex;
  align-items: center;
  gap: 9px;
  height: 30px;
}
.meta-ic {
  width: 26px;
  height: 26px;
  border-radius: 8px;
  background: #f0f4ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.meta-ic svg {
  width: 16px;
  height: 16px;
}
.meta-label {
  font-size: 13px;
  color: #93a0bd;
  width: 38px;
}
.r-meta b {
  font-size: 13px;
  color: #3c486b;
  font-weight: 600;
}

/* 特征分析 */
.r-traits {
  margin-top: 16px;
  background: linear-gradient(135deg, #f6f8ff 0%, #f3f7ff 100%);
  border: 1px solid #edf0fd;
  border-radius: 12px;
  padding: 13px 16px;
}
.r-traits h4 {
  display: flex;
  align-items: center;
  gap: 7px;
  margin: 0 0 10px;
  font-size: 14px;
  font-weight: 700;
  color: #2b365c;
}
.spark-ic {
  width: 22px;
  height: 22px;
  color: #6d5ce0;
  display: inline-flex;
}
.spark-ic svg {
  width: 22px;
  height: 22px;
}
.trait-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.trait {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  background: #fff;
  border: 1px solid #e6ebfb;
  border-radius: 7px;
  padding: 4px 10px;
  font-size: 12.5px;
  color: #3c486b;
}
.trait svg {
  width: 14px;
  height: 14px;
  flex-shrink: 0;
}
.trait-more {
  color: #b3bfe0;
  font-size: 16px;
  font-weight: 700;
  margin-left: 2px;
}

/* 操作按钮 */
.r-actions {
  display: flex;
  gap: 14px;
  margin-top: 18px;
}
.primary-btn,
.outline-btn {
  height: 48px;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
}
.primary-btn {
  flex: 1.5;
  border: none;
  background: linear-gradient(135deg, #6d5ce0, #7b6ef6);
  color: #fff;
  box-shadow: 0 10px 22px rgba(109, 92, 224, 0.3);
}
.primary-btn svg {
  width: 19px;
  height: 19px;
}
.primary-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 14px 26px rgba(109, 92, 224, 0.38);
}
.primary-btn:disabled {
  opacity: 0.75;
  cursor: default;
}
.outline-btn {
  flex: 1;
  background: #fff;
  border: 1.6px solid #dcdffa;
  color: #6d5ce0;
}
.outline-btn svg {
  width: 18px;
  height: 18px;
}
.outline-btn:hover {
  border-color: #a9b4f6;
  background: #f8f9ff;
}

/* ================= 底部三栏 ================= */
.bottom-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1.5fr) minmax(0, 1fr);
  gap: 16px;
  margin-top: 16px;
}
/* AI 助手 */
.assist-card {
  border-radius: 16px;
  background: linear-gradient(125deg, #eef1ff 0%, #f2ecff 60%, #e9f0ff 100%);
  border: 1px solid #e6e2fb;
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.18s, box-shadow 0.18s;
}
.assist-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 26px rgba(120, 110, 220, 0.18);
}
.robot-img {
  width: 78px;
  height: 78px;
  border-radius: 16px;
  object-fit: cover;
  flex-shrink: 0;
}
.assist-txt {
  flex: 1;
  min-width: 0;
}
.assist-txt b {
  font-size: 13.5px;
  line-height: 1.55;
  color: #333e8c;
  font-weight: 700;
}
.assist-txt p {
  margin: 6px 0 0;
  font-size: 11.5px;
  color: #8b93c4;
}
.consult-btn {
  align-self: center;
  flex-shrink: 0;
  background: linear-gradient(135deg, #6d5ce0, #7b6ef6);
  color: #fff;
  font-size: 12.5px;
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 999px;
  box-shadow: 0 6px 14px rgba(109, 92, 224, 0.28);
}
/* 记录 / 贴士 卡片 */
.sub-card {
  padding-bottom: 16px;
}
.rec-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  padding: 4px 16px 0;
}
.rec-item {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 8px;
  border-radius: 10px;
  cursor: pointer;
  min-width: 0;
  transition: background 0.15s;
}
.rec-item:hover {
  background: #f5f7ff;
}
.rec-item img {
  width: 44px;
  height: 44px;
  border-radius: 9px;
  object-fit: cover;
  flex-shrink: 0;
}
.rec-meta {
  min-width: 0;
}
.rec-meta b {
  display: block;
  font-size: 12.5px;
  color: #3c486b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.rec-meta small {
  display: block;
  margin-top: 3px;
  font-size: 11px;
  color: #a2acc8;
  white-space: nowrap;
}
.tips {
  list-style: none;
  margin: 0;
  padding: 4px 20px 0;
}
.tips li {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #5c6a8c;
  line-height: 2.1;
}
.tip-check {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #e8f1ff;
  color: #4f7cf0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.tip-check svg {
  width: 12px;
  height: 12px;
}

/* ================= 响应式 ================= */
@media (max-width: 1280px) {
  .bottom-grid {
    grid-template-columns: 1fr 1fr;
  }
  .assist-card {
    grid-column: 1 / -1;
  }
}
@media (max-width: 1020px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
  .hero-note {
    display: none;
  }
  .bottom-grid {
    grid-template-columns: 1fr;
  }
  .assist-card {
    grid-column: auto;
  }
}
@media (max-width: 760px) {
  .hero-inner {
    flex-direction: column;
  }
  .hero-left {
    padding: 28px 22px 8px;
  }
  .hero-visual {
    min-height: 240px;
  }
  .r-main {
    flex-direction: column;
    align-items: center;
    text-align: left;
  }
  .r-actions {
    flex-direction: column;
  }
  .sample-list {
    grid-template-columns: repeat(3, 1fr);
  }
  .rec-list {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
