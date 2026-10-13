# 宠物平台（宠知AI）接口测试文档

> 适用于 Apifox / Postman / Swagger 导入前的手工录入。共 **27 个 Controller、约 130 个接口**。

---

## 一、基础信息

| 项目 | 值 |
|---|---|
| BaseURL | `http://localhost:8080/api` |
| 协议 | HTTP / JSON（`Content-Type: application/json`） |
| 文件上传 | `multipart/form-data` |
| 认证方式 | JWT，请求头 `Authorization: Bearer {token}` |

### 1.1 统一响应结构 `Result<T>`

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- `code=200` 成功；`401` 未登录/Token 失效；`403` 无权限（角色不匹配）；`500` 业务/系统异常
- 分页接口 `data` 为 `PageResult<T>`：

```json
{
  "code": 200,
  "message": "success",
  "data": { "total": 100, "list": [] }
}
```

### 1.2 三种角色与登录入口（Token 相互隔离，跨角色访问返回 403）

| 角色 | 登录接口 | 测试账号 | 可访问路径 |
|---|---|---|---|
| 普通用户 | `POST /auth/login` | 自行注册 | `/auth/**` `/order/**` `/cart/**` `/post/**` `/adoption/**` `/archive/**` `/chat/**` `/recognition/**` |
| 管理员 | `POST /admin/login` | `admin / 123456` | `/admin/**` |
| 商家 | `POST /merchant/login` | `merchant / 123456` | `/merchant/**` |

登录成功响应 `data`（`LoginVO`）：

```json
{ "token": "eyJhbGciOi...", "id": 1, "account": "admin", "nickname": "管理员", "avatar": "", "role": "admin" }
```

### 1.3 免登录白名单（无需 Token 即可访问）

- `POST /admin/login`、`POST /merchant/login`、`POST /merchant/register`、`POST /auth/login`、`POST /auth/register`
- `GET /product/**`（商品列表/详情）、`GET /info/**`（资讯）、`GET /breed/**`（品种百科）
- `GET /post/list`、`GET /post/{id}`、`GET /post/topics`、`GET /post/active-users`、`GET /post/{id}/comments`
- `GET /adoption/list`、`GET /adoption/{id}`
- `POST /upload-session`、`GET /upload-session/{id}`、`POST /upload-session/{id}/photo`（手机扫码拍照）
- 静态资源：`/upload/**`、`/images/**`

> 说明：写操作（发帖、下单、申请、识别保存等）均需用户 Token。

---

## 二、用户端接口

### 2.1 认证模块 `/auth`

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| POST | `/auth/register` | 免 | 用户注册 |
| POST | `/auth/login` | 免 | 用户登录，返回 token |
| POST | `/auth/logout` | 用户 | 退出登录 |
| GET | `/auth/current` | 用户 | 获取当前登录用户信息 |
| PUT | `/auth/password` | 用户 | 修改密码 |
| PUT | `/auth/profile` | 用户 | 修改资料（昵称/头像） |

**请求体示例**

```json
// POST /auth/register
{ "account": "test01", "password": "123456", "nickname": "测试用户" }

// POST /auth/login
{ "account": "test01", "password": "123456" }

// PUT /auth/password
{ "oldPassword": "123456", "newPassword": "abc12345" }

// PUT /auth/profile
{ "nickname": "新昵称", "avatar": "/upload/xxx.png" }
```

---

### 2.2 商品模块 `/product`

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| GET | `/product/list` | 免 | 商品分页列表 |
| GET | `/product/{id}` | 免 | 商品详情 |

**查询参数（/product/list）**：`page=1`、`size=10`、`category`（可选，如 主粮/零食/玩具/用品）

---

### 2.3 购物车模块 `/cart`（均需用户 Token）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/cart` | 我的购物车列表 |
| POST | `/cart` | 加入购物车 |
| PUT | `/cart/{cartId}?quantity=2` | 修改数量（query 传 quantity，不能超过库存） |
| DELETE | `/cart/{cartId}` | 删除单条 |
| DELETE | `/cart` | 清空购物车 |

```json
// POST /cart
{ "productId": 1, "quantity": 2 }
```

---

