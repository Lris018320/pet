package com.pet.platform.controller.mall;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.dto.OrderCreateDTO;
import com.pet.platform.entity.Order;
import com.pet.platform.entity.OrderItem;
import com.pet.platform.service.OrderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单前台控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** 创建订单 */
    @PostMapping
    public Result<Long> create(@RequestBody @Valid OrderCreateDTO dto) {
        return Result.success(orderService.create(dto));
    }

    /** 我的订单 */
    @GetMapping("/my")
    public Result<PageResult<Order>> my(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        return Result.success(orderService.pageMy(page, size, status));
    }

    /** 订单详情 */
    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.success(orderService.getDetail(id));
    }

    /** 订单明细 */
    @GetMapping("/{id}/items")
    public Result<List<OrderItem>> items(@PathVariable Long id) {
        return Result.success(orderService.listItems(id));
    }

    /** 支付（模拟） */
    @PutMapping("/{id}/pay")
    public Result<Boolean> pay(@PathVariable Long id) {
        orderService.pay(id);
        return Result.success(true);
    }

    /** 取消订单 */
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.success(true);
    }

    /** 确认收货 */
    @PutMapping("/{id}/confirm")
    public Result<Boolean> confirm(@PathVariable Long id) {
        orderService.confirm(id);
        return Result.success(true);
    }
}
