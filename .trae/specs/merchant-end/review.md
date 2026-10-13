# 商家端功能审查记录

## 审查范围
宠知AI 平台商家端功能，覆盖后端（实体/Mapper/Service/Controller/拦截器/数据库迁移）和前端（API/路由/页面）。

## 审查检查点

### 1. 数据库与实体一致性
- merchant 表结构与 Merchant 实体字段一致 ✓
- product.merchant_id 字段与 Product.merchantId 一致 ✓
- migration.sql 使用 DROP TABLE IF EXISTS + CREATE TABLE 保证幂等 ✓

### 2. 认证与权限
- MerchantInterceptor 校验 role=="merchant" ✓
- WebMvcConfig 白名单包含 /merchant/login、/merchant/register ✓
- JWT token role=merchant，与 admin/user 隔离 ✓
- 商家访问 /admin/** 返回 40301 ✓

### 3. 商品数据隔离
- saveAsMerchant 自动绑定 merchantId ✓
- pageMerchant 按 merchantId 过滤 ✓
- getMerchantById/updateAsMerchant/deleteAsMerchant/updateStatusAsMerchant 均校验归属 ✓
- merchant2 修改 merchant1 商品返回 40301 ✓

### 4. 订单数据隔离
- pageMerchant 通过 order_item→product.merchant_id 关联过滤 ✓
- listMerchantItems 仅返回本商家商品明细 ✓
- shipForMerchant 校验订单含本商家商品且状态为 paid ✓
- 非 paid 订单发货返回 40001 ✓

### 5. 前端路由与守卫
- /merchant/login 为 guest 页面 ✓
- /merchant/* 路由 requireMerchant 守卫 ✓
- 已登录商家访问 /merchant/login 重定向到看板 ✓
- 401 时按当前路径跳转 /merchant/login 或 /login ✓

### 6. 构建验证
- mvn compile 通过 ✓
- npx vite build 通过 ✓

### 7. 冒烟测试
- 商家登录：role=merchant ✓
- 商家看板：200 ✓
- 商家商品 CRUD：merchant_id 正确绑定，列表仅含自身商品 ✓
- 商家订单：用户下单后商家可见，可发货，重复发货被拒 ✓
- 商家注册：成功 ✓

## 审查结论
**pass** — 所有验收标准 AC1-AC6 均有通过证据，AC7（页面风格）构建通过且与管理员端一致。无阻塞性问题。

## 备注
- order_item 表在 migration 中未被清理，历史测试数据可能残留，但不影响功能逻辑。
- 管理员端商品管理不受影响，仍可管理全部商品（merchant_id 为 NULL 的管理员商品在商家端不可见）。
