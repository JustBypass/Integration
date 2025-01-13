package edu.example.testingsystem.security;

import edu.example.testingsystem.entities.Userr;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Set;

/**
 * Обрабатывает успешные попытки авторизации пользователей
 */
@Configuration
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    /**
     * При наличии конкреной роли, направляет пользователя после авторизации на базовую для него страницу
     * Пользователй без назначенной роли перенаправляет на страницу с соответствующей информацией
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {
            httpServletResponse.sendRedirect("/admin");
        } else if(roles.contains("ROLE_DIRECTOR")){
            httpServletResponse.sendRedirect("/director");
        }else if(roles.contains("ROLE_ANALYST")){
            httpServletResponse.sendRedirect("/analyst");
        }else if(roles.contains("ROLE_TESTER")){
            httpServletResponse.sendRedirect("/tester");
        }else {
            httpServletResponse.sendRedirect("/");
        }
    }
}