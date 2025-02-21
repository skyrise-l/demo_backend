package com.zju.QueryArtisan.Interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zju.QueryArtisan.annotations.PassToken;
import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.entity.mysqlEntity.User;
import com.zju.QueryArtisan.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;


@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Resource
    private UserService userService;

    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String token = request.getHeader("token");// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)handler;
        Method method = handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassToken.class)) {
            PassToken passToken = method.getAnnotation(PassToken.class);
            if (passToken.required()) {
                return true;
            }
        }

        //检查有没有需要用户权限的注解
        ObjectMapper mapper = new ObjectMapper();
        String resp = mapper.writeValueAsString(Response.userNotLogin);
        //检查有没有需要用户权限的注解
        if (method.isAnnotationPresent(UserLoginToken.class)) {
            UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
            if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    response.addHeader("Content-Type", "application/json;charset=UTF-8");
                    PrintWriter writer = response.getWriter();
                    writer.print(resp);
                    return false;
                }
                // 获取 token 中的 user id
                Long userId;
                try {
                    userId = Long.valueOf(JWT.decode(token).getAudience().get(0));
                } catch (JWTDecodeException j) {
                    response.addHeader("Content-Type", "application/json;charset=UTF-8");
                    PrintWriter writer = response.getWriter();
                    writer.print(resp);
                    return false;
                }
                User user = userService.findById(userId);
                if (user == null) {
                    response.addHeader("Content-Type", "application/json;charset=UTF-8");
                    PrintWriter writer = response.getWriter();
                    writer.print(resp);
                    return false;
                }
                // 验证 token
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
                try {
                    jwtVerifier.verify(token);
                } catch (JWTVerificationException e) {
                    response.addHeader("Content-Type", "application/json;charset=UTF-8");
                    PrintWriter writer = response.getWriter();
                    writer.print(resp);
                    return false;
                }


                return true;
            }
        }
        return true;
    }
}
