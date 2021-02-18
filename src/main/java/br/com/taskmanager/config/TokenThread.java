package br.com.taskmanager.config;

import br.com.taskmanager.domain.AccessToken;
import lombok.Data;

@Data
public final class TokenThread {

    private static ThreadLocal<AccessToken> tClocal = new ThreadLocal<>();

    private TokenThread(){
        super();
    }

    public static void setToken(AccessToken token) {
        tClocal.set(token);
    }

    public static void removeToken() {
        tClocal.remove();
    }

    public static ThreadLocal<AccessToken> getToken() {
        return tClocal;
    }
}
