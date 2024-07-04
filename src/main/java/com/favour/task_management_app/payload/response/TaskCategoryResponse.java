package com.favour.task_management_app.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TaskCategoryResponse {
    private Long id;
    private String responseCode;
    private String responseMessage;
    private String title;
    private String description;
}
