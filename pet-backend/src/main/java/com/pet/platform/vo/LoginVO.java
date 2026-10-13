package com.pet.platform.vo;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginVO {

    private String token;
    private Long id;
    private String account;
    private String nickname;
    private String avatar;
    /** 角色：user / admin */
    private String role;
}
