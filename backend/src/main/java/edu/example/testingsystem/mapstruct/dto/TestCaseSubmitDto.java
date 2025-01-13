package edu.example.testingsystem.mapstruct.dto;

public record TestCaseSubmitDto(Integer scenario,
                                boolean passed,
                                String comment){

}

