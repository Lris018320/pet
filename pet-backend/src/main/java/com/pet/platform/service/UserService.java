package com.pet.platform.service;

import com.pet.platform.dto.LoginDTO;
import com.pet.platform.dto.PasswordDTO;
import com.pet.platform.dto.ProfileUpdateDTO;
import com.pet.platform.dto.RegisterDTO;
import com.pet.platform.vo.LoginVO;
import com.pet.platform.vo.UserVO;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 注册
     */
    void register(RegisterDTO dto);

    /**
     * 登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 修改密码
     */
    void changePassword(PasswordDTO dto);

    /**
     * 获取当前登录用户信息
     */
    UserVO currentUser();

    /**
     * 更新个人资料（昵称、头像）
     */
    UserVO updateProfile(ProfileUpdateDTO dto);
}
