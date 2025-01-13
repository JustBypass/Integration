package edu.example.testingsystem.controllers.statistics;

import com.google.gson.*;
import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.TestPlan;
import edu.example.testingsystem.entities.TestPlanStat;
import edu.example.testingsystem.integration.file.FileWriterGateway;
import edu.example.testingsystem.repos.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsCollector {

    //Ряд классов для сериализации вложенных объектов. Нужны, чтобы исключить рекурсию и включать в итоговый JSON только названия без деталей
    private static class ProjectSerializer implements JsonSerializer<Project> {
        public JsonElement serialize(Project src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            return new JsonPrimitive(src.getTitle());
        }
    }

    private static class TestPlanSerializer implements JsonSerializer<TestPlan> {
        public JsonElement serialize(TestPlan src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            return new JsonPrimitive(src.getTitle());
        }
    }

    private static class ScenarioSerializer implements JsonSerializer<Scenario> {
        public JsonElement serialize(Scenario src, Type typeOfSrc,
                                     JsonSerializationContext context) {
            return new JsonPrimitive(src.getTitle());
        }
    }

    ProjectRepository projectRepo;
    ScenarioRepository scenarioRepo;
    TestPlanRepository testPlanRepo;
    ConnectionRepository connectionRepo;
    TestPlanStatRepo testPlanStatRepo;
    FileWriterGateway fileWriterGateway;

    /**
     * Собирает статистику по всем проектам, вплоть до сценариев
     *
     * @return Иерархическая структура из объектов, представляющих собой статистику по каждому элементу
     */
    public List<ProjectStatistics> collectStatistics() {
        List<ScenarioStatistics> scenarioStatistics = collectScenarioStatistics();

        List<TestPlanStatistics> testPlanStatistics = collectTestPlanStatistics(scenarioStatistics);

        return collectProjectStatistics(testPlanStatistics);
    }

    /**
     * Собирает статистику по указанному проекту, вплоть до сценариев
     * @param project Проект, по котрому собирается статистика
     * @return Иерархическая структура из объектов, представляющих собой статистику по каждому элементу
     */
    public List<ProjectStatistics> collectStatisticsByProject(Project project) {
        List<ScenarioStatistics> scenarioStatistics = collectScenarioStatistics();

        List<TestPlanStatistics> testPlanStatistics = collectTestPlanStatistics(scenarioStatistics);

        return collectProjectStatisticsByProject(testPlanStatistics, project.getTitle());
    }

    /**
     * Составляет статистику по каждому сценарию
     *
     * @return список объектов со статистикой
     */
    private List<ScenarioStatistics> collectScenarioStatistics() {
        List<Scenario> allScenarios = scenarioRepo.findAll();
        List<ScenarioStatistics> scenarioStatistics = new ArrayList<>();
        for (Scenario scenario : allScenarios) {
            scenarioStatistics.add(new ScenarioStatistics(scenario.getId(), scenario.getTitle(),
                    connectionRepo.countByScenario(scenario), connectionRepo.countByScenarioAndPassedIsTrue(scenario)));
        }
        return scenarioStatistics;
    }

    /**
     * Собирает статистику по всем тест-планам,
     * актуализирует эту информацию в соответствующей табличке бд
     *
     * @param scenarioStatistics Статистика по сценариям
     * @return список объектов со статистикой
     */
    private List<TestPlanStatistics> collectTestPlanStatistics(List<ScenarioStatistics> scenarioStatistics) {
        //составляем статистику по каждому тест-плану
        List<TestPlanStatistics> testPlanStatistics = new ArrayList<>();
        List<TestPlan> allTestPlans = testPlanRepo.findAll();
        List<TestPlanStat> allTestPlanStats = new ArrayList<>();
        for (TestPlan testPlan : allTestPlans) {
            //собираем статистику по сценариям тест-плана
            List<ScenarioStatistics> scenarioStatisticsForTestPlan = scenarioStatistics.stream()
                    .filter(scenarioStat -> testPlan.getScenarios().stream().map(Scenario::getId).toList().contains(scenarioStat.getId())).toList();//если у тест плана есть такой сценарий
            TestPlanStatistics buffer = new TestPlanStatistics(testPlan.getId(), testPlan.getTitle(),
                    scenarioStatisticsForTestPlan.stream().mapToInt(ScenarioStatistics::getTotal).sum(),
                    scenarioStatisticsForTestPlan.stream().mapToInt(ScenarioStatistics::getPassed).sum(),
                    scenarioStatisticsForTestPlan);
            testPlanStatistics.add(buffer);

            //заносим статистику в бд
            Optional<TestPlanStat> optionalTestPlanStats = testPlanStatRepo.findById(testPlan.getId());
            if (optionalTestPlanStats.isPresent()) {//если для этого тест плана уже создавался объект со статистикой
                TestPlanStat testPlanStats = optionalTestPlanStats.get();
                testPlanStats.setPassedTestsAmount(buffer.getPassed());
                testPlanStats.setTotalTestsAmount(buffer.getTotal());
                allTestPlanStats.add(testPlanStats);
            } else {//если нет, то создаем новый и записываем в бд
                allTestPlanStats.add(new TestPlanStat(null, testPlan, buffer.getTotal(), buffer.getPassed()));
                System.out.println("New test plan stat created and written to db");
            }
        }
        testPlanStatRepo.saveAll(allTestPlanStats);
        return testPlanStatistics;
    }

    /**
     * Собирает статистику по всем проектам,
     *
     * @param testPlanStatistics Статистика по тест-планам
     * @return список объектов со статистикой
     */
    private List<ProjectStatistics> collectProjectStatistics(List<TestPlanStatistics> testPlanStatistics) {
        //составляем статистику по каждому проекту
        List<ProjectStatistics> projectStatistics = new ArrayList<>();
        List<Project> allProjects = projectRepo.findAll();
        for (Project project : allProjects) {
            List<TestPlanStatistics> testPlanStatisticsForProject = testPlanStatistics.stream()
                    .filter(testPlanStat -> project.getTestPlans().stream().map(TestPlan::getId).toList().contains(testPlanStat.getId())).toList();
            projectStatistics.add(new ProjectStatistics(project.getTitle(),
                    testPlanStatisticsForProject.stream().mapToInt(TestPlanStatistics::getTotal).sum(),
                    testPlanStatisticsForProject.stream().mapToInt(TestPlanStatistics::getPassed).sum(),
                    testPlanStatisticsForProject));
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        fileWriterGateway.writeToFile("Testing statistics " + formatter.format(new Date()) + ".json",
                constructJsonString(projectStatistics));

        return projectStatistics;
    }

    /**
     * Собирает статистику по указанному проекту
     * @param testPlanStatistics Статистика по тест-планам
     * @param projectTitle Название проекта
     * @return список объектов со статистикой
     */
    private List<ProjectStatistics> collectProjectStatisticsByProject(List<TestPlanStatistics> testPlanStatistics, String projectTitle) {
        //составляем статистику по каждому проекту
        List<ProjectStatistics> projectStatistics = new ArrayList<>();
        Optional<Project> optionalProject = projectRepo.findById(projectTitle);
        if (optionalProject.isEmpty())
            throw new EntityNotFoundException("No project found with title " + projectTitle);
        Project project = optionalProject.get();

        List<TestPlanStatistics> testPlanStatisticsForProject = testPlanStatistics.stream()
                .filter(testPlanStat -> project.getTestPlans().stream().map(TestPlan::getId).toList().contains(testPlanStat.getId())).toList();
        projectStatistics.add(new ProjectStatistics(project.getTitle(),
                testPlanStatisticsForProject.stream().mapToInt(TestPlanStatistics::getTotal).sum(),
                testPlanStatisticsForProject.stream().mapToInt(TestPlanStatistics::getPassed).sum(),
                testPlanStatisticsForProject));

        return projectStatistics;
    }

    /**
     * Конструирует текстовое представление собранной статистики
     *
     * @param projectStatisticsList результат работы метода {@link StatisticsCollector#collectStatistics}
     * @return Строковое представление статистики в виде JSON
     */
    public String constructJsonString(List<ProjectStatistics> projectStatisticsList) {
        //регистрируются классы для сериализации, чтоб отображать только имена объектов
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Project.class, new ProjectSerializer())
                .registerTypeAdapter(TestPlan.class, new TestPlanSerializer())
                .registerTypeAdapter(Scenario.class, new ScenarioSerializer())
                .create();

        return gson.toJson(projectStatisticsList);
    }
}
