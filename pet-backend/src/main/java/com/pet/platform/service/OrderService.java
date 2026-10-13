package com.pet.platform.service;

import com.pet.platform.common.PageResult;
import com.pet.platform.dto.OrderCreateDTO;
import com.pet.platform.entity.Order;
import com.pet.platform.entity.OrderItem;

import java.util.List;

public interface OrderService {

    /** 创建订单 */
    Long create(OrderCreateDTO dto);

    /** 我的订单 */
    PageResult<Order> pageMy(Integer page, Integer size, String status);

    /** 订单详情（含明细） */
    Order getDetail(Long id);

    /** 订单明细 */
    List<OrderItem> listItems(Long orderId);

    /** 支付（模拟） */
    void pay(Long id);

    /** 取消订单 */
    void cancel(Long id);

    /** 确认收货 */
    void confirm(Long id);

    /** 管理员订单列表 */
    PageResult<Order> pageAdmin(Integer page, Integer size, String status);

    /** 管理员发货 */
    void ship(Long id);

    /** 管理员修改状态 */
    void updateStatus(Long id, String status);

    // ========== 商家端 ==========

    /** 商家订单列表（仅含本商家商品的订单） */
    PageResult<Order> pageMerchant(Integer page, Integer size, String status, Long merchantId);

    /** 商家订单详情（校验含本商家商品） */
    Order getMerchantDetail(Long id, Long merchantId);

    /** 商家订单明细（仅本商家商品） */
    List<OrderItem> listMerchantItems(Long orderId, Long merchantId);

    /** 商家发货（校验含本商家商品且状态为 paid） */
    void shipForMerchant(Long id, Long merchantId);
}
