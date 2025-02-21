package com.zju.QueryArtisan.controller;

import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.dataStruct.QueryData;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.pojo.Query.SettingsPojo;
import com.zju.QueryArtisan.service.QueryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class QueryController {

    @Resource
    private QueryService queryService;

    @UserLoginToken
    @PostMapping("/Home/Query")
    public Response StartQuery(@RequestBody QueryData queryData){
        return queryService.StartQuery(queryData);
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
        return queryService.Settings(settingsPojo);
    }

    @UserLoginToken
    @PostMapping("/Home/batchQuery")
    public Response batchQuery(@RequestParam("queries") String queries, @RequestParam(value = "files", required = false) MultipartFile[] files){
        if (files == null || files.length == 0) {
            return queryService.batchQuery(queries, null);
        } else {
            return queryService.batchQuery(queries, files);
        }
    }

    @UserLoginToken
    @GetMapping("/Home/GetHistoryQueries")
    public Response GetHistoryQueries(){
        return queryService.GetHistoryQueries();
    }

    @UserLoginToken
    @GetMapping("/Home/DeleteHistoryQuery")
    public Response DeleteHistoryQuery(@RequestParam Long QueryId){
        return queryService.DeleteHistoryQuery(QueryId);
    }

}
