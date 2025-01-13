package edu.example.testingsystem.api;

import edu.example.testingsystem.entities.*;
import edu.example.testingsystem.mapstruct.dto.ProjectDto;
import edu.example.testingsystem.mapstruct.dto.ScenarioDto;
import edu.example.testingsystem.mapstruct.mapper.ScenarioMapper;
import edu.example.testingsystem.repos.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/scenario")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScenarioRestController {
    ScenarioMapper scenarioMapper;
    ProjectRepository projectRepository;
    TestPlanRepository testPlanRepository;
    ScenarioRepository scenarioRepository;
    UserRepository userRepository;
    ConnectionRepository connectionRepository;
    TestCaseRepository testCaseRepository;

    @GetMapping("/all")
    public ResponseEntity<List<ScenarioDto>> getScenarios() {
        return ResponseEntity.ok(scenarioMapper.toDtos(scenarioRepository.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScenarioDto> getScenarioById(@PathVariable("id") Integer id) {
        Optional<Scenario> scenario = scenarioRepository.findById(id);
        if (scenario.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(scenarioMapper.toDto(scenario.get()), HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<List<ScenarioDto>> assignScenarioToTester(@RequestBody ScenarioDto scenarioDto) {
        Optional<Userr> optionalExecutor = userRepository.findById(scenarioDto.executor());
        if (optionalExecutor.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Userr executor = optionalExecutor.get();
        Scenario scenario;
        List<Scenario> scenarios = new ArrayList<>();
        for(Integer scenarioId : scenarioDto.scenariosToTester()){
            Optional<Scenario> scenarioOptional = scenarioRepository.findById(scenarioId);
            if(scenarioOptional.isEmpty())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            scenario = scenarioOptional.get();
            scenario.setExecutor(executor);
            scenarios.add(scenario);
        }
        return ResponseEntity.ok(scenarioMapper.toDtos(scenarioRepository.saveAll(scenarios)));
    }

    @PostMapping("{id}")
    public ResponseEntity<ScenarioDto> patchScenario(@PathVariable("id") Integer id, @RequestBody ScenarioDto scenarioDto) {
        if(scenarioDto.title() != null && scenarioDto.title().isBlank())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        //поиск проекта
        Optional<Project> optionalProject = projectRepository.findById(scenarioDto.projectTitle());
        if(optionalProject.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        //поиск самого сценария
        Optional<Scenario> scenarioOptional = scenarioRepository.findById(id);
        if(scenarioOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        //назначение тест-кейсов сценарию
        assignTestCasesToScenario(scenarioDto.testCases(), scenarioOptional.get());

        Scenario scenario = scenarioOptional.get();
        scenario = scenarioMapper.patchScenario(scenario, scenarioDto, optionalProject.get());



        return ResponseEntity.ok(scenarioMapper.toDto(scenarioRepository.save(scenario)));
    }

    @PostMapping
    public ResponseEntity<ScenarioDto> createScenario(@RequestBody ScenarioDto scenarioDto) {
        if(scenarioDto.title() == null || scenarioDto.title().isBlank())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Project> optionalProject= projectRepository.findById(scenarioDto.projectTitle());
        if(optionalProject.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Scenario newScenario = scenarioRepository.save(scenarioMapper.toScenario(scenarioDto,optionalProject.get()));
        //назначение тест-кейсов сценарию
        assignTestCasesToScenario(scenarioDto.testCases(), newScenario);
        return ResponseEntity.ok(scenarioMapper.toDto(newScenario));
    }

    @GetMapping("/byproject/{title}")
    public ResponseEntity<List<ScenarioDto>> getScenariosByProject(@PathVariable("title") String title){
        Optional<Project> optionalProject= projectRepository.findById(title);
        if(optionalProject.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<Scenario> scenarios = scenarioRepository.findByProject(optionalProject.get());

        return ResponseEntity.ok(scenarioMapper.toDtos(scenarios));
    }

    @GetMapping("/bytestplan/{id}")
    public ResponseEntity<List<ScenarioDto>> getScenariosByTestPlan(@PathVariable("id") Integer id){
        Optional<TestPlan> optionalTestPlan= testPlanRepository.findById(id);
        if(optionalTestPlan.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<Scenario> scenarios = optionalTestPlan.get().getScenarios();
        return ResponseEntity.ok(scenarioMapper.toDtos(scenarios));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> deleteScenario(@PathVariable("id") Integer id) {
        Optional<Scenario> scenarioOptional = scenarioRepository.findById(id);
        if(scenarioOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        connectionRepository.deleteByScenario(scenarioOptional.get());
        scenarioRepository.delete(scenarioOptional.get());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void assignTestCasesToScenario(List<Integer> testCases, Scenario scenario){
        List<ScenarioCaseConnection> scenarioCaseConnections =connectionRepository.findByScenario(scenario);
        List<Integer> currentTestCases = scenarioCaseConnections.stream()
                .map(ScenarioCaseConnection::getTestCase)
                .map(TestCase::getId)
                .toList();
        Optional<TestCase> optionalTestCase;
        for(Integer testCaseId : testCases){
            optionalTestCase = testCaseRepository.findById(testCaseId);
            if(optionalTestCase.isEmpty())
                throw new EntityNotFoundException(testCaseId.toString());
            if(!currentTestCases.contains(testCaseId))
                scenarioCaseConnections.add(new ScenarioCaseConnection(null, null, false, false, scenario, optionalTestCase.get(),null));
        }
        connectionRepository.saveAll(scenarioCaseConnections);
    }
}
