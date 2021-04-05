package br.com.taskmanager.service;

import br.com.taskmanager.domain.AddressEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AddressService {

    public static String VIA_CEP_URL = "https://viacep.com.br/ws/"+"user_cep"+"/json/";

    public List<AddressEntity> getAddressByCep(String cep, String numero, String complemento) {
        RestTemplate restTemplate = new RestTemplate();
        String url = VIA_CEP_URL.replace("user_cep",cep);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseEntity<?> response = restTemplate.getForEntity(url, String.class);
            AddressEntity address = objectMapper.readValue(response.getBody().toString(), AddressEntity.class);
            if(!complemento.isBlank()){
                address.setComplemento(complemento);
            }
            address.setLogradouro(address.getLogradouro()+ " "+numero);
            log.info("ENDEREÇO ENCONTRADO -> {}",address);
            return Arrays.asList(address);
        } catch (Exception e){
            return new ArrayList<>();
        }
    }

}
