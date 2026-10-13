# 商家端实现任务清单

## Task 1: 数据库迁移 — merchant 表 + product.merchant_id
- **Status**: completed
- **Priority**: high
- **AC 覆盖**: F1, F2
- **Completion Evidence**: migration.sql 新增 merchant 表（DROP+CREATE），product 表改为 DROP+CREATE 并含 merchant_id 字段。后端启动后表结构正确，DataInitializer 创建默认商家成功。

## Task 2: 后端实体与 Mapper
- **Status**: completed
- **Priority**: high
- **AC 覆盖**: F1, F2
- **Completion Evidence**: Merchant.java、MerchantMapper.java 已创建；Product.java 新增 merchantId 字段。mvn compile 通过。

## Task 3: 商家认证 Service + Controller + 拦截器
- **Status**: completed
- **Priority**: high
- **AC 覆盖**: F1, AC1
- **Completion Evidence**: MerchantService/MerchantServiceImpl、MerchantAuthController、MerchantInterceptor 已创建；WebMvcConfig 注册拦截器并加入白名单；DataInitializer 写入默认商家 merchant/123456。测试：POST /merchant/login 返回 role=merchant；访问 /merchant/dashboard 200；访问 /admin/dashboard 返回 40301。

## Task 4: 商家商品 Service + Controller
- **Status**: completed
- **Priority**: high
- **AC 覆盖**: F2, AC2, AC3
- **Completion Evidence**: ProductService 新增 6 个商家方法（含归属校验）；MerchantProductController 已创建。测试：商家新增商品 merchant_id=1；商家列表仅返回自身商品；merchant2 修改 merchant1 商品返回 40301。

## Task 5: 商家订单 Service + Controller
- **Status**: completed
- **Priority**: high
- **AC 覆盖**: F3, AC4, AC5
- **Completion Evidence**: OrderService 新增 4 个商家方法（通过 order_item→product.merchant_id 过滤）；MerchantOrderController 已创建。测试：用户下单商家商品后，商家订单列表可见；明细仅含商家商品；发货成功；重复发货返回 40001"仅已付款订单可发货"。

## Task 6: 商家看板 Controller
- **Status**: completed
- **Priority**: medium
- **AC 覆盖**: F4
- **Completion Evidence**: MerchantDashboardController 已创建，返回 productCount、orderCount、revenue。测试：GET /merchant/dashboard 返回 200。

## Task 7: 前端 API 模块 + 路由
- **Status**: completed
- **Priority**: high
- **AC 覆盖**: F1-F4, AC6
- **Completion Evidence**: src/api/merchant.js 已创建；router 新增 /merchant/login 及 /merchant/* 路由（requireMerchant）；路由守卫处理 requireMerchant；request.js 401 时按路径跳转 /merchant/login。前端构建通过。

## Task 8: 前端页面
- **Status**: completed
- **Priority**: high
- **AC 覆盖**: F1-F4, AC7
- **Completion Evidence**: MerchantLogin.vue（登录/注册切换）、MerchantLayout.vue（看板/商品/订单菜单）、MerchantDashboard.vue（统计卡片）、MerchantProduct.vue（CRUD+上下架）、MerchantOrder.vue（列表+明细+发货）已创建。与管理员端风格一致，npx vite build 通过。

## Task 9: 构建验证与冒烟测试
- **Status**: completed
- **Priority**: high
- **AC 覆盖**: 全部 AC
- **Completion Evidence**: mvn compile 通过；npx vite build 通过；后端启动成功；所有 AC1-AC6 冒烟测试通过（商家登录/商品隔离/订单过滤/发货校验/注册）。
