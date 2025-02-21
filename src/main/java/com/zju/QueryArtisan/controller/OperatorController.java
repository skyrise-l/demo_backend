package com.zju.QueryArtisan.controller;

import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.pojo.Operator.AddOperatorPojo;
import com.zju.QueryArtisan.pojo.Operator.AddPromptPojo;
import com.zju.QueryArtisan.pojo.Operator.EditOperatorPojo;
import com.zju.QueryArtisan.service.OperatorService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
public class OperatorController {
    @Resource
    private OperatorService operatorService;

    @UserLoginToken
    @GetMapping("/Home/ReadOperator")
    public Response ReadOperator(@RequestParam boolean isStandard){
        return operatorService.ReadOperator(isStandard);
    }

    @UserLoginToken
    @PostMapping("/Home/AddOperator")
    public Response AddOperator(@RequestBody AddOperatorPojo addOperatorPojo){
        return operatorService.AddOperator(addOperatorPojo);
    }
    @UserLoginToken
    @PostMapping("/Home/EditOperator")
    public Response EditOperator(@RequestBody EditOperatorPojo editOperatorPojo){
        return operatorService.EditOperator(editOperatorPojo);
    }

    @UserLoginToken
    @GetMapping("/Home/ActiveOperator")
    public Response ActiveOperator(@RequestParam boolean isStandard, @RequestParam Long operatorId){
        return operatorService.ActiveOperator(isStandard, operatorId);
    }

    @UserLoginToken
    @GetMapping("/Home/DeleteOperator")
    public Response DeleteOperator(@RequestParam Long operatorId){
        return operatorService.DeleteOperator(operatorId);
    }

    @UserLoginToken
    @PostMapping("/Home/AddPrompt")
    public Response AddPrompt(@RequestBody AddPromptPojo addPromptPojo){
        return operatorService.AddPrompt(addPromptPojo);
    }


}
