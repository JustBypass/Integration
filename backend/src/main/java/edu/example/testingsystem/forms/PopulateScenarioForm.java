package edu.example.testingsystem.forms;

import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.TestCase;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class PopulateScenarioForm {
    private Scenario formScenario;
    private List<TestCase> testCases;
}
