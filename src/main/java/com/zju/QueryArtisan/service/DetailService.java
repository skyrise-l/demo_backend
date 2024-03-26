package com.zju.QueryArtisan.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.zju.QueryArtisan.entity.Response;
import org.springframework.stereotype.Service;

@Service
public class DetailService {
    //todo: change the path to the real path
    HashMap<String, String> csv_urls = new HashMap<>() {{
        put("users.csv", "C:\\Users\\li308\\My\\Temp\\data\\users.csv");
        put("pre_results", "C:\\Users\\li308\\My\\Temp\\data\\Script\\query1_result.csv");
        put("books.csv", "C:\\Users\\li308\\My\\Temp\\data\\books.csv");
        put("book_img_desc.csv", "C:\\Users\\li308\\My\\Temp\\data\\book_img_desc.csv");
        put("query1_result.csv", "C:/Users/li308/My/Temp/data/queryResult/query1/query1_result.csv");
        put("query2_result.csv", "C:/Users/li308/My/Temp/data/queryResult/query2/query2_result.csv");
        put("query3_result.csv", "C:/Users/li308/My/Temp/data/queryResult/query3/query3_result.csv");
        put("show_books_reviews.json", "C:/Users/li308/My/Temp/data/show_books_reviews.json");
        put("show_all.json", "C:/Users/li308/My/Temp/data/show_all.json");
        put("show_graph_total.csv", "C:\\Users\\li308\\My\\Temp\\data\\Graph\\show_graph_total.csv");
        put("show_graph.csv", "C:\\Users\\li308\\My\\Temp\\data\\Graph\\show_graph.csv");
        put("show_graph_total_edge.csv", "C:\\Users\\li308\\My\\Temp\\data\\Graph\\show_graph_total_edge.csv");
        put("show_graph3_total.csv", "C:\\Users\\li308\\My\\Temp\\data\\Graph\\show_graph3_total.csv");
        put("show_graph3.csv", "C:\\Users\\li308\\My\\Temp\\data\\Graph\\show_graph3.csv");
        put("show_graph3_total_edge.csv", "C:\\Users\\li308\\My\\Temp\\data\\Graph\\show_graph3_total_edge.csv");
    }};

    DetailService() {
        csvInfoInit();
        userInfoInit();
    }

    String[] queryResultUrls = new String[]{"query1_result.csv", "query2_result.csv", "query3_result.csv",};
    Response[] main_cache = new Response[3];

    public Response mainQuery(int id) {
        if (main_cache[id] != null) return main_cache[id];
        var result = csvInfo.get(queryResultUrls[id]);
        return main_cache[id] = Response.success("success", result);
    }

    Response table_cache = null;
    HashSet<String> used_table_urls = new HashSet<>() {{
        add("users.csv");
        add("books.csv");
        add("book_img_desc.csv");
        add("query1_result.csv");
    }};

    public Response tableQuery(int id) {
        if (table_cache != null) return table_cache;
        Map<String, Map<String, Object>> result = new HashMap<>();
        used_table_urls.forEach((k) -> result.put(k, csvInfo.get(k)));
        return table_cache = Response.success("success", result);
    }

    public Response jsonQuery(int queryId) {
        if (queryId == 0) {
            return Response.fail(1002, "No JSON file for query 0", null);
        } else {
            List<String> jsonList = new ArrayList<>();
            String url = csv_urls.get(queryId == 1 ? "show_books_reviews.json" : "show_all.json");
            try {
                Reader reader = Files.newBufferedReader(Paths.get(url));
                Scanner sc = new Scanner(reader);
                while (sc.hasNextLine()) {
                    jsonList.add(sc.nextLine());
                }
                return Response.success("success", jsonList);
            } catch (Exception e) {
                return Response.fail(1001, "Error reading JSON file", null);
            }
        }
    }

