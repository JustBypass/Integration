package edu.example.testingsystem.repos;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RequirementRepository
        extends JpaRepository<Requirement, Integer> {

    List<Requirement> findByProject(Project project);
}