package br.com.taskmanager.service;

import br.com.taskmanager.domain.ChangeUserDataEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.exceptions.ObjectNotFoundException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.ChangeUserDataRepository;
import br.com.taskmanager.repository.UserRepository;
import br.com.taskmanager.utils.EmailTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.taskmanager.utils.Constants.BIRTHDAY_BODY_EMAIL;
import static br.com.taskmanager.utils.Constants.BIRTHDAY_SUBJECT_EMAIL;
import static br.com.taskmanager.utils.Constants.SUPER_ADM;

@Service
@EnableScheduling
@Slf4j
public class ScheduledService {

    private final AccessTokenRepository accessTokenRepository;
    private final ProfileService profileService;
    private final ChangeUserDataRepository changeUserDataRepository;

    public ScheduledService(AccessTokenRepository accessTokenRepository, ProfileService profileService, ChangeUserDataRepository changeUserDataRepository) {
        this.accessTokenRepository = accessTokenRepository;
        this.profileService = profileService;
        this.changeUserDataRepository = changeUserDataRepository;

    }

    @Scheduled(initialDelay = 100L, fixedRate = 1200000L)
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


    @Scheduled(initialDelay = 1L,fixedDelay = 10000L)
    public void disableOldDataChangeToken(){
        List<ChangeUserDataEntity> changeUserDataEntityList = changeUserDataRepository.findAllByUsed(0);
        if (!changeUserDataEntityList.isEmpty()){
            changeUserDataEntityList.forEach(token->{
                if (LocalDateTime.now().getMinute() - token.getDateCreated().getMinute()  >= 15){
                    token.setDateUsed(LocalDateTime.now());
                    token.setUsed(1);
                    changeUserDataRepository.save(token);
                    log.info("All tokens are disable after 15 minutes");
                }
            });
        }
    }

}
