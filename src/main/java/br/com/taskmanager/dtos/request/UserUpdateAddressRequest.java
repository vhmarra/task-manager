package br.com.taskmanager.dtos.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class UserUpdateAddressRequest {

    @ApiParam(required = true)
    private String cep;

    @ApiParam(required = false,defaultValue = " ")
    private Integer numero;

    @ApiParam(required = false,defaultValue = " ")
    private String complemento;
}
