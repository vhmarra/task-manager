package br.com.taskmanager.service;


import br.com.taskmanager.domain.EmailEntity;
import br.com.taskmanager.dtos.request.IntegrationSendEmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class IntegrationService {

    private final RestTemplate restTemplate;
    private final Environment env;

    public IntegrationService(RestTemplate restTemplate, Environment env) {
        this.restTemplate = restTemplate;
        this.env = env;
    }

    public void sendEmail(EmailEntity emailEntity) {
        var request =  new IntegrationSendEmailRequest();
        request.setEmailTo(emailEntity.getEmailAddress());
        request.setSubject(emailEntity.getEmailSubject());
        request.setMessage(emailEntity.getMessage());
        try {
            restTemplate.postForEntity(env.getProperty("manager.utils.address")+"email",request,Void.class);
        } catch (Exception e) {
            log.error("Error to intregate with service");
            throw e;
        }



    }

}
