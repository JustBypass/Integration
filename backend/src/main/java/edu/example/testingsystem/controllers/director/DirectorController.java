package edu.example.testingsystem.controllers.director;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.Userr;
import edu.example.testingsystem.repos.ProjectRepository;
import edu.example.testingsystem.security.UserInfoService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/director")
@SessionAttributes("selectedProject")
public class DirectorController {

    ProjectRepository projectRepo;

    public DirectorController(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @GetMapping
    public String showDirectorPage() {
        return "director";
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

    /**
     * При выборе проекта, с которым будет работать руководитель тестирования, он автоматически становится главными в нем.
     * После назначения на проект, он перенаправляется на страницу с выбранным проектом
     */
    @PostMapping("/projectName")
    public String becomeProjectDirector(@ModelAttribute("projectName") Project project, Model model){
        if(project.getTitle() == null)
            return "redirect:/director";
        //Project selectedProject = projectRepo.findById(projectName).get();
        project.setDirector(UserInfoService.getCurrentUser());
        projectRepo.save(project);
        model.addAttribute("selectedProject", project);
        return "redirect:/director/manageProject";
    }
}
