package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 品种科普实体
 */
@Data
@TableName("breed_knowledge")
public class BreedKnowledge {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String breedName;

    private String petType;

    private String description;

    private String personality;

    private String feedingTaboo;

    private String careTips;

    private LocalDateTime createTime;
}
