package com.pet.platform.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应
 */
@Data
public class UserVO {

    private Long id;
    private String account;
    private String nickname;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
}
