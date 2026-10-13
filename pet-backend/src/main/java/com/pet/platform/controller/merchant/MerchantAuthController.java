package com.pet.platform.controller.merchant;

import com.pet.platform.common.Result;
import com.pet.platform.dto.LoginDTO;
import com.pet.platform.dto.MerchantRegisterDTO;
import com.pet.platform.entity.Merchant;
import com.pet.platform.service.MerchantService;
import com.pet.platform.vo.LoginVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商家鉴权控制器
 */
@RestController
@RequestMapping("/merchant")
public class MerchantAuthController {

    @Autowired
    private MerchantService merchantService;

    /** 商家登录 */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(merchantService.login(dto));
    }

    /** 商家注册 */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid MerchantRegisterDTO dto) {
        merchantService.register(dto);
        return Result.success(null);
    }

    /** 当前商家信息 */
    @GetMapping("/current")
    public Result<Merchant> current() {
        return Result.success(merchantService.getCurrent());
    }
}
