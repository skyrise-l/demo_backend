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
    String[] csv_urls = new String[]{
            "C:/Users/li308/My/Temp/query&data/queryResult/query1/query1_result.csv",
            "C:/Users/li308/My/Temp/query&data/queryResult/query2/query2_result.csv",
            "C:/Users/li308/My/Temp/query&data/queryResult/query3/query3_result.csv",
    };
    //每行是一个json对象，读取book_id=[490,724,948,1130,1343,2098]，返回
    String json_urls_1 = "C:/Users/li308/My/Temp/query&data/data/show_books_reviews.json";
    String json_urls_2 = "C:/Users/li308/My/Temp/query&data/data/show_all.json";
    Response[] csv_cache = new Response[3];

    public Response mainQuery(String queryId) {
        int id = Integer.parseInt(queryId);
        if (csv_cache[id] != null) {
            return csv_cache[Integer.parseInt(queryId)];
        }
        try {
            Reader reader = Files.newBufferedReader(Paths.get(csv_urls[id]));
            CSVReader csvReader = new CSVReaderBuilder(reader).build();

            String[] header = csvReader.readNext();
            String[] line;
            List<Map<String, String>> csvData = new ArrayList<>();
            List<String> columns = Arrays.asList(header);

            while ((line = csvReader.readNext()) != null) {
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < header.length; i++) {
                    row.put(header[i], line[i]);
                }
                csvData.add(row);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("columns", columns);
            result.put("data", csvData);

            return csv_cache[id] = Response.success("success", result);
        } catch (Exception e) {
            return Response.fail(1001, "Error reading CSV file", null);
        }
    }

    public Response tableQuery(String queryId) {
        return null;
    }

    public Response jsonQuery(String queryId) {
        if (queryId.equals("0")) {
            return Response.fail(1002, "No JSON file for query 0", null);
        } else {

            List<String> jsonList = new ArrayList<>();
            try {
                Reader reader = Files.newBufferedReader(Paths.get(queryId.equals("1") ? json_urls_1 : json_urls_2));
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
}