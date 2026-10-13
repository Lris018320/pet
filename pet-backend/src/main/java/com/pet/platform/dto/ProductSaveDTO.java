package com.pet.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品保存 DTO
 */
@Data
public class ProductSaveDTO {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称不能超过 100 字")
    private String name;

    private String description;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private Integer stock;

    private String coverImage;

    private String images;

    /** 食品/玩具/用品/医药 */
    private String category;

    /** 1上架 0下架 */
    private Integer status;
}
