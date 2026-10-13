# 宠知AI 平台功能扩展 - 实施任务

## 任务总览

按模块垂直切片实施，每个模块包含：数据库 → 后端 Entity/Mapper → Service → Controller → 前端 API → 前端页面 → 路由导航。

依赖关系：无模块间强依赖，可按 A→B→C 顺序串行实施。每个模块完成后可独立验证。

---

## Task 1: 数据库表结构与初始数据

**优先级**: high
**状态**: completed
**对应 AC**: AC-1, AC-2

### 工作内容
1. 在 init.sql 中新增 9 张表：
   - `adoption`（领养寄养信息）
   - `adoption_application`（领养寄养申请）
   - `post`（社区帖子）
   - `comment`（评论）
   - `post_like`（点赞）
   - `product`（商品）
   - `cart`（购物车）
   - `order`（订单，注意 MySQL 保留字需反引号）
   - `order_item`（订单明细）
2. 每张表包含合理字段、主键、索引、注释
3. 插入演示数据：商品 4-6 条、领养寄养 2-3 条、社区帖子 2-3 条

### 测试要求
- **rule**: `init.sql` 可在 MySQL 8 中执行无报错
- **rule**: 9 张表全部创建成功，字段与 spec 功能需求对应

### 完成证据
- init.sql 追加 9 张表 DDL（表 8-16），含主键/索引/注释
- 演示数据：6 个商品、3 条领养寄养信息（approved）、3 个社区帖子

---

## Task 2: 后端 Entity 与 Mapper（全部 9 张表）

**优先级**: high
**状态**: completed
**对应 AC**: AC-3~AC-8

### 工作内容
1. 为 9 张表各创建 Entity 类（`@TableName` + `@TableId` + Lombok `@Data`）
2. 为 9 张表各创建 Mapper 接口（继承 BaseMapper<T>）
3. 注意 `order` 表的 `@TableName("`order`")` 反引号处理

### 测试要求
- **rule**: `mvn compile` 通过，无编译错误

### 完成证据
- 9 个 Entity + 9 个 Mapper 全部创建
- `mvn compile` 编译通过
- WebMvcConfig 白名单新增 product/adoption/post 公开列表路径

---

## Task 3: 领养寄养模块 - 后端

**优先级**: high
**状态**: in_progress
**对应 AC**: AC-5, AC-6

### 工作内容
1. DTO：AdoptionSaveDTO, AdoptionApplyDTO
2. Service/ServiceImpl：AdoptionService
   - publish（发布）
   - pagePublic（前台列表，仅已审核）
   - getDetail（详情）
   - updateMine / deleteMine（用户操作自己的）
   - apply（提交申请，同一用户同一信息只能一次）
   - pageMyPublished / pageMyApplied
   - reviewApplication（发布者审核申请）
   - pageAdmin / reviewAdmin / deleteAdmin（管理员）
3. Controller：AdoptionController（前台，路径 /adoption）
4. Controller：AdminAdoptionController（后台，路径 /admin/adoption）

### 测试要求
- **rule**: 发布领养寄养信息接口正常返回 id
- **rule**: 列表接口仅返回 status=approved 的数据
- **rule**: 申请接口对同一用户同一信息重复调用返回业务错误
- **rule**: 非发布者调用审核接口返回 403 或业务错误
- **rule**: 管理员审核/下架接口正常工作

---

## Task 4: 领养寄养模块 - 前端

**优先级**: high
**状态**: in_progress
**对应 AC**: AC-9, AC-10

### 工作内容
1. API：`src/api/adoption.js`
2. 页面：
   - `AdoptionList.vue`（列表，路由 /adoption）
   - `AdoptionDetail.vue`（详情+申请，路由 /adoption/:id）
   - `AdoptionPublish.vue`（发布，路由 /adoption/publish）
3. 路由注册（router/index.js）
4. 导航栏添加入口（Layout.vue）

### 测试要求
- **rule**: 列表页可正常渲染、分页、筛选
- **rule**: 详情页可查看信息并提交申请
- **rule**: 发布页表单可提交成功
- **rubric(AC-16)**: 页面风格与现有页面统一，0-2 分，≥1 通过

---

## Task 5: 社区交流模块 - 后端

**优先级**: high
**状态**: completed
**对应 AC**: AC-7, AC-8

### 工作内容
1. DTO：PostSaveDTO, CommentSaveDTO
2. Service/ServiceImpl：PostService
   - create（发帖）
   - pagePublic（列表）
   - getDetail（详情，含作者信息）
   - updateMine / deleteMine
   - toggleLike（点赞/取消，原子操作 like_count）
   - addComment / listComments / deleteComment
   - pageMyPosts
   - pageAdminPosts / deletePost / pageAdminComments / deleteComment（管理员）
3. Controller：PostController（前台，路径 /post）
4. Controller：AdminPostController（后台，路径 /admin/post）

### 测试要求
- **rule**: 发帖接口正常返回 id
- **rule**: 点赞接口调用两次后 like_count 不变（toggle）
- **rule**: 评论接口正常，评论列表返回正确
- **rule**: 非作者删除帖子返回 403
- **rule**: 管理员删除帖子/评论正常

---

## Task 6: 社区交流模块 - 前端

**优先级**: high
**状态**: in_progress
**对应 AC**: AC-9, AC-10

