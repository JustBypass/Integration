package edu.example.testingsystem.api;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.ScenarioCaseConnection;
import edu.example.testingsystem.entities.TestCase;
import edu.example.testingsystem.mapstruct.dto.TestCaseDto;
import edu.example.testingsystem.mapstruct.dto.TestCaseSubmitDto;
import edu.example.testingsystem.mapstruct.mapper.TestCaseMapper;
import edu.example.testingsystem.messaging.TestCaseMessagingService;
import edu.example.testingsystem.messaging.kafka.KafkaTestCaseMessagingService;
import edu.example.testingsystem.repos.ConnectionRepository;
import edu.example.testingsystem.repos.ProjectRepository;
import edu.example.testingsystem.repos.ScenarioRepository;
import edu.example.testingsystem.repos.TestCaseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

//TODO: PageRequest? ResponseEntity? CrossOrigin?


@RestController
@RequestMapping("/api/testCase")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TestCaseRestController {
    TestCaseRepository testCaseRepo;
    TestCaseMessagingService messagingService;
    TestCaseMapper testCaseMapper;
    ConnectionRepository connectionRepo;
    private final ProjectRepository projectRepository;
    private final ScenarioRepository scenarioRepository;

    @GetMapping("/all")
    public ResponseEntity<List<TestCaseDto>> getAll() {
        return ResponseEntity.ok(testCaseMapper.toDtos(testCaseRepo.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestCaseDto> getTestCaseById(@PathVariable("id") Integer id) {
        Optional<TestCase> testCase = testCaseRepo.findById(id);
        if (testCase.isPresent()) {
            return ResponseEntity.ok(testCaseMapper.toDto(testCase.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/byscenario/{id}")
    public ResponseEntity<List<TestCaseDto>> getByScenarioId(@PathVariable("id") Integer id) {
        Optional<Scenario> optionalScenario = scenarioRepository.findById(id);
        if (optionalScenario.isEmpty())
            return ResponseEntity.notFound().build();

        List<Integer> testCaseIds = connectionRepo.findByScenarioAndExecutedIsFalse(optionalScenario.get())
                .stream()
                .map(ScenarioCaseConnection::getTestCase)
                .map(TestCase::getId)
                .toList();

        return ResponseEntity.ok(testCaseMapper.toDtos(testCaseRepo.findAllById(testCaseIds)));
    }

    @PostMapping("/{id}")
    public ResponseEntity<TestCaseDto> patchTestCase(@PathVariable("id") Integer id, @RequestBody TestCaseDto testCaseDto) {
        if(testCaseDto.title() != null && testCaseDto.title().isBlank())
            return ResponseEntity.badRequest().build();
        Optional<TestCase> optionalTestCase = testCaseRepo.findById(id);
        if(optionalTestCase.isEmpty())
            return ResponseEntity.notFound().build();
        Optional<Project> optionalProject = projectRepository.findById(testCaseDto.projectTitle());
        if(optionalProject.isEmpty())
            return ResponseEntity.notFound().build();
        TestCase patched = testCaseMapper.patchTestCase(optionalTestCase.get(),testCaseDto, optionalProject.get());
        return ResponseEntity.ok(testCaseMapper.toDto(testCaseRepo.save(patched)));
    }

    @PostMapping
    public ResponseEntity<TestCaseDto> createTestCase(@RequestBody TestCaseDto testCaseDto) {
        //TODO: добавить указание создателя тест-кейса
        if(testCaseDto.title() == null || testCaseDto.title().isBlank())
            return ResponseEntity.badRequest().build();
        Optional<Project> optionalProject = projectRepository.findById(testCaseDto.projectTitle());
        if(optionalProject.isEmpty())
            return ResponseEntity.notFound().build();
        TestCase savedTestCase = testCaseRepo.save(testCaseMapper.toTestCase(testCaseDto, optionalProject.get()));
        return ResponseEntity.ok(testCaseMapper.toDto(savedTestCase));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> deleteTestCase(@PathVariable("id") Integer id) {
        Optional<TestCase> optionalTestCase = testCaseRepo.findById(id);
        if(optionalTestCase.isEmpty())
            throw new NoSuchElementException("Attempt to delete test case with non-existing id " + id);
        connectionRepo.deleteByTestCase(optionalTestCase.get());
        testCaseRepo.deleteById(optionalTestCase.get().getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("{id}/submit")
    public ResponseEntity<HttpStatus> submitTestCase(@PathVariable("id") Integer id, @RequestBody TestCaseSubmitDto submitDto) {
        Optional<TestCase> optionalTestCase = testCaseRepo.findById(id);
        if(optionalTestCase.isEmpty())
            throw new NoSuchElementException("There is no test case with id " + id);
        Optional<Scenario> optionalScenario = scenarioRepository.findById(submitDto.scenario());
        if(optionalScenario.isEmpty())
            throw new NoSuchElementException("There is no scenario with id " + id);

        Optional<ScenarioCaseConnection> optionalConnection = connectionRepo.findByScenarioAndTestCase(optionalScenario.get(), optionalTestCase.get());
        if(optionalConnection.isEmpty())
            throw  new NoSuchElementException("Probably this test case isnt connected to given scenario " + id);

        ScenarioCaseConnection connection = optionalConnection.get();
        connection.setExecuted(true);
        connection.setPassed(submitDto.passed());
        connection.setComment(submitDto.comment());
        connectionRepo.save(connection);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