### 2.4 订单模块 `/order`（均需用户 Token）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/order` | 创建订单（防超卖：库存条件扣减） |
| GET | `/order/my?page=1&size=10&status=paid` | 我的订单分页（status 可选） |
| GET | `/order/{id}` | 订单详情（仅本人） |
| GET | `/order/{id}/items` | 订单明细（仅本人，管理员可看全部） |
| PUT | `/order/{id}/pay` | 模拟支付 |
| PUT | `/order/{id}/cancel` | 取消订单（回补库存） |
| PUT | `/order/{id}/confirm` | 确认收货 |

```json
// POST /order
{ "productId": 1, "quantity": 1, "receiverName": "张三", "receiverPhone": "13800000000", "receiverAddress": "北京市朝阳区xx路1号" }
```

订单状态枚举：`pending` 待付款 / `paid` 已付款 / `shipped` 已发货 / `completed` 已完成 / `cancelled` 已取消

---

### 2.5 社区模块 `/post`

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| POST | `/post` | 用户 | 发布帖子 |
| GET | `/post/list?page=1&size=10&category=养宠交流&sort=latest&feed=all` | 免 | 帖子分页（sort: latest/hot；feed: all/follow，follow 需登录） |
| GET | `/post/{id}` | 免 | 帖子详情（自增浏览量，带作者与点赞状态） |
| PUT | `/post/{id}` | 用户 | 修改自己的帖子 |
| DELETE | `/post/{id}` | 用户 | 删除自己的帖子 |
| POST | `/post/{id}/like` | 用户 | 点赞/取消点赞（幂等切换），返回最新点赞数 |
| POST | `/post/follow/{userId}` | 用户 | 关注/取消关注用户，返回最新关注状态 |
| GET | `/post/topics` | 免 | 热门话题 Top |
| GET | `/post/active-users` | 免 | 活跃用户 Top |
| POST | `/post/{id}/comment` | 用户 | 发表评论 |
| GET | `/post/{id}/comments?page=1&size=10` | 免 | 评论分页 |
| DELETE | `/post/comment/{commentId}` | 用户 | 删除自己的评论 |
| GET | `/post/mine?page=1&size=10` | 用户 | 我发布的帖子 |

```json
// POST /post （分类限：养宠交流/领养求助/宠物健康/萌宠晒图/宠物问答）
{ "title": "金毛日常", "content": "今天带狗狗去公园...", "coverImage": "/upload/a.png", "category": "萌宠晒图" }

// POST /post/{id}/comment
{ "content": "好可爱！", "parentId": null }
```

---

### 2.6 领养寄养模块 `/adoption`

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| POST | `/adoption` | 用户 | 发布领养/寄养信息 |
| GET | `/adoption/list?page=1&size=10` | 免 | 分页列表 |
| GET | `/adoption/{id}` | 免 | 详情 |
| PUT | `/adoption/{id}` | 用户 | 修改自己发布的信息 |
| DELETE | `/adoption/{id}` | 用户 | 删除自己发布的信息 |
| POST | `/adoption/{id}/favorite` | 用户 | 收藏/取消收藏 |
| GET | `/adoption/favorites/ids` | 用户 | 我收藏的信息 ID 列表 |
| POST | `/adoption/{id}/apply` | 用户 | 提交领养/寄养申请（防重复申请） |
| GET | `/adoption/mine` | 用户 | 我发布的信息 |
| GET | `/adoption/applications/mine` | 用户 | 我提交的申请 |
| GET | `/adoption/{id}/applications` | 用户 | 我发布的信息收到的申请列表 |
| PUT | `/adoption/applications/{applicationId}/status?status=approved` | 用户 | 处理申请（发布者操作） |

**/adoption/list 查询参数**：`type`（adoption 领养 / foster 寄养）、`petType`（猫/狗）、`age`、`gender`（公/母/未知）、`area`、`sizeType`、`keyword`

```json
// POST /adoption
{ "type": "adoption", "petName": "豆豆", "breed": "金毛", "petType": "狗", "age": "2岁", "gender": "公",
  "photoUrl": "/upload/dog.png", "description": "性格温顺，已疫苗", "contact": "13800000000",
  "area": "北京", "size": "大型", "steril": "已绝育" }

// POST /adoption/{id}/apply
{ "message": "我有养宠经验，居住环境稳定", "contact": "13900000000" }
```

申请状态：`pending` 待处理 / `approved` 已通过 / `rejected` 已拒绝

---

