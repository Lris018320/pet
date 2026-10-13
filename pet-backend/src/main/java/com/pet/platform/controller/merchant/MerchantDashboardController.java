package com.pet.platform.controller.merchant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.Result;
import com.pet.platform.entity.Order;
import com.pet.platform.entity.OrderItem;
import com.pet.platform.entity.Product;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.OrderItemMapper;
import com.pet.platform.mapper.OrderMapper;
import com.pet.platform.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家看板控制器
 */
@RestController
@RequestMapping("/merchant/dashboard")
public class MerchantDashboardController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping
    public Result<Map<String, Object>> dashboard() {
        Long merchantId = UserContext.getUserId();
        Map<String, Object> data = new HashMap<>();

        // 商品数
        Long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getMerchantId, merchantId));
        data.put("productCount", productCount);

        // 商家商品ID
        List<Long> productIds = productMapper.selectList(
                        new LambdaQueryWrapper<Product>().eq(Product::getMerchantId, merchantId)
                                .select(Product::getId))
                .stream().map(Product::getId).collect(Collectors.toList());

        if (productIds.isEmpty()) {
            data.put("orderCount", 0L);
            data.put("revenue", BigDecimal.ZERO);
            return Result.success(data);
        }

        // 含本商家商品的订单
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().in(OrderItem::getProductId, productIds)
                        .select(OrderItem::getOrderId, OrderItem::getSubtotal));
        List<Long> orderIds = items.stream().map(OrderItem::getOrderId).distinct().collect(Collectors.toList());
        data.put("orderCount", (long) orderIds.size());

        // 营业额：已完成订单中本商家商品的 subtotal 之和
        BigDecimal revenue = BigDecimal.ZERO;
        if (!orderIds.isEmpty()) {
            List<Order> completedOrders = orderMapper.selectList(
                    new LambdaQueryWrapper<Order>().in(Order::getId, orderIds)
                            .eq(Order::getStatus, "completed")
                            .select(Order::getId));
            if (!completedOrders.isEmpty()) {
                List<Long> completedOrderIds = completedOrders.stream()
                        .map(Order::getId).collect(Collectors.toList());
                revenue = items.stream()
                        .filter(it -> completedOrderIds.contains(it.getOrderId()))
                        .map(OrderItem::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
        }
        data.put("revenue", revenue);

        return Result.success(data);
    }
}
