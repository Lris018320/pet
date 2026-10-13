package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商家ID，NULL表示管理员发布 */
    private Long merchantId;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String coverImage;

    private String images;

    /** 食品/玩具/用品/医药 */
    private String category;

    /** 1上架 0下架 */
    private Integer status;

    /** 销量 */
    private Integer sales;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
