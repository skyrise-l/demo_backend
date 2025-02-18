package com.zju.QueryArtisan.controller;

import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.Response;
import com.zju.QueryArtisan.pojo.Query.QueryPojo;
import com.zju.QueryArtisan.pojo.Query.SettingsPojo;
import com.zju.QueryArtisan.service.QueryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class QueryController {

    @Resource
    private QueryService queryService;

    @UserLoginToken
    @PostMapping("/Home/Query")
    public Response StartQuery(@RequestBody QueryPojo queryPojo){
        return queryService.StartQuery(queryPojo);
    }

    @UserLoginToken
    @GetMapping("/Home/findData")
    public Response findData(){
        return queryService.findData();
    }
    @UserLoginToken
    @GetMapping("/Home/FlowChart_raw")
    public Response FlowChart_raw(){
        return queryService.FlowChart_raw();
    }
    @UserLoginToken
    @GetMapping("/Home/GetCode")
    public Response GetCode(){
        return queryService.GetCode();
    }
    @UserLoginToken
    @GetMapping("/Home/FlowChart_final")
    public Response FlowChart_final(){
        return queryService.FlowChart_final();
    }

    @UserLoginToken
    @GetMapping("/Home/GetTitles")
    public Response GetTitles(){
        return queryService.GetTitles();
    }
    @UserLoginToken
    @GetMapping("/Home/GetChat")
    public Response GetChat(@RequestParam Long QueryId){
        return queryService.GetChat(QueryId);
    }

    @UserLoginToken
    @GetMapping("/Home/GetPrompt")
    public Response GetPrompt(){
        return queryService.GetPrompt();
    }

    @UserLoginToken
    @GetMapping("/Home/GetResults")
    public Response GetResults(){
        return queryService.GetResults();
    }

    @UserLoginToken
    @PostMapping("/Home/Setting")
    public Response Settings(@RequestBody SettingsPojo settingsPojo){
        return queryService.Settings();
    }
}
