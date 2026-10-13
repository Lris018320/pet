package com.pet.platform.controller.user;

import com.pet.platform.common.Result;
import com.pet.platform.dto.LoginDTO;
import com.pet.platform.dto.PasswordDTO;
import com.pet.platform.dto.ProfileUpdateDTO;
import com.pet.platform.dto.RegisterDTO;
import com.pet.platform.service.UserService;
import com.pet.platform.vo.LoginVO;
import com.pet.platform.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户鉴权控制器：注册/登录/退出/当前用户/修改密码/更新资料
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /**
     * 退出登录
     * 说明：JWT 无状态，前端清除 token 即可；后端保留接口便于扩展黑名单
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<UserVO> current() {
        return Result.success(userService.currentUser());
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody @Valid PasswordDTO dto) {
        userService.changePassword(dto);
        return Result.success();
    }

    /**
     * 更新个人资料（昵称、头像）
     */
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@RequestBody @Valid ProfileUpdateDTO dto) {
        return Result.success(userService.updateProfile(dto));
    }
}
