package com.zju.QueryArtisan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MysqlMessage {
    @Id
    private Long id;
    private String title;
    private String hashValue;
    private String messages;
    private String model;
    private String dataSource;
    private Long max_token;
}
