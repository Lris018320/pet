package com.pet.platform.ai;

import com.pet.platform.entity.ChatMessage;

import java.util.List;

/**
 * AI 聊天提供方接口
 * 通过 application.yml 中 pet.ai.chat.provider 切换实现（mock / 真实大模型）
 */
public interface ChatAiProvider {

    /**
     * 根据用户提问生成养护建议回复
     *
     * @param userMessage 用户最新消息
     * @param history     近期对话历史（时间升序，不含最新消息），供多轮对话使用
     * @return AI 回复文本
     */
    String reply(String userMessage, List<ChatMessage> history);

    /**
     * 当前 Provider 名称（mock / openai ...）
     */
    String name();
}
