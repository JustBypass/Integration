package edu.example.testingsystem.controllers.tester;

import edu.example.testingsystem.entities.*;
import edu.example.testingsystem.repos.ConnectionRepository;
import edu.example.testingsystem.repos.ProjectRepository;
import edu.example.testingsystem.repos.TestPlanRepository;
import edu.example.testingsystem.security.UserInfoService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/tester")
@SessionAttributes({"scenario","scenarios","testsToExecuteConnections","selectedProject"})
public class TesterController {

    TestPlanRepository testPlanRepo;
    ProjectRepository projectRepo;
    ConnectionRepository connectionRepo;

    public TesterController(TestPlanRepository testPlanRepo, ProjectRepository projectRepo, ConnectionRepository connectionRepo) {
        this.testPlanRepo = testPlanRepo;
        this.projectRepo = projectRepo;
        this.connectionRepo = connectionRepo;
    }

    @GetMapping
    public String showTesterPage() {
        return "tester";
    }

    @ModelAttribute("projects")
    public List<Project> addProjectsToModel() {
        return projectRepo.findAll();
    }

    @ModelAttribute("currentUser")
    public Userr addCurrentUserToModel() {
        return UserInfoService.getCurrentUser();
    }

    @GetMapping("/logout")
    public String logOut(){
        SecurityContextHolder.clearContext();
        return "/login";
    }

    @PostMapping("/projectName")
    public String showSelectedProjectData(@ModelAttribute("projectName") Project project, Model model){
        if(project.getTitle() == null)
            return "redirect:/tester";
        //Project selectedProject = projectRepo.findById(projectName).get();
        addScenariosToModel(project, model);
        model.addAttribute("selectedProject", project);
        return "redirect:/tester";
    }

    @PostMapping("/scenario")
    public String startTestingProcess(@ModelAttribute("selectedScenario") Scenario scenario,Model model) {
        if(scenario == null)
            return "redirect:/tester";
        model.addAttribute("testsToExecuteConnections", connectionRepo.findByScenarioAndExecutedIsFalse(scenario));
        return "redirect:/tester/testingProcess";
    }

    /**
     * Выбирает сценарии тестирования из тест планов выбранного проекта,
     * причем исполнителем сценария должен быть назначен вошедший в систему тестировщик
     *  и в этом сценарии должно быть >0 не выполненных тест-кейсов
     * @param project проект с которым пользователь решил работать
     */
    public void addScenariosToModel(Project project, Model model) {
        List<TestPlan> approvedTestPlans = testPlanRepo.findByProjectAndApprovedIsTrue(project);
        Set<Scenario> scenarios = new HashSet<>();
        for (TestPlan testPlan : approvedTestPlans) {
            scenarios.addAll(testPlan.getScenarios().stream().
                    filter(scenario -> (Objects.equals(scenario.getExecutor().getId(), UserInfoService.getCurrentUser().getId())
                            && connectionRepo.countByScenarioAndExecutedIsFalse(scenario) > 0))
                    .toList());
        }
        model.addAttribute("scenarios", scenarios);
    }
}
