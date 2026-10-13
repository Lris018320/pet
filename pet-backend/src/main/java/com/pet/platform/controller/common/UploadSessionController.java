package com.pet.platform.controller.common;

import com.pet.platform.common.BusinessException;
import com.pet.platform.common.Result;
import com.pet.platform.util.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扫码上传会话控制器
 * 电脑端生成带会话ID的二维码 -> 手机扫码打开H5页拍照上传 -> 电脑端轮询取图
 * 手机端通常无登录态，故本接口在白名单中放行；会话10分钟过期，内存存储重启失效
 */
@Slf4j
@RestController
@RequestMapping("/upload-session")
public class UploadSessionController {

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /** 会话有效期：10 分钟 */
    private static final long TTL_MILLIS = 10 * 60 * 1000L;

    /** 会话存储：sessionId -> 会话信息 */
    private static final Map<String, SessionInfo> SESSIONS = new ConcurrentHashMap<>();

    private static class SessionInfo {
        volatile String imageUrl;
        final long createTime = System.currentTimeMillis();
    }

    /** 创建会话，返回 sessionId 供生成二维码 */
    @PostMapping
    public Result<Map<String, Object>> create() {
        cleanup();
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        SESSIONS.put(sessionId, new SessionInfo());
        Map<String, Object> data = new HashMap<>(2);
        data.put("sessionId", sessionId);
        data.put("expireSeconds", TTL_MILLIS / 1000);
        return Result.success(data);
    }

    /** 查询会话状态（电脑端轮询） */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> status(@PathVariable String id) {
        SessionInfo info = getValid(id);
        Map<String, Object> data = new HashMap<>(2);
        if (info.imageUrl != null) {
            data.put("status", "done");
            data.put("imageUrl", info.imageUrl);
        } else {
            data.put("status", "waiting");
        }
        return Result.success(data);
    }

    /** 手机端上传照片并绑定会话 */
    @PostMapping("/{id}/photo")
    public Result<Map<String, String>> photo(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        SessionInfo info = getValid(id);
        String[] saved = fileUploadUtil.save(file);
        info.imageUrl = saved[0];
        log.info("扫码上传成功 session={} url={}", id, saved[0]);
        Map<String, String> data = new HashMap<>(1);
        data.put("imageUrl", saved[0]);
        return Result.success(data);
    }

    /** 校验会话存在且未过期 */
    private SessionInfo getValid(String id) {
        SessionInfo info = SESSIONS.get(id);
        if (info == null || System.currentTimeMillis() - info.createTime > TTL_MILLIS) {
            SESSIONS.remove(id);
            throw new BusinessException("会话不存在或已过期，请重新扫码");
        }
        return info;
    }

    /** 惰性清理过期会话 */
    private void cleanup() {
        long now = System.currentTimeMillis();
        SESSIONS.entrySet().removeIf(e -> now - e.getValue().createTime > TTL_MILLIS);
    }
}
