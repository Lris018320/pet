package com.pet.platform.util;

import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传工具
 * 负责保存上传图片到本地 uploads 目录，返回访问 URL
 */
@Slf4j
@Component
public class FileUploadUtil {

    @Value("${pet.upload.path}")
    private String uploadPath;

    @Value("${pet.upload.url-prefix}")
    private String urlPrefix;

    /** 允许的图片扩展名 */
    private static final Set<String> ALLOWED_EXT = new HashSet<>(Arrays.asList("jpg", "jpeg", "png"));

    /** 最大文件大小：5MB */
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    /**
     * 保存上传文件
     *
     * @return 返回两个值：[0]=访问URL，[1]=本地绝对路径
     */
    public String[] save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "上传文件不能为空");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "文件名非法");
        }
        String ext = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase()
                : "";
        if (!ALLOWED_EXT.contains(ext)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "仅支持 JPG/PNG 格式图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "图片大小不能超过 5MB");
        }

        // 按日期分目录：uploads/2026-09-06/xxx.jpg
        // 注意：transferTo 必须使用绝对路径，否则 SpringBoot 会把相对路径解析到临时目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String baseDir = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
        File baseFile = new File(baseDir).getAbsoluteFile();
        File dirFile = new File(baseFile, dateDir);
        if (!dirFile.exists() && !dirFile.mkdirs()) {
            throw new BusinessException("上传目录创建失败: " + dirFile.getAbsolutePath());
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        File dest = new File(dirFile, fileName);
        try {
            dest = dest.getAbsoluteFile();
            dest.getParentFile().mkdirs();
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("文件保存失败 dest={} size={}", dest.getAbsolutePath(), file.getSize(), e);
            throw new BusinessException("文件上传失败，请稍后重试");
        }
        String accessUrl = urlPrefix + "/" + dateDir + "/" + fileName;
        log.info("上传成功: {} -> {}", dest.getAbsolutePath(), accessUrl);
        return new String[]{accessUrl, dest.getAbsolutePath()};
    }
}
