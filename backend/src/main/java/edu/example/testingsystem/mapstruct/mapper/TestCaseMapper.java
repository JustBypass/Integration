package edu.example.testingsystem.mapstruct.mapper;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.TestCase;
import edu.example.testingsystem.mapstruct.dto.TestCaseDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestCaseMapper {

    @Mapping(target = "projectTitle", expression = "java((testCase.getProject() != null)?testCase.getProject().getTitle():null)")
    @Mapping(target = "creatorId", expression = "java((testCase.getCreator() != null)?testCase.getCreator().getId():null)")
    TestCaseDto toDto(TestCase testCase);

    default List<TestCaseDto> toDtos(List<TestCase> testCases){
        return testCases.stream().map(this::toDto).toList();
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "testCaseDto.title")
    @Mapping(target = "description", source = "testCaseDto.description")
    @Mapping(target = "inputData", source = "testCaseDto.inputData")
    @Mapping(target = "outputData", source = "testCaseDto.outputData")
    @Mapping(target = "project", source = "project")
    TestCase toTestCase(TestCaseDto testCaseDto, Project project);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "title", source = "patch.title")
    @Mapping(target = "description", source = "patch.description")
    @Mapping(target = "inputData", source = "patch.inputData")
    @Mapping(target = "outputData", source = "patch.outputData")
    @Mapping(target = "project", source = "project")
    TestCase patchTestCase(@MappingTarget TestCase testCase, TestCaseDto patch, Project project);
}
