package edu.example.testingsystem.security;

import edu.example.testingsystem.entities.Role;
import edu.example.testingsystem.entities.Userr;
import edu.example.testingsystem.repos.RoleRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService {
    private static RoleRepository roleRepo;

    private UserInfoService(RoleRepository roleRepo) {
        UserInfoService.roleRepo = roleRepo;
    }

    public static Optional<Role> findUsersRole(String role) {
        return roleRepo.findById(role);
    }

    /**
     * Позволяет получить информацию о пользователях в формах
     * @return Текущий пользователь
     */
    public static Userr getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //если ест авторизованный пользователь
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (Userr) authentication.getPrincipal();
        }else
            throw new RuntimeException("Authentication required for this page");
    }

}
