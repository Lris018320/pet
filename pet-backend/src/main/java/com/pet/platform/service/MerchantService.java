package com.pet.platform.service;

import com.pet.platform.dto.LoginDTO;
import com.pet.platform.dto.MerchantRegisterDTO;
import com.pet.platform.entity.Merchant;
import com.pet.platform.vo.LoginVO;

/**
 * 商家服务
 */
public interface MerchantService {

    /** 商家登录 */
    LoginVO login(LoginDTO dto);

    /** 商家注册 */
    void register(MerchantRegisterDTO dto);

    /** 当前商家信息 */
    Merchant getCurrent();
}
