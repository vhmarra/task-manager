package br.com.taskmanager.controllers;

import br.com.taskmanager.dtos.request.CreateTaskRequest;
import br.com.taskmanager.dtos.response.TaskResponse;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
                                        @RequestHeader(name = "task-dead-line",required = false) String taskDeadLine){
        taskService.createTask(taskDeadLine, taskDescription);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("find-all")
    public List<TaskResponse> findAll(@RequestHeader(name = "access-token") String token) throws InvalidInputException {
        return taskService.findAllTask();
    }

    @GetMapping("find-all-by-state")
    public List<TaskResponse> findAllByState(@RequestHeader(name = "access-token") String token,@RequestHeader(name = "finalized") String finalized) throws InvalidInputException {
        return taskService.findAllTaskByState(finalized);
    }

    @PostMapping("finalize")
    public ResponseEntity<?> finalizeTask(@RequestHeader(name = "access-token") String token, @RequestHeader(name = "task-id") Long taskId) throws InvalidInputException {
        taskService.finalizeTask(taskId);
        return ResponseEntity.ok().build();
    }
}
