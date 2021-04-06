package br.com.taskmanager.controllers;

import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.AddressEntity;
import br.com.taskmanager.dtos.request.UserUpdateAddressRequest;
import br.com.taskmanager.dtos.response.SuccessResponse;
import br.com.taskmanager.exceptions.ExternalApiException;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotEnoughPermissionsException;
import br.com.taskmanager.exceptions.NotFoundException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.service.AddressService;
import br.com.taskmanager.service.ProfileService;
import br.com.taskmanager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

import static br.com.taskmanager.utils.Constants.SUPER_ADM;

@RestController
@RequestMapping("address")
public class AddressController {

    private final AddressService service;
    private final AccessTokenRepository accessTokenRepository;
    private final ProfileService profileService;
    private final UserService userService;

    public AddressController(AddressService service, AccessTokenRepository accessTokenRepository, ProfileService profileService, UserService userService) {
        this.service = service;
        this.accessTokenRepository = accessTokenRepository;
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<AddressEntity>> getAddress(@RequestHeader(name = "access-token") String token, @RequestHeader String cep, @RequestHeader String numero) throws NotFoundException, InvalidInputException, NotEnoughPermissionsException {
        AccessToken accessToken = accessTokenRepository.findByTokenAndIsActive(token, true).orElse(null);
        if (accessToken == null) {
            throw new NotFoundException("Token not found");
        }
        if (!accessToken.getUser().getProfiles().contains(profileService.findProfileByID(SUPER_ADM).get(0))){
            throw new NotEnoughPermissionsException("User dont have permission");
        }

        return ResponseEntity.ok(service.getAddressByCep(cep, numero, ""));
    }

    @PostMapping("update-user-address")
    public ResponseEntity<?> getUserAddress(@RequestHeader(name = "access-token") String token, @RequestAttribute @ModelAttribute UserUpdateAddressRequest request) throws InvalidInputException, ExternalApiException, NotFoundException, MessagingException, IOException {
        userService.updateUserAddress(token,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse("Endereço alterado com sucesso",201L));
    }

    @GetMapping("get-all-addresses")
    public ResponseEntity<List<AddressEntity>> getAllAddresses(@RequestHeader(name = "access-token") String token) throws NotFoundException {
        return ResponseEntity.ok(service.getAllUserAddresses());
    }
}
