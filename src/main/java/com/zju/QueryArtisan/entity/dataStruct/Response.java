package com.zju.QueryArtisan.entity.dataStruct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private int code;
    private String message;
    private Object data;

    public static Response success(Object data) {
        return success("success", data);
    }

    public static Response success(String message, Object data) {
        return new Response(0, message, data);
    }

    public static Response fail(int code, String message, Object data) {
        return new Response(code, message, data);
    }

    public static Response userNotExist = fail(1001, "用户名不存在", null);

    public static Response userNotLogin = fail(3001, "user not login", null);
    public static Response tokenError = fail(3002, "token error", null);

}

