package com.zju.QueryArtisan.pojo.Operator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPromptPojo {
    private String prompt;
    private String description;
}
