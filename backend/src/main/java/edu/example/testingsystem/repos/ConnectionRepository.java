package edu.example.testingsystem.repos;

import edu.example.testingsystem.entities.Scenario;
import edu.example.testingsystem.entities.ScenarioCaseConnection;
import edu.example.testingsystem.entities.TestCase;
import edu.example.testingsystem.entities.TestPlan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ConnectionRepository
        extends JpaRepository<ScenarioCaseConnection, Integer> {

        @Transactional
        void deleteByScenario(Scenario scenario);

        @Transactional
        void deleteByTestCase(TestCase testCase);

        @Transactional
        void deleteByTestPlan(TestPlan testPlan);

        List<ScenarioCaseConnection> findByScenarioAndExecutedIsFalse(Scenario scenario);

        List<ScenarioCaseConnection> findByTestCase(TestCase testCase);

        List<ScenarioCaseConnection> findByScenario(Scenario scenario);

        Optional<ScenarioCaseConnection> findByScenarioAndTestCase(Scenario scenario, TestCase testCase);

        Integer countByScenarioAndExecutedIsFalse(Scenario scenario);

        Integer countByScenario(Scenario scenario);


        Integer countByScenarioAndPassedIsTrue(Scenario scenario);
}