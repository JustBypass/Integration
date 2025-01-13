package edu.example.testingsystem.controllers.director;

import edu.example.testingsystem.entities.*;
import edu.example.testingsystem.forms.ProjectManagementForm;
import edu.example.testingsystem.repos.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/director/manageProject")
@SessionAttributes("selectedProject")
public class ProjectManagementController {

    TestPlanRepository planRepo;
    ProjectRepository projectRepo;
    UserRepository userRepo;
    RoleRepository roleRepo;
    ScenarioRepository scenarioRepo;

    public ProjectManagementController(TestPlanRepository planRepo, ProjectRepository projectRepo,
                                       UserRepository userRepo, RoleRepository roleRepo, ScenarioRepository scenarioRepo) {
        this.planRepo = planRepo;
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.scenarioRepo = scenarioRepo;
    }

    @GetMapping
    public String showManageProjectPage(Model model) {
        model.addAttribute("assignForm", new ProjectManagementForm());
        return "manageProject";
    }

    @ModelAttribute("testPlans")
    public List<TestPlan> addTestPlansToModel(@ModelAttribute("selectedProject") Project selectedProject) {
        return planRepo.findByProjectAndApprovedIsFalse(selectedProject);
    }

    @ModelAttribute("testers")
    public List<Userr> addTestersToModel() {
        Role role = roleRepo.findById("tester").isPresent() ? roleRepo.findById("tester").get() : null;
        if(role == null)
            throw new RuntimeException("Tester role was not found in the database");
        return userRepo.findByRole(role);
    }

    @ModelAttribute("scenarios")
    public Set<Scenario> addScenariosToModel(@ModelAttribute("selectedProject") Project selectedProject) {
        //TODO:проверить дубликаты сценариев
        List<TestPlan> testPlans = planRepo.findByProjectAndApprovedIsTrue(selectedProject);
        Set<Scenario> scenarios = new HashSet<>();//чтобы избежать дубликатов, когда один сценарий в двух планах
        for (TestPlan testPlan : testPlans) {
            scenarios.addAll(testPlan.getScenarios());
        }
        return scenarios;
        //return scenarioRepo.findAll();
    }

    @PostMapping("/approve")
    public String approveTestPlans(@ModelAttribute("approvedPlans") ArrayList<TestPlan> approvedPlans) {
        for (TestPlan testPlan : approvedPlans) {
            testPlan.setApproved(true);
        }
        planRepo.saveAll(approvedPlans);
        return "redirect:/director/manageProject";
    }

    @PostMapping("/assign")
    public String assignScenarioToTester(@ModelAttribute("assignForm") ProjectManagementForm form){
        if(form.getFormScenarios() == null || form.getFormTester() == null)
            return "manageProject";
        for(Scenario scenario : form.getFormScenarios() ){
            scenario.setExecutor(form.getFormTester());
        }
        scenarioRepo.saveAll(form.getFormScenarios());
        return "redirect:/director/manageProject";
    }
}
