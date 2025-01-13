package edu.example.testingsystem.forms;

import edu.example.testingsystem.entities.ScenarioCaseConnection;
import edu.example.testingsystem.entities.TestCase;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@Service
public class ExecutedTestCaseForm {
    private Boolean passed;
    private String comment;
}
