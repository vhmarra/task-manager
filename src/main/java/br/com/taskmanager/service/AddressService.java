package br.com.taskmanager.service;

import br.com.taskmanager.domain.AddressEntity;
import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.exceptions.NotFoundException;
import br.com.taskmanager.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static br.com.taskmanager.utils.Constants.VIA_CEP_URL;

@Service
@Slf4j
public class AddressService extends TokenService {

    private final UserRepository userRepository;

    public AddressService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<AddressEntity> getAddressByCep(String cep, String numero, String complemento) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ResponseEntity<?> response = new RestTemplate().getForEntity(VIA_CEP_URL.replace("user_cep",cep), String.class);
            AddressEntity address = objectMapper.readValue(response.getBody().toString(), AddressEntity.class);
            if(!complemento.isBlank()){
                address.setComplemento(complemento);
            }
            address.setLogradouro(address.getLogradouro()+ " "+numero);
            log.info("ENDEREÇO ENCONTRADO->{}",address.toStringNoId());
            return Arrays.asList(address);
        } catch (Exception e){
            return new ArrayList<>();
        }
    }

    public List<AddressEntity> getAllUserAddresses() throws NotFoundException {
        UserEntity user = userRepository.findById(getUserId()).orElseThrow(()-> new NotFoundException("User not found"));
        List<AddressEntity> addressEntities = user.getAddresses();
        return addressEntities;
    }

}
