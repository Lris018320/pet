package com.pet.platform.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 个人资料更新 DTO
 * 仅允许修改昵称和头像，账号不可改
 */
@Data
public class ProfileUpdateDTO {

    @Size(max = 30, message = "昵称不能超过 30 字")
    private String nickname;

    @Size(max = 255, message = "头像URL过长")
    private String avatar;
}
