package com.zju.QueryArtisan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class sourceData {
    private int id;
    private String name;
    private String nodeType;
    private String dataType;
}
