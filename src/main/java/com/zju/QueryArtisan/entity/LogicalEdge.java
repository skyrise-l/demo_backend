package com.zju.QueryArtisan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogicalEdge {
    private int id;
    private int source;
    private int target;
}
