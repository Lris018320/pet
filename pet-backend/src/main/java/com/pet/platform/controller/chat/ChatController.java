package com.pet.platform.controller.chat;

import com.pet.platform.common.Result;
import com.pet.platform.dto.ChatSendDTO;
import com.pet.platform.entity.ChatMessage;
import com.pet.platform.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * AI 养护问答控制器
 * 提供发送消息（返回 AI 养护建议）、历史记录、清空对话 3 个接口
 * 路径 /chat/** 需登录访问（不在拦截器白名单中）
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 发送消息，返回 AI 回复
     */
    @PostMapping("/send")
    public Result<ChatMessage> send(@RequestBody @Valid ChatSendDTO dto) {
        return Result.success(chatService.send(dto.getContent()));
    }

    /**
     * 我的聊天历史（最近 50 条，时间升序）
     */
    @GetMapping("/history")
    public Result<List<ChatMessage>> history() {
        return Result.success(chatService.history());
    }

    /**
     * 清空我的对话记录
     */
    @DeleteMapping("/history")
    public Result<Integer> clearHistory() {
        return Result.success(chatService.clearHistory());
    }
}
