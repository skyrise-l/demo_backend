package com.zju.QueryArtisan.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.entity.mysqlEntity.User;
import com.zju.QueryArtisan.mysql.UserRepository;
import com.zju.QueryArtisan.utils.Pair;
import com.zju.QueryArtisan.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TokenService {
    @Autowired
    private UserRepository userRepository;

    public String getToken(User user) {
        Date start = new Date();
        long currentTime = System.currentTimeMillis() +  24 * 60 * 60 * 1000;//一天有效时间
        Date end = new Date(currentTime);
        String token = "";

        token = JWT.create().withAudience(String.valueOf(user.getId())).withIssuedAt(start).withExpiresAt(end)
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }

    public Pair<User, Response> checkToken() {
        String token;
        Long userId;
        try {
            token = TokenUtil.getToken();
        } catch (Exception e) {
            log.info("tokenError :" + e.getMessage());
            return new Pair<>(null, Response.tokenError);
        }

        if (TokenUtil.getUserId() == null) {
            return new Pair<>(null, Response.userNotLogin);
        }
        userId = Long.valueOf(TokenUtil.getUserId());
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            boolean verified = TokenUtil.verifyToken(token, user.getPassword(), user.getUsername());

            if (!verified) {
                log.info("tokenError : token验证失败");
                return new Pair<>(null, Response.tokenError);
            }

            return new Pair<>(user, null);
        } else {
            log.info("tokenError : 用户不存在");
            return new Pair<>(null, Response.tokenError);
        }
    }
}