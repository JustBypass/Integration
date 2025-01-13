package edu.example.testingsystem.api.tester;

import edu.example.testingsystem.entities.*;
import edu.example.testingsystem.mapstruct.dto.ConnectionDto;
import edu.example.testingsystem.mapstruct.dto.ScenarioDto;
import edu.example.testingsystem.mapstruct.mapper.ConnectionMapper;
import edu.example.testingsystem.mapstruct.mapper.ScenarioMapper;
import edu.example.testingsystem.repos.*;
import edu.example.testingsystem.security.UserInfoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tester")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TesterRestController {
    ScenarioMapper scenarioMapper;
    UserRepository userRepository;
    ConnectionRepository connectionRepository;
    TestPlanRepository testPlanRepository;
    ProjectRepository projectRepository;
    ScenarioRepository scenarioRepository;
    ConnectionMapper connectionMapper;

    @GetMapping("/{id}/project/{projectTitle}")
    public ResponseEntity<List<ScenarioDto>> getScenariosByProject(@PathVariable("id") int id, @PathVariable("projectTitle") String projectTitle) {
        Optional<Userr> user = userRepository.findById(id);
        if(user.isEmpty())
            return ResponseEntity.notFound().build();
        Optional<Project> optionalProject = projectRepository.findById(projectTitle);
        if(optionalProject.isEmpty())
            return ResponseEntity.notFound().build();
        List<TestPlan> approvedTestPlans = testPlanRepository.findByProjectAndApprovedIsTrue(optionalProject.get());
        Set<Scenario> scenarios = new HashSet<>();
        for (TestPlan testPlan : approvedTestPlans) {
            scenarios.addAll(testPlan.getScenarios().stream().
                    filter(scenario -> (Objects.equals(scenario.getExecutor().getId(), user.get().getId())
                            && connectionRepository.countByScenarioAndExecutedIsFalse(scenario) > 0))
                    .toList());
        }
        List<Scenario> scenarioList = new ArrayList<>();
        scenarioList.addAll(scenarios);
        return ResponseEntity.ok(scenarioMapper.toDtos(scenarioList));
    }

    @GetMapping("/scenario/{scenarioId}/nextConnection")
    public ResponseEntity<ConnectionDto> getNextTestCase(@PathVariable("scenarioId") Integer scenarioId) {
        Optional<Scenario> optionalScenario = scenarioRepository.findById(scenarioId);
        if(optionalScenario.isEmpty())
            return ResponseEntity.notFound().build();
        List<ScenarioCaseConnection> connectionList = connectionRepository.findByScenarioAndExecutedIsFalse(optionalScenario.get());
        if(connectionList.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(connectionMapper.toDto(connectionList.get(0)));
    }

    @GetMapping("/scenario/{scenarioId}/allConnections")
    public ResponseEntity<List<ConnectionDto>> getAllTestCases(@PathVariable("scenarioId") Integer scenarioId) {
        Optional<Scenario> optionalScenario = scenarioRepository.findById(scenarioId);
        if(optionalScenario.isEmpty())
            return ResponseEntity.notFound().build();
        List<ScenarioCaseConnection> connectionList = connectionRepository.findByScenarioAndExecutedIsFalse(optionalScenario.get());
        if(connectionList.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(connectionMapper.toDtoList(connectionList));
    }

    @PostMapping("/connection/{connectionId}/submit")
    public ResponseEntity<ConnectionDto> submit(@PathVariable("connectionId") Integer connectionId, @RequestBody ConnectionDto connectionDto) {
        Optional<ScenarioCaseConnection> optionalConnection = connectionRepository.findById(connectionId);
        if(optionalConnection.isEmpty())
            return ResponseEntity.notFound().build();
        ScenarioCaseConnection updatedConnection = (connectionMapper.submit(optionalConnection.get(), connectionDto));
        ScenarioCaseConnection saved = connectionRepository.save(updatedConnection);
        return ResponseEntity.ok(connectionMapper.toDto(saved));
    }

}
