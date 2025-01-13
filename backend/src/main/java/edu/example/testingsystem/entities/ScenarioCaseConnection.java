package edu.example.testingsystem.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(indexes = {
        @Index(columnList = "test_case_id"),
        @Index(columnList = "test_plan_id"),
        @Index(columnList = "scenario_id, executed"),
        @Index(columnList = "scenario_id, passed")
})
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioCaseConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Integer id;
    private String comment;
    private Boolean executed;
    private Boolean passed;

    @ManyToOne
    private Scenario scenario;
    @ManyToOne
    private TestCase testCase;

    @ManyToOne
    private TestPlan testPlan;

}