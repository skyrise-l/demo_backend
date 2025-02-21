package com.zju.QueryArtisan.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zju.QueryArtisan.entity.dataStruct.QueryMessage;

import java.util.List;
import java.util.UUID;

/**
 * 字符串操作工具类
 */
public class StringUtils {
    /**
     * 将输入字符串转变为trim之后的字符串
     * 如果字符串为空，则输出null
     * @param src 原始字符串
     * @return 输出结果
     */
    public static String getTrimString(String src){
        String ret;
        if (src == null || src.length() == 0){
            return null;
        }
        ret = src.trim();
        if (ret.length() == 0){
            return null;
        }
        return ret;
    }

    public static boolean isNullOrEmpty(String src){
        if (src == null || src.length() == 0){
            return true;
        }
        String trim = src.trim();
        return trim.length() == 0;
    }

    public static String generateRandomHash() {
        // 使用UUID生成随机的哈希值
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String extractFirstCharacters(String input, int numCharacters) {
        // 如果输入字符串长度小于等于要提取的字符数，则直接返回整个字符串
        if (input.length() <= numCharacters) {
            return input;
        }

        // 否则，提取前numCharacters个字符
        return input.substring(0, numCharacters);
    }

    public static String convertMessagesToJson(List<QueryMessage> messages) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(messages);
        } catch (Exception e) {
            throw new RuntimeException("Error converting messages to JSON", e);
        }
    }
}
