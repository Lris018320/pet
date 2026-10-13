package com.pet.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 聊天发送消息请求
 */
@Data
public class ChatSendDTO {

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 500, message = "单次消息不能超过500字")
    private String content;
}
