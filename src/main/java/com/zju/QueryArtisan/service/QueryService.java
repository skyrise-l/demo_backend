package com.zju.QueryArtisan.service;

import com.zju.QueryArtisan.entity.*;
import com.zju.QueryArtisan.pojo.Query.QueryPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QueryService{
    public Response StartQuery(QueryPojo queryPojo){

        String query = "Find the IDs, total prices, order descriptions, and product images of all orders from people known by Mike.";
        return Response.success("query success");
    }

    public Response findData(){
        List<ColumnData> columns= new ArrayList<>();
        List<sourceData> tables = new ArrayList<>();
        List<DataLink> dataLinks = new ArrayList<>();

        tables.add(new sourceData(1, "users", "Data Type", "Table"));
        tables.add(new sourceData(2, "shops", "Data Type", "Table"));
        tables.add(new sourceData(3, "orders", "Data Type", "json"));
        tables.add(new sourceData(4, "orderItems", "Data Type", "json"));
        tables.add(new sourceData(5, "person_knows_person", "Data Type", "graph"));

        columns.add(new ColumnData(6, "user_id", "Column Type", "string", 1, "users"));
        columns.add(new ColumnData(7, "user_name", "Column Type", "string", 1, "users"));
        columns.add(new ColumnData(8, "gender", "Column Type", "string", 1, "users"));

        columns.add(new ColumnData(9, "shop_id", "Column Type", "string", 2, "shops"));
        columns.add(new ColumnData(10, "shop_name", "Column Type", "string", 2, "shops"));
        columns.add(new ColumnData(11, "join_time", "Column Type", "Date", 2, "shops"));

        columns.add(new ColumnData(12, "order_id", "Column Type", "string", 3, "orders"));
        columns.add(new ColumnData(13, "user_id", "Column Type", "string", 3, "orders"));
        columns.add(new ColumnData(14, "shop_id", "Column Type", "string", 3, "orders"));
        columns.add(new ColumnData(15, "total_price", "Column Type", "Long", 3, "orders"));
        columns.add(new ColumnData(26, "order_description", "Column Type", "text", 3, "orders"));

        columns.add(new ColumnData(16, "detail_id", "Column Type", "string", 4, "orderItems"));
        columns.add(new ColumnData(17, "order_id", "Column Type", "string", 4, "orderItems"));
        columns.add(new ColumnData(18, "product_image", "Column Type", "image", 4, "orderItems"));
        columns.add(new ColumnData(19, "num", "Column Type", "int", 4, "orderItems"));

        columns.add(new ColumnData(20, "from_userId", "Column Type", "string", 5, "person_knows_person"));
        columns.add(new ColumnData(21, "to_userId", "Column Type", "string", 5, "person_knows_person"));

        dataLinks.add(new DataLink(22, 1, 5, "users.user_id Foreign(person_knows_person.from_userId, person_knows_person.to_userId)"));
        dataLinks.add(new DataLink(23, 2, 3, "shops.shop_id Foreign(orders.shop_id)"));
        dataLinks.add(new DataLink(24, 1, 3, "users.user_id Foreign(orders.user_id)"));
        dataLinks.add(new DataLink(25, 3, 4, "orders.order_id Foreign(orderItems.order_id)"));


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

}

