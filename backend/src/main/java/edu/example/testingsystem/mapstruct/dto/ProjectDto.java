package edu.example.testingsystem.mapstruct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProjectDto(String title,
                         Integer director,
                         List<TestPlanDto> testPlans,
                         List<Integer> testPlanIds) {
}
