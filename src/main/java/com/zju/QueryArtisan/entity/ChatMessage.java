package com.zju.QueryArtisan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage  {
    private String author; // "user" 或 "system" 来区分发送者
    private String message; // 消息内容
    private long timestamp; // 消息发送的时间戳
}
