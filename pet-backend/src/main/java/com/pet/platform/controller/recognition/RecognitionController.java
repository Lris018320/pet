package com.pet.platform.controller.recognition;

import com.pet.platform.common.PageResult;
import com.pet.platform.common.Result;
import com.pet.platform.dto.RecognitionSaveDTO;
import com.pet.platform.entity.RecognitionRecord;
import com.pet.platform.service.RecognitionService;
import com.pet.platform.vo.RecognitionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * AI 识别控制器
 * 提供图片上传、识别、保存记录、我的识别历史 4 个接口
 */
@Slf4j
@RestController
@RequestMapping("/recognition")
public class RecognitionController {

    @Autowired
    private RecognitionService recognitionService;

    /**
     * 上传图片
     * 返回 imageUrl（前端后续识别/建档使用）
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String[] result = recognitionService.upload(file);
        Map<String, String> data = new HashMap<>(2);
        data.put("imageUrl", result[0]);
        return Result.success(data);
    }

    /**
     * 调用 AI 识别
     */
    @PostMapping("/recognize")
    public Result<RecognitionVO> recognize(@RequestBody Map<String, String> body) {
        String imageUrl = body.get("imageUrl");
        return Result.success(recognitionService.recognize(imageUrl));
    }

    /**
     * 保存识别记录
     */
    @PostMapping("/save")
    public Result<Long> save(@RequestBody @Valid RecognitionSaveDTO dto) {
        return Result.success(recognitionService.save(dto));
    }

    /**
     * 我的识别记录分页
     */
    @GetMapping("/my/page")
    public Result<PageResult<RecognitionRecord>> myPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer success) {
        return Result.success(recognitionService.myRecords(page, size, success));
    }

    /**
     * 批量删除我的识别记录
     */
    @DeleteMapping("/my/batch")
    public Result<Integer> batchDeleteMine(@RequestBody java.util.List<Long> ids) {
        return Result.success(recognitionService.batchDeleteMine(ids));
    }
}
