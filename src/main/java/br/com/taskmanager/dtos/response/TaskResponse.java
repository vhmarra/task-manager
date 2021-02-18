package br.com.taskmanager.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskResponse {

    @JsonProperty(value = "task-id")
    private Long id;

    @JsonProperty(value = "date-end")
    private String dateEnd;

    @JsonProperty(value = "date-created")
    private String dateCreated;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "finalized")
    private String finalized;

}
