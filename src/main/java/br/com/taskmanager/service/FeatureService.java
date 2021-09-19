package br.com.taskmanager.service;

import br.com.taskmanager.repository.FeatureRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class FeatureService {

    private final FeatureRepository featureRepository;

    //FIXME fix this to include all toggle in a migration .sql archive on application initialization
    @Scheduled(initialDelay = 10L, fixedRate = 6000000000000000000L)
    public void createFeature() {
        log.info("Generating sql for features");
        featureRepository.saveFeature();
    }

}
