package br.com.taskmanager.service;

import br.com.taskmanager.domain.TaskEntity;
import br.com.taskmanager.dtos.request.CreateTaskRequest;
import br.com.taskmanager.dtos.response.TaskResponse;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.repository.TaskRepository;
import br.com.taskmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TaskService extends TokenService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public void createTask(String taskDeadLine,String taskDescription){
        TaskEntity task = new TaskEntity();

        task.setTaskDescription(taskDescription);
        task.setDateCreated(LocalDateTime.now());

        if(!StringUtils.isEmpty(taskDeadLine)){
            task.setDateEnd(LocalDate.parse(taskDeadLine, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        task.setFinalized(false);
        task.setUser(getUserEntity());

        taskRepository.save(task);

    }

    public List<TaskResponse> findAllTask() throws InvalidInputException {
        List<TaskEntity> taskEntityList = taskRepository.findAllByUser_Id(getUserId());
        if(taskEntityList.isEmpty()){
            throw new InvalidInputException("User current have 0 active tasks");
        }

        List<TaskResponse> taskResponseList = new ArrayList<>();
        taskEntityList.forEach(task->{
            TaskResponse response = new TaskResponse();
            response.setDateCreated(task.getDateCreated().toString());
            if(task.getDateEnd() != null){
                response.setDateEnd(task.getDateEnd().toString());
            }
            response.setDescription(task.getTaskDescription());
            response.setFinalized(task.getFinalized().toString());
            response.setId(task.getId());
            taskResponseList.add(response);
        });
        return taskResponseList;
    }

    public void finalizeTask(Long id) throws InvalidInputException {
        TaskEntity task = taskRepository.findById(id).orElse(null);

        if(task == null){
            throw new InvalidInputException("Task not found");
        }
        if(task.getFinalized().equals(true)){
            throw new InvalidInputException("Task already finalized");
        }

        task.setFinalized(true);
        task.setDateEnd(LocalDate.now());

        taskRepository.save(task);

    }

    public List<TaskResponse> findAllTaskByState(String finalized) throws InvalidInputException {
        List<TaskEntity> taskEntityList = taskRepository.findAllByUser_IdAndFinalized(getUserId(),Boolean.valueOf(finalized));

        if(!finalized.equalsIgnoreCase("true") && !finalized.equalsIgnoreCase("false")){
            throw new InvalidInputException("Status invalid");
        }

        if(taskEntityList.isEmpty() && finalized.equals("true")){
            throw new InvalidInputException("User current have 0 finalized tasks");
        }

        if(taskEntityList.isEmpty() && finalized.equals("false")){
            throw new InvalidInputException("User current have 0 active tasks");
        }

        List<TaskResponse> taskResponseList = new ArrayList<>();
        taskEntityList.forEach(task->{
            TaskResponse response = new TaskResponse();
            response.setDateCreated(task.getDateCreated().toString());
            if(task.getDateEnd() != null){
                response.setDateEnd(task.getDateEnd().toString());
            }
            response.setDescription(task.getTaskDescription());
            response.setFinalized(task.getFinalized().toString());
            response.setId(task.getId());
            taskResponseList.add(response);
        });
        return taskResponseList;
    }

}
