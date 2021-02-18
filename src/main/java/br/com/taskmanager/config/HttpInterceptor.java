package br.com.taskmanager.config;

import br.com.taskmanager.repository.AccessTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@Slf4j
public class HttpInterceptor extends WebRequestHandlerInterceptorAdapter {

    private final AccessTokenRepository repository;
    private final WebRequestInterceptor requestInterceptor;

    public HttpInterceptor(WebRequestInterceptor requestInterceptor, AccessTokenRepository repository) {
        super(requestInterceptor);
        this.requestInterceptor = requestInterceptor;
        this.repository = repository;
    }

    @Bean
    public HttpInterceptor myCustomHandlerInterceptor() {
        return new HttpInterceptor(requestInterceptor, repository);
    }

    @Bean
    public WebMvcConfigurerAdapter adapter() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new HttpInterceptor(requestInterceptor, repository))
                        .addPathPatterns("/**")
                        .excludePathPatterns("/v2/api-docs","/swagger-resources/**","/swagger-ui.html", "/webjars/**","/auth/**");}};}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
