package edu.example.testingsystem.mapstruct.mapper;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.TestPlan;
import edu.example.testingsystem.mapstruct.dto.ProjectDto;
import edu.example.testingsystem.mapstruct.dto.TestPlanDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "testPlanIds", ignore = true)
    @Mapping(target = "director", expression = "java(project.getDirector().getId())")
//    @Mapping(target = "testPlanIds", expression = "java(project.getTestPlans().stream().map(testPlan -> testPlan.getId()).toList())")
    ProjectDto projectToProjectDto(Project project);


    default TestPlanDto testPlanToTestPlanDto(TestPlan testPlan) {
        return Mappers.getMapper(TestPlanMapper.class).toDto(testPlan);
    }
}
