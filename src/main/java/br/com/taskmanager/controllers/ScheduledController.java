package br.com.taskmanager.controllers;

import br.com.taskmanager.exceptions.InvalidInputException;
import br.com.taskmanager.exceptions.NotEnoughPermissionsException;
import br.com.taskmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sync")
public class ScheduledController {

    private final UserService userService;

    public ScheduledController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("force-send-email")
    public ResponseEntity<?> forceSendEmails(@RequestHeader(name = "access-token") String accessToken) throws InvalidInputException, NotEnoughPermissionsException {
        userService.forceSendEmail();
        return ResponseEntity.ok().build();
    }

    @PostMapping("force-disable-tokens")
    public ResponseEntity<?> forceDisableTokens(@RequestHeader(name = "access-token") String accessToken) throws InvalidInputException, NotEnoughPermissionsException {
        userService.forceDisableTokens();
        return ResponseEntity.ok().build();
    }



}
