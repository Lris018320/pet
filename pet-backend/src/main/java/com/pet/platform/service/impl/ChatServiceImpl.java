package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.platform.ai.ChatAiProvider;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.entity.ChatMessage;
import com.pet.platform.interceptor.UserContext;
import com.pet.platform.mapper.ChatMessageMapper;
import com.pet.platform.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * AI 聊天服务实现
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    /** 历史消息返回条数 */
    private static final int HISTORY_LIMIT = 50;
    /** 传给 AI 的近期上下文条数 */
    private static final int CONTEXT_LIMIT = 10;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatAiProvider chatAiProvider;

    @Override
    public ChatMessage send(String content) {
        Long userId = currentUserId();
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "消息内容不能为空");
        }
        String question = content.trim();
        if (question.length() > 500) {
            throw new BusinessException(ErrorCode.PARAM_INVALID.getCode(), "单次消息不能超过500字");
        }

        // 1. 保存用户消息
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserId(userId);
        userMsg.setRole("user");
        userMsg.setContent(question);
        userMsg.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(userMsg);

        // 2. 取近期对话作为上下文（时间倒序取 N 条后反转为升序）
        List<ChatMessage> recent = chatMessageMapper.selectList(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getUserId, userId)
                        .lt(ChatMessage::getId, userMsg.getId())
                        .orderByDesc(ChatMessage::getId)
                        .last("LIMIT " + CONTEXT_LIMIT)
        );
        Collections.reverse(recent);

        // 3. 调用 AI 生成回复
        String replyText;
        try {
            replyText = chatAiProvider.reply(question, recent);
        } catch (Exception e) {
            log.error("AI 回复生成失败 userId={}", userId, e);
            replyText = "小助手暂时开小差了，请稍后再试 🐾";
        }
        if (!StringUtils.hasText(replyText)) {
            replyText = "这个问题我还在学习中，可以换个说法再问我一次～";
        }

        // 4. 保存 AI 回复
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setRole("assistant");
        aiMsg.setContent(replyText);
        aiMsg.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(aiMsg);

        log.info("聊天回复完成 userId={} provider={} 用户消息id={} 回复id={}",
                userId, chatAiProvider.name(), userMsg.getId(), aiMsg.getId());
        return aiMsg;
    }

    @Override
    public List<ChatMessage> history() {
        Long userId = currentUserId();
        // 取最近 LIMIT 条（倒序），再反转为时间升序返回
        List<ChatMessage> messages = chatMessageMapper.selectList(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getUserId, userId)
                        .orderByDesc(ChatMessage::getId)
                        .last("LIMIT " + HISTORY_LIMIT)
        );
        Collections.reverse(messages);
        return messages;
    }

    @Override
    public int clearHistory() {
        Long userId = currentUserId();
        int count = chatMessageMapper.delete(
                new LambdaQueryWrapper<ChatMessage>().eq(ChatMessage::getUserId, userId)
        );
        log.info("清空聊天记录 userId={} count={}", userId, count);
        return count;
    }

    private Long currentUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return userId;
    }
}
