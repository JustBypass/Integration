package edu.example.testingsystem.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NEntry {
    @JsonProperty("id")
    String fullName;
    @JsonProperty("role")
    String role;
}
