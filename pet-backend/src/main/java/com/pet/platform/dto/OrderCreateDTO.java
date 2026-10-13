package com.pet.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 订单创建 DTO
 */
@Data
public class OrderCreateDTO {

    /** 购物车项 ID 列表（从购物车结算时传） */
    private List<Long> cartIds;

    /** 立即购买时的商品 ID */
    private Long productId;

    /** 立即购买时的数量 */
    private Integer quantity;

    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收件人电话不能为空")
    private String receiverPhone;

    @NotBlank(message = "收件地址不能为空")
    private String receiverAddress;
}
