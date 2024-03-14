package com.zju.QueryArtisan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogicalNode {
    private int id;
    private String label;
    private String operator;
    private String targetColumns;
    private String targetSteps;
    private String details;
    private int[] relatedCodeLines;
}