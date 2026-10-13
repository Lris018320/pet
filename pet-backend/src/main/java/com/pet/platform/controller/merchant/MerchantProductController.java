package com.pet.platform.controller.merchant;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.dto.ProductSaveDTO;
import com.pet.platform.entity.Product;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.service.ProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家商品管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/merchant/product")
public class MerchantProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public Result<PageResult<Product>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Long merchantId = UserContext.getUserId();
        return Result.success(productService.pageMerchant(page, size, category, keyword, merchantId));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Long merchantId = UserContext.getUserId();
        return Result.success(productService.getMerchantById(id, merchantId));
    }

    @PostMapping
    public Result<Long> save(@RequestBody @Valid ProductSaveDTO dto) {
        Long merchantId = UserContext.getUserId();
        return Result.success(productService.saveAsMerchant(dto, merchantId));
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Valid ProductSaveDTO dto) {
        Long merchantId = UserContext.getUserId();
        productService.updateAsMerchant(id, dto, merchantId);
        return Result.success(true);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Long merchantId = UserContext.getUserId();
        productService.deleteAsMerchant(id, merchantId);
        return Result.success(true);
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Long merchantId = UserContext.getUserId();
        productService.updateStatusAsMerchant(id, status, merchantId);
        return Result.success(true);
    }
}
