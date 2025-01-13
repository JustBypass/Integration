package edu.example.testingsystem.controllers.analyst;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.TestPlan;
import edu.example.testingsystem.forms.PopulateTestPlanForm;
import edu.example.testingsystem.forms.TestPlanForm;
import edu.example.testingsystem.repos.ConnectionRepository;
import edu.example.testingsystem.repos.ProjectRepository;
import edu.example.testingsystem.repos.ScenarioRepository;
import edu.example.testingsystem.repos.TestPlanRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("analyst/testPlans")
@SessionAttributes("selectedProject")
public class TestPlansController {

    TestPlanRepository planRepo;
    ProjectRepository projectRepo;
    ScenarioRepository scenarioRepo;
    ConnectionRepository connectionRepo;

    public TestPlansController(TestPlanRepository planRepository , ScenarioRepository scenarioRepo,
                               ProjectRepository projectRepo, ConnectionRepository connectionRepo) {
        this.planRepo = planRepository;
        this.projectRepo = projectRepo;
        this.scenarioRepo = scenarioRepo;
    }

    @GetMapping
    public String showTestPlansPage(Model model) {
        model.addAttribute("testPlanForm", new TestPlanForm());
        model.addAttribute("populateTestPlanForm", new PopulateTestPlanForm());
        return "testPlans";
    }

    @ModelAttribute("testPlans")
    public List<TestPlan> addPlansToModel(@ModelAttribute("selectedProject") Project project) {
        return planRepo.findByProject(project);
    }

    @ModelAttribute("scenarios")
    public List<Scenario> addScenariosToModel(@ModelAttribute("testPlans") List<TestPlan> testPlans) {
//        List<Scenario> scenarios = new ArrayList<>();
//        for (TestPlan testPlan : testPlans) {
//            scenarios.addAll(testPlan.getScenarios());
//        }
//        return scenarios;
        return scenarioRepo.findAll();
    }

    @PostMapping(value="/alter", params="action=delete")
    @Transactional
    public String processTestPlan(@ModelAttribute("chosenTestPlan") TestPlan testPlan) {
        if (testPlan.getId() == null)
            return "testPlans";
        connectionRepo.deleteByTestPlan(testPlan);
        planRepo.delete(testPlan);
        return "redirect:/analyst/testPlans";
    }

    @PostMapping(value="/alter", params="action=change")
    public String changeTestPlan(@ModelAttribute("chosenTestPlan") TestPlan testPlan){
        if(testPlan.getId() == null)
            return "redirect:/analyst/testPlans";
        return "redirect:/analyst/testPlans/" + testPlan.getId();
    }

    @PostMapping("/add")
    public String addTestPlan(@ModelAttribute("testPlanForm") TestPlanForm testPlanForm, @SessionAttribute("selectedProject") Project project) {
        planRepo.save(testPlanForm.toTestPlan(project));
        return "redirect:/analyst/testPlans";
    }

    @PostMapping("/populate")
    //TODO:удаление выбранных элементов
    public String populateTestPlan(@ModelAttribute("populateTestPlanForm") PopulateTestPlanForm form) {
        if(form.getFormTestPlan() == null)
            return "redirect:/analyst/testPlans";
        form.getFormTestPlan().setScenarios(form.getScenarios());
        planRepo.save(form.getFormTestPlan());
        return "redirect:/analyst/testPlans";
    }


}
