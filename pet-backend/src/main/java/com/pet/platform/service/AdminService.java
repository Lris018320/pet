package com.pet.platform.service;

import com.pet.platform.dto.LoginDTO;
import com.pet.platform.vo.LoginVO;

/**
 * 管理员服务
 */
public interface AdminService {

    /**
     * 管理员登录
     */
    LoginVO login(LoginDTO dto);
}
