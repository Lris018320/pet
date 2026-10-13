package com.pet.platform.controller.info;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.PetInfo;
import com.pet.platform.service.PetInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 前台资讯控制器（免登录访问）
 * 路径 /api/info/* 已在 WebMvcConfig JwtInterceptor 白名单中
 */
@Slf4j
@RestController
@RequestMapping("/info")
public class PetInfoController {

    @Autowired
    private PetInfoService petInfoService;

    /**
     * 前台资讯列表分页（仅已发布）
     */
    @GetMapping("/list")
    public Result<PageResult<PetInfo>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category) {
        return Result.success(petInfoService.pagePublic(page, size, category));
    }

    /**
     * 前台资讯详情
     */
    @GetMapping("/{id}")
    public Result<PetInfo> detail(@PathVariable Long id) {
        return Result.success(petInfoService.getPublicById(id));
    }
}
