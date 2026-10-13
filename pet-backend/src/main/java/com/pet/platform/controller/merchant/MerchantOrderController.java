package com.pet.platform.controller.merchant;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.Order;
import com.pet.platform.entity.OrderItem;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家订单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/merchant/order")
public class MerchantOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    public Result<PageResult<Order>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        Long merchantId = UserContext.getUserId();
        return Result.success(orderService.pageMerchant(page, size, status, merchantId));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        Long merchantId = UserContext.getUserId();
        return Result.success(orderService.getMerchantDetail(id, merchantId));
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> items(@PathVariable Long id) {
        Long merchantId = UserContext.getUserId();
        return Result.success(orderService.listMerchantItems(id, merchantId));
    }

    @PutMapping("/{id}/ship")
    public Result<Boolean> ship(@PathVariable Long id) {
        Long merchantId = UserContext.getUserId();
        orderService.shipForMerchant(id, merchantId);
        return Result.success(true);
    }
}
