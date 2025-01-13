package edu.example.testingsystem.api;

import edu.example.testingsystem.controllers.statistics.ProjectStatistics;
import edu.example.testingsystem.controllers.statistics.StatisticsCollector;
import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.integration.file.FileWriterGateway;
import edu.example.testingsystem.repos.ProjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/statistics")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StatisticsRestController {

    FileWriterGateway fileWriterGateway;
    StatisticsCollector collector;
    ProjectRepository projectRepository;

    @GetMapping("/project/{title}")
    public ResponseEntity<Object> statisticsByProject(@PathVariable("title") String title) {
        Optional<Project> optionalProject = projectRepository.findById(title);
        if(optionalProject.isEmpty())
            return ResponseEntity.notFound().build();
        Project project = optionalProject.get();

        List<ProjectStatistics> projectStatisticsList = collector.collectStatisticsByProject(project);
        String jsonString = collector.constructJsonString(projectStatisticsList);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/plain"))
                .body(jsonString.getBytes());
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> returnAllStatistics() throws FileNotFoundException {
        List<ProjectStatistics> projectStatisticsList = collector.collectStatistics();
        String jsonString = collector.constructJsonString(projectStatisticsList);
        fileWriterGateway.writeToFile("Statistics.txt", jsonString);

        InputStreamResource resource = new InputStreamResource(new FileInputStream("backend/src/main/stat/Statistics.txt"));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(jsonString.getBytes().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "statistics.txt")
                .body(resource);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> receiveFile(@RequestParam("file") MultipartFile file) throws IOException {
        Path path = Path.of("backend/src/main/tmp/");
        Path destinationFile = path.resolve(Paths.get(file.getOriginalFilename())).normalize().toAbsolutePath();

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile,
                    StandardCopyOption.REPLACE_EXISTING);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<ProjectStatistics>> projectStatOrderedByPassedAsc() {
        List<ProjectStatistics> projectStatisticsList = collector.collectStatistics();
        projectStatisticsList = projectStatisticsList.stream().sorted((first, second) -> {
            //если у кого-то впринципе нет содержимого, то считаем его наименее выполненным
            if(first.getTotal() == 0 && second.getTotal() != 0)
                return -1;
            if(first.getTotal() != 0 && second.getTotal() == 0)
                return 1;
            if(first.getTotal() == 0 && second.getTotal() == 0)
                return 0;
            //далее сравниваем процент выполнения
            Float firstCompletedPart = (float) (first.getPassed() / first.getTotal());
            Float secondCompletedPart = (float) (second.getPassed() / second.getTotal());
            return firstCompletedPart.compareTo(secondCompletedPart);
        }).toList();

        return ResponseEntity.ok(projectStatisticsList);
    }
}