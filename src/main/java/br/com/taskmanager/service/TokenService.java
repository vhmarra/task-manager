package br.com.taskmanager.service;

import br.com.taskmanager.config.TokenThread;
import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    public String getAccessToken() {
        return TokenThread.getToken().get().getToken();
    }

    public UserEntity getUserEntity() {
        return TokenThread.getToken().get().getUser();
    }

    public AccessToken getAccessTokenEntity() {
        return TokenThread.getToken().get();
    }

    public Long getUserId() {
        return TokenThread.getToken().get().getUser().getId();
    }

}
