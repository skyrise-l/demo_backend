package com.zju.QueryArtisan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MysqlMessage {
    private Long id;
    private String title;
    private String hashValue;
    private String messages;
    private String model;
    private String dataSource;
    private Long max_token;
}
