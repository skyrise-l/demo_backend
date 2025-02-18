package com.zju.QueryArtisan.pojo.Query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsPojo {
    private String model;
    private String dataSource;
    private Long max_token;
}
