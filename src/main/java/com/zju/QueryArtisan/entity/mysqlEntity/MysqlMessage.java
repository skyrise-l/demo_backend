package com.zju.QueryArtisan.entity.mysqlEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MysqlMessage {
    @Id
    private String id;
    private String title;
    private String hashValue;
    private String messages;
    private String model;
    private String dataSource;
    private Long max_token;
}
