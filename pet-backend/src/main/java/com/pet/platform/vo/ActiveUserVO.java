package com.pet.platform.vo;

import lombok.Data;

/**
 * 活跃用户 VO
 */
@Data
public class ActiveUserVO {

    private Long userId;

    private String nickname;

    private String avatar;

    /** 等级：按活跃度换算 Lv.1 ~ Lv.9 */
    private Integer level;

    /** 发帖数 */
    private Long postCount;

    /** 当前登录用户是否已关注（未登录恒为 false） */
    private Boolean followed;
}
