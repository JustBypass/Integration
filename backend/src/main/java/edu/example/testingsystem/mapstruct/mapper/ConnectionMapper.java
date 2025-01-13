package edu.example.testingsystem.mapstruct.mapper;

import edu.example.testingsystem.entities.ScenarioCaseConnection;
import edu.example.testingsystem.mapstruct.dto.ConnectionDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConnectionMapper {

    @Mapping(target = "scenarioId", expression = "java(connection.getScenario().getId())")
    @Mapping(target = "testCaseId", expression = "java(connection.getTestCase().getId())")
    @Mapping(target = "testPlanId", expression = "java(connection.getTestPlan().getId())")
    ConnectionDto toDto(ScenarioCaseConnection connection);

    default List<ConnectionDto> toDtoList(List<ScenarioCaseConnection> connections){
        return connections.stream().map(this::toDto).toList();
    }


    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "executed", defaultValue = "true")
    @Mapping(target = "passed", source = "passed")
    @Mapping(target = "comment", defaultValue = "comment")
    ScenarioCaseConnection submit(@MappingTarget ScenarioCaseConnection connection, ConnectionDto patch);
}
