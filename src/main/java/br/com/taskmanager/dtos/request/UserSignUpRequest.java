package br.com.taskmanager.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class UserSignUpRequest {

    @ApiParam(required = true)
    private String name;
    @ApiParam(required = true)
    private String password;
    @ApiParam(required = false)
    private String email;
    @ApiParam(required = true)
    private String cpf;

    @JsonProperty(value = "birth-date")
    @ApiParam(required = true)
    private String birthDate;

    @ApiParam(required = false,defaultValue = "0000000")
    private String cep;

    @ApiParam(required = false,defaultValue = "0")
    private Integer numero;

    @ApiParam(required = false)
    private String complemento;
}