### 工作内容
1. API：`src/api/community.js`
2. 页面：
   - `CommunityList.vue`（帖子列表，路由 /community）
   - `CommunityDetail.vue`（帖子详情+评论+点赞，路由 /community/:id）
   - `PostPublish.vue`（发帖，路由 /community/publish）
3. 路由注册
4. 导航栏添加入口

### 测试要求
- **rule**: 帖子列表可渲染、分页
- **rule**: 详情页可查看帖子、发表评论、点赞/取消
- **rule**: 发帖页可提交
- **rubric(AC-16)**: 页面风格统一，≥1 分

---

## Task 7: 商城与订单模块 - 后端

**优先级**: high
**状态**: pending
**对应 AC**: AC-3, AC-4

### 工作内容
1. DTO：ProductSaveDTO, CartAddDTO, OrderCreateDTO
2. Service/ServiceImpl：ProductService
   - pagePublic / getDetail（前台）
   - pageAdmin / save / update / delete / updateStatus（管理员）
3. Service/ServiceImpl：CartService
   - list / add / updateQuantity / remove / clear
4. Service/ServiceImpl：OrderService
   - create（从购物车选中项或立即购买，扣减库存）
   - pageMy / getDetail
   - pay（模拟，状态 pending→paid）
   - cancel（待付款可取消，回滚库存）
   - confirm（已发货→已完成）
   - pageAdmin / ship / updateStatus（管理员）
5. Controller：ProductController, CartController, OrderController（前台）
6. Controller：AdminProductController, AdminOrderController（后台）

### 测试要求
- **rule**: 商品列表/详情正常
- **rule**: 购物车增删改查正常
- **rule**: 创建订单成功，库存扣减，购物车对应项清除
- **rule**: 支付后订单状态变为 paid
- **rule**: 取消待付款订单回滚库存
- **rule**: 管理员发货后订单状态变为 shipped
- **rule**: 库存不足时创建订单返回业务错误

---

## Task 8: 商城与订单模块 - 前端

**优先级**: high
**状态**: in_progress
**对应 AC**: AC-9, AC-10

### 工作内容
1. API：`src/api/mall.js`（商品+购物车+订单）
2. 页面：
   - `MallList.vue`（商品列表，路由 /mall）
   - `MallDetail.vue`（商品详情，路由 /mall/:id）
   - `Cart.vue`（购物车，路由 /cart）
   - `Checkout.vue`（结算页，路由 /checkout）
   - `OrderList.vue`（我的订单，路由 /order）
   - `OrderDetail.vue`（订单详情，路由 /order/:id）
3. 路由注册
4. 导航栏添加入口
5. 用户下拉菜单添加"我的订单"入口

### 测试要求
- **rule**: 商品列表/详情可渲染
- **rule**: 加入购物车、修改数量、移除正常
- **rule**: 结算页可提交订单，跳转到订单详情
- **rule**: 订单列表可按状态筛选，支付/取消/确认收货按钮正常
- **rubric(AC-16)**: 页面风格统一，≥1 分

---

## Task 9: 管理员后台新增菜单

**优先级**: medium
**状态**: pending
**对应 AC**: AC-4, AC-6, AC-8

### 工作内容
1. AdminLayout.vue 侧边栏新增菜单项：领养寄养管理、社区管理、商品管理、订单管理
2. 后台页面（如已有 AdminInfo 模式）：
   - AdminAdoption.vue
   - AdminCommunity.vue（帖子+评论管理）
   - AdminProduct.vue
   - AdminOrder.vue
3. 路由注册

### 测试要求
- **rule**: 管理员后台可看到全部新增菜单并进入
- **rule**: 各管理页列表可渲染、操作按钮可用

---

## Task 10: 构建与端到端验证

**优先级**: high
**状态**: completed
**对应 AC**: AC-9, AC-11, AC-12, AC-13

### 工作内容
1. 后端 `mvn compile` 通过
2. 后端 `mvn spring-boot:run` 启动，执行 init.sql
3. 前端 `vite build` 通过
4. 接口冒烟测试（用脚本或 curl 验证关键接口）
5. 浏览器验证：登录后遍历新路由，确认无白屏、无控制台报错、数据正常展示

### 测试要求
- **rule**: 后端编译通过
- **rule**: 前端构建通过
- **rule**: 所有新路由可访问，无白屏
- **rule**: 未登录访问需登录接口返回 401
- **rule**: 非作者操作他人数据被拒绝
- **rubric(AC-14)**: 功能完整度 ≥2 分
- **rubric(AC-15)**: 代码一致性 ≥1 分

### 完成证据
- 后端 `mvn compile` 成功
- 后端启动成功，9 张新表创建，演示数据插入
- 前端 `vite build` 成功（15.01s）
- 接口冒烟测试：
  - 商品列表、领养寄养列表、社区帖子列表正常返回
  - 加购→创建订单→支付：订单状态 pending→paid，库存 100→98
  - 社区点赞 toggle（13↔12）正常
  - 社区评论创建成功
  - 领养申请权限校验正确（不能申请自己发布的）
- 浏览器验证：
  - 前台 /adoption、/community、/mall、/cart、/order 全部可访问
  - 后台 /admin/adoption、/admin/community、/admin/product、/admin/order 全部可访问
- 未登录访问需登录接口返回 401（JwtInterceptor）
- 非作者操作他人数据被拒绝（checkOwner 校验）
