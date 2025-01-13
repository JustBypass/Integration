package edu.example.testingsystem.controllers.statistics;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.integration.file.FileWriterGateway;
import edu.example.testingsystem.repos.ProjectRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private StatisticsCollector statisticsCollector;
    private ProjectRepository projectRepo;
    private FileWriterGateway fileWriterGateway;

    public StatisticsController(ProjectRepository projectRepo, StatisticsCollector statisticsCollector, FileWriterGateway fileWriterGateway) {
        this.projectRepo = projectRepo;
        this.statisticsCollector = statisticsCollector;
        this.fileWriterGateway = fileWriterGateway;
    }

    @GetMapping
    public String showStatisticsPage() {
        return "statistics";
    }

    @ModelAttribute("projects")
    public List<Project> addProjectsToModel() {
        return projectRepo.findAll();
    }

    @ModelAttribute("totalStatistics")
    public List<ProjectStatistics> addStatisticsToModel() {
        List<ProjectStatistics> projectStatisticsList = statisticsCollector.collectStatistics();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        fileWriterGateway.writeToFile("Testing statistics " + formatter.format(new Date()),
                statisticsCollector.constructJsonString(projectStatisticsList));

        return projectStatisticsList;
    }
}