### 2.7 宠物档案模块 `/archive`（均需用户 Token）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/archive` | 创建宠物档案 |
| GET | `/archive/list` | 我的档案列表（不分页） |
| GET | `/archive/{id}` | 档案详情（仅本人） |
| PUT | `/archive/{id}` | 修改档案 |
| DELETE | `/archive/{id}` | 删除档案 |

```json
// POST /archive
{ "petName": "咪咪", "breed": "英短", "petType": "猫", "age": "1岁", "gender": "母",
  "photoUrl": "/upload/cat.png", "source": "识别建档" }
```

---

### 2.8 AI 问答模块 `/chat`（均需用户 Token）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/chat/send` | 发送消息，返回 AI 回复（BaiduAiProvider / MockAiProvider 可配置切换） |
| GET | `/chat/history` | 我的对话记录 |
| DELETE | `/chat/history` | 清空对话记录 |

```json
// POST /chat/send
{ "content": "猫咪不爱喝水怎么办？" }
```

---

### 2.9 品种百科模块 `/breed`

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| GET | `/breed/list?page=1&size=12&petType=狗&keyword=金毛` | 免 | 品种分页 |
| GET | `/breed/{id}` | 免 | 品种详情 |
| GET | `/breed/by-name?name=金毛` | 免 | 按名称精确查询 |

---

### 2.10 资讯模块 `/info`

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| GET | `/info/list?page=1&size=10&category=健康` | 免 | 资讯分页（category 固定四类） |
| GET | `/info/{id}` | 免 | 资讯详情（自增浏览量） |

---

### 2.11 宠物识别模块 `/recognition`（均需用户 Token）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/recognition/upload` | 上传图片（multipart，字段名 `file`），返回图片访问 URL |
| POST | `/recognition/recognize` | 对图片 URL 执行 AI 识别 |
| POST | `/recognition/save` | 保存识别记录 |
| GET | `/recognition/my/page?page=1&size=10&success=1` | 我的识别记录分页（success 可选：1 成功 / 0 失败） |
| DELETE | `/recognition/my/batch` | 批量删除（body 传 ID 数组） |

```json
// POST /recognition/recognize
{ "imageUrl": "/upload/xxx.jpg" }

// POST /recognition/save
{ "imageUrl": "/upload/xxx.jpg", "petType": "狗", "breed": "金毛寻回犬", "confidence": 96.5, "success": 1 }

// DELETE /recognition/my/batch
[1, 2, 3]
```

---

### 2.12 手机扫码拍照模块 `/upload-session`（免登录，10 分钟过期）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/upload-session` | 创建拍照会话（电脑端调用），返回会话 ID 与手机访问路径 |
| GET | `/upload-session/{id}` | 轮询会话状态与已拍照片 URL（电脑端 2 秒轮询） |
| POST | `/upload-session/{id}/photo` | 手机端上传照片（multipart，字段名 `file`） |

**测试流程**：电脑端 `POST /upload-session` → 用手机浏览器打开 `{电脑局域网IP}/mobile/capture?id={会话ID}` → 拍照上传 → 电脑端轮询 `GET /upload-session/{id}` 取到 `photoUrl`。

---

## 三、商家端接口 `/merchant/**`（均需商家 Token）

### 3.1 商家认证 `/merchant`

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| POST | `/merchant/login` | 免 | 商家登录 |
| POST | `/merchant/register` | 免 | 商家注册 |
| GET | `/merchant/current` | 商家 | 当前商家信息 |

```json
// POST /merchant/login
{ "account": "merchant", "password": "123456" }

// POST /merchant/register
{ "account": "shop01", "password": "123456", "name": "爱宠用品店", "phone": "13800000000" }
```

### 3.2 商家看板 `/merchant/dashboard`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/merchant/dashboard` | 商家数据看板（仅统计本店商品/订单） |

### 3.3 商家商品 `/merchant/product`（数据按 merchant_id 隔离）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/merchant/product/page?page=1&size=10&category=&keyword=` | 本店商品分页 |
| GET | `/merchant/product/{id}` | 商品详情（仅本店） |
| POST | `/merchant/product` | 新增商品 |
| PUT | `/merchant/product/{id}` | 修改商品（仅本店） |
| DELETE | `/merchant/product/{id}` | 删除商品（仅本店） |
| PUT | `/merchant/product/{id}/status?status=1` | 上下架（1 上架 / 0 下架） |

