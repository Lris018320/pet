package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.entity.Cart;
import com.pet.platform.entity.Product;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.CartMapper;
import com.pet.platform.mapper.ProductMapper;
import com.pet.platform.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车服务实现
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Cart> list() {
        Long userId = UserContext.getUserId();
        return cartMapper.selectList(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .orderByDesc(Cart::getCreateTime));
    }

    @Override
    public void add(Long productId, Integer quantity) {
        Long userId = UserContext.getUserId();
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "商品不存在或已下架");
        }
        int qty = quantity == null || quantity < 1 ? 1 : quantity;
        // 检查是否已存在
        Cart existing = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getUserId, userId)
                        .eq(Cart::getProductId, productId));
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qty);
            cartMapper.updateById(existing);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(qty);
            cart.setCreateTime(LocalDateTime.now());
            try {
                cartMapper.insert(cart);
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // 并发场景：另一请求已插入，改为累加数量
                Cart again = cartMapper.selectOne(
                        new LambdaQueryWrapper<Cart>()
                                .eq(Cart::getUserId, userId)
                                .eq(Cart::getProductId, productId));
                if (again != null) {
                    again.setQuantity(again.getQuantity() + qty);
                    cartMapper.updateById(again);
                }
            }
        }
        log.info("加入购物车 productId={} quantity={}", productId, qty);
    }

    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        Long userId = UserContext.getUserId();
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "购物车项不存在");
        }
        int qty = quantity == null || quantity < 1 ? 1 : quantity;
        cart.setQuantity(qty);
        cartMapper.updateById(cart);
    }

    @Override
    public void remove(Long cartId) {
        Long userId = UserContext.getUserId();
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "购物车项不存在");
        }
        cartMapper.deleteById(cartId);
    }

    @Override
    public void clear() {
        Long userId = UserContext.getUserId();
        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
    }
}
