package br.com.taskmanager.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IntegrationSendEmailRequest {
    private String message;
    private String subject;
    private String emailTo;
}
