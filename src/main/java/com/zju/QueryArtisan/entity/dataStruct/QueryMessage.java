package com.zju.QueryArtisan.entity.dataStruct;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryMessage {
    private String author;
    private String message;
    private Long timestamp;
}
