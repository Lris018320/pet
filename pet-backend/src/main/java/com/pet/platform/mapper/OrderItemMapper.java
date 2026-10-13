package com.pet.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pet.platform.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细 Mapper
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