```json
// POST /merchant/product
{ "name": "猫粮试吃装", "description": "营养均衡", "price": 29.90, "stock": 100,
  "coverImage": "/upload/p.png", "images": ["/upload/p1.png"], "category": "主粮", "status": 1 }
```

### 3.4 商家订单 `/merchant/order`（仅含本店商品的订单）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/merchant/order/page?page=1&size=10&status=paid` | 本店订单分页 |
| GET | `/merchant/order/{id}` | 订单详情（仅本店，越权返回 403） |
| GET | `/merchant/order/{id}/items` | 订单明细 |
| PUT | `/merchant/order/{id}/ship` | 发货（paid → shipped） |

---

## 四、管理端接口 `/admin/**`（均需管理员 Token）

### 4.1 管理员认证 `/admin`

| 方法 | 路径 | 鉴权 | 说明 |
|---|---|---|---|
| POST | `/admin/login` | 免 | 管理员登录 |

```json
{ "account": "admin", "password": "123456" }
```

### 4.2 数据看板 `/admin/dashboard`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/dashboard?days=7` | 看板聚合数据（统计卡 + 近 N 天用户注册趋势 + 最新用户等） |

### 4.3 用户管理 `/admin/user`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/user/stats` | 用户统计卡数据 |
| GET | `/admin/user/page?page=1&size=10&keyword=&role=&status=&date=` | 用户分页（role: user/merchant/admin；status: 1 正常 / 0 禁用；date: yyyy-MM-dd） |
| GET | `/admin/user/{id}` | 用户详情 |
| PUT | `/admin/user/{id}` | 编辑用户 |
| PUT | `/admin/user/{id}/status?status=0` | 启用/禁用 |
| DELETE | `/admin/user/{id}` | 删除用户 |

### 4.4 宠物管理 `/admin/pet`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/pet/stats` | 宠物统计（总数/猫/狗/环比） |
| GET | `/admin/pet/page?page=1&size=10&keyword=&petType=&healthStatus=&breed=` | 宠物分页（含所属用户信息） |
| GET | `/admin/pet/{id}` | 宠物详情 |
| POST | `/admin/pet` | 新增宠物（需指定绑定用户 userId） |
| PUT | `/admin/pet/{id}` | 编辑宠物 |
| DELETE | `/admin/pet/{id}` | 删除宠物 |
| POST | `/admin/pet/batch` | 批量新增（body 为数组） |
| GET | `/admin/pet/{id}/health` | 健康记录列表 |
| POST | `/admin/pet/{id}/health` | 新增健康记录 |
| DELETE | `/admin/pet/health/{recordId}` | 删除健康记录 |
| GET | `/admin/pet/{id}/activities` | 宠物动态聚合（识别+咨询+订单时间线） |

```json
// POST /admin/pet
{ "userId": 2, "petName": "球球", "breed": "柯基", "petType": "狗", "age": "3岁", "gender": "公",
  "photoUrl": "/upload/d.png", "healthStatus": "健康" }

// POST /admin/pet/{id}/health
{ "recordType": "疫苗", "title": "狂犬疫苗", "recordDate": "2026-10-01", "description": "第三针", "nextDate": "2027-10-01" }
```

### 4.5 资讯管理 `/admin/info`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/info/stats` | 资讯统计 |
| GET | `/admin/info/hot` | 热门资讯 Top5（按浏览量） |
| GET | `/admin/info/feed` | 最新动态 |
| GET | `/admin/info/page?page=1&size=10&keyword=&category=&status=` | 资讯分页 |
| GET | `/admin/info/{id}` | 资讯详情 |
| POST | `/admin/info` | 新增资讯（富文本，wangEditor） |
| PUT | `/admin/info/{id}` | 编辑资讯 |
| DELETE | `/admin/info/{id}` | 删除资讯 |
| PUT | `/admin/info/{id}/status?status=1` | 发布/下架 |

```json
// POST /admin/info
{ "title": "秋季养宠指南", "content": "<p>正文...</p>", "coverImage": "/upload/i.png", "category": "健康", "status": 1 }
```

### 4.6 知识记录管理 `/admin/knowledge`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/knowledge/stats` | 知识统计（总数/今日/分类计数） |
| GET | `/admin/knowledge/hot-tags` | 热门标签云 |
| GET | `/admin/knowledge/recommend` | 推荐知识 |
| GET | `/admin/knowledge/page?page=1&size=10&keyword=&category=&petType=&sort=` | 知识分页 |
| GET | `/admin/knowledge/{id}` | 详情 |
| POST | `/admin/knowledge` | 新增 |
| PUT | `/admin/knowledge/{id}` | 编辑 |
| DELETE | `/admin/knowledge/{id}` | 删除 |

