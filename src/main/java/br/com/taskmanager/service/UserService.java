package br.com.taskmanager.service;

import br.com.taskmanager.config.TokenThread;
import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.Address;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.dtos.request.UserSignUpRequest;
import br.com.taskmanager.dtos.response.SuccessResponse;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotEnoughPermissionsException;
import br.com.taskmanager.exceptions.NotFoundException;
import br.com.taskmanager.exceptions.ObjectAlreadyExistsException;
import br.com.taskmanager.exceptions.ObjectNotFoundException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.AddressRepository;
import br.com.taskmanager.repository.EmailRepository;
import br.com.taskmanager.repository.UserRepository;
import br.com.taskmanager.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static br.com.taskmanager.utils.Constants.SUPER_ADM;
import static br.com.taskmanager.utils.EmailTypeEnum.WELCOME;
import static br.com.taskmanager.utils.TokenUtils.generateToken;

@Service
@Slf4j
public class UserService extends TokenService {

    private final UserRepository userRepository;
    private final ValidationService validator;
    private final ProfileService profileService;
    private final EmailRepository emailRepository;
    private final AccessTokenRepository accessTokenRepository;
    private final ScheduledService scheduledService;
    private final EmailService emailService;
    private final AddressRepository addressRepository;
    private final AddressService addressService;


    public UserService(UserRepository userRepository, ValidationService validator, ProfileService profileService,
                       EmailRepository emailRepository, AccessTokenRepository accessTokenRepository,
                       ScheduledService scheduledService, EmailService emailService,
                       AddressRepository addressRepository, AddressService addressService) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.profileService = profileService;
        this.emailRepository = emailRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.scheduledService = scheduledService;
        this.emailService = emailService;
        this.addressRepository = addressRepository;
        this.addressService = addressService;
    }

    public SuccessResponse saveUser(UserSignUpRequest request) throws InvalidInputException, ObjectAlreadyExistsException {
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new ObjectAlreadyExistsException("User already has register");
        }
        if (!validator.validateCPF(request.getCpf())) {
            throw new InvalidInputException("cpf invalid");
        }
        if (!validator.validateEmail(request.getEmail())) {
            throw new InvalidInputException("email invalid");
        }
        if (request.getNumero() < 0){
            throw new InvalidInputException("address number is invalid");
        }

        UserEntity user = new UserEntity();
        user.setCpf(request.getCpf());

        List<Address> userAddres = addressService.getAddressByCep(request.getCep(),request.getNumero().toString(), request.getComplemento());
        if(!userAddres.isEmpty()){
            user.setAddresses(userAddres);
            addressRepository.save(userAddres.get(0));
        }

        //TODO USADO PARA TESTES
        user.setEmail("marravh@gmail.com");

        user.setPassword(DigestUtils.sha512Hex(request.getPassword()));
        user.setName(request.getName());
        user.setProfiles(profileService.findProfileByID(Constants.ROLE_USER));
        user.setBirthDate(LocalDate.parse(request.getBirthDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        user.setCpf(request.getCpf());

        AccessToken token = new AccessToken();
        token.setToken(generateToken());
        token.setUser(user);
        token.setIsActive(false);

        userRepository.save(user);
        accessTokenRepository.save(token);
        emailRepository.save(emailService.sendEmailToUser(user,(user.getName() + " SEJA BEM VINDO"),WELCOME,"Seja bem vindo ao seu gerenciador de tasks"));

        if(userAddres.isEmpty()){
            return new SuccessResponse("Usuario cadastrado com sucesso, mas com endereço incompleto",1101L);
        }else {
            return new SuccessResponse("Usuario cadastrado com sucesso",1102L);
        }
    }

    public String authenticateUser(String cpf, String pass) throws NotFoundException, InvalidInputException {
        UserEntity user = userRepository.findByCpfAndPassword(cpf, DigestUtils.sha512Hex(pass)).orElse(null);

        if (user == null) {
            log.error("user with cpf {} not found", pass);
            throw new NotFoundException("CPF or Password are invalid");
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

    public void forceSendEmail() throws InvalidInputException, NotEnoughPermissionsException, ObjectNotFoundException {
        if (!getUserEntity().getProfiles().contains(profileService.findProfileByID(SUPER_ADM).get(0))) {
            throw new NotEnoughPermissionsException("No permission for this action found!!!");
        }
        scheduledService.sendAllEmails();
        scheduledService.sendBirthdayEmail();
    }

    public void forceDisableTokens() throws NotEnoughPermissionsException, InvalidInputException {
        if (!getUserEntity().getProfiles().contains(profileService.findProfileByID(SUPER_ADM).get(0))) {
            throw new NotEnoughPermissionsException("No permission for this action found!!!");
        }
        scheduledService.disableTokenEvery20min();
    }


}
