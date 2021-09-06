package br.com.taskmanager.dtos.response;

import br.com.taskmanager.domain.ProfileEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String cpf;
    private String password;
    private LocalDate birthDate;
    private List<TaskResponse> tasks;
    private List<ProfileEntity> profiles;

}
