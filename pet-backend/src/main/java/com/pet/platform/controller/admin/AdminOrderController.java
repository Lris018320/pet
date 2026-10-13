package com.pet.platform.controller.admin;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.Order;
import com.pet.platform.entity.OrderItem;
import com.pet.platform.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台订单管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    public Result<PageResult<Order>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        return Result.success(orderService.pageAdmin(page, size, status));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.success(orderService.getDetail(id));
    }

    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> items(@PathVariable Long id) {
        return Result.success(orderService.listItems(id));
    }

    @PutMapping("/{id}/ship")
    public Result<Boolean> ship(@PathVariable Long id) {
        orderService.ship(id);
        return Result.success(true);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        orderService.updateStatus(id, status);
        return Result.success(true);
    }
}
