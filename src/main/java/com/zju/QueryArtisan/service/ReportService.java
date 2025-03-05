package com.zju.QueryArtisan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zju.QueryArtisan.entity.LogicalEdge;
import com.zju.QueryArtisan.entity.LogicalNode;
import com.zju.QueryArtisan.entity.dataStruct.QueryMessage;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.entity.mysqlEntity.QueryHistory;
import com.zju.QueryArtisan.entity.mysqlEntity.User;
import com.zju.QueryArtisan.mysql.QueryHistoryRepository;
import com.zju.QueryArtisan.utils.otherUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class ReportService {
    @Autowired
    private QueryHistoryRepository queryHistoryRepository;

    public Response GetFullChat(String QueryId){
        Optional<QueryHistory> queryHistory = queryHistoryRepository.findById(QueryId);

        if (queryHistory.isEmpty()){
            return Response.fail(1011, "Error QueryId", null);
        }

        return Response.success("success", queryHistory);
    }


    public Response GetLogicalPlan(String queryId){
        String BASE_PATH = "D:\\study\\vldb_demo\\demo\\chat\\config";
        String filePath = BASE_PATH + queryId + ".txt";
        String logical_plan = "";
        Path path = Paths.get(filePath);

        try {
            if (!Files.exists(path)) {
                Response.fail(1111, "File not found", null);
            }

            logical_plan = Files.readString(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Response.fail(1111, "Error reading file:", null);
        }

        return Response.success("success", logical_plan);
    }

    public Response GetCode(String queryId) {
        String BASE_PATH = "D:\\study\\vldb_demo\\demo\\chat\\config\\";
        String filePath = BASE_PATH + queryId + ".py";
        String code = "";
        Path path = Paths.get(filePath);

        try {
            if (!Files.exists(path)) {
                Response.fail(1111, "File not found", null);
            }

            code = Files.readString(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Response.fail(1111, "Error reading file:", null);
        }
        return Response.success("success", code);
    }

    public Response FlowChart_raw(@RequestParam String QueryId){
        String url = "http://127.0.0.1:9000/logical_read";
        String jsonRequest = String.format("{\"queryId\": \"%s\", \"flag\": \"0\"}", QueryId);
        JsonNode response = null;

        try {
            response = otherUtils.sendPostRequest(url, jsonRequest);
        } catch (Exception e) {
            Response.fail(1111, "Error response", null);
        }

        return Response.success("success", response);
    }

    public Response FlowChart_final(@RequestParam String QueryId){
        String url = "http://127.0.0.1:9000/logical_read";
        String jsonRequest = String.format("{\"queryId\": \"%s\", \"flag\": \"1\"}", QueryId);
        JsonNode response = null;

        try {
            response = otherUtils.sendPostRequest(url, jsonRequest);
        } catch (Exception e) {
            Response.fail(1111, "Error response", null);
        }

        return Response.success("success", response);
    }

    public Response read_datasource(){
        String url = "http://127.0.0.1:9000/read_datasource";
        JsonNode response = null;
        try {
            response = otherUtils.sendPostRequest(url, "{}");
        } catch (Exception e) {
            Response.fail(1111, "Error response", null);
        }

        return Response.success("success", response);
    }

    public  Response code_result(String QueryId) {
        String url = "http://127.0.0.1:9000/code_result";
        String jsonRequest = String.format("{\"queryId\": \"%s\"}", QueryId);
        JsonNode response = null;
        try {
            response = otherUtils.sendPostRequest(url, jsonRequest);
        } catch (Exception e) {
            Response.fail(1111, "Error response", null);
        }

        return Response.success("success", response);
    }
}
