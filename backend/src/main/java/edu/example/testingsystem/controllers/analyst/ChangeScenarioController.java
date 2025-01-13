package edu.example.testingsystem.controllers.analyst;

import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.TestCase;
import edu.example.testingsystem.forms.TestCaseForm;
import edu.example.testingsystem.repos.ScenarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
@RequestMapping("/analyst/scenarios/{id}")
public class ChangeScenarioController {

    private final ScenarioRepository scenarioRepo;

    public ChangeScenarioController(ScenarioRepository scenarioRepo) {
        this.scenarioRepo = scenarioRepo;
    }

    @GetMapping
    public String showScenarioPage(Model model, @PathVariable Integer id) {
        Scenario scenario = findScenario(id);
        model.addAttribute("scenarioTitle", scenario.getTitle());
        return "changeScenario";
    }

    @PostMapping("/submit")
    public String submitChanges(@ModelAttribute("scenarioTitle") String scenarioTitle, @PathVariable("id") Integer id) {
        Scenario scenario = findScenario(id);
        //Обновляем данные в сценарии после изменения и сохраняем его
        scenario.setTitle(scenarioTitle);
        scenarioRepo.save(scenario);
        return "redirect:/analyst/scenarios";
    }

    private Scenario findScenario(Integer id) {
        Optional<Scenario> scenario = scenarioRepo.findById(id);
        if(scenario.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }else
            return scenario.get();
    }
}
