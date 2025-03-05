package com.zju.QueryArtisan.pojo.Query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSettingPojo {
    private String task;
    private String algorithm;
    private String visualization_tool;
    private String reason;
}
