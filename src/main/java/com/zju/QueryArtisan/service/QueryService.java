package com.zju.QueryArtisan.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zju.QueryArtisan.annotations.UserLoginToken;
import com.zju.QueryArtisan.entity.*;
import com.zju.QueryArtisan.entity.mysqlEntity.BatchQueries;
import com.zju.QueryArtisan.entity.dataStruct.QueryData;
import com.zju.QueryArtisan.entity.dataStruct.QueryMessage;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.entity.mysqlEntity.*;
import com.zju.QueryArtisan.mysql.*;
import com.zju.QueryArtisan.pojo.Query.SettingsPojo;
import com.zju.QueryArtisan.pojo.Query.TaskSettingPojo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zju.QueryArtisan.utils.otherUtils;
import com.zju.QueryArtisan.utils.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.UUID;

import static com.zju.QueryArtisan.utils.otherUtils.objectMapper;

@Slf4j
@Service
public class QueryService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MysqlMessageRepository mysqlMessageRepository;

    @Autowired
    private CustomPromptRepository customPromptRepository;

    @Autowired
    private BatchQueriesRepository batchQueriesRepository;

    @Autowired
    private QueryHistoryRepository queryHistoryRepository;

    @Autowired
    private taskRecommendRepository taskRecommendRepository;

    //主查询模块
    public Response StartQuery(QueryData queryData){
        String model = null;
        String dataSource = null;
        Long maxToken = 4096L;

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            model = properties.getProperty("model");
            dataSource = properties.getProperty("dataSource");
            maxToken = Long.parseLong(properties.getProperty("max_token"));
        } catch (Exception e) {
            Response.fail(1056, "Error model properties", null);
        }

        try {
            String newId = queryData.getId();
            if (Objects.equals(newId, "0x0")){
                newId = UUID.randomUUID().toString();
            }
            queryData.setId(newId);
            String title = queryData.getTitle();
            if (Objects.equals(title, "New Title")){
                title =  queryData.getMessages().get(queryData.getMessages().size() - 1).getMessage().substring(0, 8);
            }
            queryData.setTitle(title);
            String hashValue = queryData.getHashValue();
            if (hashValue.isEmpty()){
                hashValue = StringUtils.generateRandomHash();
            }
            queryData.setHashValue(hashValue);

            String user_query =  queryData.getMessages().get(queryData.getMessages().size() - 1).getMessage();

            MysqlMessage mysqlMessage = new MysqlMessage(
                    newId,
                    title,
                    hashValue,
                    StringUtils.convertMessagesToJson(queryData.getMessages()),
                    model,
                    dataSource,
                    maxToken
            );
            String url = "http://127.0.0.1:9000/query";

            long startTime = System.nanoTime();
            JsonNode response = otherUtils.sendPostRequest(url, mysqlMessage.getMessages());
            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1_000_000_000.0; // 纳秒转秒，保留小数

            String responseMessage = null;
            String query_id = UUID.randomUUID().toString();
            if (response != null) {
                responseMessage = response.path("response").asText() + query_id;
            }

            String decomposeQuery = String.valueOf(response.path("decompose_query"));
            String decomposeAnalysis = String.valueOf(response.path("decompose_analysis"));

            QueryMessage systemMessage = new QueryMessage("system", responseMessage, System.currentTimeMillis());

            queryData.getMessages().add(systemMessage);

            mysqlMessage.setMessages(StringUtils.convertMessagesToJson(queryData.getMessages()));
            mysqlMessageRepository.save(mysqlMessage);

            QueryHistory queryHistory = new QueryHistory();
            queryHistory.setQuery(user_query);
            queryHistory.setDecomposeQueryArray(decomposeQuery);
            queryHistory.setDecomposeAnalysisArray(decomposeAnalysis);
            queryHistory.setExecuteTime(duration);
            queryHistory.setId(query_id);
            queryHistoryRepository.save(queryHistory);
        } catch (Exception e) {
            return Response.fail(1073, "Error processing query", null);
        }
        return Response.success("success", queryData);
    }

    // 批量查询模块
    public Response batchQuery(String queries, MultipartFile[] files){
        String resultUrl = null;
        String upload_path = null;
        try {
            if (files != null && files.length > 0) {
                upload_path = "D:\\数据库\\vldb_demo\\demo\\QueryArtisan\\src\\main\\resources\\upload_report\\" + System.currentTimeMillis();
                otherUtils.saveFiles(files, upload_path);
            }

            String url = "http://127.0.0.1:9000/batchquery";

            String jsonPayload = "{\"query\": \"" + queries + "\", \"filepath\": \"" + upload_path + "\"}";

            JsonNode response = otherUtils.sendPostRequest(url, jsonPayload);

            resultUrl = response.path("response").asText();

            BatchQueries batchQueries = new BatchQueries();
            batchQueries.setQueries(queries);
            batchQueries.setFilePath(upload_path);
            batchQueriesRepository.saveAndFlush(batchQueries);

            return Response.success("success", resultUrl);
        } catch (Exception e){
            otherUtils.sleep(3000);
            BatchQueries batchQueries = new BatchQueries();
            batchQueries.setQueries(queries);
            batchQueries.setFilePath(upload_path);
            batchQueriesRepository.saveAndFlush(batchQueries);
            resultUrl = "http://127.0.0.1:9000";
            return Response.success("success", resultUrl);
            //return Response.fail(1074, "Error processing batch query", null);
        }
    }

    // 查询复用模块
    public Response GetHistoryQueries() {
        // 获取所有查询历史
        List<QueryHistory> queryHistory = queryHistoryRepository.findAll();

        // 获取最多200条记录
        int limit = Math.min(queryHistory.size(), 200);
        List<QueryHistory> limitedQueryHistory = queryHistory.subList(0, limit);

        // 返回限制后的记录
        return Response.success("success", limitedQueryHistory);
    }

    public Response DeleteHistoryQuery(String QueryId) {
        QueryHistory queryHistory = queryHistoryRepository.findById(QueryId).orElse(null);
        if (queryHistory != null) {
            queryHistoryRepository.delete(queryHistory);
            return Response.success("success");
        }
        System.out.println(QueryId);

        return Response.fail(1077, "Error QueryId", null);
    }

    public Response GetQuerySuggestions(String query) {
        String url = "http://localhost:9000/data-discovery";
        String jsonRequest = String.format("{\"query\": \"%s\"}", query);

        System.out.println(query);

        JsonNode response = otherUtils.sendPostRequest(url, jsonRequest);

        if (response != null) {
            return Response.success("success", response.path("recommended_queries"));
        } else {
            return Response.fail(1078, "Error recommended_queries", null);
        }
    }

    // 查询的算法和可视化工具设置
    public Response GetTaskRecommend(String query){
        String url = "http://localhost:9000/task-tool-recommend";
        String jsonRequest = String.format("{\"query\": \"%s\"}", query);

        System.out.println(query);

        JsonNode response = otherUtils.sendPostRequest(url, jsonRequest);

        try {
            String jsonString = objectMapper.writeValueAsString(response);

            TaskRecommend taskRecommend = new TaskRecommend();
            taskRecommend.setTaskRecommendString(String.valueOf(response));
            taskRecommendRepository.save(taskRecommend);

            return Response.success("success", jsonString);
        }
        catch (Exception e){
            System.out.println(e);
            return Response.fail(1078, "Error GetTaskRecommend", null);
        }
    }

    public Response GetNewTaskSetting() {
        try {
            TaskRecommend taskRecommend = taskRecommendRepository.findMaxIdTaskRecommend();
            return Response.success("success", taskRecommend.getTaskRecommendString());
        }
        catch (Exception e){
            return Response.fail(1078, "Error GetNewTaskSetting", null);
        }
    }
    @Transactional
    public Response DeleteNewTaskSetting() {
        try {
            TaskRecommend taskRecommend = taskRecommendRepository.findMaxIdTaskRecommend();
            if (taskRecommend.getId() != 1){
                taskRecommendRepository.deleteMaxIdTaskRecommend(taskRecommend.getId());
            }
            return Response.success("success");
        }
        catch (Exception e){
            e.printStackTrace();
            return Response.fail(1078, "Error DeleteNewTaskSetting", null);
        }
    }

    public Response UpdateRecommend(List<TaskSettingPojo> taskSettingPojos){
        try {
            TaskRecommend taskRecommend = new TaskRecommend();
            String judgment = "I understand your request. Here are my recommendations:";

            // 创建 task_recommendations 数组
            StringBuilder taskRecommendationsBuilder = new StringBuilder();
            taskRecommendationsBuilder.append("[");

            // 遍历 taskSettingPojos 并构建每个任务的 JSON 对象
            for (int i = 0; i < taskSettingPojos.size(); i++) {
                TaskSettingPojo task = taskSettingPojos.get(i);
                taskRecommendationsBuilder.append("{")
                        .append("\"task\":\"").append(task.getTask()).append("\",")
                        .append("\"algorithm\":\"").append(task.getAlgorithm()).append("\",")
                        .append("\"visualization_tool\":\"").append(task.getVisualization_tool()).append("\",")
                        .append("\"reason\":\"").append(task.getReason()).append("\"")
                        .append("}");
                if (i < taskSettingPojos.size() - 1) {
                    taskRecommendationsBuilder.append(",");
                }
            }

            taskRecommendationsBuilder.append("]");

            String jsonString = "{\"judgment\":\"" + judgment + "\",\"task_recommendations\":"
                    + taskRecommendationsBuilder.toString() + "}";

            taskRecommend.setTaskRecommendString(jsonString);

            taskRecommendRepository.save(taskRecommend);

        } catch (Exception e){
            return Response.fail(1078, "Error UpdateRecommend", null);
        }
        return Response.success("success");
    }



    public Response findData(){
        List<ColumnData> columns= new ArrayList<>();
        List<sourceData> tables = new ArrayList<>();
        List<DataLink> dataLinks = new ArrayList<>();

        tables.add(new sourceData(1, "users", "Data Type", "Table"));
      //  tables.add(new sourceData(2, "shops", "Data Type", "Table"));
        tables.add(new sourceData(3, "books", "Data Type", "Table"));
        tables.add(new sourceData(4, "book_img_desc", "Data Type", "Table"));
        tables.add(new sourceData(5, "books_reviews", "Data Type", "json"));
        tables.add(new sourceData(6, "review_items", "Data Type", "json"));
        tables.add(new sourceData(7, "user_relations", "Data Type", "graph"));

        columns.add(new ColumnData(8, "user_id", "Column Type", "string", 1, "users"));
        columns.add(new ColumnData(9, "user_name", "Column Type", "string", 1, "users"));
        columns.add(new ColumnData(10, "gender", "Column Type", "string", 1, "users"));
        columns.add(new ColumnData(11, "age", "Column Type", "Integer", 1, "users"));

        /*
        columns.add(new ColumnData(12, "shop_id", "Column Type", "string", 2, "shops"));
        columns.add(new ColumnData(13, "shop_name", "Column Type", "string", 2, "shops"));
        columns.add(new ColumnData(14, "join_time", "Column Type", "Date", 2, "shops"));
        columns.add(new ColumnData(15, "item_num", "Column Type", "Integer", 2, "shops"));
        */
        columns.add(new ColumnData(16, "book_id", "Column Type", "string", 3, "books"));
        columns.add(new ColumnData(17, "title", "Column Type", "string", 3, "books"));
        columns.add(new ColumnData(18, "author", "Column Type", "string", 3, "books"));
        columns.add(new ColumnData(19, "language", "Column Type", "Long", 3, "books"));
        columns.add(new ColumnData(20, "genre", "Column Type", "text", 3, "books"));

        columns.add(new ColumnData(21, "book_id", "Column Type", "string", 5, "books_reviews"));
        columns.add(new ColumnData(22, "feedback", "Column Type", "double", 5, "books_reviews"));
        columns.add(new ColumnData(23, "review_num", "Column Type", "integer", 5, "books_reviews"));


        columns.add(new ColumnData(25, "id", "Column Type", "string", 6, "review_items"));
        columns.add(new ColumnData(26, "user_id", "Column Type", "string", 6, "review_items"));
        columns.add(new ColumnData(27, "review_time", "Column Type", "Date", 6, "review_items"));

        columns.add(new ColumnData(28, "book_id", "Column Type", "string", 4, "book_img_desc"));
        columns.add(new ColumnData(29, "title", "Column Type", "string", 4, "book_img_desc"));
        columns.add(new ColumnData(30, "img_url", "Column Type", "image", 4, "book_img_desc"));
        columns.add(new ColumnData(31, "description", "Column Type", "text", 4, "book_img_desc"));

        columns.add(new ColumnData(32, "from_userId", "Column Type", "string", 7, "user_relations"));
        columns.add(new ColumnData(33, "to_userId", "Column Type", "string", 7, "user_relation"));
        columns.add(new ColumnData(34, "relation", "Column Type", "string", 7, "user_relations"));

        dataLinks.add(new DataLink(35, 1, 7, "users.user_id Foreign(user_relations.from_userId, user_relations.to_userId)"));
        dataLinks.add(new DataLink(36, 6, 1, "review_items.user_id Foreign(users.user_id)"));
        dataLinks.add(new DataLink(37, 3, 4, "books.book_id Foreign(book_img_desc.book_id)"));
        dataLinks.add(new DataLink(38, 3, 5, "books.book_id Foreign(books_reviews.book_id)"));
        dataLinks.add(new DataLink(39, 5, 6, "json : reviews:review_items"));


        Map<String, Object> data = new HashMap<>();
        data.put("columns", columns);
        data.put("tables", tables);
        data.put("dataLinks", dataLinks);

        return Response.success("success", data);
    }

    public Response GetTitles(){
        List<MysqlMessage> mysqlMessageLists = mysqlMessageRepository.findAll();

        List<QueryData> queryDataList = new ArrayList<>();
        for (MysqlMessage mysqlMessage : mysqlMessageLists) {
            queryDataList.add(otherUtils.convertToQueryData(mysqlMessage));
        }

        return Response.success("success", queryDataList);
    }

    public Response GetChat(String QueryId){
        Optional<MysqlMessage> mysqlMessage = mysqlMessageRepository.findById(QueryId);

        if (mysqlMessage.isEmpty()){
            return Response.fail(1011, "Error QueryId", null);
        }

        QueryData queryData = otherUtils.convertToQueryData(mysqlMessage.get());

        return Response.success("success",queryData);
    }
















    public Response GetResults(){

        String filePath = null;

        Optional<User> target = userRepository.findById(1L);
        User user = target.get();

        if (user.getFlag() == 0){
            filePath = "src/main/resources/queryResult/query1/query1_result.csv";
        } else if (user.getFlag() == 1) {
            filePath = "src/main/resources/queryResult/query2/query2_result.csv";
        } else{
            filePath = "src/main/resources/queryResult/query3/query3_result.csv";
        }

        List<Map<String, String>> results = null;
        try {
            results = otherUtils.readCsvWithHeader(filePath);
        } catch (Exception e) {
            return Response.fail(1023, "Get result File error", null);
        }

        return Response.success("success", results);
    }

    // setting
    public Response Settings(SettingsPojo settingsPojo){

        try {
            // 更新配置文件
            File configFile = new File("config.properties");
            if (!configFile.exists()) {
                configFile.createNewFile();  // 如果文件不存在，创建一个新的文件
            }

            Properties properties = new Properties();
            try (FileInputStream inStream = new FileInputStream(configFile)) {
                properties.load(inStream);  // 加载当前的配置文件内容
            }

            // 更新配置项
            properties.setProperty("model", settingsPojo.getModel());
            properties.setProperty("dataSource", settingsPojo.getDataSource());
            properties.setProperty("max_token", String.valueOf(settingsPojo.getMax_token()));

            // 保存更新后的配置
            try (FileOutputStream outStream = new FileOutputStream(configFile)) {
                properties.store(outStream, null);  // 将更新的属性写入文件
            }

        } catch (Exception e) {
            return Response.fail(1053, "Failed to update settings", null);
        }

        return Response.success("suceess");
    }

}

