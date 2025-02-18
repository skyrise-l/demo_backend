package com.zju.QueryArtisan.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;


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
}
