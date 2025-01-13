package edu.example.testingsystem.security;

import edu.example.testingsystem.entities.Userr;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Data
@Service
public class RegistrationForm {
    private String username;
    private String password;
    //private String role;



//    public RegistrationForm(String username, String password, String role) {
//        this.username = username;
//        this.password = password;
//        this.role = role;
//    }

    public Userr toUser(PasswordEncoder passwordEncoder) {
        //Optional<Role> usersRole = findUsersRole(role);
        //Optional<Role> usersRole = roleRepo.findById(role);
        //if(usersRole.isEmpty())
            //throw new RuntimeException("Role not found");
        return new Userr(username, passwordEncoder.encode(password));
    }
}