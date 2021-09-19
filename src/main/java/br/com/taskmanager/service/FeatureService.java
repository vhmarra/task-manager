package br.com.taskmanager.service;

import br.com.taskmanager.repository.FeatureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@Slf4j
public class FeatureService {

    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @Scheduled(initialDelay = 10L, fixedRate = 6000000000000000000L)
    public void createFeature() {
        log.info("Generating sql for features");
        featureRepository.saveFeature();
    }

}