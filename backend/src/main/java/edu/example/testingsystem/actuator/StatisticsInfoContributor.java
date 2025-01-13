package edu.example.testingsystem.actuator;

import edu.example.testingsystem.controllers.statistics.ProjectStatistics;
import edu.example.testingsystem.controllers.statistics.ScenarioStatistics;
import edu.example.testingsystem.controllers.statistics.TestPlanStatistics;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import edu.example.testingsystem.controllers.statistics.StatisticsCollector;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Позволяет просматривать статистику в конечной точке info актуатора
 */
@Component
public class StatisticsInfoContributor implements InfoContributor {

    private StatisticsCollector collector;

    public StatisticsInfoContributor(StatisticsCollector collector) {
        this.collector = collector;
    }

    @Override
    public void contribute(Info.Builder builder) {
        List<ProjectStatistics> projectStatistics = collector.collectStatistics();
        Map<String, String> statMap = new LinkedHashMap<>();
        for (ProjectStatistics project : projectStatistics) {
            statMap.put("Project - " + project.getTitle(), project.getPassed() + "/" + project.getTotal());
            for(TestPlanStatistics plan : project.getTestPlanStatistics()) {
                statMap.put("TestPlan - " + plan.getTitle(), plan.getPassed() + "/" + plan.getTotal());
                for(ScenarioStatistics scenario : plan.getScenarioStatistics()) {
                    statMap.put("Scenario - " + scenario.getTitle(), scenario.getTotal() + "/" + scenario.getTotal());
                }
            }
        }
        builder.withDetail("Testing statistics", statMap);
    }
}
