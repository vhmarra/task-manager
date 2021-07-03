package br.com.taskmanager.controllers;

import br.com.taskmanager.dtos.response.UserResponse;
import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotEnoughPermissionsException;
import br.com.taskmanager.service.EmailService;
import br.com.taskmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RequestMapping("user")
@RestController
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("get-all-data")
    public ResponseEntity<List<UserResponse>> getAllUserData(@RequestHeader(name = "access-token") String token) throws NotEnoughPermissionsException {
        return ResponseEntity.ok(userService.findAllUser());
    }

    @PostMapping("send-email-to-adm")
    public ResponseEntity<?> sendEmailToAdm(@RequestHeader(name = "access-token") String token) throws NotEnoughPermissionsException, InvalidInputException, MessagingException, IOException {
        userService.sendUserDataToAdmEmail();
        return ResponseEntity.ok().build();
    }

    @PostMapping("send-email-by-id")
    public ResponseEntity<?> sendEmailById(@RequestHeader(name = "access-token") String token ,
                                           @RequestHeader(name = "email-id") Long emailId)
            throws InvalidInputException, MessagingException, NotEnoughPermissionsException, IOException {
        emailService.sendEmailById(emailId);
        return ResponseEntity.ok().build();
    }
}
