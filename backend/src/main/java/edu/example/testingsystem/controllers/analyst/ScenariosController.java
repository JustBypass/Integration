package edu.example.testingsystem.controllers.analyst;

import edu.example.testingsystem.entities.*;
import edu.example.testingsystem.forms.PopulateScenarioForm;
import edu.example.testingsystem.repos.ConnectionRepository;
import edu.example.testingsystem.repos.ScenarioRepository;
import edu.example.testingsystem.repos.TestCaseRepository;
import edu.example.testingsystem.repos.TestPlanRepository;
import edu.example.testingsystem.security.UserInfoService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("analyst/scenarios")
@SessionAttributes("selectedProject")
public class ScenariosController {

    private static final Logger log = Logger.getLogger(ScenariosController.class);

    private ScenarioRepository scenarioRepo;
    private ConnectionRepository connectionRepo;
    private TestPlanRepository planRepo;
    private TestCaseRepository testCaseRepo;

    public ScenariosController(ScenarioRepository scenarioRepo, ConnectionRepository connectionRepo,
                               TestPlanRepository testPlanRepo, TestCaseRepository testCaseRepo) {
        this.scenarioRepo = scenarioRepo;
        this.connectionRepo = connectionRepo;
        this.planRepo = testPlanRepo;
        this.testCaseRepo = testCaseRepo;
    }

    @GetMapping
    public String showScenariosPage(Model model) {
        model.addAttribute("populateScenarioForm", new PopulateScenarioForm());
        return "scenarios";
    }

    @ModelAttribute("scenarios")
    public List<Scenario> addScenariosToModel(@ModelAttribute("selectedProject") Project project) {
        //все тест планы текущего проекта
        //все сценарии этих тест планов
//        List<TestPlan> allProjectPlans =  planRepo.findByProject(project);
//        List<Scenario> scenarios = new ArrayList<>();
//        for(TestPlan testPlan : allProjectPlans) {
//            scenarios.addAll(testPlan.getScenarios());
//        }
//        return scenarios;
        return scenarioRepo.findAll();
    }

    @ModelAttribute("testCases")
    public List<TestCase> addTestCasesToModel(@ModelAttribute("selectedProject") Project project) {
        return testCaseRepo.findByProject(project);
    }

    @PostMapping(value="/alter", params="action=delete")
    @Transactional
    public String deleteScenario(@ModelAttribute("chosenScenario") Scenario scenario) {
        if (scenario.getId() != null) {
            connectionRepo.deleteByScenario(scenario);
            scenarioRepo.delete(scenario);
        }
        return "redirect:/analyst/scenarios";
    }

    @PostMapping(value="/alter", params="action=change")
    public String changeScenario(@ModelAttribute("chosenScenario") Scenario scenario) {
        if (scenario.getId() == null)
            return "redirect:/analyst/scenarios";
        return "redirect:/analyst/scenarios/" + scenario.getId();
    }

    @PostMapping("/add")
    public String addScenario(@ModelAttribute("newScenarioTitle") String newScenarioTitle, Model model) {
        Scenario newScenario = new Scenario(null,newScenarioTitle, UserInfoService.getCurrentUser(),
                null,null, (Project) model.getAttribute("selectedProject"));
        scenarioRepo.save(newScenario);
        return "redirect:/analyst/scenarios";
    }

    @PostMapping("/populate")
    //TODO:удаление выбранных элементов
    public String populateScenario(@ModelAttribute("populateScenarioForm") PopulateScenarioForm form) {
        if(form.getFormScenario() == null)
            return "redirect:/analyst/scenarios";
        List<ScenarioCaseConnection> scenarioCaseConnections = new ArrayList<>();
        for(TestCase testCase : form.getTestCases())
            scenarioCaseConnections.add(new ScenarioCaseConnection(null, null, false, false, form.getFormScenario(), testCase,null));
        connectionRepo.saveAll(scenarioCaseConnections);
        return "redirect:/analyst/scenarios";
    }

    @ExceptionHandler(SQLException.class)
    public String conflict() {
        log.error("Error occurred during the execution of the transaction");
        return "redirect:/analyst/scenarios";
    }
}
