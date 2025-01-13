package edu.example.testingsystem.repos;

import edu.example.testingsystem.entities.Authority;
import edu.example.testingsystem.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository
        extends CrudRepository<Authority, String> {
}