package com.pet.platform.service.impl;

import com.pet.platform.ai.AiRecognitionProvider;
import com.pet.platform.ai.RecognitionResult;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.common.PageResult;
import com.pet.platform.dto.RecognitionSaveDTO;
import com.pet.platform.entity.BreedKnowledge;
import com.pet.platform.entity.RecognitionRecord;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.RecognitionRecordMapper;
import com.pet.platform.service.BreedKnowledgeService;
import com.pet.platform.service.RecognitionService;
import com.pet.platform.util.FileUploadUtil;
import com.pet.platform.vo.RecognitionVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 识别服务实现
 */
@Slf4j
@Service
public class RecognitionServiceImpl implements RecognitionService {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private BreedKnowledgeService breedKnowledgeService;

    @Autowired
    private AiRecognitionProvider aiProvider;

    @Autowired
    private RecognitionRecordMapper recognitionRecordMapper;

    @Value("${pet.upload.path}")
    private String uploadPath;

    @Value("${pet.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public String[] upload(MultipartFile file) {
        return fileUploadUtil.save(file);
    }

    @Override
    public RecognitionVO recognize(String imageUrl) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (!StringUtils.hasText(imageUrl)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "图片URL不能为空");
        }
        // 由 imageUrl 反推本地路径
        String localPath = resolveLocalPath(imageUrl);

        RecognitionVO vo = new RecognitionVO();
        vo.setImageUrl(imageUrl);

        RecognitionResult result = aiProvider.recognize(localPath);
        if (!result.isSuccess()) {
            vo.setSuccess(false);
            vo.setMessage(result.getErrorMessage());
            return vo;
        }
        vo.setSuccess(true);
        vo.setPetType(result.getPetType());
        vo.setBreed(result.getBreed());
        vo.setConfidence(result.getConfidence() == null ? null : BigDecimal.valueOf(result.getConfidence()));

        // 匹配品种科普信息
        BreedKnowledge knowledge = breedKnowledgeService.matchBreed(result.getBreed());
        if (knowledge != null) {
            RecognitionVO.KnowledgeVO kvo = new RecognitionVO.KnowledgeVO();
            kvo.setDescription(knowledge.getDescription());
            kvo.setPersonality(knowledge.getPersonality());
            kvo.setFeedingTaboo(knowledge.getFeedingTaboo());
            kvo.setCareTips(knowledge.getCareTips());
            vo.setKnowledge(kvo);
            // 若 AI 未返回 petType，使用科普表中的类型
            if (!StringUtils.hasText(vo.getPetType())) {
                vo.setPetType(knowledge.getPetType());
            }
            // 若 AI 返回名较长，用科普表标准名覆盖
            if (!knowledge.getBreedName().equals(vo.getBreed())) {
                vo.setBreed(knowledge.getBreedName());
            }
        }
        log.info("识别成功 userId={} breed={} confidence={}%", userId, vo.getBreed(), vo.getConfidence());
        return vo;
    }

    @Override
    public Long save(RecognitionSaveDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        RecognitionRecord record = new RecognitionRecord();
        record.setUserId(userId);
        record.setImageUrl(dto.getImageUrl());
        record.setPetType(dto.getPetType());
        record.setBreed(dto.getBreed());
        record.setConfidence(dto.getConfidence());
        record.setSuccess(dto.getSuccess() == null ? 1 : dto.getSuccess());
        record.setCreateTime(LocalDateTime.now());
        recognitionRecordMapper.insert(record);
        log.info("保存识别记录 userId={} recordId={}", userId, record.getId());
        return record.getId();
    }

    /**
     * 把 imageUrl 反推为本地绝对路径
     * imageUrl: /uploads/2024-09-06/xxx.jpg
     * local:    {uploadPath}2024-09-06/xxx.jpg
     */
    private String resolveLocalPath(String imageUrl) {
        String prefix = urlPrefix.startsWith("/") ? urlPrefix : "/" + urlPrefix;
        String relative = imageUrl.startsWith(prefix) ? imageUrl.substring(prefix.length()) : imageUrl;
        if (relative.startsWith("/")) {
            relative = relative.substring(1);
        }
        String base = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        return base + relative;
    }

    @Override
    public PageResult<RecognitionRecord> myRecords(Integer page, Integer size, Integer success) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        LambdaQueryWrapper<RecognitionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecognitionRecord::getUserId, userId)
               .eq(success != null, RecognitionRecord::getSuccess, success)
               .orderByDesc(RecognitionRecord::getCreateTime);
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : Math.min(size, 50);
        Page<RecognitionRecord> pageObj = new Page<>(p, s);
        Page<RecognitionRecord> result = recognitionRecordMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public int batchDeleteMine(java.util.List<Long> ids) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        // 仅删除当前用户的记录，避免越权删除他人数据
        LambdaQueryWrapper<RecognitionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecognitionRecord::getUserId, userId)
               .in(RecognitionRecord::getId, ids);
        int count = recognitionRecordMapper.delete(wrapper);
        log.info("批量删除识别记录 userId={} count={}", userId, count);
        return count;
    }
}
