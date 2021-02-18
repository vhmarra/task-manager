package br.com.taskmanager.controllers;

import br.com.taskmanager.dtos.request.UserSignUpRequest;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotFoundException;
import br.com.taskmanager.exceptions.ObjectAlreadyExistsException;
import br.com.taskmanager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AutenticaController {

    private final UserService userService;

    public AutenticaController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("sign-up")
    public ResponseEntity<?> singUp(UserSignUpRequest request) throws InvalidInputException, ObjectAlreadyExistsException {
        userService.saveUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticateUser(String cpf,String password) throws NotFoundException {
        userService.authenticateUser(cpf, password);
        return ResponseEntity.ok().build();
    }


}
