package com.pet.platform.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.entity.RecognitionRecord;
import com.pet.platform.mapper.RecognitionRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 后台识别记录管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/recognition")
public class AdminRecognitionController {

    @Autowired
    private RecognitionRecordMapper recognitionRecordMapper;

    /**
     * 全部识别记录分页
     */
    @GetMapping("/page")
    public Result<PageResult<RecognitionRecord>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) Integer success) {
        LambdaQueryWrapper<RecognitionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, RecognitionRecord::getUserId, userId)
               .like(StringUtils.hasText(breed), RecognitionRecord::getBreed, breed)
               .eq(success != null, RecognitionRecord::getSuccess, success)
               .orderByDesc(RecognitionRecord::getCreateTime);
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : Math.min(size, 100);
        Page<RecognitionRecord> pageObj = new Page<>(p, s);
        Page<RecognitionRecord> result = recognitionRecordMapper.selectPage(pageObj, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }
}
