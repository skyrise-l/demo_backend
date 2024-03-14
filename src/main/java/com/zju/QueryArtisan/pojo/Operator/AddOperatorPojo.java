package com.zju.QueryArtisan.pojo.Operator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOperatorPojo {
    private String operator;
    private String description;
    private String codeExample;
    private String format;
}