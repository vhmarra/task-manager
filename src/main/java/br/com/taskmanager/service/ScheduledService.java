package br.com.taskmanager.service;

import br.com.taskmanager.domain.ChangeUserDataEntity;
import br.com.taskmanager.domain.EmailEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.exceptions.ObjectNotFoundException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.ChangeUserDataRepository;
import br.com.taskmanager.repository.EmailRepository;
import br.com.taskmanager.repository.TaskRepository;
import br.com.taskmanager.repository.UserRepository;
import br.com.taskmanager.utils.EmailTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.taskmanager.utils.Constants.BIRTHDAY_BODY_EMAIL;
import static br.com.taskmanager.utils.Constants.BIRTHDAY_SUBJECT_EMAIL;
import static br.com.taskmanager.utils.Constants.SUPER_ADM;

@Service
@EnableScheduling
@Slf4j
public class ScheduledService {

    private final AccessTokenRepository accessTokenRepository;
    private final ProfileService profileService;
    private final EmailRepository emailRepository;
    private final EmailService emailService;
    private final ChangeUserDataRepository changeUserDataRepository;
    private final UserRepository userRepository;

    public ScheduledService(AccessTokenRepository accessTokenRepository, ProfileService profileService, EmailRepository emailRepository, EmailService emailService, ChangeUserDataRepository changeUserDataRepository, UserRepository userRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.profileService = profileService;
        this.emailRepository = emailRepository;
        this.emailService = emailService;
        this.changeUserDataRepository = changeUserDataRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(initialDelay = 1200000L, fixedRate = 1200000L)
    public void disableTokenEvery20min() {
        log.info("DISABLING TOKENS...");
        accessTokenRepository.findAllByIsActive(true).forEach(accessToken -> {
            try {
                if (!accessToken.getUser().getProfiles().contains(profileService.findProfileByID(SUPER_ADM).get(0))) {
                    accessToken.setIsActive(false);
                    accessTokenRepository.save(accessToken);
                }
            } catch (Exception e) {
                log.error("ERROR TO DISABLE TOKENS");
            }
        });
        accessTokenRepository.deleteAll(accessTokenRepository.findAllByIsActive(false));
        log.info("ALL TOKENS HAS BEEN DISABLE!");
    }

    @Scheduled(cron = "0 * * ? * *")
    public void sendAllEmails() {
        List<EmailEntity> emails = emailRepository.findAllBySented(0);
        if (emails.isEmpty()) {
            log.warn("No emails to sent");
        } else {
            emails.forEach(email -> {
                if (email.getType() != EmailTypeEnum.BIRTHDAY) {
                    try {
                        email.setSented(1);
                        email.setDateSented(LocalDateTime.now());
                        emailService.sendEmail(email);
                        emailRepository.save(email);

                    } catch (Exception e) {
                        log.error("error {}", e.getCause().toString());
                    }
                }
            });
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void disableALlChangeDataTokens() {
        log.info("STARTING DISABLE TOKENS JOB...");
        List<ChangeUserDataEntity> changeUserDataEntityList = changeUserDataRepository.findAllByUsed(0);

        changeUserDataEntityList.forEach(entity -> {
            entity.setUsed(1);
            entity.setDateUsed(LocalDateTime.now());
            changeUserDataRepository.save(entity);
        });
        log.info("DISABLE TOKENS JOB FINISHED!");
    }

    //@Scheduled(cron = "0 0 0 * * ?")
    public void sendBirthdayEmail() throws ObjectNotFoundException {
        List<UserEntity> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ObjectNotFoundException("Error to fetch user list");
        }
        users.forEach(user -> {
            if (user.getBirthDate().getMonth() == LocalDate.now().getMonth()
                    && user.getBirthDate().getDayOfMonth() == LocalDate.now().getDayOfMonth()) {
                EmailEntity email = emailService.sendEmailToUser(user, BIRTHDAY_SUBJECT_EMAIL, EmailTypeEnum.BIRTHDAY, BIRTHDAY_BODY_EMAIL.replace("user_name", user.getName()));
                email.setSented(1);
                try {
                    emailService.sendEmail(email);
                } catch (Exception e) {
                    log.error("Error to send email {}", e.getCause().toString());
                }
                emailRepository.save(email);
            }
        });
    }

}
