package edu.example.testingsystem.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(indexes = @Index(columnList = "project_title"))
@AllArgsConstructor
@NoArgsConstructor
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Integer id;
    private String title;
    @ManyToOne
    private Project project;
    @ManyToOne
    private Userr creator;
    @NotNull
    private String description;
    @NotNull
    private String inputData;
    @NotNull
    private String outputData;
//    @OneToMany(mappedBy = "testCase")
//    private List<ScenarioCaseConnection> connections = new ArrayList<>();
}