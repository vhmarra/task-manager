package br.com.taskmanager.controllers;

import br.com.taskmanager.domain.Address;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotEnoughPermissionsException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.service.AddressService;
import br.com.taskmanager.service.ProfileService;
import br.com.taskmanager.utils.Constants;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping()
    public ResponseEntity<List<Address>> getAddress(@RequestHeader String cep, @RequestHeader String numero){
        return ResponseEntity.ok(service.getAddressByCep(cep, numero, ""));
    }
}
