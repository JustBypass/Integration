package edu.example.testingsystem.entities;

import edu.example.testingsystem.controllers.statistics.TestPlanStatistics;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(indexes = @Index(columnList = "project_title, approved"))
@AllArgsConstructor
@NoArgsConstructor
public class TestPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Date startDate;
    private Date endDate;
    private String title;
    @ManyToOne
    private Userr creator;
    private Boolean approved;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "test_plan_scenarios",
            joinColumns = { @JoinColumn(name = "test_plan_id") },
            inverseJoinColumns = { @JoinColumn(name = "scenario_id") }
    )
    private List<Scenario> scenarios = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private Project project;


//    @OneToOne(mappedBy = "testPlan", cascade = CascadeType.ALL)
//    @PrimaryKeyJoinColumn
//    private TestPlanStat statistics;
}