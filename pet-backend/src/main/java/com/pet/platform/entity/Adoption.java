package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 领养寄养信息实体
 */
@Data
@TableName("adoption")
public class Adoption {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** adoption领养 / foster寄养 */
    private String type;

    private String petName;

    private String breed;

    /** 猫/狗 */
    private String petType;

    private String age;

    /** 公/母/未知 */
    private String gender;

    private String photoUrl;

    private String description;

    private String contact;

    /** 地区，如 广州市 天河区 */
    private String area;

    /** 体型：小型/中型/大型 */
    private String size;

    /** 是否已绝育：是/否 */
    private String steril;

    /** pending待审核 approved已审核 rejected已拒绝 offline已下架 */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
