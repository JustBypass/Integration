package edu.example.testingsystem.mapstruct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.example.testingsystem.entities.Scenario;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TestPlanDto(Integer id,
                          Date startDate,
                          Date endDate,
                          String title,
                          Integer creatorId,
                          Boolean approved,
                          List<ScenarioDto> scenarios,
                          List<Integer> scenarioIds,
                          String projectTitle) {
}
