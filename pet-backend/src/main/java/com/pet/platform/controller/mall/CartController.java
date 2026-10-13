package com.pet.platform.controller.mall;

import com.pet.platform.common.Result;
import com.pet.platform.dto.CartAddDTO;
import com.pet.platform.entity.Cart;
import com.pet.platform.service.CartService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /** 购物车列表 */
    @GetMapping
    public Result<List<Cart>> list() {
        return Result.success(cartService.list());
    }

    /** 加入购物车 */
    @PostMapping
    public Result<Boolean> add(@RequestBody @Valid CartAddDTO dto) {
        cartService.add(dto.getProductId(), dto.getQuantity());
        return Result.success(true);
    }

    /** 修改数量 */
    @PutMapping("/{cartId}")
    public Result<Boolean> update(@PathVariable Long cartId, @RequestParam Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return Result.success(true);
    }

    /** 移除 */
    @DeleteMapping("/{cartId}")
    public Result<Boolean> remove(@PathVariable Long cartId) {
        cartService.remove(cartId);
        return Result.success(true);
    }

    /** 清空 */
    @DeleteMapping
    public Result<Boolean> clear() {
        cartService.clear();
        return Result.success(true);
    }
}
