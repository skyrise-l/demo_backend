package com.zju.QueryArtisan.controller;

import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.dataStruct.QueryData;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.pojo.Query.SettingsPojo;
import com.zju.QueryArtisan.pojo.Query.TaskSettingPojo;
import com.zju.QueryArtisan.service.QueryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;


@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class QueryController {

    @Resource
    private QueryService queryService;

    @UserLoginToken
    @PostMapping("/api/Home/Query")
    public Response StartQuery(@RequestBody QueryData queryData){
        return queryService.StartQuery(queryData);
    }

    @UserLoginToken
    @GetMapping("/api/Home/findData")
    public Response findData(){
        return queryService.findData();
    }

    @UserLoginToken
    @GetMapping("/api/Home/GetTitles")
    public Response GetTitles(){
        return queryService.GetTitles();
    }

    @UserLoginToken
    @GetMapping("/api/Home/GetChat")
    public Response GetChat(@RequestParam String QueryId){
        return queryService.GetChat(QueryId);
    }

    @UserLoginToken
    @GetMapping("/api/Home/GetResults")
    public Response GetResults(){
        return queryService.GetResults();
    }

    @UserLoginToken
    @PostMapping("/api/Home/Setting")
    public Response Settings(@RequestBody SettingsPojo settingsPojo){
        return queryService.Settings(settingsPojo);
    }

    @UserLoginToken
    @PostMapping("/api/Home/batchQuery")
    public Response batchQuery(@RequestParam("queries") String queries, @RequestParam(value = "files", required = false) MultipartFile[] files){
        if (files == null || files.length == 0) {
            return queryService.batchQuery(queries, null);
        } else {
            return queryService.batchQuery(queries, files);
        }
    }

    @UserLoginToken
    @GetMapping("/api/Home/GetHistoryQueries")
    public Response GetHistoryQueries(){
        return queryService.GetHistoryQueries();
    }

    @UserLoginToken
    @GetMapping("/api/Home/DeleteHistoryQuery")
    public Response DeleteHistoryQuery(@RequestParam String QueryId){
        return queryService.DeleteHistoryQuery(QueryId);
    }

    @UserLoginToken
    @PostMapping("/api/Home/GetQuerySuggestions")
    public Response GetQuerySuggestions(@RequestParam("query") String query){
        return queryService.GetQuerySuggestions(query);
    }

    @UserLoginToken
    @GetMapping("/api/Home/GetNewTaskSetting")
    public Response GetNewTaskSetting(){
        return queryService.GetNewTaskSetting();
    }

    @UserLoginToken
    @GetMapping("/api/Home/DeleteNewTaskSetting")
    public Response DeleteNewTaskSetting(){
        return queryService.DeleteNewTaskSetting();
    }

    @UserLoginToken
    @PostMapping("/api/Home/GetTaskRecommend")
    public Response GetTaskRecommend(@RequestParam("query") String query){
        return queryService.GetTaskRecommend(query);
    }

    @UserLoginToken
    @PostMapping("/api/Home/UpdateRecommend")
    public Response UpdateRecommend(@RequestBody List<TaskSettingPojo> taskSettingPojos){
        return queryService.UpdateRecommend(taskSettingPojos);
    }

}
