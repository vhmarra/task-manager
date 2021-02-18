package br.com.taskmanager.dtos.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskRequest {

    @ApiParam(name = "task-description",required = true)
    private String taskDescription;

    @ApiParam(name = "dead-line",required = false)
    private String taskDeadLine;

}
