package com.zju.QueryArtisan.entity.dataStruct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryData {
    private Long id;
    private String title; // 对话标题
    private String hashValue; // 对话的hash值，用于验证或快速检索
    private List<QueryMessage> messages;
}
