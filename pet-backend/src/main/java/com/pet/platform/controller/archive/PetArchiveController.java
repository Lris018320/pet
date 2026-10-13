package com.pet.platform.controller.archive;

import com.pet.platform.common.Result;
import com.pet.platform.dto.PetArchiveSaveDTO;
import com.pet.platform.entity.PetArchive;
import com.pet.platform.service.PetArchiveService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宠物档案控制器
 * 提供建档（含一键建档）、查询列表、查询详情、修改、删除接口
 * 路径对齐技术方案 /api/archive/*
 */
@Slf4j
@RestController
@RequestMapping("/archive")
public class PetArchiveController {

    @Autowired
    private PetArchiveService petArchiveService;

    /**
     * 创建宠物档案（识别页"一键建档"或档案页手动新建）
     */
    @PostMapping
    public Result<Long> save(@RequestBody @Valid PetArchiveSaveDTO dto) {
        return Result.success(petArchiveService.save(dto));
    }

    /**
     * 我的宠物档案列表
     */
    @GetMapping("/list")
    public Result<List<PetArchive>> list() {
        return Result.success(petArchiveService.listMine());
    }

    /**
     * 档案详情
     */
    @GetMapping("/{id}")
    public Result<PetArchive> detail(@PathVariable Long id) {
        return Result.success(petArchiveService.getByIdAndUser(id));
    }

    /**
     * 修改档案
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody @Valid PetArchiveSaveDTO dto) {
        petArchiveService.updateMine(id, dto);
        return Result.success(true);
    }

    /**
     * 删除档案
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        petArchiveService.deleteMine(id);
        return Result.success(true);
    }
}
