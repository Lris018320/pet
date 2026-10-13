package com.pet.platform.controller.adoption;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.dto.AdoptionApplyDTO;
import com.pet.platform.dto.AdoptionSaveDTO;
import com.pet.platform.entity.Adoption;
import com.pet.platform.entity.AdoptionApplication;
import com.pet.platform.service.AdoptionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 领养寄养前台控制器
 * 列表接口 /adoption/list 已在 WebMvcConfig 白名单中（免登录）
 */
@Slf4j
@RestController
@RequestMapping("/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    /** 发布 */
    @PostMapping
    public Result<Long> publish(@RequestBody @Valid AdoptionSaveDTO dto) {
        return Result.success(adoptionService.publish(dto));
    }

    /** 前台列表（仅已审核，支持多条件筛选） */
    @GetMapping("/list")
    public Result<PageResult<Adoption>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String petType,
            @RequestParam(required = false) String age,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String sizeType,
            @RequestParam(required = false) String steril,
            @RequestParam(required = false) String keyword) {
        return Result.success(adoptionService.pagePublic(page, size, type, petType, age, gender, area, sizeType, steril, keyword));
    }

    /** 切换收藏 */
    @PostMapping("/{id}/favorite")
    public Result<Boolean> toggleFavorite(@PathVariable Long id) {
        return Result.success(adoptionService.toggleFavorite(id));
    }

    /** 我的收藏ID列表 */
    @GetMapping("/favorites/ids")
    public Result<java.util.Set<Long>> myFavoriteIds() {
        return Result.success(adoptionService.listMyFavoriteIds());
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<Adoption> detail(@PathVariable Long id) {
        return Result.success(adoptionService.getDetail(id));
    }

    /** 修改自己的 */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Valid AdoptionSaveDTO dto) {
        adoptionService.updateMine(id, dto);
        return Result.success(true);
    }

    /** 删除自己的 */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        adoptionService.deleteMine(id);
        return Result.success(true);
    }

    /** 提交申请 */
    @PostMapping("/{id}/apply")
    public Result<Long> apply(@PathVariable Long id, @RequestBody @Valid AdoptionApplyDTO dto) {
        return Result.success(adoptionService.apply(id, dto));
    }

    /** 我发布的 */
    @GetMapping("/mine")
    public Result<PageResult<Adoption>> myPublished(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adoptionService.pageMyPublished(page, size));
    }

    /** 我申请的 */
    @GetMapping("/applications/mine")
    public Result<PageResult<AdoptionApplication>> myApplied(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adoptionService.pageMyApplied(page, size));
    }

    /** 查看某条信息的申请列表（发布者） */
    @GetMapping("/{id}/applications")
    public Result<PageResult<AdoptionApplication>> applications(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adoptionService.pageApplicationsByAdoption(id, page, size));
    }

    /** 发布者审核申请 */
    @PutMapping("/applications/{applicationId}/status")
    public Result<Boolean> reviewApplication(
            @PathVariable Long applicationId,
            @RequestParam String status) {
        adoptionService.reviewApplication(applicationId, status);
        return Result.success(true);
    }
}
