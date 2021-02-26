package br.com.taskmanager.controllers;

import br.com.taskmanager.config.TokenThread;
import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.Address;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotEnoughPermissionsException;
import br.com.taskmanager.exceptions.NotFoundException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.service.AddressService;
import br.com.taskmanager.service.ProfileService;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static br.com.taskmanager.utils.Constants.SUPER_ADM;

@RestController
@RequestMapping("address")
public class AddressController {

    private final AddressService service;
    private final AccessTokenRepository accessTokenRepository;
    private final ProfileService profileService;

    public AddressController(AddressService service, AccessTokenRepository accessTokenRepository, ProfileService profileService) {
        this.service = service;
        this.accessTokenRepository = accessTokenRepository;
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<List<Address>> getAddress(@RequestHeader(name = "access-token") String token, @RequestHeader String cep, @RequestHeader String numero) throws NotFoundException, InvalidInputException, NotEnoughPermissionsException {
        AccessToken accessToken = accessTokenRepository.findByTokenAndIsActive(token, true).orElse(null);
        if (accessToken == null) {
            throw new NotFoundException("Token not found");
        }
        if (!accessToken.getUser().getProfiles().contains(profileService.findProfileByID(SUPER_ADM).get(0))){
            throw new NotEnoughPermissionsException("User dont have permission");
        }

        return ResponseEntity.ok(service.getAddressByCep(cep, numero, ""));
    }
}