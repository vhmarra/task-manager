package br.com.taskmanager.service;

import br.com.taskmanager.config.TokenThread;
import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.TaskEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.exceptions.*;
import br.com.taskmanager.integration.IntegrationSendEmailRequest;
import br.com.taskmanager.dtos.request.UserSignUpRequest;
import br.com.taskmanager.dtos.response.SuccessResponse;
import br.com.taskmanager.dtos.response.TaskResponse;
import br.com.taskmanager.dtos.response.UserResponse;
import br.com.taskmanager.integration.SendEmailIntegrationService;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.TaskRepository;
import br.com.taskmanager.repository.UserRepository;
import br.com.taskmanager.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.taskmanager.service.ValidationService.isValidatePassword;
import static br.com.taskmanager.utils.Constants.SUPER_ADM;
import static br.com.taskmanager.utils.TokenUtils.generateToken;

@Service
@Slf4j
@AllArgsConstructor
public class UserService extends TokenService {

    private final UserRepository userRepository;
    private final ValidationService validator;
    private final ProfileService profileService;
    private final AccessTokenRepository accessTokenRepository;
    private final ScheduledService scheduledService;
    private final TaskRepository taskRepository;
    private final SendEmailIntegrationService emailIntegrationService;


    @Transactional(rollbackFor = Exception.class)
    public SuccessResponse saveUser(UserSignUpRequest request) throws InvalidInputException, ObjectAlreadyExistsException, ExternalApiException {
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new ObjectAlreadyExistsException("User already has register");
        }
        if (!validator.validateCPF(request.getCpf())) {
            throw new InvalidInputException("cpf invalid");
        }
        if (!validator.validateEmail(request.getEmail())) {
            throw new InvalidInputException("email invalid");
        }
        if (request.getNumero() < 0) {
            throw new InvalidInputException("address number is invalid");
        }

        UserEntity user = new UserEntity();
        user.setCpf(request.getCpf());


        //TODO USADO PARA TESTES
        user.setEmail("marravh@gmail.com");

        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());
        user.setProfiles(profileService.findProfileByID(Constants.ROLE_USER));
        user.setBirthDate(LocalDate.parse(request.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        user.setCpf(request.getCpf());

        AccessToken token = new AccessToken();
        token.setToken(generateToken());
        token.setUser(user);
        token.setIsActive(false);

        try {
            userRepository.save(user);
            accessTokenRepository.save(token);
            var email = new IntegrationSendEmailRequest();
            email.setEmailTo(user.getEmail());
            email.setSubject(user.getName() + " SEJA BEM VINDO");
            email.setMessage("Seja bem vindo ao seu gerenciador de tasks");
            emailIntegrationService.sendEmail(email);

        } catch (Exception e) {
            log.error("Erro ao salvar novo usuario {}", user.getName());
            throw e;
        }


        return new SuccessResponse("Usuario cadastrado com sucesso", 1102L);

    }

    public String authenticateUser(String cpf, String pass) throws NotFoundException, InvalidInputException {
        UserEntity user = userRepository.findByCpf(cpf).orElseThrow(() -> new NotFoundException("CPF or Password are invalid"));

        if (!isValidatePassword(pass, user)) {
            log.error("Password wrong");
            throw new InvalidInputException("Invalid input");
        }


        Optional<AccessToken> oldToken = accessTokenRepository.findByUser_IdAndIsActive(user.getId(), true);
        AccessToken token = new AccessToken();

        if (oldToken.isEmpty()) {
            token.setToken(generateToken());
            token.setUser(user);
            token.setIsActive(true);
            TokenThread.setToken(token);
            log.info("token thread {}", getAccessToken());
            accessTokenRepository.save(token);
        }
        if (oldToken.isPresent()) {
            throw new InvalidInputException("User is authenticated");
        }

        return getAccessToken();

    }

    public List<UserResponse> findAllUser() throws NotEnoughPermissionsException {
        if (!getUserEntity().getProfiles().stream().anyMatch(profile -> profile.getId().equals(SUPER_ADM))) {
            throw new NotEnoughPermissionsException("No permission for this action found!!!");
        }
        List<UserEntity> allUsers = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        List<TaskResponse> taskResponses = new ArrayList<>();

        allUsers.forEach(user -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setName(user.getName());
            userResponse.setEmail(user.getEmail());
            userResponse.setCpf(user.getCpf());
            userResponse.setBirthDate(user.getBirthDate());
            userResponse.setPassword("**************");
            userResponse.setId(user.getId());
            userResponse.setProfiles(user.getProfiles());
            List<TaskEntity> userTask = taskRepository.findAllByUser_Id(user.getId());
            userTask.forEach(task -> {
                TaskResponse taskResponse = new TaskResponse(task);
                taskResponses.add(taskResponse);
            });
            userResponse.setTasks(taskResponses);
            responses.add(userResponse);
        });
        return responses;
    }


    public void forceDisableTokens() throws NotEnoughPermissionsException, InvalidInputException {
        if (!getUserEntity().getProfiles().stream().anyMatch(profile -> profile.getId().equals(SUPER_ADM))) {
            throw new NotEnoughPermissionsException("No permission for this action found!!!");
        }
        scheduledService.disableTokenEvery20min();
    }

}
