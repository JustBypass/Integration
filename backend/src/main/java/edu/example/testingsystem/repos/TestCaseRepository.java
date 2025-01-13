package edu.example.testingsystem.repos;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.TestCase;
import jakarta.persistence.PostPersist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TestCaseRepository
        extends JpaRepository<TestCase, Integer> {

    List<TestCase> findByProject(Project project);
}