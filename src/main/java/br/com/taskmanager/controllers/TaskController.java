package br.com.taskmanager.controllers;

import br.com.taskmanager.dtos.request.CreateTaskRequest;
import br.com.taskmanager.dtos.request.EditTaskRequest;
import br.com.taskmanager.dtos.response.TaskResponse;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.TokenNotFoundException;
import br.com.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("create-task")
    public ResponseEntity<?> createTask(@RequestHeader(name = "access-token") String token,
                                        @RequestHeader(name = "task-description") String taskDescription,
                                        @RequestHeader(name = "task-dead-line-day",required = false) String taskDeadLine,
                                        @RequestHeader(name = "task-dead-line-hour",required = false,defaultValue = "00") String taskDeadLineHour,
                                        @RequestHeader(name = "task-dead-line-minute",required = false,defaultValue = "00") String taskDeadLineMinute,
                                        @RequestHeader(name = "priority",required = false) Integer priority)
            throws TokenNotFoundException, InvalidInputException {

        taskService.createTask(taskDeadLine, taskDescription,taskDeadLineHour,taskDeadLineMinute,priority);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("edit-task-description")
    public ResponseEntity<?> editTaskDescription(@RequestHeader(name = "access-token") String token,
                                                 @RequestHeader Long taskId,
                                                 @RequestHeader String newDescription,
                                                 @RequestHeader(required = false) Integer priority) throws InvalidInputException, TokenNotFoundException {

        taskService.editTaskDescription(taskId,newDescription,priority);
        return ResponseEntity.ok().build();
    }

    @PostMapping("edit-task-deadline")
    public ResponseEntity<?> editTaskDescriptionDeadLine(@RequestHeader(name = "access-token") String token,
                                                 @RequestHeader Long taskId,
                                                 @RequestHeader String newDeadLineDay,
                                                 @RequestHeader(required = false,defaultValue = "00") String newDeadLineHour,
                                                 @RequestHeader(required = false,defaultValue = "00") String newDeadLineMinute) throws InvalidInputException, TokenNotFoundException {

        taskService.editTaskDeadLine(taskId, newDeadLineDay, newDeadLineHour, newDeadLineMinute);
        return ResponseEntity.ok().build();
    }

    @GetMapping("find-by-id")
    public TaskResponse findById(@RequestHeader(name = "access-token") String token,@RequestHeader(name = "task-id") Long taskId) throws InvalidInputException, TokenNotFoundException {
        return taskService.findTask(taskId);
    }

    @GetMapping("find-all")
    public List<TaskResponse> findAll(@RequestHeader(name = "access-token") String token) throws InvalidInputException, TokenNotFoundException {
        return taskService.findAllTask();
    }

    @GetMapping("find-all-by-state")
    public List<TaskResponse> findAllByState(@RequestHeader(name = "access-token") String token,@RequestHeader(name = "finalized") String finalized) throws InvalidInputException, TokenNotFoundException {
        return taskService.findAllTaskByState(finalized);
    }

    @PostMapping("finalize")
    public ResponseEntity<?> finalizeTask(@RequestHeader(name = "access-token") String token, @RequestHeader(name = "task-id") Long taskId) throws InvalidInputException, TokenNotFoundException {
        taskService.finalizeTask(taskId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("finalize-all")
    public ResponseEntity<?> finalizeTask(@RequestHeader(name = "access-token") String token) throws InvalidInputException, TokenNotFoundException {
        taskService.finalizeAllTasks();
        return ResponseEntity.ok().build();
    }

}
