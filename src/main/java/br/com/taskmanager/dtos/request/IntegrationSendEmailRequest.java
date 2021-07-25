package br.com.taskmanager.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IntegrationSendEmailRequest {
    private String message;
    private String subject;
    @JsonProperty(value = "email-to") private String emailTo;
}
