package edu.example.testingsystem.api.director;

import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.mapstruct.dto.ScenarioAssignForm;
import edu.example.testingsystem.repos.ScenarioRepository;
import edu.example.testingsystem.repos.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/director")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DirectorRestController {
    ScenarioRepository scenarioRepository;
    UserRepository userRepository;

    @PostMapping("/assign")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<HttpStatus> assignScenariosToTesters(@RequestBody ScenarioAssignForm form) {
        List<Scenario> scenarios = new ArrayList<>();
        for(Integer scenarioid : form.scenarioIds()){
            Scenario scenario = scenarioRepository.findById(scenarioid).get();
            scenario.setExecutor(userRepository.findById(form.testerId()).get());
            scenarios.add(scenario);
        }
        scenarioRepository.saveAll(scenarios);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
