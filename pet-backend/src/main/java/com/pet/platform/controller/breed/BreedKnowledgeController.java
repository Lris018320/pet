package com.pet.platform.controller.breed;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.BreedKnowledge;
import com.pet.platform.service.BreedKnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 品种科普控制器（前台免登录访问）
 * 路径 /api/breed/* 已在 WebMvcConfig JwtInterceptor 白名单
 */
@Slf4j
@RestController
@RequestMapping("/breed")
public class BreedKnowledgeController {

    @Autowired
    private BreedKnowledgeService breedKnowledgeService;

    /**
     * 品种科普分页
     */
    @GetMapping("/list")
    public Result<PageResult<BreedKnowledge>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) String keyword) {
        return Result.success(breedKnowledgeService.page(page, size, petType, keyword));
    }

    /**
     * 品种详情
     */
    @GetMapping("/{id}")
    public Result<BreedKnowledge> detail(@PathVariable Long id) {
        return Result.success(breedKnowledgeService.getById(id));
    }

    /**
     * 按品种名查询科普（供识别记录页跳转使用）
     */
    @GetMapping("/by-name")
    public Result<BreedKnowledge> byName(@RequestParam String name) {
        return Result.success(breedKnowledgeService.matchBreed(name));
    }
}
