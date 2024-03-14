package com.zju.QueryArtisan.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class TokenUtil {
    public static String getToken() {
        HttpServletRequest httpServletRequest = getRequest();
        if (httpServletRequest == null){
            return null;
        }
        return httpServletRequest.getHeader("token");// 从 http 请求头中取出 token
    }

    public static String getTokenContent() {
//        if (token == null){
//            return null;
//        }
//        return JWT.decode(token).getAudience().get(0);
        return getToken();// 从 http 请求头中取出 token
    }

    public static String getUserId(){
        String token = getToken();
        if (token == null){
            return null;
        }
        return JWT.decode(token).getAudience().get(0);
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        return requestAttributes == null ? null : (HttpServletRequest) requestAttributes.getRequest();
    }

    public static boolean verifyToken(String token, String hmacKey, String username){
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(hmacKey)).build();

        try {
            jwtVerifier.verify(token);
            return true;
        }catch (JWTVerificationException e){
            log.info("token验证不通过, 用户名（" + username + "） token 值为 " + token);
            return false;
        }
    }
}