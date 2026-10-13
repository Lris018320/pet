package com.pet.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 商家注册请求
 */
@Data
public class MerchantRegisterDTO {

    @NotBlank(message = "账号不能为空")
    @Size(min = 3, max = 20, message = "账号长度需为3-20位")
    private String account;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 30, message = "密码长度需为6-30位")
    private String password;

    private String name;

    private String phone;
}
