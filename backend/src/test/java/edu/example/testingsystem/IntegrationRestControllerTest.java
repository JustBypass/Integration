package edu.example.testingsystem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper; // Для сериализации объектов в JSON
import edu.example.testingsystem.api.integration.IntegrationRestController;
import edu.example.testingsystem.entities.Role;
import edu.example.testingsystem.entities.Userr;
import edu.example.testingsystem.repos.RoleRepository;
import edu.example.testingsystem.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(IntegrationRestController.class)
public class IntegrationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private RoleRepository roleRepo;

    @Test
    void testActivate() throws Exception {
        List<String> testersToActivate = List.of("John Doe", "Jane Doe");

        List<Userr> mockUsers = List.of(
                new Userr(1, "John Doe", true),
                new Userr(2, "Jane Doe", true)
        );

        Mockito.when(userRepo.findByFullNameIn(testersToActivate)).thenReturn(mockUsers);

        mockMvc.perform(post("/api/integration/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testersToActivate)))
                .andExpect(status().isOk())
                .andExpect(content().string("activate"));

        Mockito.verify(userRepo).findByFullNameIn(testersToActivate);
        Mockito.verify(userRepo).saveAll(mockUsers);
    }

    @Test
    void testDeactivate() throws Exception {
        List<String> testersToDeactivate = List.of("John Doe");

        List<Userr> mockUsers = List.of(new Userr(1, "John Doe", true));

        Mockito.when(userRepo.findByFullNameIn(testersToDeactivate)).thenReturn(mockUsers);

        mockMvc.perform(post("/api/integration/deactivate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(testersToDeactivate)))
                .andExpect(status().isOk())
                .andExpect(content().string("deactivate"));

        Mockito.verify(userRepo).findByFullNameIn(testersToDeactivate);
        Mockito.verify(userRepo).saveAll(mockUsers);
    }

    @Test
    void testChRoles() throws Exception {
        Map<String, String> roleMapping = Map.of("John Doe", "Tester");

        Userr mockUser = new Userr(1, "John Doe", true);
        Role mockRole = new Role("Tester", "Testing role");

        Mockito.when(userRepo.findByFullName("John Doe")).thenReturn(Optional.of(mockUser));
        Mockito.when(roleRepo.findByTitle("Tester")).thenReturn(Optional.of(mockRole));

        mockMvc.perform(post("/api/integration/chroles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(roleMapping)))
                .andExpect(status().isOk())
                .andExpect(content().string("Roles updated successfully"));

        Mockito.verify(userRepo).findByFullName("John Doe");
        Mockito.verify(roleRepo).findByTitle("Tester");
        Mockito.verify(userRepo).save(mockUser);
    }

    @Test
    void testChData() throws Exception {
        List<Userr> usersToUpdate = List.of(
                new Userr(1, "John Doe", true),
                new Userr(2, "Jane Doe", false)
        );

        mockMvc.perform(post("/api/integration/chdata")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(usersToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("chdata"));

        Mockito.verify(userRepo).saveAll(usersToUpdate);
    }
}
