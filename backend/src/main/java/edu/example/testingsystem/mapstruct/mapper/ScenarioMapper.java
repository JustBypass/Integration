package edu.example.testingsystem.mapstruct.mapper;

import edu.example.testingsystem.entities.*;
import edu.example.testingsystem.mapstruct.dto.ScenarioDto;
import edu.example.testingsystem.mapstruct.dto.TestCaseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScenarioMapper {

    @Mapping(target = "creator", source = "scenario.creator.id",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "executor", source = "scenario.executor.id",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "testPlans", expression = "java(scenario.getTestPlans().stream().map(testPlan -> testPlan.getId()).toList())")
    @Mapping(target = "projectTitle", expression = "java(scenario.getProject().getTitle())")
    ScenarioDto toDto(Scenario scenario);

    default List<ScenarioDto> toDtos(List<Scenario> scenarios){
        return scenarios.stream().map(this::toDto).toList();
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "scenarioDto.title")
    @Mapping(target = "project", source = "project")
    Scenario patchScenario(@MappingTarget Scenario scenario, ScenarioDto scenarioDto, Project project);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "scenarioDto.title")
    @Mapping(target = "project", source = "project")
    Scenario toScenario(ScenarioDto scenarioDto, Project project);

}
