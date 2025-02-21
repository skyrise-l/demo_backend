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
    private Long id;
    private String query;
    private Double executeTime;
}
