package edu.example.testingsystem.controllers.analyst;

import edu.example.testingsystem.entities.TestCase;
import edu.example.testingsystem.entities.TestPlan;
import edu.example.testingsystem.forms.TestCaseForm;
import edu.example.testingsystem.forms.TestPlanForm;
import edu.example.testingsystem.repos.TestPlanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.util.Optional;

@Controller
@RequestMapping("analyst/testPlans/{id}")
public class ChangeTestPlanController {
    private final TestPlanRepository testPlanRepo;

    public ChangeTestPlanController(TestPlanRepository planRepository) {
        this.testPlanRepo = planRepository;
    }

    @GetMapping
    public String showTestPlanPage(Model model, @PathVariable("id") Integer id) {
        TestPlan testPlan = findTestPlan(id);

        TestPlanForm testPlanForm = new TestPlanForm();

        testPlanForm.setTestPlanTitle(testPlan.getTitle());
        testPlanForm.setStartDate(testPlan.getStartDate());
        testPlanForm.setEndDate(testPlan.getEndDate());

        model.addAttribute("changeTestPlanForm", testPlanForm);

        return "changeTestPlan";
    }

    @PostMapping("/submit")
    public String submitChanges(@ModelAttribute("testPlanForm") TestPlanForm testPlanForm, @PathVariable("id") Integer id) {
        TestPlan testPlan = findTestPlan(id);

        //Обновляем данные в тест-плане после изменения и сохраняем его
        testPlan.setTitle(testPlanForm.getTestPlanTitle());
        testPlan.setStartDate(testPlanForm.getStartDateDate());
        testPlan.setEndDate(testPlanForm.getEndDateDate());
        testPlan.setApproved(false);

        testPlanRepo.save(testPlan);

        return "redirect:/analyst/testPlans";
    }

    private TestPlan findTestPlan(Integer id) {
        Optional<TestPlan> testPlan = testPlanRepo.findById(id);
        if(testPlan.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }else
            return testPlan.get();
    }
}
