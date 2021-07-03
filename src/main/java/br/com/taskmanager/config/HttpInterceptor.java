package br.com.taskmanager.config;

import br.com.taskmanager.domain.AccessToken;
import br.com.taskmanager.domain.LogEntity;
import br.com.taskmanager.exceptions.TokenNotFoundException;
import br.com.taskmanager.repository.AccessTokenRepository;
import br.com.taskmanager.repository.LogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Service
@Slf4j
public class HttpInterceptor extends WebRequestHandlerInterceptorAdapter {

    private final AccessTokenRepository repository;
    private final LogRepository logRepository;
    private final WebRequestInterceptor requestInterceptor;

    public HttpInterceptor(WebRequestInterceptor requestInterceptor, AccessTokenRepository repository,LogRepository logRepository) {
        super(requestInterceptor);
        this.requestInterceptor = requestInterceptor;
        this.repository = repository;
        this.logRepository = logRepository;
    }

    @Bean
    public WebMvcConfigurerAdapter adapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HttpInterceptor(requestInterceptor, repository,logRepository))
                        .addPathPatterns("/**")
                        .excludePathPatterns("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**" /*, "/auth/**"*/);
            }
        };
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getServletPath().contains("/auth")) {
            var log = new LogEntity();
            log.setDate(LocalDateTime.now());
            log.setDescription(request.getServletPath());
            log.setUser(null);
            log.setIp(request.getRemoteAddr());
            logRepository.save(log);
            return true;
        }
        if (request.getServletPath().contains("/task")
                || request.getServletPath().contains("/sync")
                || request.getServletPath().contains("/user")
                || request.getServletPath().contains("/address")) {
            AccessToken accessToken = repository.findByTokenAndIsActive(request.getHeader("access-token"), true).orElse(null);

            if (accessToken == null) {
                throw new TokenNotFoundException("Token is not valid");
            }
            TokenThread.setToken(accessToken);
            log.info("token {}", accessToken.getToken());
            var l = new LogEntity();
            l.setDate(LocalDateTime.now());
            l.setDescription(request.getServletPath());
            l.setUser(accessToken.getUser());
            l.setIp(request.getRemoteAddr());
            logRepository.save(l);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
