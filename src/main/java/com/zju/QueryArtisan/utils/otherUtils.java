package com.zju.QueryArtisan.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zju.QueryArtisan.entity.mysqlEntity.MysqlMessage;
import com.zju.QueryArtisan.entity.dataStruct.QueryData;
import com.zju.QueryArtisan.entity.dataStruct.QueryMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class otherUtils {

    public static boolean areAllFieldsNotNull(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true); // 可以访问私有字段
            try {
                if (field.get(object) == null) {
                    return false; // 如果任何字段为null，返回false
                }
            } catch (IllegalAccessException e) {
                log.info(String.valueOf(e));
                return false;
            }
        }
        return true; // 所有字段都不为null
    }

    public static List<Map<String, String>> readCsvWithHeader(String filePath) throws Exception {
        List<Map<String, String>> recordsList = new ArrayList<>();

        try (
                Reader reader = Files.newBufferedReader(Paths.get(filePath));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
        ) {
            for (CSVRecord record : csvParser) {
                recordsList.add(record.toMap());
            }
        }

        return recordsList;
    }

    public static QueryData convertToQueryData(MysqlMessage mysqlMessage) {
        // 将 messages 字段的 JSON 字符串转换为 List<QueryMessage>
        List<QueryMessage> messages = parseMessages(mysqlMessage.getMessages());

        // 创建并返回 QueryData 实例
        return new QueryData(
                mysqlMessage.getId(),
                mysqlMessage.getTitle(),
                mysqlMessage.getHashValue(),
                messages
        );
    }

    // 将 JSON 字符串转换为 List<QueryMessage>
    public static List<QueryMessage> parseMessages(String messagesJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 解析 messages JSON 字符串为 List<QueryMessage>
            return objectMapper.readValue(messagesJson, objectMapper.getTypeFactory().constructCollectionType(List.class, QueryMessage.class));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing messages JSON", e);
        }
    }

    public static void saveFiles(MultipartFile[] files, String uploadPath) {
        // 确保上传路径存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            File savedFile = null;
            for (MultipartFile file : files) {
                savedFile = new File(uploadPath + File.separator + file.getOriginalFilename());
                file.transferTo(savedFile);  // 保存文件
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deal upload file", e);
        }
    }

    public static void  deleteFiles(String upload_path) {
        File directory = new File(upload_path);

        if (directory.exists() && directory.isDirectory()) {
            // 获取目录下的所有文件
            File[] files = directory.listFiles();
            if (files != null) {
                // 遍历所有文件并删除
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

    public static void sleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 发送POST请求，返回响应的JSON内容
    public static JsonNode sendPostRequest(String url, String jsonData) {
        try {
            // 创建HTTP请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonData, StandardCharsets.UTF_8))
                    .build();

            // 发送请求并获取响应
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 检查响应状态码
            if (response.statusCode() == 200) {
                // 解析返回的响应内容
                return objectMapper.readTree(response.body());
            } else {
                // 如果返回状态不是 200，抛出异常或返回错误消息
                System.err.println("Error: Received non-200 status code");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
