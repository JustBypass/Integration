package edu.example.testingsystem.forms;

import edu.example.testingsystem.entities.Userr;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class UsersFullNameForm {
    Userr user;
    String name;
    String surname;
    String patronymic;
}