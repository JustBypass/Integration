package edu.example.testingsystem.forms;

import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.TestPlan;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class PopulateTestPlanForm {
    private TestPlan formTestPlan;
    private List<Scenario> scenarios;
}
