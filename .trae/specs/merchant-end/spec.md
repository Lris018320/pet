# 商家端功能规格

## 问题与目标

当前宠知AI 平台仅有「用户端」和「管理员端」，商城商品全部由管理员统一发布。为支持多商家入驻运营，需新增独立的「商家端」，使商家能够自助管理商品和订单。

## 用户

- **商家**：拥有独立账号，可登录商家后台，管理自己发布的商品和关联订单。

## 目标

1. 商家可独立注册、登录商家后台
2. 商家可对自己的商品进行增删改查、上下架
3. 商家可查看包含自己商品的订单并发货
4. 商家数据互相隔离（只能操作自己的商品和订单）
5. 管理员端不受影响，仍可管理全部商品和订单

## 非目标

- 不实现商家审核流程（注册即生效，与当前 admin 模式一致）
- 不实现商家结算/提现
- 不实现商品多规格/SKU
- 不改动用户端购物/下单流程

## 功能需求

### F1 商家认证
- 商家注册：POST /merchant/register，参数 account、password、name、phone
- 商家登录：POST /merchant/login，返回 token（role=merchant）
- 商家信息：GET /merchant/current

### F2 商家商品管理
- 列表：GET /merchant/product/page，仅返回当前商家的商品
- 详情：GET /merchant/product/{id}，校验归属
- 新增：POST /merchant/product，自动绑定 merchantId
- 修改：PUT /merchant/product/{id}，校验归属
- 删除：DELETE /merchant/product/{id}，校验归属
- 上下架：PUT /merchant/product/{id}/status，校验归属

### F3 商家订单管理
- 列表：GET /merchant/order/page，返回包含当前商家商品的订单
- 详情：GET /merchant/order/{id}，校验订单含本商家商品
- 明细：GET /merchant/order/{id}/items，仅返回本商家的商品明细
- 发货：PUT /merchant/order/{id}/ship，校验订单含本商家商品且状态为 paid

### F4 商家看板
- GET /merchant/dashboard：返回商品数、订单数、营业额（已完成订单）

## 非功能需求

- 商家端接口前缀 `/merchant/**`，由 MerchantInterceptor 校验 role=merchant
- 商品表新增 `merchant_id` 字段（可空，兼容管理员创建的商品）
- 商家只能操作 merchant_id 等于自身的商品
- 商家订单通过 order_item → product.merchant_id 关联过滤
- 前端独立路由 `/merchant/*`，独立 Layout，路由守卫 requireMerchant
- 商家 token 过期后跳转 `/merchant/login`

## 约束与假设

- 沿用现有 JWT 机制，role 字段区分 user/admin/merchant
- 沿用同一套 token 存储（localStorage），同一时刻仅登录一种角色
- 数据库初始化脚本每次启动重建演示数据，需保证 merchant 表和 product.merchant_id 存在
- 管理员创建的商品 merchant_id 为 NULL，商家端不可见

## 验收标准

- **AC1 (rule)**：商家注册后可登录，token role 为 merchant，访问 /merchant/** 正常，访问 /admin/** 返回 403
- **AC2 (rule)**：商家新增商品后，merchant_id 正确写入；其他商家和管理员商品列表中看不到该商品（管理员端可见全部）
- **AC3 (rule)**：商家修改/删除非自己的商品返回 403
- **AC4 (rule)**：商家订单列表仅包含含本商家商品的订单，明细仅含本商家商品
- **AC5 (rule)**：商家对非 paid 状态订单发货返回业务错误
- **AC6 (rule)**：前端 /merchant/login 未登录访问 /merchant/dashboard 重定向到登录页
- **AC7 (rubric)**：商家端页面布局与管理员端风格一致，菜单含看板/商品/订单，体验流畅（评分≥4/5）
