package edu.example.testingsystem.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @NotNull
    private String title;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Authority> authorities;

    public Role(String tester, String testingRole) {
    }

//    @OneToMany(mappedBy = "role")
//    private List<User> users = new ArrayList<>();


}