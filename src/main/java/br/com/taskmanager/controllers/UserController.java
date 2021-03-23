package br.com.taskmanager.controllers;

import br.com.taskmanager.domain.UserEntity;
import br.com.taskmanager.dtos.response.UserResponse;
import br.com.taskmanager.exceptions.NotEnoughPermissionsException;
import br.com.taskmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("user")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("get-all-data")
    public ResponseEntity<List<UserResponse>> getAllUserData(@RequestHeader(name = "access-token") String token) throws NotEnoughPermissionsException {
        return ResponseEntity.ok(userService.findAllUser());
    }

}
