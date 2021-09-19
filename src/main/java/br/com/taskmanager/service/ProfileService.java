package br.com.taskmanager.service;

import br.com.taskmanager.domain.ProfileEntity;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.repository.FeatureRepository;
import br.com.taskmanager.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }


    @Scheduled(initialDelay = 10L, fixedRate = 6000000000000000000L)
    public void createProfiles() {
        if (profileRepository.findAll().isEmpty()) {
            List<ProfileEntity> perfis = new ArrayList<>();

            ProfileEntity perfil1 = new ProfileEntity();
            ProfileEntity perfil2 = new ProfileEntity();
            ProfileEntity perfil3 = new ProfileEntity();

            perfil1.setName("user");
            perfil2.setName("adm");
            perfil3.setName("super_adm");

            perfis.add(perfil1);
            perfis.add(perfil2);
            perfis.add(perfil3);

            profileRepository.saveAll(perfis);

        } else {

        }
    }

    public List<ProfileEntity> findProfileByID(Long id) throws InvalidInputException {
        ProfileEntity profile = profileRepository.findById(id).orElse(null);

        if (profile == null) {
            throw new InvalidInputException("PROFILE WITH ID " + id + " DOES NOT EXIST");
        }

        List<ProfileEntity> profileList = new ArrayList<>();
        profileList.add(profile);

        return profileList;

    }



}
