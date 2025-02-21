package com.zju.QueryArtisan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zju.QueryArtisan.entity.*;
import com.zju.QueryArtisan.entity.mysqlEntity.BatchQueries;
import com.zju.QueryArtisan.entity.dataStruct.QueryData;
import com.zju.QueryArtisan.entity.dataStruct.QueryMessage;
import com.zju.QueryArtisan.entity.dataStruct.Response;
import com.zju.QueryArtisan.entity.mysqlEntity.*;
import com.zju.QueryArtisan.mysql.*;
import com.zju.QueryArtisan.pojo.Query.SettingsPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    //主查询模块
    public Response StartQuery(QueryData queryData){
        RestTemplate restTemplate = new RestTemplate();
        String model = null;
        String dataSource = null;
        Long maxToken = 4096L;
        String systemString = null;

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
            MysqlMessage mysqlMessage = new MysqlMessage(
                    queryData.getId(),
                    queryData.getTitle(),
                    queryData.getHashValue(),
                    StringUtils.convertMessagesToJson(queryData.getMessages()),
                    model,
                    dataSource,
                    maxToken
            );
            String url = "http://127.0.0.1:9000";
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(mysqlMessage);

            // 构建 HTTP 请求
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            systemString = responseEntity.getBody();

            QueryMessage systemMessage = new QueryMessage("system", systemString, System.currentTimeMillis());

            queryData.getMessages().add(systemMessage);

            mysqlMessage.setMessages(StringUtils.convertMessagesToJson(queryData.getMessages()));
            mysqlMessageRepository.save(mysqlMessage);
        } catch (Exception e) {

            return Response.fail(1073, "Error sending request to service", null);
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

            String externalServiceUrl = "http://127.0.0.1:9000";

            URL url = new URL(externalServiceUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String jsonPayload = "{\"queries\": \"" + queries + "\", \"filepath\": \"" + upload_path + "\"}";

            OutputStream os = connection.getOutputStream();
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取返回的URL
                resultUrl = new String(connection.getInputStream().readAllBytes(), "UTF-8");
            } else {
                Response.fail(1074, "Error processing batch query", null);
            }
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
        List<QueryHistory> queryHistory = queryHistoryRepository.findAll();
        return Response.success("success", queryHistory);
    }

    public Response DeleteHistoryQuery(Long QueryId) {
        QueryHistory queryHistory = queryHistoryRepository.findById(QueryId).orElse(null);
        if (queryHistory != null) {
            queryHistoryRepository.delete(queryHistory);
            return Response.success("success");
        }
        System.out.println(QueryId);

        return Response.fail(1077, "Error QueryId", null);
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

    public Response FlowChart_raw(){
        List<LogicalEdge> logicalEdges= new ArrayList<>();
        List<LogicalNode> logicalNodes = new ArrayList<>();

        Optional<User> target = userRepository.findById(1L);
        User user = target.get();

        if (user.getFlag() == 0){
            logicalNodes.add(new LogicalNode(1, "Step 1", "read", "None", "None", " Use pandas to read 'users.csv' as users from file path '/mnt/users.csv'.", new int[]{5}));
            logicalNodes.add(new LogicalNode(2, "Step 2", "read", "None", "None", " Read Graph 'user_relations' edges('relation') to user_relations.", new int[]{6}));
            logicalNodes.add(new LogicalNode(3, "Step 3", "filter", "user_name", "Step 1", "Filter Step 1 Where user_name = 'mike'.", new int[]{11}));
            logicalNodes.add(new LogicalNode(4, "Step 4", "select", "user_id", "Step 3", "Select user_id from Step 3.", new int[]{14}));
            logicalNodes.add(new LogicalNode(5, "Step 5", "filter", "user_id, relation", "Step 2, Step 4", "Filter Step2 Where user_id = Step4.user_id and relation = 'follow'.",  new int[]{13}));
            logicalNodes.add(new LogicalNode(6, "Step 6", "select", "user_id", "Step 5", "Select user_id from Step 5.", new int[]{14}));
            logicalNodes.add(new LogicalNode(7, "Step 7", "filter", "user_id", "Step 1, Step 6", "Filter Step 1 Where user_id = Step6.user_id.", new int[]{14}));
            logicalNodes.add(new LogicalNode(8, "Step 8", "select", "user_name", "Step 7", "Select Distinct user_name from Step 7.", new int[]{14}));
            logicalNodes.add(new LogicalNode(9, "Step 9", "write", "None", "Step 8", "Write Step 8 to file_1_path.", new int[]{16,17,18}));

            logicalEdges.add(new LogicalEdge(1, 1, 3));
            logicalEdges.add(new LogicalEdge(2, 3, 4));
            logicalEdges.add(new LogicalEdge(3, 4, 5));
            logicalEdges.add(new LogicalEdge(9, 2, 5));
            logicalEdges.add(new LogicalEdge(4, 5, 6));
            logicalEdges.add(new LogicalEdge(5, 1, 7));
            logicalEdges.add(new LogicalEdge(6, 6, 7));
            logicalEdges.add(new LogicalEdge(7, 7, 8));
            logicalEdges.add(new LogicalEdge(8, 8, 9));

        } else if (user.getFlag() == 1) {
            logicalNodes.add(new LogicalNode(1, "Step 1", "read", "None", "None", " Use pandas to read '136314daa2a17c42e4eb9e305b518c34_result.csv' as pre_results from file path '/mnt/result/136314daa2a17c42e4eb9e305b518c34_result.csv'.", new int[]{5}));
            logicalNodes.add(new LogicalNode(2, "Step 2", "read", "None", "None", " Read review_items from Json file('review_time') path '/mnt/data/reviews.json.'", new int[]{6}));
            logicalNodes.add(new LogicalNode(3, "Step 3", "read", "None", "None", " Use pandas to read 'books.csv' as books from file path '/mnt/data/books.csv'.", new int[]{7}));
            logicalNodes.add(new LogicalNode(4, "Step 4", "select", "user_name", "Step 1", "Select user_name from Step 1.", new int[]{10}));

            logicalNodes.add(new LogicalNode(5, "Step 5", "cast_date", "review_time", "Step 2", "Cast Step 2 review_time to Date.", new int[]{13}));
            logicalNodes.add(new LogicalNode(6, "Step 6", "filter", "review_time", "Step 5", "Filter Step 5 Where review_time > '2020-01-01'.",  new int[]{16}));
            logicalNodes.add(new LogicalNode(7, "Step 7", "filter", "user_name", "Step 6", "Filter Step 6 Where user_name = Step4.user_name.", new int[]{19}));
            logicalNodes.add(new LogicalNode(8, "Step 8", "select", "book_id, title", "Step 3", "Select book_id, title from Step 3.", new int[]{22}));
            logicalNodes.add(new LogicalNode(9, "Step 9", "join", "book_id", "Step 7, Step 8", "Join Step 7 on Step 8 Where Step7.book_id = Step8.book_id.", new int[]{22}));
            logicalNodes.add(new LogicalNode(10, "Step 10", "write", "None", "Step 9", "Write Step 9 to file_1_path.", new int[]{25}));

            logicalEdges.add(new LogicalEdge(1, 1, 4));
            logicalEdges.add(new LogicalEdge(2, 2, 5));
            logicalEdges.add(new LogicalEdge(3, 5, 6));
            logicalEdges.add(new LogicalEdge(4, 6, 7));
            logicalEdges.add(new LogicalEdge(5, 3, 8));
            logicalEdges.add(new LogicalEdge(6, 7, 9));
            logicalEdges.add(new LogicalEdge(7, 8, 9));
            logicalEdges.add(new LogicalEdge(8, 9, 10));
            logicalEdges.add(new LogicalEdge(9, 4, 7));


        } else if (user.getFlag() == 2) {
            logicalNodes.add(new LogicalNode(1, "Step 1", "read", "None", "None", " Use pandas to read 'users.csv' as users from file path '/mnt/data/users.csv'.", new int[]{5}));
            logicalNodes.add(new LogicalNode(2, "Step 2", "read", "None", "None", " Read review_items from Json file('review_time') path '/mnt/data/reviews.json.'", new int[]{6}));
            logicalNodes.add(new LogicalNode(3, "Step 3", "read", "None", "None", " Read Graph 'user_relations' edges('relation') to user_relations.", new int[]{7}));
            logicalNodes.add(new LogicalNode(4, "Step 4", "read", "None", "None", " Use pandas to read 'books.csv' as books from file path '/mnt/data/books.csv'.", new int[]{8}));
            logicalNodes.add(new LogicalNode(5, "Step 5", "read", "None", "None", " Use pandas to read 'book_img_desc.csv' as book_img_desc from file path '/mnt/data/book_img_desc.csv'.'", new int[]{9}));
            logicalNodes.add(new LogicalNode(6, "Step 6", "join", "user_id, from_userId", "Step 1, Step 3", "Join Step 1 on Step 3 Where Step1.book_id = Step3.from_userId.", new int[]{10}));
            logicalNodes.add(new LogicalNode(7, "Step 7", "filter", "user_name, relation", "Step 6", "Filter Step 6 Where user_name = 'mike' and relation = 'like'.", new int[]{13}));

            logicalNodes.add(new LogicalNode(8, "Step 8", "join", "to_userId, user_id", "Step 2, Step 7", "Join Step 2 on Step 7 Where Step2.to_userId = Step7.user_id.",  new int[]{16}));
            logicalNodes.add(new LogicalNode(9, "Step 9", "group", "book_id", "Step 8", "Group Step 8 by book_id", new int[]{19}));
            logicalNodes.add(new LogicalNode(10, "Step 10", "average", "score", "Step 9", "Apply average of score on Step 9.", new int[]{22}));
            logicalNodes.add(new LogicalNode(11, "Step 11", "join", "book_id", "Step 4, Step 5", "Join Step 4 on Step 5 Where Step4.book_id = Step5.book_id.", new int[]{22}));
            logicalNodes.add(new LogicalNode(12, "Step 12", "filter", "score", "Step 10", "Filter Step 10 Where score > 4.5.", new int[]{25}));
            logicalNodes.add(new LogicalNode(13, "Step 13", "filter", "book_id'", "Step 11, Step 12", "Filter Step 11 Where book_id = Step12.book_id.", new int[]{25}));
            logicalNodes.add(new LogicalNode(14, "Step 14", "select", "title, author, img_url, description", "Step 13", "Select title, author, img_url, description from Step 13.", new int[]{25}));
            logicalNodes.add(new LogicalNode(15, "Step 15", "write", "None", "Step 14", "Write Step 14 to file_1_path.", new int[]{25}));


            logicalEdges.add(new LogicalEdge(1, 1, 6));
            logicalEdges.add(new LogicalEdge(2, 3, 6));
            logicalEdges.add(new LogicalEdge(3, 6, 7));
            logicalEdges.add(new LogicalEdge(4, 2, 8));
            logicalEdges.add(new LogicalEdge(5, 7, 8));
            logicalEdges.add(new LogicalEdge(6, 8, 9));
            logicalEdges.add(new LogicalEdge(7, 9, 10));
            logicalEdges.add(new LogicalEdge(8, 4, 11));
            logicalEdges.add(new LogicalEdge(9, 5, 11));
            logicalEdges.add(new LogicalEdge(10, 10, 12));
            logicalEdges.add(new LogicalEdge(11, 11, 13));
            logicalEdges.add(new LogicalEdge(12, 12, 13));
            logicalEdges.add(new LogicalEdge(13, 13, 14));
            logicalEdges.add(new LogicalEdge(14, 14, 15));

        }

        Map<String, Object> data = new HashMap<>();
        data.put("logicalEdges", logicalEdges);
        data.put("logicalNodes", logicalNodes);
        
        return Response.success("success", data);
    }

    public Response GetCode(){

        Optional<User> target = userRepository.findById(1L);
        User user = target.get();
        String data = null;
        if (user.getFlag() == 0) {
            data = "import pandas as pd;\nimport dataDeal;\n\n# Step 1 - Step 2\nusers = pd.read_csv('users.csv')\nuser_relations = dataDeal.graph_edge('user_relations', ['relation'])[0]\n\n# Step 3\nmerged_data = pd.merge(users, user_relations, left_on='user_id', right_on='from_userId')\n\n# Step 4\nmike_follows = merged_data[(merged_data['user_name'] == 'mike') & (merged_data['relation'] == 'follow')]\n\n# Step 5 - Step6 \nmike_follows_names = users[users['user_id'].isin(mike_follows['to_userId'])]['user_name']\n\n# Step 7\nmike_follows_names.to_csv('query_result.csv', index=False)";
        }  else if (user.getFlag() == 1) {
            data = "import pandas as pd;\nimport dataDeal;\n\n# Step 1 - Step 3\npre_results = pd.read_csv('/mnt/result/136314daa2a17c42e4eb9e305b518c34_result.csv')\njson_reviews_item = dataDeal.json_deal('/mnt/data/review_items.csv', ['review_time'])[0]\nbooks = pd.read_csv('/mnt/data/books.csv')\n\n# Step 4\nuser_names = pre_results['user_name']\n\n# Step 5\njson_reviews_item['review_time'] = pd.to_datetime(json_reviews_item['review_time'])\n\n# Step 6\nfiltered_reviews = json_reviews_item[json_reviews_item['review_time'] > pd.Timestamp('2020-01-01')]\n\n# Step 7\nuser_reviews = filtered_reviews[filtered_reviews['username'].isin(user_names)]\n\n# Step 8\nreviews_with_titles = pd.merge(user_reviews, books[['book_id', 'title']], on='book_id', how='left')\n\n# Step 9\nreviews_with_titles.to_csv('/mnt/data/query_result.csv', index=False)";
        } else if (user.getFlag() == 2) {
            data = "import pandas as pd;\nimport dataDeal;\n\n# Step 1 - Step 5\nusers = pd.read_csv('users.csv')\nuser_relations = dataDeal.graph_edge('user_relations', ['relation'])[0]\nreview_items = dataDeal.json_deal('/mnt/data/review_items.csv', ['score'])[0]\nbooks = pd.read_csv('books.csv')\nbook_img_desc = pd.read_csv('book_img_desc.csv')\n\n# Step 6 - Step 7\nmike_user_id = users[users['user_name'] == 'mike']['user_id'].iloc[0]\n\n# Step 8\nmike_likes_user_ids = user_relations[(user_relations['from_userId'] == mike_user_id) & (user_relations['relation'] == 'like')]['to_userId']\n\n# Step 9\nreviews_from_liked_users = review_items[review_items['user_id'].isin(mike_likes_user_ids)]\n\n# Step 10 - Step 11\naverage_scores = reviews_from_liked_users.groupby('book_id', as_index=False)['score'].mean()\n\n# Step 12 - Step 13\nbooks_above_4 = average_scores[average_scores['score'] > 4.5]['book_id']\n\n# Step 14\nbooks_info = books[books['book_id'].isin(books_above_4)]\n\n# Step 15\nbooks_img_desc = book_img_desc[book_img_desc['book_id'].isin(books_above_4)]\n\n# Step 16\nfinal_books_info = pd.merge(books_info, books_img_desc, on='book_id')\n\n# Step 17\nfinal_books_info = final_books_info[['title', 'author', 'img_url', 'description']]\n\n# Step 18\nfinal_books_info.to_csv('query_result.csv', index=False)\n";
        }

        return Response.success("success", data);
    }

    public Response FlowChart_final(){
        List<LogicalEdge> logicalEdges= new ArrayList<>();
        List<LogicalNode> logicalNodes = new ArrayList<>();

        Optional<User> target = userRepository.findById(1L);
        User user = target.get();
        if (user.getFlag() == 0) {
            logicalNodes.add(new LogicalNode(1, "Step 1", "read", "None", "None", "(1) Use pandas to read 'users.csv' as users from file path '/mnt/users.csv' (2) Perform data preprocessing on users.", new int[]{5}));
            logicalNodes.add(new LogicalNode(2, "Step 2", "read", "None", "None", "(1) Read Graph 'user_relations' edges('relation') to user_relations (2) Perform data preprocessing on user_relations.", new int[]{6}));
            logicalNodes.add(new LogicalNode(3, "Step 3", "join", "user_id, from_userId", "Step 1, Step 2", "Join Step 1 on Step 2 Where Step1.user_id = Step2.from_userId.", new int[]{9}));
            logicalNodes.add(new LogicalNode(4, "Step 4", "filter", "user_name, relation", "Step 3", "Filter Step2 Where user_name = 'mike' and relation = 'follow'.",  new int[]{12}));
            logicalNodes.add(new LogicalNode(5, "Step 5", "filter", "user_id", "Step 1, Step 4", "Filter Step 1 Where user_id = Step4.user_id.", new int[]{15}));
            logicalNodes.add(new LogicalNode(6, "Step 6", "select", "user_name", "Step 5", "Select user_name from Step 5.", new int[]{15}));
            logicalNodes.add(new LogicalNode(7, "Step 7", "write", "None", "Step 6", "Write Step 6 to file_1_path.", new int[]{18}));

            logicalEdges.add(new LogicalEdge(1, 1, 3));
            logicalEdges.add(new LogicalEdge(2, 2, 3));
            logicalEdges.add(new LogicalEdge(3, 3, 4));
            logicalEdges.add(new LogicalEdge(9, 4, 5));
            logicalEdges.add(new LogicalEdge(4, 1, 5));
            logicalEdges.add(new LogicalEdge(5, 5, 6));
            logicalEdges.add(new LogicalEdge(6, 6, 7));

        } else if (user.getFlag() == 1) {
            logicalNodes.add(new LogicalNode(1, "Step 1", "read", "None", "None", " Use pandas to read '136314daa2a17c42e4eb9e305b518c34_result.csv' as pre_results from file path '/mnt/result/136314daa2a17c42e4eb9e305b518c34_result.csv'.", new int[]{5}));
            logicalNodes.add(new LogicalNode(2, "Step 2", "read", "None", "None", " Read review_items from Json file('review_time') path '/mnt/data/reviews.json.'", new int[]{6}));
            logicalNodes.add(new LogicalNode(3, "Step 3", "read", "None", "None", " Use pandas to read 'books.csv' as books from file path '/mnt/data/books.csv'.", new int[]{7}));
            logicalNodes.add(new LogicalNode(4, "Step 4", "select", "user_name", "Step 1", "Select user_name from Step 1.", new int[]{10}));

            logicalNodes.add(new LogicalNode(5, "Step 5", "cast_date", "review_time", "Step 2", "Cast Step 2 review_time to Date.", new int[]{13}));
            logicalNodes.add(new LogicalNode(6, "Step 6", "filter", "review_time", "Step 5", "Filter Step 5 Where review_time > '2020-01-01'.",  new int[]{16}));
            logicalNodes.add(new LogicalNode(7, "Step 7", "filter", "user_name", "Step 6", "Filter Step 6 Where user_name = Step4.user_name.", new int[]{19}));
            logicalNodes.add(new LogicalNode(8, "Step 8", "select", "book_id, title", "Step 3", "Select book_id, title from Step 3.", new int[]{22}));
            logicalNodes.add(new LogicalNode(9, "Step 9", "join", "book_id", "Step 7, Step 8", "Join Step 7 on Step 8 Where Step7.book_id = Step8.book_id.", new int[]{22}));
            logicalNodes.add(new LogicalNode(10, "Step 10", "write", "None", "Step 9", "Write Step 9 to file_1_path.", new int[]{25}));

            logicalEdges.add(new LogicalEdge(1, 1, 4));
            logicalEdges.add(new LogicalEdge(2, 2, 5));
            logicalEdges.add(new LogicalEdge(3, 5, 6));
            logicalEdges.add(new LogicalEdge(4, 6, 7));
            logicalEdges.add(new LogicalEdge(5, 3, 8));
            logicalEdges.add(new LogicalEdge(6, 7, 9));
            logicalEdges.add(new LogicalEdge(7, 8, 9));
            logicalEdges.add(new LogicalEdge(8, 9, 10));
            logicalEdges.add(new LogicalEdge(9, 4, 7));
        } else if (user.getFlag() == 2) {
            logicalNodes.add(new LogicalNode(1, "Step 1", "read", "None", "None", " Use pandas to read 'users.csv' as users from file path '/mnt/data/users.csv'.", new int[]{5}));
            logicalNodes.add(new LogicalNode(2, "Step 2", "read", "None", "None", " Read review_items from Json file('review_time') path '/mnt/data/reviews.json.'", new int[]{6}));
            logicalNodes.add(new LogicalNode(3, "Step 3", "read", "None", "None", " Read Graph 'user_relations' edges('relation') to user_relations.", new int[]{7}));
            logicalNodes.add(new LogicalNode(4, "Step 4", "read", "None", "None", " Use pandas to read 'books.csv' as books from file path '/mnt/data/books.csv'.", new int[]{8}));
            logicalNodes.add(new LogicalNode(5, "Step 5", "read", "None", "None", " Use pandas to read 'book_img_desc.csv' as book_img_desc from file path '/mnt/data/book_img_desc.csv'.'", new int[]{9}));
            logicalNodes.add(new LogicalNode(6, "Step 6", "filter", "user_name", "Step 5", "Filter Step 5 Where user_name = 'mike'.", new int[]{12}));
            logicalNodes.add(new LogicalNode(7, "Step 7", "filter", "from_userId, relation", "Step 3, Step 6", "Filter Step 3 Where from_userId = Step6.user_id and relation = 'like'.", new int[]{15}));
            logicalNodes.add(new LogicalNode(8, "Step 8", "filter", "user_id", "Step 2, Step 7", "Filter Step 2 Where user_id = Step7.user_id.", new int[]{18}));
            logicalNodes.add(new LogicalNode(9, "Step 9", "group", "book_id", "Step 8", "Group Step 8 by book_id", new int[]{21}));
            logicalNodes.add(new LogicalNode(10, "Step 10", "average", "score", "Step 9", "Apply average of score on Step 9.", new int[]{21}));
            logicalNodes.add(new LogicalNode(11, "Step 11", "filter", "score", "Step 10", "Filter Step 10 Where score > 4.5.", new int[]{24}));
            logicalNodes.add(new LogicalNode(12, "Step 12", "filter", "book_id'", "Step 4, Step 11", "Filter Step 4 Where book_id = Step11.book_id.", new int[]{27}));
            logicalNodes.add(new LogicalNode(13, "Step 13", "filter", "book_id'", "Step 5, Step 11", "Filter Step 5 Where book_id = Step11.book_id.", new int[]{30}));
            logicalNodes.add(new LogicalNode(14, "Step 14", "join", "book_id", "Step 12, Step 13", "Join Step 12 on Step 13 Where Step12.book_id = Step13.book_id.",  new int[]{33}));
            logicalNodes.add(new LogicalNode(15, "Step 15", "select", "title, author, img_url, description", "Step 14", "Select title, author, img_url, description from Step 14.", new int[]{36}));
            logicalNodes.add(new LogicalNode(16, "Step 16", "write", "None", "Step 14", "Write Step 15 to file_1_path.", new int[]{39}));


            logicalEdges.add(new LogicalEdge(1, 1, 6));
            logicalEdges.add(new LogicalEdge(2, 3, 7));
            logicalEdges.add(new LogicalEdge(3, 6, 7));
            logicalEdges.add(new LogicalEdge(4, 2, 8));
            logicalEdges.add(new LogicalEdge(5, 7, 8));
            logicalEdges.add(new LogicalEdge(6, 8, 9));
            logicalEdges.add(new LogicalEdge(7, 9, 10));
            logicalEdges.add(new LogicalEdge(8, 10, 11));
            logicalEdges.add(new LogicalEdge(9, 4, 12));
            logicalEdges.add(new LogicalEdge(10, 11, 12));
            logicalEdges.add(new LogicalEdge(11, 5, 13));
            logicalEdges.add(new LogicalEdge(12, 11, 13));
            logicalEdges.add(new LogicalEdge(13, 12, 14));
            logicalEdges.add(new LogicalEdge(14, 13, 14));
            logicalEdges.add(new LogicalEdge(15, 14, 15));
            logicalEdges.add(new LogicalEdge(16, 15, 16));

        }

        Map<String, Object> data = new HashMap<>();
        data.put("logicalEdges", logicalEdges);
        data.put("logicalNodes", logicalNodes);

        return Response.success("success", data);
    }

    public Response GetTitles(){
        List<MysqlMessage> mysqlMessageLists = mysqlMessageRepository.findAll();

        List<QueryData> queryDataList = new ArrayList<>();
        for (MysqlMessage mysqlMessage : mysqlMessageLists) {
            // 使用 convertToQueryData() 函数进行转换
            queryDataList.add(otherUtils.convertToQueryData(mysqlMessage));
        }

        return Response.success("success", queryDataList);
    }

    public Response GetChat(Long QueryId){
        Optional<MysqlMessage> mysqlMessage = mysqlMessageRepository.findById(QueryId);

        if (mysqlMessage.isEmpty()){
            return Response.fail(1011, "Error QueryId", null);
        }

        QueryData queryData = otherUtils.convertToQueryData(mysqlMessage.get());

        return Response.success("success",queryData);
    }

    public Response GetPrompt(){
        List <CustomPrompt> result = customPromptRepository.findAll();
        return Response.success("success",result);
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

