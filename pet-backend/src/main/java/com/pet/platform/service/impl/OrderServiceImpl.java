package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.common.PageResult;
import com.pet.platform.dto.OrderCreateDTO;
import com.pet.platform.entity.Cart;
import com.pet.platform.entity.Order;
import com.pet.platform.entity.OrderItem;
import com.pet.platform.entity.Product;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.CartMapper;
import com.pet.platform.mapper.OrderItemMapper;
import com.pet.platform.mapper.OrderMapper;
import com.pet.platform.mapper.ProductMapper;
import com.pet.platform.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Long create(OrderCreateDTO dto) {
        Long userId = UserContext.getUserId();
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        if (dto.getCartIds() != null && !dto.getCartIds().isEmpty()) {
            // 从购物车结算
            for (Long cartId : dto.getCartIds()) {
                Cart cart = cartMapper.selectById(cartId);
                if (cart == null || !cart.getUserId().equals(userId)) {
                    continue;
                }
                Product product = productMapper.selectById(cart.getProductId());
                if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                    continue;
                }
                if (product.getStock() < cart.getQuantity()) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(),
                            "商品【" + product.getName() + "】库存不足");
                }
                OrderItem item = buildItem(product, cart.getQuantity());
                items.add(item);
                total = total.add(item.getSubtotal());
                // 扣减库存（条件更新，防超卖）
                UpdateWrapper<Product> stockUw = new UpdateWrapper<>();
                stockUw.eq("id", product.getId())
                       .ge("stock", cart.getQuantity())
                       .setSql("stock = stock - " + cart.getQuantity());
                int affected = productMapper.update(null, stockUw);
                if (affected == 0) {
                    throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(),
                            "商品【" + product.getName() + "】库存不足");
                }
                // 清除购物车项
                cartMapper.deleteById(cartId);
            }
        } else if (dto.getProductId() != null) {
            // 立即购买
            Product product = productMapper.selectById(dto.getProductId());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "商品不存在或已下架");
            }
            int qty = dto.getQuantity() == null || dto.getQuantity() < 1 ? 1 : dto.getQuantity();
            if (product.getStock() < qty) {
                throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "商品库存不足");
            }
            OrderItem item = buildItem(product, qty);
            items.add(item);
            total = total.add(item.getSubtotal());
            // 扣减库存（条件更新，防超卖）
            UpdateWrapper<Product> stockUw = new UpdateWrapper<>();
            stockUw.eq("id", product.getId())
                   .ge("stock", qty)
                   .setSql("stock = stock - " + qty);
            int affected = productMapper.update(null, stockUw);
            if (affected == 0) {
                throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "商品库存不足");
            }
        } else {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "请选择要购买的商品");
        }

        if (items.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "没有有效的商品可下单");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus("pending");
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 保存订单明细
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        log.info("创建订单 id={} orderNo={} total={}", order.getId(), order.getOrderNo(), total);
        return order.getId();
    }

    @Override
    public PageResult<Order> pageMy(Integer page, Integer size, String status) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .eq(StringUtils.hasText(status), Order::getStatus, status)
               .orderByDesc(Order::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public Order getDetail(Long id) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "订单不存在");
        }
        // 管理员可看所有，用户只能看自己的
        String role = UserContext.getRole();
        if (!"admin".equals(role) && !order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权查看该订单");
        }
        return order;
    }

    @Override
    public List<OrderItem> listItems(Long orderId) {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "订单不存在");
        }
        if (!"admin".equals(role) && !order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权查看该订单");
        }
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
    }

    @Override
    @Transactional
    public void pay(Long id) {
        Order order = checkUserOrder(id);
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "订单状态不允许支付");
        }
        order.setStatus("paid");
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("订单支付 id={}", id);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Order order = checkUserOrder(id);
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "仅待付款订单可取消");
        }
        order.setStatus("cancelled");
        orderMapper.updateById(order);
        // 回滚库存
        List<OrderItem> items = listItems(id);
        for (OrderItem item : items) {
            UpdateWrapper<Product> uw = new UpdateWrapper<>();
            uw.eq("id", item.getProductId())
              .setSql("stock = stock + " + item.getQuantity());
            productMapper.update(null, uw);
        }
        log.info("取消订单 id={}", id);
    }

    @Override
    public void confirm(Long id) {
        Order order = checkUserOrder(id);
        if (!"shipped".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "仅已发货订单可确认收货");
        }
        order.setStatus("completed");
        order.setCompleteTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("确认收货 id={}", id);
    }

    @Override
    public PageResult<Order> pageAdmin(Integer page, Integer size, String status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), Order::getStatus, status)
               .orderByDesc(Order::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public void ship(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "订单不存在");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "仅已付款订单可发货");
        }
        order.setStatus("shipped");
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("订单发货 id={}", id);
    }

    private static final Set<String> VALID_STATUS = Set.of(
            "pending", "paid", "shipped", "completed", "cancelled");

    @Override
    public void updateStatus(Long id, String status) {
        if (!VALID_STATUS.contains(status)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "无效的订单状态");
        }
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "订单不存在");
        }
        order.setStatus(status);
        orderMapper.updateById(order);
        log.info("管理员修改订单状态 id={} status={}", id, status);
    }

    private Order checkUserOrder(Long id) {
        Long userId = UserContext.getUserId();
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权操作该订单");
        }
        return order;
    }

    private OrderItem buildItem(Product product, int quantity) {
        OrderItem item = new OrderItem();
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    /** 生成订单号：yyyyMMddHHmmss + 4位随机数 */
    private String generateOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return ts + rand;
    }

    private PageResult<Order> doPage(Integer page, Integer size, LambdaQueryWrapper<Order> wrapper) {
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : Math.min(size, 50);
        IPage<Order> pageObj = new Page<>(p, s);
        IPage<Order> result = orderMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    // ========== 商家端 ==========

    /** 获取商家的商品ID列表 */
    private List<Long> getMerchantProductIds(Long merchantId) {
        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getMerchantId, merchantId)
                        .select(Product::getId));
        if (products.isEmpty()) {
            return Collections.emptyList();
        }
        return products.stream().map(Product::getId).collect(Collectors.toList());
    }

    @Override
    public PageResult<Order> pageMerchant(Integer page, Integer size, String status, Long merchantId) {
        List<Long> productIds = getMerchantProductIds(merchantId);
        if (productIds.isEmpty()) {
            return new PageResult<>(0L, Collections.emptyList());
        }
        // 找出包含本商家商品的订单ID
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().in(OrderItem::getProductId, productIds)
                        .select(OrderItem::getOrderId));
        if (items.isEmpty()) {
            return new PageResult<>(0L, Collections.emptyList());
        }
        List<Long> orderIds = items.stream().map(OrderItem::getOrderId).distinct().collect(Collectors.toList());

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Order::getId, orderIds)
               .eq(StringUtils.hasText(status), Order::getStatus, status)
               .orderByDesc(Order::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public Order getMerchantDetail(Long id, Long merchantId) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "订单不存在");
        }
        // 校验订单包含本商家商品
        List<OrderItem> merchantItems = listMerchantItems(id, merchantId);
        if (merchantItems.isEmpty()) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权查看该订单");
        }
        return order;
    }

    @Override
    public List<OrderItem> listMerchantItems(Long orderId, Long merchantId) {
        List<Long> productIds = getMerchantProductIds(merchantId);
        if (productIds.isEmpty()) {
            return Collections.emptyList();
        }
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
                        .in(OrderItem::getProductId, productIds));
    }

    @Override
    @Transactional
    public void shipForMerchant(Long id, Long merchantId) {
        Order order = getMerchantDetail(id, merchantId);
        if (!"paid".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "仅已付款订单可发货");
        }
        order.setStatus("shipped");
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("商家发货 merchantId={} orderId={}", merchantId, id);
    }
}
