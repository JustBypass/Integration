package edu.example.testingsystem.mapstruct.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.Userr;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TestCaseDto(Integer id,
                          String title,
                          String projectTitle,
                          Integer creatorId,
                          String description,
                          String inputData,
                          String outputData) {
}
