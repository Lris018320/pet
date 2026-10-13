package com.pet.platform.controller.mall;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.Product;
import com.pet.platform.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品前台控制器
 * 列表/详情接口已在 WebMvcConfig 白名单中（免登录）
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /** 商品列表 */
    @GetMapping("/list")
    public Result<PageResult<Product>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        return Result.success(productService.pagePublic(page, size, category));
    }

    /** 商品详情 */
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getDetail(id));
    }
}
