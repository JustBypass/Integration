package edu.example.testingsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Привилегии
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    @Id
    @NotNull
    private String name;
}
