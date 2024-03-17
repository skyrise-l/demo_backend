package com.zju.QueryArtisan.service;

import com.zju.QueryArtisan.entity.Response;
import com.zju.QueryArtisan.entity.User;
import com.zju.QueryArtisan.pojo.User.*;
import com.zju.QueryArtisan.utils.*;
import com.zju.QueryArtisan.mysql.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;

@Slf4j
@Service
public class UserService {

    @Resource
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public Response login(LoginPojo loginPojo){
        User user = userRepository.findByUsername(loginPojo.getUsername());
        if (user == null){
            return Response.fail(1001, "Nonexistent account", null);
        }

        String passwordHashHex = PasswordUtil.passwordHash(loginPojo.getPassword(), user.getSalt());

        if (!user.getPassword().equals(passwordHashHex)){
            return Response.fail(1002, "password error", null);
        }

        userRepository.save(user);

        String token = tokenService.getToken(user);
        Map<String, String> resp = new HashMap<>();
        resp.put("token", token);

        return Response.success(resp);
    }


    public Response register(RegisterPojo registerPojo){

        if (registerPojo.getUsername() == null || registerPojo.getPassword() == null){
            return Response.fail(1003, "Username and password are required", null);
        }
        User user = userRepository.findByUsername(registerPojo.getUsername());
        if (user != null){
            return Response.fail(1004, "Username already exists", null);
        }

        user = new User();

        String salt = PasswordUtil.getRandSalt();
        String passwordHashHex = PasswordUtil.passwordHash(registerPojo.getPassword(), salt);
        user.setUsername(registerPojo.getUsername());
        user.setSalt(salt);
        user.setPassword(passwordHashHex);


        userRepository.save(user);

        Map<String, String> resp = new HashMap<>();
        resp.put("username", registerPojo.getUsername());

        return Response.success(resp);
    }

    public Response updatePassword(ChangePWPojo changePWPojo){
        Pair<User, Response> pair = tokenService.checkToken();
        Response check = pair.getValue();
        if (check != null){
            log.info("token error: " + TokenUtil.getTokenContent());
            log.info("msg: " + check.getMessage());
            return Response.fail(1000,"用户验证不通过",null);
        }

        User user = pair.getKey();

        String pw = StringUtils.getTrimString(changePWPojo.getNewpassword());
        if (pw == null){
            String message = "pw为空";
            log.info(message);
            return Response.fail(1005, message, null);
        }

        String passwordHashHex = PasswordUtil.passwordHash(changePWPojo.getOldpassword(), user.getSalt());

        if (!user.getPassword().equals(passwordHashHex)){
            return Response.fail(1006, "旧密码错误", null);
        }

        String newPW = PasswordUtil.passwordHash(pw, user.getSalt());
        user.setPassword(newPW);
        userRepository.save(user);

        String successMessage = String.format("userId(%s)修改密码成功", user.getId());
        log.info(successMessage);
        return Response.success(successMessage, null);
    }

}
