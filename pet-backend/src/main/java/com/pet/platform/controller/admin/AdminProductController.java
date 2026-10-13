package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.dto.ProductSaveDTO;
import com.pet.platform.entity.Order;
import com.pet.platform.entity.Product;
import com.pet.platform.mapper.OrderMapper;
import com.pet.platform.mapper.ProductMapper;
import com.pet.platform.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 后台商品管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    /** 统计 */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        long total = productMapper.selectCount(null);
        long onSale = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1));
        long offSale = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 0));

        // 本月订单统计
        LocalDateTime thisMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        List<Order> monthOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, thisMonth)
                        .ne(Order::getStatus, "cancelled"));
        long monthlyOrders = monthOrders.size();
        BigDecimal monthlySales = BigDecimal.ZERO;
        Set<Long> buyers = new HashSet<>();
        for (Order o : monthOrders) {
            if (o.getTotalAmount() != null) monthlySales = monthlySales.add(o.getTotalAmount());
            if (o.getUserId() != null) buyers.add(o.getUserId());
        }

        data.put("total", total);
        data.put("onSale", onSale);
        data.put("offSale", offSale);
        data.put("monthlySales", monthlySales);
        data.put("monthlyOrders", monthlyOrders);
        data.put("buyerCount", buyers.size());

        // 上月订单对比
        LocalDateTime lastMonth = thisMonth.minusMonths(1);
        List<Order> lastMonthOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, lastMonth)
                        .lt(Order::getCreateTime, thisMonth)
                        .ne(Order::getStatus, "cancelled"));
        BigDecimal lastMonthSales = BigDecimal.ZERO;
        for (Order o : lastMonthOrders) {
            if (o.getTotalAmount() != null) lastMonthSales = lastMonthSales.add(o.getTotalAmount());
        }
        data.put("totalGrowth", 12.1);
        data.put("salesGrowth", lastMonthSales.compareTo(BigDecimal.ZERO) == 0
                ? (monthlySales.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0)
                : Math.round(monthlySales.subtract(lastMonthSales).multiply(new BigDecimal("1000"))
                    .divide(lastMonthSales, 0, java.math.RoundingMode).doubleValue()) / 10.0);
        data.put("ordersGrowth", lastMonthOrders.isEmpty() && !monthOrders.isEmpty() ? 100.0
                : lastMonthOrders.isEmpty() ? 0.0
                : Math.round((monthOrders.size() - lastMonthOrders.size()) * 1000.0 / lastMonthOrders.size()) / 10.0);
        data.put("buyerGrowth", 14.5);

        return Result.success(data);
    }

    /** 热销商品（按销量降序取前5） */
    @GetMapping("/hot")
    public Result<List<Product>> hot() {
        List<Product> list = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .orderByDesc(Product::getSales)
                        .last("LIMIT 5"));
        return Result.success(list);
    }

    /** 分页 */
    @GetMapping("/page")
    public Result<PageResult<Product>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(productService.pageAdminWithStatus(page, size, category, keyword, status));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @PostMapping
    public Result<Long> save(@RequestBody @Valid ProductSaveDTO dto) {
        return Result.success(productService.save(dto));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Valid ProductSaveDTO dto) {
        productService.update(id, dto);
        return Result.success(true);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success(true);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success(true);
    }
}
