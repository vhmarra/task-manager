package br.com.taskmanager.service;

import br.com.taskmanager.domain.Address;
import br.com.taskmanager.exceptions.InvalidInputException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AddressService {

    public List<Address> getAddressByCep(String cep, String numero, String complemento) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://viacep.com.br/ws/"+cep+"/json/";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ResponseEntity<?> response = restTemplate.getForEntity(url, String.class);
            Address address = objectMapper.readValue(response.getBody().toString(),Address.class);
            if(!complemento.isBlank()){
                address.setComplemento(complemento);
            }
            address.setLogradouro(address.getLogradouro()+ " "+numero);
            return Arrays.asList(address);
        } catch (Exception e){
            log.error("failed to get address -> {} {}",cep,e.getCause().getLocalizedMessage());
        }
        return new ArrayList<>();
    }

}
