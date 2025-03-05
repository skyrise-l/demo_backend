package com.zju.QueryArtisan.controller;

import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.service.QueryService;
import com.zju.QueryArtisan.service.ReportService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class ReportController {
    @Resource
    private ReportService reportService;

    @UserLoginToken
    @GetMapping("/api/Report/GetFullChat")
    public Response GetFullChat(@RequestParam String QueryId){
        return reportService.GetFullChat(QueryId);
    }

    @UserLoginToken
    @GetMapping("/api/Report/GetLogicalPlan")
    public Response GetLogicalPlan(@RequestParam String QueryId) {
        return reportService.GetLogicalPlan(QueryId);
    }

    @UserLoginToken
    @GetMapping("/api/Home/GetCode")
    public Response GetCode(@RequestParam String QueryId){
        return reportService.GetCode(QueryId);
    }

    @UserLoginToken
    @GetMapping("/api/Home/FlowChart_raw")
    public Response FlowChart_raw(@RequestParam String QueryId){
        return reportService.FlowChart_raw(QueryId);
    }

    @UserLoginToken
    @GetMapping("/api/Home/FlowChart_final")
    public Response FlowChart_final(@RequestParam String QueryId){
        return reportService.FlowChart_final(QueryId);
    }

    @UserLoginToken
    @GetMapping("/api/Report/read_datasource")
    public Response read_datasource(){
        return reportService.read_datasource();
    }

    @UserLoginToken
    @GetMapping("/api/Report/code_result")
    public Response code_result(@RequestParam String QueryId){
        return reportService.code_result(QueryId);
    }
}
