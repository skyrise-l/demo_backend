package com.zju.QueryArtisan.entity;

import com.sun.jdi.event.StepEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryMessage {
    private String author;
    private String message;
    private String timestamp;
}
