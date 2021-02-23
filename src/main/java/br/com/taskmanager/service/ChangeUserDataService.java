package br.com.taskmanager.service;

import br.com.taskmanager.config.TokenThread;
import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.ChangeUserDataEntity;
import br.com.taskmanager.domain.EmailEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.ChangeUserDataRepository;
import br.com.taskmanager.repository.EmailRepository;
import br.com.taskmanager.repository.UserRepository;
import br.com.taskmanager.utils.EmailTypeEnum;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.taskmanager.utils.Constants.CHANGE_PASSWORD_BODY_EMAIL;
import static br.com.taskmanager.utils.Constants.CHANGE_PASSWORD_SUBJECT_EMAIL;
import static br.com.taskmanager.utils.Constants.CHANGE_PASSWORD_SUCCESSFULLY_BODY_EMAIL;
import static br.com.taskmanager.utils.Constants.CHANGE_PASSWORD_SUCCESSFULLY_SUBJECT_EMAIL;

@Service
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

        String code = DigestUtils.md2Hex(UUID.randomUUID().toString() + DigestUtils.sha256Hex(user.getEmail() + user.getName()) + DigestUtils.sha256(user.getName() + user.getEmail()));

        ChangeUserDataEntity changeUserDataEntity = new ChangeUserDataEntity();
        changeUserDataEntity.setUser(user);
        changeUserDataEntity.setUsed(0);
        changeUserDataEntity.setDateCreated(LocalDateTime.now());
        changeUserDataEntity.setCode(code);

        EmailEntity email = new EmailEntity();
        email.setSented(1);
        email.setEmailSubject(CHANGE_PASSWORD_SUBJECT_EMAIL);
        email.setMessage(CHANGE_PASSWORD_BODY_EMAIL.replace("user_name", user.getName()).replace("c_pass", code));
        email.setDateCreated(LocalDateTime.now());
        email.setDateSented(LocalDateTime.now());
        email.setEmailAddress(user.getEmail());
        email.setUser(user);
        email.setType(EmailTypeEnum.CHANGE_PASSWORD);

        emailService.sendEmail(email.getEmailAddress(), email.getUser().getName(), email.getEmailSubject(), email.getMessage());

        emailRepository.save(email);
        changeUserDataRepository.save(changeUserDataEntity);

    }

    public void changePassword(String code, String newPassword) throws InvalidInputException, MessagingException {
        ChangeUserDataEntity changeUserDataEntity = changeUserDataRepository.findByCode(code).orElse(null);

        if (changeUserDataEntity == null || changeUserDataEntity.getUsed().equals(1)) {
            throw new InvalidInputException("Invalid code, try again!");
        }

        UserEntity user = changeUserDataEntity.getUser();
        user.setPassword(DigestUtils.sha512Hex(newPassword));

        changeUserDataEntity.setDateUsed(LocalDateTime.now());
        changeUserDataEntity.setUsed(1);

        EmailEntity email = new EmailEntity();
        email.setSented(1);
        email.setEmailSubject(CHANGE_PASSWORD_SUCCESSFULLY_SUBJECT_EMAIL);
        email.setMessage(CHANGE_PASSWORD_SUCCESSFULLY_BODY_EMAIL.replace("user_name", user.getName()));
        email.setDateCreated(LocalDateTime.now());
        email.setDateSented(LocalDateTime.now());
        email.setEmailAddress(user.getEmail());
        email.setUser(user);
        email.setType(EmailTypeEnum.CHANGE_PASSWORD);

        AccessToken accessToken = accessTokenRepository.findByUser_Id(changeUserDataEntity.getUser().getId()).orElse(null);

        if (accessToken == null) {
            throw new InvalidInputException("Token validation failed");
        }

        accessToken.setIsActive(false);

        userRepository.save(user);
        accessTokenRepository.save(accessToken);

        emailService.sendEmail(email.getEmailAddress(), email.getUser().getName(), email.getEmailSubject(), email.getMessage());

        emailRepository.save(email);
        changeUserDataRepository.save(changeUserDataEntity);

    }


}
