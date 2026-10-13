package com.pet.platform.service;

import com.pet.platform.common.PageResult;
import com.pet.platform.dto.RecognitionSaveDTO;
import com.pet.platform.entity.RecognitionRecord;
import com.pet.platform.vo.RecognitionVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 识别服务
 */
public interface RecognitionService {

    /**
     * 上传图片
     *
     * @return [0]=访问URL，[1]=本地路径
     */
    String[] upload(MultipartFile file);

    /**
     * 调用 AI 识别图片
     */
    RecognitionVO recognize(String imageUrl);

    /**
     * 保存识别记录
     */
    Long save(RecognitionSaveDTO dto);

    /**
     * 查询当前用户的识别记录分页
     */
    PageResult<RecognitionRecord> myRecords(Integer page, Integer size, Integer success);

    /**
     * 批量删除当前用户的识别记录
     *
     * @param ids 要删除的记录 ID 列表
     * @return 实际删除条数
     */
    int batchDeleteMine(java.util.List<Long> ids);
}
