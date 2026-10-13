package com.pet.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 领养寄养申请实体
 */
@Data
@TableName("adoption_application")
public class AdoptionApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adoptionId;

    private Long applicantId;

    private String message;

    private String contact;

    /** pending待审核 approved已通过 rejected已拒绝 */
    private String status;

    private LocalDateTime createTime;
}
