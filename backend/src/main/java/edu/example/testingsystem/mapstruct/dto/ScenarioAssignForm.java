package edu.example.testingsystem.mapstruct.dto;

import java.util.List;

public record ScenarioAssignForm(Integer testerId, List<Integer> scenarioIds) {
}
