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
public class QueryHistory {
    @Id
    private String id;
    private String query;
    private Double executeTime;
    private String decomposeQueryArray;
    private String decomposeAnalysisArray;
    private String command;
}
