package edu.example.testingsystem.mapstruct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.TestPlan;
import edu.example.testingsystem.entities.Userr;

import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScenarioDto(Integer id,
                          String title,
                          Integer creator,
                          Integer executor,
                          List<Integer> testPlans,
                          String projectTitle,
                          List<Integer> scenariosToTester,
                          List<Integer> testCases) {
}
