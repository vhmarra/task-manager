package br.com.taskmanager.dtos.response;

import br.com.taskmanager.domain.TaskEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

    @JsonProperty(value = "task-id")
    private String id;

    @JsonProperty(value = "date-end")
    private String dateEnd;

    @JsonProperty(value = "date-created")
    private String dateCreated;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "finalized")
    private String finalized;

    @JsonProperty(value = "priority")
    private String priority;

    public TaskResponse(TaskEntity task) {
        this.id = task.getId();
        this.dateCreated = task.getDateCreated().toString();
        this.dateEnd = task.getDateEnd().toString();
        this.description = task.getTaskDescription();
        this.finalized = task.getFinalized().toString();
        if (task.getPriority() == 1) {
            this.priority = "true";
        }
        if (task.getPriority() == 0) {
            this.priority = "false";
        }
    }

}
