package br.com.taskmanager.service;

import br.com.taskmanager.domain.TaskEntity;
import br.com.taskmanager.dtos.request.EditTaskRequest;
import br.com.taskmanager.dtos.response.TaskResponse;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.TokenNotFoundException;
import br.com.taskmanager.repository.TaskRepository;
import br.com.taskmanager.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TaskService extends TokenService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ValidationService validationService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, ValidationService validationService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.validationService = validationService;
    }


    public void createTask(String taskDeadLine, String taskDescription, String deadLineHour, String deadLineMinute, Integer priority) throws TokenNotFoundException, InvalidInputException, IOException {
        if (!getAccessTokenEntity().getIsActive()) {
            throw new TokenNotFoundException("Token is invalid!");
        }
        if (!validationService.validateDate(taskDeadLine)) {
            throw new InvalidInputException("Date is invalid");
        }
        if (!deadLineHour.isBlank() && Integer.valueOf(deadLineHour) > 23 || Integer.valueOf(deadLineHour) < 0) {
            throw new InvalidInputException("Hour is invalid");
        }
        if (!deadLineMinute.isBlank() && Integer.valueOf(deadLineMinute) > 59 || Integer.valueOf(deadLineMinute) < 0) {
            throw new InvalidInputException("Minute is invalid");
        }

        TaskEntity task = new TaskEntity();
        task.setTaskDescription(taskDescription);
        task.setDateCreated(LocalDateTime.now());

        if (StringUtils.isEmpty(taskDeadLine)) {
            task.setDateEnd(LocalDateTime.MAX);
        }
        if (!StringUtils.isEmpty(taskDeadLine)) {
            if (!StringUtils.isEmpty(deadLineHour) && !StringUtils.isEmpty(deadLineMinute)) {
                String deadLine = taskDeadLine + " " + deadLineHour + ":" + deadLineMinute;
                task.setDateEnd(LocalDateTime.parse(deadLine, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            } else if (StringUtils.isEmpty(deadLineHour) && StringUtils.isEmpty(deadLineMinute)) {
                String deadLine = taskDeadLine + " " + "00" + ":" + "00";
                task.setDateEnd(LocalDateTime.parse(deadLine, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        }
        if (priority == null) {
            task.setPriority(0);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        task.setFinalized(false);
        task.setUser(getUserEntity());


        taskRepository.save(task);
    }

    public List<TaskResponse> findAllTask() throws InvalidInputException, TokenNotFoundException {
        if (!getAccessTokenEntity().getIsActive()) {
            throw new TokenNotFoundException("Token is invalid");
        }

        List<TaskEntity> taskEntityList = taskRepository.findAllByUser_Id(getUserId());
        if (taskEntityList.isEmpty()) {
            throw new InvalidInputException("User current have 0 active tasks");
        }

        List<TaskResponse> taskResponseList = new ArrayList<>();
        taskEntityList.forEach(task -> {
            TaskResponse response = new TaskResponse(task);
            taskResponseList.add(response);
        });
        return taskResponseList;
    }

    public void finalizeTask(Long id) throws InvalidInputException, TokenNotFoundException {
        if (!getAccessTokenEntity().getIsActive()) {
            throw new TokenNotFoundException("Token is invalid");
        }

        TaskEntity task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            throw new InvalidInputException("Task not found");
        }
        if (task.getFinalized().equals(true)) {
            throw new InvalidInputException("Task already finalized");
        }

        task.setFinalized(true);
        task.setDateEnd(LocalDateTime.now());

        taskRepository.save(task);

    }

    public List<TaskResponse> findAllTaskByState(String finalized) throws InvalidInputException, TokenNotFoundException {
        if (!getAccessTokenEntity().getIsActive()) {
            throw new TokenNotFoundException("Token is invalid");
        }

        List<TaskEntity> taskEntityList = taskRepository.findAllByUser_IdAndFinalized(getUserId(), Boolean.valueOf(finalized));

        if (!finalized.equalsIgnoreCase("true") && !finalized.equalsIgnoreCase("false")) {
            throw new InvalidInputException("Status invalid");
        }

        if (taskEntityList.isEmpty() && finalized.equals("true")) {
            throw new InvalidInputException("User current have 0 finalized tasks");
        }

        if (taskEntityList.isEmpty() && finalized.equals("false")) {
            throw new InvalidInputException("User current have 0 active tasks");
        }

        List<TaskResponse> taskResponseList = new ArrayList<>();
        taskEntityList.forEach(task -> {
            TaskResponse response = new TaskResponse(task);
            taskResponseList.add(response);
        });
        return taskResponseList;
    }

    public void finalizeAllTasks() throws TokenNotFoundException, InvalidInputException {
        if (!getAccessTokenEntity().getIsActive()) {
            throw new TokenNotFoundException("Token is invalid");
        }
        List<TaskEntity> tasks = taskRepository.findAllByUser_IdAndFinalized(getUserId(), false);

        if (tasks.isEmpty()) {
            throw new InvalidInputException("User not have any active tasks");
        }
        tasks.forEach(task -> {
            task.setFinalized(true);
            task.setDateEnd(LocalDateTime.now());
            taskRepository.save(task);
        });
    }

    public TaskResponse findTask(Long taskId) throws TokenNotFoundException, InvalidInputException {
        if (!getAccessTokenEntity().getIsActive()) {
            throw new TokenNotFoundException("Token is invalid");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);

        if (taskEntity == null) {
            throw new InvalidInputException("Task not found");
        }
        if (taskEntity.getFinalized()) {
            throw new InvalidInputException("Cant get a finalized task");
        }
        TaskResponse response = new TaskResponse(taskEntity);
        return response;
    }

    public void editTaskDescription(Long taskId, String newDescription, Integer priority) throws TokenNotFoundException, InvalidInputException {
        if (!getAccessTokenEntity().getIsActive()) {
            throw new TokenNotFoundException("Token is invalid");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);

        if (taskEntity == null) {
            throw new InvalidInputException("Task not found");
        }
        if (newDescription.isBlank()) {
            throw new InvalidInputException("Field cannot be null");
        }
        if (priority != 0 || priority != 1) {
            throw new InvalidInputException("Invalid priority value");
        }
        if (taskEntity.getPriority() == priority) {
            throw new InvalidInputException("Invalid priority update");
        }
        if (priority != null) {
            taskEntity.setPriority(priority);
        }
        taskEntity.setTaskDescription(newDescription);

    }

    public void editTaskDeadLine(Long taskId, String newDeadLineDay, String newDeadLineHour, String newDeadLineMinute) throws TokenNotFoundException, InvalidInputException {
        if (!getAccessTokenEntity().getIsActive()) {
            throw new TokenNotFoundException("Token is invalid");
        }
        TaskEntity taskEntity = taskRepository.findById(taskId).orElse(null);

        if (taskEntity == null) {
            throw new InvalidInputException("Task not found");
        }
        if (newDeadLineDay.isBlank()) {
            throw new InvalidInputException("Field cannot be null");
        }
        if (!validationService.validateDate(newDeadLineDay)) {
            throw new InvalidInputException("Date " + newDeadLineDay + " is invalid");
        }
        if (Integer.valueOf(newDeadLineHour) > 23 || Integer.valueOf(newDeadLineHour) < 0
                && Integer.valueOf(newDeadLineMinute) > 59 || Integer.valueOf(newDeadLineMinute) < 0) {
            throw new InvalidInputException("Minute " + newDeadLineMinute + " or Hour " + newDeadLineHour + " is invalid");
        }
        if (newDeadLineHour.equals("00") || newDeadLineMinute.equals("00")) {
            String deadLine = newDeadLineDay + " " + "00" + ":" + "00";
            taskEntity.setDateEnd(LocalDateTime.parse(deadLine, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
        if (!newDeadLineHour.equals("00") && !newDeadLineMinute.equals("00")) {
            String deadLine = newDeadLineDay + " " + newDeadLineHour + ":" + newDeadLineMinute;
            taskEntity.setDateEnd(LocalDateTime.parse(deadLine, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        }
        taskRepository.save(taskEntity);

    }
}
