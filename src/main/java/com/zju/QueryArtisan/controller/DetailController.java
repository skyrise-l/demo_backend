package com.zju.QueryArtisan.controller;

import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.Response;
import com.zju.QueryArtisan.service.DetailService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class DetailController {
    @Resource
    private DetailService detailService;

    @UserLoginToken
    @GetMapping("/Detail/Main/{queryId}")
    public Response mainQuery(@PathVariable int queryId) {
        return detailService.mainQuery(queryId);
    }

    @UserLoginToken
    @GetMapping("/Detail/Table/{queryId}")
    public Response tableQuery(@PathVariable int queryId) {
        return detailService.tableQuery(queryId);
    }

    @UserLoginToken
    @GetMapping("/Detail/Json/{queryId}")
    public Response jsonQuery(@PathVariable int queryId) {
        return detailService.jsonQuery(queryId);
    }

    @UserLoginToken
    @GetMapping("/Detail/Graph/{queryId}")
    public Response graphQuery(@PathVariable int queryId) {
        return detailService.graphQuery(queryId);
    }

}
