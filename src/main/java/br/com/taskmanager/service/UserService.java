package br.com.taskmanager.service;

import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.dtos.request.UserSignUpRequest;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.ObjectAlreadyExistsException;
import br.com.taskmanager.repository.UserRepository;
import br.com.taskmanager.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ValidationService validator;
    private final ProfileService profileService;


    public UserService(UserRepository userRepository, ValidationService validator, ProfileService profileService) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.profileService = profileService;
    }

    public void saveUser(UserSignUpRequest request) throws InvalidInputException, ObjectAlreadyExistsException {
        if (userRepository.existsByCpf(request.getCpf())){
            throw new ObjectAlreadyExistsException("Usuario já possui cadastro");
        }
        if (!validator.validateCPF(request.getCpf())) {
            throw new InvalidInputException("cpf invalido");
        }
        if(!validator.validateEmail(request.getEmail())){
            throw new InvalidInputException("email invalido");
        }

        UserEntity user = new UserEntity();
        user.setCpf(request.getCpf());

        //TODO USADO PARA TESTES
        user.setEmail("marravh@gmail.com");

        user.setPassword(DigestUtils.sha512Hex(request.getPassword()));
        user.setName(request.getName());
        user.setProfiles(profileService.findProfileByID(Constants.ROLE_USER));
        user.setBirthDate(LocalDateTime.parse(request.getBirthDate(),DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        user.setCpf(request.getCpf());


    }

}
