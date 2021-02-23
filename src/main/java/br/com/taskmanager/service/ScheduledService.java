package br.com.taskmanager.service;

import br.com.taskmanager.domain.ChangeUserDataEntity;
import br.com.taskmanager.domain.TaskEntity;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.ChangeUserDataRepository;
import br.com.taskmanager.repository.EmailRepository;
import br.com.taskmanager.repository.TaskRepository;
import br.com.taskmanager.utils.EmailTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import static br.com.taskmanager.utils.Constants.SUPER_ADM;
import static br.com.taskmanager.utils.Constants.WELCOME_BODY_EMAIL;
import static br.com.taskmanager.utils.Constants.WELCOME_SUBJECT_EMAIL;

@Service
@EnableScheduling
@Slf4j
public class ScheduledService {

    private final AccessTokenRepository accessTokenRepository;
    private final ProfileService profileService;
    private final EmailRepository emailRepository;
    private final EmailService emailService;
    private final ChangeUserDataRepository changeUserDataRepository;
    private final TaskRepository taskRepository;

    public ScheduledService(AccessTokenRepository accessTokenRepository, ProfileService profileService, EmailRepository emailRepository, EmailService emailService, ChangeUserDataRepository changeUserDataRepository, TaskRepository taskRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.profileService = profileService;
        this.emailRepository = emailRepository;
        this.emailService = emailService;
        this.changeUserDataRepository = changeUserDataRepository;
        this.taskRepository = taskRepository;
    }

    @Scheduled(initialDelay = 1200000L, fixedRate = 1200000L)
    public void disableTokenEvery20min(){
        log.info("DISABLING TOKENS...");
        accessTokenRepository.findAllByIsActive(true).forEach(accessToken -> {
            try {
                if(!accessToken.getUser().getProfiles().contains(profileService.findProfileByID(SUPER_ADM).get(0))){
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

    @Scheduled(initialDelay = 15L, fixedRate = 6000l)
    public void sendAllEmails() {
        emailRepository.findAllBySented(0).forEach(email -> {
            if (email.getType().equals(EmailTypeEnum.WELCOME)) {
                try {
                    emailService.sendEmail(email.getEmailAddress(),
                            email.getUser().getEmail(),
                            WELCOME_SUBJECT_EMAIL,
                            WELCOME_BODY_EMAIL.replace("user_name", email.getUser().getName()));
                    email.setSented(1);
                    email.setDateSented(LocalDateTime.now());
                    emailRepository.save(email);
                } catch (Exception e) {
                    log.error("ERROR TO SEND EMAIL! -> {}", e.getCause());
                }
            }

        });
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

}
