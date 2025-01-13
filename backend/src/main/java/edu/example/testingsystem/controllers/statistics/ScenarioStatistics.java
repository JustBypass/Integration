package edu.example.testingsystem.controllers.statistics;

import edu.example.testingsystem.entities.Scenario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Представляет собранную в моменте статистику по сценарию
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioStatistics {
    private Integer id;
    private String title;
    private Integer total;
    private Integer passed;
}
