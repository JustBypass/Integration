package edu.example.testingsystem.controllers.statistics;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.TestPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Представляет собранную в моменте статистику по проекту
 * Является началом иерархической цепочки для записи статистики по тест-планам и сценариям
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStatistics {
    private String title;
    private Integer total;
    private Integer passed;
    private List<TestPlanStatistics> testPlanStatistics;
}
