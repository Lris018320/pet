package com.pet.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 购物车添加 DTO
 */
@Data
public class CartAddDTO {

    @NotNull(message = "商品 ID 不能为空")
    private Long productId;

    private Integer quantity;
}
