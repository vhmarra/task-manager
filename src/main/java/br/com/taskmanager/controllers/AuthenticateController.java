package br.com.taskmanager.controllers;

import br.com.taskmanager.dtos.request.UserSignUpRequest;
import br.com.taskmanager.dtos.response.SuccessResponse;
import br.com.taskmanager.exceptions.ExternalApiException;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotFoundException;
import br.com.taskmanager.exceptions.ObjectAlreadyExistsException;
import br.com.taskmanager.service.ChangeUserDataService;
import br.com.taskmanager.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthenticateController {

    private final UserService userService;
    private final ChangeUserDataService changeUserDataService;

    @PostMapping("sign-up")
    public ResponseEntity<SuccessResponse> singUp(UserSignUpRequest request) throws InvalidInputException, ObjectAlreadyExistsException, ExternalApiException {
        return ResponseEntity.ok(userService.saveUser(request));
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticateUser(String cpf, String password) throws NotFoundException, InvalidInputException {
        return ResponseEntity.ok(userService.authenticateUser(cpf, password));
    }

    @PostMapping("request-change-password")
    public ResponseEntity<?> requestChangePassword(@RequestHeader(name = "cpf") String userCpf, @RequestHeader(name = "email") String userEmail) throws InvalidInputException, MessagingException {
        changeUserDataService.requestChangePassword(userCpf, userEmail);
        return ResponseEntity.ok().build();

    }

    @PostMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestHeader(name = "code") String code, @RequestHeader(name = "new-pass") String newPass) throws InvalidInputException, MessagingException {
        changeUserDataService.changePassword(code, newPass);
        return ResponseEntity.ok().build();

    }


}
