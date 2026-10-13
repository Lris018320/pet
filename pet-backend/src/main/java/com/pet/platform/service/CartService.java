package com.pet.platform.service;

import com.pet.platform.entity.Cart;

import java.util.List;

public interface CartService {

    /** 购物车列表（含商品信息） */
    List<Cart> list();

    /** 加入购物车 */
    void add(Long productId, Integer quantity);

    /** 修改数量 */
    void updateQuantity(Long cartId, Integer quantity);

    /** 移除 */
    void remove(Long cartId);

    /** 清空 */
    void clear();
}
