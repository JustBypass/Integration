package edu.example.testingsystem.repos;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.TestPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TestPlanRepository
        extends JpaRepository<TestPlan, Integer> {

    List<TestPlan> findByProject(Project project);

    List<TestPlan> findByProjectAndApprovedIsFalse(Project project);

    List<TestPlan> findByProjectAndApprovedIsTrue(Project project);
}