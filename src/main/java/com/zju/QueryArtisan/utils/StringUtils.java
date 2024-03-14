package com.zju.QueryArtisan.utils;

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
}
