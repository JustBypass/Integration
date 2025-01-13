package edu.example.testingsystem.controllers;

import edu.example.testingsystem.entities.Role;
import edu.example.testingsystem.entities.Userr;
import edu.example.testingsystem.forms.ActiveUserForm;
import edu.example.testingsystem.forms.RoleAssigningForm;
import edu.example.testingsystem.forms.UsersFullNameForm;
import edu.example.testingsystem.repos.RoleRepository;
import edu.example.testingsystem.repos.UserRepository;
import edu.example.testingsystem.security.UserInfoService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private UserRepository userRepo;
    private RoleRepository roleRepo;

    public AdminController(UserRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @ModelAttribute("users")
    public List<Userr> addUsersToModel(){
        return userRepo.findAll();
    }

    @ModelAttribute("roles")
    public List<Role> addRolesToModel(){
        return roleRepo.findAll();
    }

    @ModelAttribute("currentUser")
    public Userr addCurrentUserToModel() {
        return UserInfoService.getCurrentUser();
    }

    @GetMapping
    public String showAdminPage(Model model) {
        model.addAttribute("assignForm", new RoleAssigningForm());
        model.addAttribute("activeForm", new ActiveUserForm());
        model.addAttribute("fullNameForm", new UsersFullNameForm());
        return "admin";
    }

    @GetMapping("/logout")
    public String logOut(){
        SecurityContextHolder.clearContext();
        return "/login";
    }

    @PostMapping("/assignRole")
    public String assignRole(@ModelAttribute("assignForm") RoleAssigningForm form) {
        //если не стоит галочка убрать роль, то роль должна быть выбрана
        if(form == null || form.getUser() == null || (!form.getRemove() && form.getRole() == null))
            return "redirect:/admin";
        if(!form.getRemove())
            form.getUser().assignRoleAndMakeActive(form.getRole());
        else
            form.getUser().assignRoleAndMakeActive(null);
        userRepo.save(form.getUser());
        return "redirect:/admin";
    }

    @PostMapping("/setActive")
    public String setActive(@ModelAttribute("activeForm") ActiveUserForm form) {
        if(form == null || form.getUser() == null)
            return "redirect:/admin";
        form.getUser().setIsActive(form.getActive());
        userRepo.save(form.getUser());
        return "redirect:/admin";
    }

    @PostMapping("/setFullName")
    public String setFullName(@ModelAttribute("fullNameForm") UsersFullNameForm form) {
        if(form == null || form.getUser() == null)
            return "redirect:/admin";
        form.getUser().setName(form.getName());
        form.getUser().setSurname(form.getSurname());
        form.getUser().setPatronymic(form.getPatronymic());
        userRepo.save(form.getUser());
        return "redirect:/admin";
    }

    @PostMapping("/deleteInactive")
    public String deleteInactiveUsers() {
        userRepo.deleteByIsActive(false);
        return "redirect:/admin";
    }

    @PostMapping("/deleteWithoutRoles")
    public String deleteUsersWithoutRoles() {
        userRepo.deleteByRole(null);
        return "redirect:/admin";
    }
}
