package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 领养寄养收藏实体
 */
@Data
@TableName("adoption_favorite")
public class AdoptionFavorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long adoptionId;

    private LocalDateTime createTime;
}
