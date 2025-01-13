package edu.example.testingsystem.forms;

import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.Userr;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class ProjectManagementForm {
    private Userr formTester;
    private List<Scenario> formScenarios;
}
