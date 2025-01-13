package edu.example.testingsystem.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Integer id;
    private String title;
    @ManyToOne
    private Userr creator;
    @ManyToOne
    private Userr executor;
    //@ManyToMany(mappedBy = "scenarios")
    @ManyToMany(fetch = FetchType.EAGER)
            //(mappedBy = "scenarios")
    @ToString.Exclude
    @JoinTable(
            name = "test_plan_scenarios",
            joinColumns = { @JoinColumn(name = "scenario_id") },
            inverseJoinColumns = { @JoinColumn(name = "test_plan_id") }

    )
    private List<TestPlan> testPlans = new ArrayList<>();

    @ManyToOne
    private Project project;
//    @OneToMany(mappedBy = "scenario")
//    private List<ScenarioCaseConnection> connections = new ArrayList<>();
}
