package com.pet.platform.controller.admin;

import com.pet.platform.common.Result;
import com.pet.platform.dto.LoginDTO;
import com.pet.platform.service.AdminService;
import com.pet.platform.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * 管理员鉴权控制器
 * 注意：/admin/login 在 WebMvcConfig 中已排除 JWT 拦截器，其他 /admin/** 由两层拦截器保护
 */
@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(adminService.login(dto));
    }
}
