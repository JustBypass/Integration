package edu.example.testingsystem.forms;

import edu.example.testingsystem.entities.Role;
import edu.example.testingsystem.entities.Userr;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class RoleAssigningForm {
    private Role role;
    private Userr user;
    private Boolean remove;
}
