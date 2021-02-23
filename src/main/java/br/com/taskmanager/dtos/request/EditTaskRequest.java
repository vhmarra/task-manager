package br.com.taskmanager.dtos.request;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditTaskRequest {

    @ApiParam(required = true)
    private Long taskId;

    @ApiParam(required = true)
    private String newDescription;

    @ApiParam(required = false)
    private String newDateEnd;

    @ApiParam(required = false)
    private String deadLineHour;

    @ApiParam(required = false)
    private String deadLineMinute;
}
