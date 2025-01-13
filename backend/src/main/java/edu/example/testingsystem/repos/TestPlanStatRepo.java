package edu.example.testingsystem.repos;

import edu.example.testingsystem.entities.TestPlan;
import edu.example.testingsystem.entities.TestPlanStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPlanStatRepo
        extends JpaRepository<TestPlanStat, Integer> {
}
