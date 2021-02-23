package br.com.taskmanager.service;

import br.com.taskmanager.config.TokenThread;
import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.EmailEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.dtos.request.UserSignUpRequest;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotEnoughPermissionsException;
import br.com.taskmanager.exceptions.NotFoundException;
import br.com.taskmanager.exceptions.ObjectAlreadyExistsException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.EmailRepository;
import br.com.taskmanager.repository.UserRepository;
import br.com.taskmanager.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;

import static br.com.taskmanager.utils.Constants.SUPER_ADM;
import static br.com.taskmanager.utils.EmailTypeEnum.WELCOME;

@Service
@Slf4j
public class UserService extends TokenService {

    private final UserRepository userRepository;
    private final ValidationService validator;
    private final ProfileService profileService;
    private final EmailRepository emailRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final ScheduledService scheduledService;


    public UserService(UserRepository userRepository, ValidationService validator, ProfileService profileService, EmailRepository emailRepository, AccessTokenRepository accessTokenRepository, ScheduledService scheduledService) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.profileService = profileService;
        this.emailRepository = emailRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.scheduledService = scheduledService;
    }

    public void saveUser(UserSignUpRequest request) throws InvalidInputException, ObjectAlreadyExistsException {
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new ObjectAlreadyExistsException("Usuario já possui cadastro");
        }
        if (!validator.validateCPF(request.getCpf())) {
            throw new InvalidInputException("cpf invalido");
        }
        if (!validator.validateEmail(request.getEmail())) {
            throw new InvalidInputException("email invalido");
        }

        UserEntity user = new UserEntity();
        user.setCpf(request.getCpf());

        //TODO USADO PARA TESTES
        user.setEmail("marravh@gmail.com");

        user.setPassword(DigestUtils.sha512Hex(request.getPassword()));
        user.setName(request.getName());
        user.setProfiles(profileService.findProfileByID(Constants.ROLE_USER));
        user.setBirthDate(LocalDate.parse(request.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        user.setCpf(request.getCpf());

        EmailEntity emailBoasVindas = new EmailEntity();
        emailBoasVindas.setEmailAddress(user.getEmail());
        emailBoasVindas.setEmailSubject(user.getName() + " SEJA BEM VINDO");
        emailBoasVindas.setUser(user);
        emailBoasVindas.setDateCreated(LocalDateTime.now());
        emailBoasVindas.setSented(0);
        emailBoasVindas.setType(WELCOME);
        emailBoasVindas.setMessage("Seja bem vindo ao seu gerenciador de tasks");

        AccessToken token = new AccessToken();
        Random r = new Random();
        token.setToken(DigestUtils.sha1Hex(r.ints(97, 122)
                .limit(30L)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString()));
        token.setUser(user);
        token.setIsActive(false);

        userRepository.save(user);
        accessTokenRepository.save(token);
        emailRepository.save(emailBoasVindas);
    }

    public String authenticateUser(String cpf, String pass) throws NotFoundException, InvalidInputException {
        UserEntity user = userRepository.findByCpfAndPassword(cpf, DigestUtils.sha512Hex(pass)).orElse(null);

        if (user == null) {
            log.error("user with cpf {} not found", pass);
            throw new NotFoundException("CPF or Password are invalid");
        }

        AccessToken token = new AccessToken();
        Random r = new Random();

        Optional<AccessToken> oldToken = accessTokenRepository.findByUser_IdAndIsActive(user.getId(), true);

        if (oldToken.isEmpty()) {
            token.setToken(DigestUtils.sha1Hex(r.ints(97, 122)
                    .limit(30L)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString()));
            token.setUser(user);
            token.setIsActive(true);
            TokenThread.setToken(token);
            log.info("token thread {}", getAccessToken());
            accessTokenRepository.save(token);
        }
        if (oldToken.isPresent()) {
            throw new InvalidInputException("User is authenticated");
        }

        return token.getToken();

    }

    public void forceSendEmail() throws InvalidInputException, NotEnoughPermissionsException {
        if (!getUserEntity().getProfiles().contains(profileService.findProfileByID(SUPER_ADM).get(0))) {
            throw new NotEnoughPermissionsException("No permission for this action found!!!");
        }
        scheduledService.sendAllEmails();
    }

    public void forceDisableTokens() throws NotEnoughPermissionsException, InvalidInputException {
        if (!getUserEntity().getProfiles().contains(profileService.findProfileByID(SUPER_ADM).get(0))) {
            throw new NotEnoughPermissionsException("No permission for this action found!!!");
        }
        scheduledService.disableTokenEvery20min();
    }


}
