package edu.example.testingsystem.mapstruct.dto;

import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.TestCase;

public record ConnectionDto(Integer id,
                            String comment,
                            Boolean executed,
                            Boolean passed,
                            Integer scenarioId,
                            Integer testCaseId,
                            Integer testPlanId) {
}
