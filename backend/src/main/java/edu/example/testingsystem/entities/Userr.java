package edu.example.testingsystem.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

    @Data
    @Entity
    @Table(indexes = {
            @Index(columnList = "login"),
            @Index(columnList = "role_title"),
            @Index(columnList = "is_active")
    })
    @NoArgsConstructor
    public class Userr implements UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @NotNull
        private Integer id;
        @JsonIgnore
        @Generated( value = GenerationTime.ALWAYS )
        @Column(columnDefinition = "varchar(255) GENERATED ALWAYS AS (surname || ' ' || name || ' ' || patronymic) STORED")
        private String fullName;
        private String name;
        private String surname;
        private String patronymic;
        @NotNull
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String login;
        @NotNull
        private String password;
        @ManyToOne
        @JsonIgnore
        private Role role;
        private Boolean isActive;

    public Userr(String login, String password) {
        this.login = login;
        this.password = password;
        this.role = null;
        this.isActive = false;
    }

    public Userr(int i, String johnDoe, boolean b) {

    }

    public void assignRoleAndMakeActive(Role role) {
        this.role = role;
        this.isActive = true;
    }

    /**
     * Добавляет в список все привилегии, связанные с ролью пользователя
     * @return список всех привилегий пользователя
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (role == null)
            return authorities;
        for(Authority authority : role.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return login;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isActive;
    }

//    @OneToMany(mappedBy = "director")
//    private List<Project> projectDirectors;
//    @OneToMany(mappedBy = "creator")
//    private List<TestPlan> testPlanCreators;
//    @OneToMany(mappedBy = "creator")
//    private List<Scenario> scenarioCreators;
//    @OneToMany(mappedBy = "executor")
//    private List<Scenario> scenarioExecutors;
//    @OneToMany(mappedBy = "creator")
//    private List<TestCase> testCaseCreators;
}