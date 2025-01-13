package edu.example.testingsystem.controllers.tester;

import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.ScenarioCaseConnection;
import edu.example.testingsystem.entities.TestCase;
import edu.example.testingsystem.forms.ExecutedTestCaseForm;
import edu.example.testingsystem.repos.ConnectionRepository;
import edu.example.testingsystem.repos.TestCaseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tester/testingProcess")
@SessionAttributes("testsToExecuteConnections")
public class TestingProcessController {

    private ConnectionRepository connectionRepo;
    private TestCaseRepository testCaseRepo;

    public TestingProcessController(ConnectionRepository connectionRepo, TestCaseRepository testCaseRepo) {
        this.connectionRepo = connectionRepo;
        this.testCaseRepo = testCaseRepo;
    }

    @ModelAttribute("currentConnection")
    public ScenarioCaseConnection setNextConnectionToModel(@ModelAttribute("testsToExecuteConnections") List<ScenarioCaseConnection> connections) {
        if (connections.isEmpty())
            return null;
        return connections.get(0);
    }

    @GetMapping
    public String showTestingProcessPage(Model model) {
        model.addAttribute("executedTestCaseForm", new ExecutedTestCaseForm());
        return "testingProcess";
    }

    /**
     * Метод, вызываемый
     * @param form Данные, которые заполняет тестировщик о прохождении тест-кейса
     * @param connections Список оставшихся к прохождению тест-кейсов этим тестировщиком
     * @param currentConnection Информация о текущем тест-кейсе(в связи со сценарием, в рамках которого он выполняется)
     * @return страница со следующим тест-кейсом
     */
    @PostMapping("/submit")
    public String submit(@ModelAttribute("executedTestCaseForm") ExecutedTestCaseForm form,Model model,
                         @ModelAttribute("testsToExecuteConnections") List<ScenarioCaseConnection> connections,
                         @ModelAttribute("currentConnection" ) ScenarioCaseConnection currentConnection) {
        if(form == null || form.getComment() == null)
            return "redirect:/testing/testingProcess";
        //убираем из списка оставшихся тест-кейсов
        connections.remove(currentConnection);
        //обновляем список оставшихся
        model.addAttribute("testsToExecuteConnections", connections);
        //заносим и сохраняем информацию о похождении теста
        currentConnection.setPassed(form.getPassed());
        currentConnection.setComment(form.getComment());
        currentConnection.setExecuted(true);
        connectionRepo.save(currentConnection);
        return "redirect:/tester/testingProcess";
    }

}
