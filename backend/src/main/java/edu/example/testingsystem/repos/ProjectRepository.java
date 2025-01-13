package edu.example.testingsystem.repos;

import edu.example.testingsystem.entities.Project;
import edu.example.testingsystem.entities.Userr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepository
        extends JpaRepository<Project, String> {

    void deleteByDirector(Userr user);

    Project findByDirector(Userr user);
}