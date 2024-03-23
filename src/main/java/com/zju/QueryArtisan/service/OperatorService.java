package com.zju.QueryArtisan.service;

import com.zju.QueryArtisan.entity.CustomPrompt;
import com.zju.QueryArtisan.entity.Standard_operator;
import com.zju.QueryArtisan.entity.Response;
import com.zju.QueryArtisan.entity.Customer_operator;
import com.zju.QueryArtisan.mysql.CustomPromptRepository;
import com.zju.QueryArtisan.mysql.Customer_operatorRepository;
import com.zju.QueryArtisan.mysql.Standard_operatorRepository;
import com.zju.QueryArtisan.pojo.Operator.AddOperatorPojo;
import com.zju.QueryArtisan.pojo.Operator.AddPromptPojo;
import com.zju.QueryArtisan.pojo.Operator.EditOperatorPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zju.QueryArtisan.utils.otherUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OperatorService {

    @Autowired
    private Standard_operatorRepository standard_operatorRepository;

    @Autowired
    private Customer_operatorRepository customer_operatorRepository;

    @Autowired
    private CustomPromptRepository customPromptRepository;

    public Response ReadOperator(boolean isStandard){
        if (isStandard){
            List<Standard_operator> results = standard_operatorRepository.findAll();
            return Response.success("success", results);
        }
        else {
            List<Customer_operator> results = customer_operatorRepository.findAll();
            return Response.success("success", results);
        }
    }

    public Response AddOperator(AddOperatorPojo addOperatorPojo){
        if (!otherUtils.areAllFieldsNotNull(addOperatorPojo)) {
            return Response.fail(1008, "Missing value", null);
        }
        Customer_operator operator = new Customer_operator();
        operator.setOperator(addOperatorPojo.getOperator());
        operator.setFormat(addOperatorPojo.getFormat());
        operator.setDescription(addOperatorPojo.getDescription());
        operator.setCodeExample(addOperatorPojo.getCodeExample());
        operator.setStatus(1L);
        customer_operatorRepository.save(operator);
        return Response.success("Add operator success");
    }

    public Response ActiveOperator(boolean isStandard, Long operatorId){
        String message = "";
        if (isStandard){
            Optional<Standard_operator> target = standard_operatorRepository.findById(operatorId);
            if (target.isEmpty()){
                return Response.fail(1009, "Error operatorId", null);
            }
            Standard_operator operator = target.get();
            if (operator.getStatus() == 0){
                operator.setStatus(1L);
                message = "Active " + operator.getOperator() + " Operator success";
            } else{
                operator.setStatus(0L);
                message = "InActive " + operator.getOperator() + " Operator success";
            }

            standard_operatorRepository.save(operator);

        }
        else {
            Optional<Customer_operator> target = customer_operatorRepository.findById(operatorId);
            if (target.isEmpty()){
                return Response.fail(1009, "Error operatorId", null);
            }
            Customer_operator operator = target.get();
            if (operator.getStatus() == 0){
                operator.setStatus(1L);
                message = "Active " + operator.getOperator() + " Operator success";
            } else{
                operator.setStatus(0L);
                message = "InActive " + operator.getOperator() + " Operator success";
            }
            customer_operatorRepository.save(operator);

        }
        return Response.success(message);
    }

    public Response DeleteOperator(Long operatorId){
        Optional<Customer_operator> target = customer_operatorRepository.findById(operatorId);
        if (target.isEmpty()){
            return Response.fail(1010, "Error customer operatorId", null);
        }
        Customer_operator operator = target.get();
        customer_operatorRepository.delete(operator);
        String message = "Delete " + operator.getOperator() + " Operator success";
        return Response.success(message);
    }

    public Response EditOperator(EditOperatorPojo editOperatorPojo){
        Optional<Customer_operator> target = customer_operatorRepository.findById(editOperatorPojo.getId());
        if (target.isEmpty()){
            return Response.fail(1010, "Error customer operatorId", null);
        }
        Customer_operator operator = target.get();
        operator.setOperator(editOperatorPojo.getOperator());
        operator.setOperator(editOperatorPojo.getOperator());
        operator.setFormat(editOperatorPojo.getFormat());
        operator.setDescription(editOperatorPojo.getDescription());
        operator.setCodeExample(editOperatorPojo.getCodeExample());
        customer_operatorRepository.save(operator);
        String message = "Edit " + operator.getOperator() + " Operator success";
        return Response.success(message);
    }

    public Response AddPrompt(AddPromptPojo addPromptPojo){

        String message = "Add prompt success";
        CustomPrompt customPrompt = new CustomPrompt();
        customPrompt.setPrompt(addPromptPojo.getPrompt());
        customPrompt.setDescription(addPromptPojo.getDescription());

        customPromptRepository.save(customPrompt);

        return Response.success(message);
    }
}


