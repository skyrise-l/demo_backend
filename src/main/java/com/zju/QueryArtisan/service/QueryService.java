package com.zju.QueryArtisan.service;

import com.zju.QueryArtisan.entity.*;
import com.zju.QueryArtisan.mysql.CustomPromptRepository;
import com.zju.QueryArtisan.mysql.QueryListRepository;
import com.zju.QueryArtisan.mysql.QueryMessageRepository;
import com.zju.QueryArtisan.mysql.UserRepository;
import com.zju.QueryArtisan.pojo.Query.QueryPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QueryService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueryListRepository queryListRepository;

    @Autowired
    private CustomPromptRepository customPromptRepository;

    @Autowired
    private QueryMessageRepository queryMessageRepository;
    public static void addSystemMessageToQueryData(QueryMessage queryMessage, QueryData queryData, String systemMessageContent) {
        // 创建系统消息
        ChatMessage systemMessage = new ChatMessage("system", systemMessageContent, System.currentTimeMillis());

        // 检查QueryPojo和QueryData是否为null，然后添加消息
        queryData.addMessage(systemMessage);
        queryMessage.setMessage(systemMessageContent);
    }

    private static void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String generateRandomHash() {
        // 使用UUID生成随机的哈希值
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String extractFirstCharacters(String input, int numCharacters) {
        // 如果输入字符串长度小于等于要提取的字符数，则直接返回整个字符串
        if (input.length() <= numCharacters) {
            return input;
        }

        // 否则，提取前numCharacters个字符
        return input.substring(0, numCharacters);
    }

    public Response StartQuery(QueryPojo queryPojo){

        List<ChatMessage> userMessages = new ArrayList<>();
        List<ChatMessage> systemMessages = new ArrayList<>();
        QueryList queryList = new QueryList();

        if (queryPojo.getQueryData() != null) {
            for (ChatMessage message : queryPojo.getQueryData().getMessages()) {
                if ("user".equals(message.getAuthor())) {
                    userMessages.add(message);
                } else if ("system".equals(message.getAuthor())) {
                    systemMessages.add(message);
                }
            }
        } else {
            return Response.fail(1011, "The query content cannot be empty.", null);
        }
        QueryData queryData = queryPojo.getQueryData();

        int totalUserMessages = userMessages.size();
        String lastUserMessageContent = totalUserMessages > 0 ? userMessages.get(totalUserMessages - 1).getMessage() : "无";

        if (queryData.getTitle().equals("New Title")){
            queryData.setTitle(extractFirstCharacters(lastUserMessageContent, 10));
            String hash = generateRandomHash();
            sleep(3000);
            queryList.setTitle(queryData.getTitle());
            queryList.setHashValue(hash);
            queryData.setHashValue(hash);
            queryListRepository.save(queryList);
            queryList = queryListRepository.findByHashValue(hash);
        } else {
            queryList = queryListRepository.findByHashValue(queryData.getHashValue());
        }

        QueryMessage queryMessage = new QueryMessage();
        queryMessage.setQueryId(queryList.getId());
        queryMessage.setAuthor("user");
        queryMessage.setMessage(lastUserMessageContent);
        queryMessage.setTimestamp(new Date());
        queryMessageRepository.save((queryMessage));

        QueryMessage syetemMessgae = new QueryMessage();
        syetemMessgae.setQueryId(queryList.getId());
        syetemMessgae.setAuthor("system");
        syetemMessgae.setTimestamp(new Date());


        if (lastUserMessageContent.equals("I would like to know how much time this query consumed.")) {
            sleep(500);
            addSystemMessageToQueryData(syetemMessgae, queryData, "This query took a total of 5.123 seconds, with the data analysis code execution time being 0.613 seconds.");
        } else if (lastUserMessageContent.equals("Can you provide the content of the query result for download?")) {
            sleep(1000);
            addSystemMessageToQueryData(syetemMessgae, queryData, "The query results have been packaged. Please click to download.");
        } else if (lastUserMessageContent.equals("It seems like you misunderstood my request. Please try again.")) {
            sleep(3000);
            addSystemMessageToQueryData(syetemMessgae, queryData, "I apologize for the error in our system. We will regenerate the prompts and attempt again.");
        } else {
            if (totalUserMessages == 1) {
                sleep(3000);
                addSystemMessageToQueryData(syetemMessgae, queryData, "Our system prompt and your custom prompt (click to view) have been generated and sent. Query is currently in progress...");
            } else if (totalUserMessages == 2) {
                sleep(3000);
                addSystemMessageToQueryData(syetemMessgae, queryData, "Our system prompt and your custom prompt (click to view) have been generated and sent. Query is currently in progress...");

            } else {
                sleep(3000);
                addSystemMessageToQueryData(syetemMessgae, queryData, "Our system prompt and your custom prompt (click to view) have been generated and sent. Query is currently in progress...");
            }
        }
        queryMessageRepository.save((syetemMessgae));

        String query = "Find the IDs, total prices, order descriptions, and product images of all orders from people known by Mike.";

        return Response.success("success", queryData);
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

    public Response FlowChart(){
        List<LogicalEdge> logicalEdges= new ArrayList<>();
        List<LogicalNode> logicalNodes = new ArrayList<>();

        logicalNodes.add(new LogicalNode(1, "Step 1", "read", "None", "None", "(1) Use pandas to read 'Reviews.csv' as Reviews from file path '/mnt/Reviews.csv' (2) Perform data preprocessing on Reviews.", new int[]{4}));
        logicalNodes.add(new LogicalNode(2, "Step 2", "read", "None", "None", "(1) Use pandas to read 'Business.csv' as Business from file path '/mnt/Business.csv' (2) Perform data preprocessing on Business.", new int[]{7}));
        logicalNodes.add(new LogicalNode(3, "Step 3", "join", "None", "Step 1, Step 2", "Step 1 join Step 2 on Reviews.business_id = Business.business_id.", new int[]{10}));
        logicalNodes.add(new LogicalNode(4, "Step 4", "filter", "Business.active, Business.city", "Step 3", "Filter Step 3 Where Business.active = 'True' and Business.city = 'Phoenix'.", new int[]{13}));
        logicalNodes.add(new LogicalNode(5, "Step 5", "group_by", "Reviews.business_id", "Step 4", "group Step X by Reviews.business_id.",  new int[]{13}));
        logicalNodes.add(new LogicalNode(6, "Step 6", "having", "Reviews.review_length", "Step 5", "Having Avg(Reviews.review_length)  > 10.", new int[]{13}));
        logicalNodes.add(new LogicalNode(7, "Step 7", "select", "Business.business_id", "Step 6", "Select Business.business_id from Step 6.", new int[]{13}));
        logicalNodes.add(new LogicalNode(8, "Step 8", "write", "None", "Step 7", "Write Step 7 to file_1_path.", new int[]{16,17,18}));

        logicalEdges.add(new LogicalEdge(1, 1, 3));
        logicalEdges.add(new LogicalEdge(2, 2, 3));
        logicalEdges.add(new LogicalEdge(3, 3, 4));
        logicalEdges.add(new LogicalEdge(4, 4, 5));
        logicalEdges.add(new LogicalEdge(5, 5, 6));
        logicalEdges.add(new LogicalEdge(6, 6, 7));
        logicalEdges.add(new LogicalEdge(7, 7, 8));

        Map<String, Object> data = new HashMap<>();
        data.put("logicalEdges", logicalEdges);
        data.put("logicalNodes", logicalNodes);
        
        return Response.success("success", data);
    }

    public Response GetCode(){
        String data = "import pandas as pd\n\n# Read the book_physical.csv file\nphysical_df = pd.read_csv('/mnt/d/数据库/GPT3_project/src/GPT3/data/bench/book_physical.csv')\n\n# Read the book_basic.csv file\nbasic_df = pd.read_csv('/mnt/d/数据库/GPT3_project/src/GPT3/data/bench/book_basic.csv')\n\n# Merge the two dataframes on the book_id column\nmerged_df = pd.merge(basic_df, physical_df, on='book_id')\n\n# Find the title of the heaviest book\nheaviest_book_title = merged_df[merged_df['weight'] == merged_df['weight'].max()]['title'].values[0]\n\n# Write the result to the output file\nresult_path = '/mnt/d/数据库/GPT3_project/src/GPT3/result/unibench_gpt/2.txt'\nwith open(result_path, 'w') as f:\n    f.write(\"The title of the heaviest book is: \" + heaviest_book_title)";

        return Response.success("success", data);
    }

    public Response FlowChart2(){
        List<LogicalEdge> logicalEdges= new ArrayList<>();
        List<LogicalNode> logicalNodes = new ArrayList<>();

        logicalNodes.add(new LogicalNode(1, "Step 1", "read", "None", "None", "(1) Use pandas to read 'Reviews.csv' as Reviews from file path '/mnt/Reviews.csv' (2) Perform data preprocessing on Reviews.", new int[]{4}));
        logicalNodes.add(new LogicalNode(2, "Step 2", "read", "None", "None", "(1) Use pandas to read 'Business.csv' as Business from file path '/mnt/Business.csv' (2) Perform data preprocessing on Business.", new int[]{7}));
        logicalNodes.add(new LogicalNode(3, "Step 4", "join", "None", "Step 1, Step 2", "Step 1 join Step 2 on Reviews.business_id = Business.business_id.", new int[]{10}));
        logicalNodes.add(new LogicalNode(4, "Step 3", "filter", "Business.active, Business.city", "Step 2", "Filter Step 2 Where Business.active = 'True' and Business.city = 'Phoenix'.", new int[]{13}));
        logicalNodes.add(new LogicalNode(5, "Step 5", "group_by", "Reviews.business_id", "Step 4", "group Step X by Reviews.business_id.",  new int[]{13}));

        logicalEdges.add(new LogicalEdge(1, 1, 3));
        logicalEdges.add(new LogicalEdge(2, 2, 3));
        logicalEdges.add(new LogicalEdge(3, 3, 4));
        logicalEdges.add(new LogicalEdge(4, 4, 5));

        Map<String, Object> data = new HashMap<>();
        data.put("logicalEdges", logicalEdges);
        data.put("logicalNodes", logicalNodes);

        return Response.success("success", data);
    }

    public Response GetTitles(){
        List<QueryList> queryLists = queryListRepository.findAll();


        return Response.success("success", queryLists);
    }

    public Response GetChat(Long QueryId){
        Optional<QueryList> target = queryListRepository.findById(QueryId);
        if (target.isEmpty()){
            return Response.fail(1011, "Error QueryId", null);
        }
        QueryList queryList = target.get();
        List<QueryMessage> queryMessageList = queryMessageRepository.findAllByQueryId(QueryId);

        List<ChatMessage> chatMessages = new ArrayList<>();

        for (QueryMessage qm : queryMessageList) {
            ChatMessage chatMessage = new ChatMessage(qm.getAuthor(), qm.getMessage(), qm.getTimestamp().getTime());
            chatMessages.add(chatMessage);
        }

        QueryData queryData = new QueryData(queryList.getId(), queryList.getTitle(), queryList.getHashValue(), chatMessages);

        return Response.success("success",queryData);
    }

    public Response GetPrompt(){
        List <CustomPrompt> result = customPromptRepository.findAll();
        return Response.success("success",result);
    }
}