    //所有节点，关联节点，所有边
    String[] graph_urls_1 = new String[]{
            "show_graph_total.csv",
            "show_graph.csv",
            "show_graph_total_edge.csv",
    };
    String[] graph_urls_3 = new String[]{
            "show_graph3_total.csv",
            "show_graph3.csv",
            "show_graph3_total_edge.csv",
    };
    Response[] graph_cache = new Response[3];

    public Response graphQuery(int queryId) {
        if (graph_cache[queryId] != null) {
            return graph_cache[queryId];
        }
        if (queryId == 1) return graph_cache[queryId] = Response.fail(1002, "No graph for query 1", null);
//        [{id: 1, title: "Node 1",group: 0}],
//        [{id: "1-2", from: 1, to: 2, title: "This is edge from Node 1 to Node 2",}]
        List<Map<String, Object>> nodesOthers = new ArrayList<>();
        List<Map<String, Object>> nodesShow= new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        String[] name_urls = queryId == 0 ? graph_urls_1 : graph_urls_3;
        var info_o = csvInfo.get(name_urls[0]);
        if (info_o != null) {
            for (Map<String, String> row : (List<Map<String, String>>) info_o.get("data")) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", row.get("id"));
                node.put("title", userInfo.get(row.get("id")));
                node.put("group", 1);
                nodesOthers.add(node);
            }
        }
        var info_s = csvInfo.get(name_urls[1]);
        if (info_s != null) {
            for (Map<String, String> row : (List<Map<String, String>>) info_s.get("data")) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", row.get("id"));
                node.put("title", userInfo.get(row.get("id")));
                node.put("group", 2);
                nodesShow.add(node);
            }
        }
        var e = csvInfo.get(name_urls[2]);
        if (e != null) {
            int id = 1;
            for (Map<String, String> row : (List<Map<String, String>>) e.get("data")) {
                Map<String, Object> edge = new HashMap<>();
                edge.put("id", id++);
                edge.put("from", row.get("from"));
                edge.put("to", row.get("to"));
                edge.put("title", row.get("title"));
                edges.add(edge);
            }
        }
        Map<String, Object> ret = new HashMap<>();
//        ret.put("nodes", new ArrayList<Map<String, Object>>() {{
//            addAll(nodesOthers);
//            addAll(nodesShow);
//        }});
        ret.put("nodesOthers", nodesOthers);
        ret.put("nodesShow", nodesShow);
        ret.put("edges", edges);
        return graph_cache[queryId] = Response.success("success", ret);
    }


    Map<String, Map<String, Object>> csvInfo = new HashMap<>();
    Map<String, String> userInfo = new HashMap<>();

    public void userInfoInit() {
        var sortIn = Arrays.asList("user_id", "user_name", "age","gender");
        var cp = csvInfo.get("users.csv");
        for (Map<String, String> row : (List<Map<String, String>>) cp.get("data")) {
            StringBuilder title = new StringBuilder();
            row.entrySet()
                    .stream().sorted(Comparator.comparingInt(e -> sortIn.indexOf(e.getKey())))
                    .forEach(e -> title.append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
            userInfo.put(row.get("user_id"), title.toString());
        }
    }

    public void csvInfoInit() {
        csv_urls.forEach((k, v) -> csvInfo.put(k, csvParser(v)));
    }

    public Map<String, Object> csvParser(String url) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(url));
            CSVReader csvReader = new CSVReaderBuilder(reader).build();

            String[] header = csvReader.readNext();
            String[] line;
            List<Map<String, String>> csvData = new ArrayList<>();
            List<String> columns = Arrays.asList(header);

            while ((line = csvReader.readNext()) != null) {
                //排除空行
                if (line.length == 0) continue;
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < header.length; i++) {
                    row.put(header[i], line[i]);
                }
                csvData.add(row);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("columns", columns);
            result.put("data", csvData);
            reader.close();
            return result;
        } catch (Exception ignored) {
            return null;
        }
    }

}