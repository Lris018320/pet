package com.pet.platform.ai;

/**
 * AI 识别提供方接口
 * 通过 application.yml 中 pet.ai.provider 切换实现
 */
public interface AiRecognitionProvider {

    /**
     * 识别图片
     *
     * @param imageLocalPath 图片本地路径（uploads 目录下）
     * @return 识别结果
     */
    RecognitionResult recognize(String imageLocalPath);

    /**
     * 当前 Provider 名称（mock / baidu）
     */
    String name();
}
