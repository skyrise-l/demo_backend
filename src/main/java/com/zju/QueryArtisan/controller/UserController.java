package com.zju.QueryArtisan.controller;

import com.zju.QueryArtisan.entity.Response;
import com.zju.QueryArtisan.annotations.*;
import com.zju.QueryArtisan.pojo.User.*;
import com.zju.QueryArtisan.service.UserService;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Response register(@RequestBody RegisterPojo registerPojo){
        return userService.register(registerPojo);
    }

    @PostMapping("/login")
    public Response login(@RequestBody LoginPojo loginPojo){
        return userService.login(loginPojo);
    }

    @PostMapping("/updatePassword")
    @UserLoginToken
    public Response updatePassword(@RequestBody ChangePWPojo changePWPojo){
        return userService.updatePassword(changePWPojo);
    }

}