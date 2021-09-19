package br.com.taskmanager.integration;

import br.com.taskmanager.config.RestTemplateConfig;
import br.com.taskmanager.exceptions.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SendEmailIntegrationService {

     private RestTemplateConfig restTemplate;
     private Environment env;

    public SendEmailIntegrationService(RestTemplateConfig restTemplate, Environment env) {
        this.restTemplate = restTemplate;
        this.env = env;
    }

    public void sendEmail(IntegrationSendEmailRequest request) throws ExternalApiException {
        log.info("Sending email...");
        var emailUrl = env.getProperty("manager.utils.address") + "email";
        var headers = new HttpHeaders();
        headers.add("subject",request.getSubject());
        headers.add("message",request.getMessage());
        headers.add("emailTo",request.getEmailTo());
        try {
            log.info("calling url {}",emailUrl);
            restTemplate.getRestemplate().postForEntity(emailUrl,headers,void.class);
        } catch (Exception e) {
            //throw new ExternalApiException("Error to call utils api");
        }

    }
}
