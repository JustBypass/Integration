package edu.example.testingsystem.controllers.statistics;

import edu.example.testingsystem.entities.TestPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Представляет собранную в моменте статистику по тест-плану
 * Содержит ссылки на статистику дочерних сценариев
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestPlanStatistics {
    private Integer id;
    private String title;
    private Integer total;
    private Integer passed;
    private List<ScenarioStatistics> scenarioStatistics;
}
