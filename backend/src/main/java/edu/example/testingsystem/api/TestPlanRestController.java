package edu.example.testingsystem.api;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.TestPlan;
import edu.example.testingsystem.mapstruct.dto.ProjectDto;
import edu.example.testingsystem.mapstruct.dto.ScenarioDto;
import edu.example.testingsystem.mapstruct.dto.TestPlanDto;
import edu.example.testingsystem.mapstruct.mapper.TestPlanMapper;
import edu.example.testingsystem.repos.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/testplan")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TestPlanRestController {
    TestPlanRepository testPlanRepository;
    ProjectRepository projectRepository;
    TestPlanMapper testPlanMapper;
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final ScenarioRepository scenarioRepository;

    @GetMapping("/all")
    public ResponseEntity<List<TestPlanDto>> getAll() {
        List<TestPlan> testPlanDtoList = testPlanRepository.findAll();
        return ResponseEntity.ok(testPlanMapper.toDtos(testPlanDtoList));
    }

    @GetMapping("/unapproved/{title}")
    public ResponseEntity<List<TestPlanDto>> getUnapproved(@PathVariable("title") String title) {
        Optional<Project> optionalProject = projectRepository.findById(title);
        if (optionalProject.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        else
            return ResponseEntity.ok(testPlanMapper.toDtos(testPlanRepository.findByProjectAndApprovedIsFalse(optionalProject.get())));
    }

    @GetMapping("/approved/{title}")
    public ResponseEntity<List<TestPlanDto>> getApproved(@PathVariable("title") String title) {
        Optional<Project> optionalProject = projectRepository.findById(title);
        if (optionalProject.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        else
            return ResponseEntity.ok(testPlanMapper.toDtos(testPlanRepository.findByProjectAndApprovedIsTrue(optionalProject.get())));
    }

    @GetMapping("/byproject/{title}")
    public ResponseEntity<List<TestPlanDto>> getTestPlanByProject(@PathVariable("title") String title) {
        Optional<Project> optionalProject = projectRepository.findById(title);
        if (optionalProject.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        else
            return ResponseEntity.ok(testPlanMapper.toDtos(testPlanRepository.findByProject(optionalProject.get())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestPlanDto> getById(@PathVariable("id") Integer id) {
        Optional<TestPlan> testPlanOptional = testPlanRepository.findById(id);
        if(testPlanOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(testPlanMapper.toDto(testPlanOptional.get()));
    }

    @PostMapping("/approve")
    public ResponseEntity<HttpStatus> approve(@RequestBody ProjectDto approvedPlans) {
        for (Integer testPlanId : approvedPlans.testPlanIds()) {
            TestPlan testPlanObject = testPlanRepository.findById(testPlanId).get();
            testPlanObject.setApproved(true);
            testPlanRepository.save(testPlanObject);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TestPlanDto> create(@RequestBody TestPlanDto testPlanDto) {
        if(testPlanDto.title() == null || testPlanDto.title().isBlank()
                || testPlanDto.startDate() == null || testPlanDto.endDate() == null || testPlanDto.endDate().before(testPlanDto.startDate()))
            return ResponseEntity.badRequest().build();
        Optional<Project> optionalProject = projectRepository.findById(testPlanDto.projectTitle());
        if(optionalProject.isEmpty())
            return ResponseEntity.notFound().build();

        TestPlan newTestPlan = testPlanMapper.toTestPlan(testPlanDto, optionalProject.get());
        assignScenariosToTestPlan(testPlanDto.scenarioIds(), newTestPlan);

        return ResponseEntity.ok(testPlanMapper.toDto(testPlanRepository.save(newTestPlan)));
    }

    @PostMapping("/{id}")
    public ResponseEntity<TestPlanDto> update(@PathVariable("id") Integer id, @RequestBody TestPlanDto testPlanDto) {
        if((testPlanDto.title() != null && testPlanDto.title().isBlank())
                || (testPlanDto.startDate() != null && testPlanDto.endDate() != null && testPlanDto.endDate().before(testPlanDto.startDate())))
            return ResponseEntity.badRequest().build();
        Optional<TestPlan> optionalTestPlan = testPlanRepository.findById(id);
        if(optionalTestPlan.isEmpty())
            return ResponseEntity.notFound().build();
        Optional<Project> optionalProject= projectRepository.findById(testPlanDto.projectTitle());
        if(optionalProject.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        TestPlan testPlanObject = testPlanMapper.patchTestPlan(optionalTestPlan.get(), testPlanDto, optionalProject.get());
        assignScenariosToTestPlan(testPlanDto.scenarioIds(), testPlanObject);
        return ResponseEntity.ok(testPlanMapper.toDto(testPlanRepository.save(testPlanObject)));
    }

    private void assignScenariosToTestPlan(List<Integer> scenarios, TestPlan testPlan){
        List<Integer> alreadyAddedScenarioIds = testPlan.getScenarios().stream()
                .map(Scenario::getId)
                .toList();
        List<Scenario> scenarioToAddList = scenarioRepository.findAllById(scenarios).stream()
                .filter(scenario -> !alreadyAddedScenarioIds.contains(scenario.getId()))
                .toList();
        testPlan.getScenarios().addAll(scenarioToAddList);
    }



    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Integer id) {
        Optional<TestPlan> testPlanOptional = testPlanRepository.findById(id);
        if(testPlanOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        connectionRepository.deleteByTestPlan(testPlanOptional.get());
        testPlanRepository.delete(testPlanOptional.get());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