```json
// POST /admin/knowledge
{ "title": "猫咪化毛指南", "category": "日常护理", "petType": "猫", "summary": "...", "content": "...",
  "tags": ["化毛", "肠胃"], "coverImage": "/upload/k.png" }
```

### 4.7 商城管理 `/admin/product`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/product/stats` | 商品统计（含订单聚合、销售额） |
| GET | `/admin/product/hot` | 热销商品 Top5 |
| GET | `/admin/product/page?page=1&size=10&category=&keyword=&status=` | 商品分页（status: 1 / 0） |
| GET | `/admin/product/{id}` | 详情 |
| POST | `/admin/product` | 新增商品（body 同商家端 ProductSaveDTO） |
| PUT | `/admin/product/{id}` | 编辑商品 |
| DELETE | `/admin/product/{id}` | 删除商品 |
| PUT | `/admin/product/{id}/status?status=1` | 上下架 |

### 4.8 订单管理 `/admin/order`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/order/page?page=1&size=10&status=` | 全平台订单分页 |
| GET | `/admin/order/{id}` | 订单详情 |
| GET | `/admin/order/{id}/items` | 订单明细 |
| PUT | `/admin/order/{id}/ship` | 发货 |
| PUT | `/admin/order/{id}/status?status=completed` | 修改状态（白名单：pending/paid/shipped/completed/cancelled） |

### 4.9 领养寄养管理 `/admin/adoption`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/adoption/stats` | 统计（总数/领养/寄养/申请数/环比） |
| GET | `/admin/adoption/hot-pets` | 热门宠物 Top5 |
| GET | `/admin/adoption/page?page=1&size=10&type=&status=&keyword=&petType=` | 分页（含发布者信息，申请人手机脱敏） |
| PUT | `/admin/adoption/{id}/status?status=approved` | 审核状态 |
| DELETE | `/admin/adoption/{id}` | 删除 |

### 4.10 社交管理 `/admin/post`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/post/stats` | 统计（总帖/今日帖/评论数/活跃用户/分类计数/环比） |
| GET | `/admin/post/hot-topics` | 热门话题 Top6 |
| GET | `/admin/post/recommend-users` | 推荐用户 Top4（按发帖数） |
| GET | `/admin/post/page?page=1&size=10&category=&keyword=` | 帖子分页（含作者昵称/头像） |
| DELETE | `/admin/post/{id}` | 删除帖子 |
| GET | `/admin/post/comment/page?page=1&size=10` | 评论分页（含评论者昵称与帖子标题） |
| DELETE | `/admin/post/comment/{commentId}` | 删除评论 |

### 4.11 识别记录管理 `/admin/recognition`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/recognition/page?page=1&size=10&userId=&breed=&success=` | 全平台识别记录分页 |

---

## 五、快速测试脚本（cURL）

```bash
# 1. 管理员登录拿 token
TOKEN=$(curl -s -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"account":"admin","password":"123456"}' | jq -r .data.token)

# 2. 携带 token 访问管理端
curl -s "http://localhost:8080/api/admin/dashboard?days=7" \
  -H "Authorization: Bearer $TOKEN"

# 3. 用户登录
UTOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"account":"test01","password":"123456"}' | jq -r .data.token)

# 4. 用户发帖
curl -s -X POST http://localhost:8080/api/post \
  -H "Authorization: Bearer $UTOKEN" -H "Content-Type: application/json" \
  -d '{"title":"测试帖","content":"内容","category":"养宠交流"}'

# 5. 验证角色隔离（用户 token 访问管理端应返回 403）
curl -s "http://localhost:8080/api/admin/user/page" -H "Authorization: Bearer $UTOKEN"
```

---

## 六、常见错误码排查

| code | 含义 | 排查 |
|---|---|---|
| 401 | 未登录 / Token 过期或缺失 | 检查 `Authorization: Bearer ` 前缀是否完整 |
| 403 | 角色不匹配 / 越权访问他人资源 | 确认 token 角色（admin/merchant/user）与路径前缀匹配；商家只能操作本店数据 |
| 500 | 业务异常 | 查看 `message`（如"库存不足""重复申请""订单不存在"） |
