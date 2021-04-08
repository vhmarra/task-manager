package br.com.taskmanager.service;

import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.ChangeUserDataEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.ChangeUserDataRepository;
import br.com.taskmanager.repository.EmailRepository;
import br.com.taskmanager.repository.UserRepository;
import br.com.taskmanager.utils.EmailTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.taskmanager.utils.Constants.CHANGE_PASSWORD_BODY_EMAIL;
import static br.com.taskmanager.utils.Constants.CHANGE_PASSWORD_SUBJECT_EMAIL;
import static br.com.taskmanager.utils.Constants.CHANGE_PASSWORD_SUCCESSFULLY_BODY_EMAIL;
import static br.com.taskmanager.utils.Constants.CHANGE_PASSWORD_SUCCESSFULLY_SUBJECT_EMAIL;
import static br.com.taskmanager.utils.TokenUtils.generateEmailToken;

@Service
@Slf4j
public class ChangeUserDataService {

    private final UserRepository userRepository;
    private final ChangeUserDataRepository changeUserDataRepository;
    private final EmailService emailService;
    private final EmailRepository emailRepository;
    private final AccessTokenRepository accessTokenRepository;

    public ChangeUserDataService(UserRepository userRepository, ChangeUserDataRepository changeUserDataRepository, EmailService emailService, EmailRepository emailRepository, AccessTokenRepository accessTokenRepository) {
        this.userRepository = userRepository;
        this.changeUserDataRepository = changeUserDataRepository;
        this.emailService = emailService;
        this.emailRepository = emailRepository;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Transactional
    public void requestChangePassword(String userCpf, String userEmailConfirm) throws InvalidInputException, MessagingException {
        UserEntity user = userRepository.findByCpf(userCpf).orElse(null);

        if (user == null || !user.getEmail().equalsIgnoreCase(userEmailConfirm)) {
            throw new InvalidInputException("Dados invalidos");
        }

        String code = generateEmailToken(user);

        ChangeUserDataEntity changeUserDataEntity = new ChangeUserDataEntity();
        changeUserDataEntity.setUser(user);
        changeUserDataEntity.setUsed(0);
        changeUserDataEntity.setDateCreated(LocalDateTime.now());
        changeUserDataEntity.setCode(code);

        changeUserDataRepository.save(changeUserDataEntity);
        emailRepository.save(emailService.sendEmailToUser(user,
                CHANGE_PASSWORD_SUBJECT_EMAIL,
                EmailTypeEnum.CHANGE_PASSWORD,CHANGE_PASSWORD_BODY_EMAIL
                        .replace("user_name", user.getName()).replace("c_pass", code)));
    }

    public void changePassword(String code, String newPassword) throws InvalidInputException, MessagingException {
        ChangeUserDataEntity changeUserDataEntity = changeUserDataRepository.findByCodeAndUsed(code,0).orElse(null);

        if (changeUserDataEntity == null || changeUserDataEntity.getUsed().equals(1)) {
            throw new InvalidInputException("Invalid code, try again!");
        }

        UserEntity user = changeUserDataEntity.getUser();
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));

        changeUserDataEntity.setDateUsed(LocalDateTime.now());
        changeUserDataEntity.setUsed(1);

        List<AccessToken> accessToken = accessTokenRepository.findAllByUser_Id(changeUserDataEntity.getUser().getId());

        accessToken.forEach(token->{
            if (token.getIsActive().equals(true)){
                token.setIsActive(false);
            }
        });
        userRepository.save(user);
        accessTokenRepository.saveAll(accessToken);
        changeUserDataRepository.save(changeUserDataEntity);
        emailRepository.save(emailService.sendEmailToUser(user,
                CHANGE_PASSWORD_SUCCESSFULLY_SUBJECT_EMAIL,
                EmailTypeEnum.CHANGE_PASSWORD,CHANGE_PASSWORD_SUCCESSFULLY_BODY_EMAIL
                        .replace("user_name", user.getName())));
        log.info("Senha do usuario {} alterada com sucesso",user.getName());

    }


}
