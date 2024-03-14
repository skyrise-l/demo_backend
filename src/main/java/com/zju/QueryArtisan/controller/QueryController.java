package com.zju.QueryArtisan.controller;

import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.Response;
import com.zju.QueryArtisan.pojo.Query.QueryPojo;
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


    @GetMapping("/Home/findData")
    public Response findData(){
        return queryService.findData();
    }

    @GetMapping("/Home/FlowChart")
    public Response FlowChart(){
        return queryService.FlowChart();
    }

    @GetMapping("/Home/GetCode")
    public Response GetCode(){
        return queryService.GetCode();
    }

    @GetMapping("/Home/FlowChart2")
    public Response FlowChart2(){
        return queryService.FlowChart2();
    }

}
