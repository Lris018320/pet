package com.pet.platform.service;

import com.pet.platform.entity.ChatMessage;

import java.util.List;

/**
 * AI 聊天服务
 */
public interface ChatService {

    /**
     * 发送消息：保存用户消息 -> 调用 AI 生成养护建议 -> 保存并返回 AI 回复
     *
     * @param content 用户提问内容
     * @return AI 回复消息（已持久化）
     */
    ChatMessage send(String content);

    /**
     * 当前用户的历史消息（最近 50 条，时间升序，直接用于前端展示）
     */
    List<ChatMessage> history();

    /**
     * 清空当前用户的对话记录
     *
     * @return 删除条数
     */
    int clearHistory();
}
