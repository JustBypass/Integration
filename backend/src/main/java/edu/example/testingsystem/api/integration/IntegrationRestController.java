package edu.example.testingsystem.api.integration;

import java.util.*;
import java.util.stream.Collectors;

import edu.example.testingsystem.entities.*;
import edu.example.testingsystem.mapstruct.mapper.ScenarioMapper;
import edu.example.testingsystem.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import edu.example.testingsystem.mapstruct.dto.UserDto;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.function.*;


@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationRestController {

    private final ProjectRepository projectRepository;
    private final TestPlanRepository testPlanRepository;
    private final ScenarioRepository scenarioRepository;
    private final  UserRepository userRepo;
    private final ConnectionRepository connectionRepository;
    private final TestCaseRepository testCaseRepository;
    private final RoleRepository roleRepo;
    private final RestTemplate restTemplate;
    private static final Logger logger = LogManager.getLogger(IntegrationRestController.class);

    // Преобразование NUser в Userr
    private Userr convertToUserr(NUser nUser) {
        Userr userr = new Userr();
        userr.setName(nUser.getName());
        userr.setSurname(nUser.getSurname());
        userr.setPatronymic(nUser.getPatronymic());
        userr.setLogin(nUser.getLogin());
        userr.setPassword(nUser.getPassword());
        userr.setFullName(nUser.getFullName());

        return userr;
    }

    private List<Userr> convertToUserrList(List<NUser> nUserList) {
        return nUserList.stream()
                .map(this::convertToUserr)
                .collect(Collectors.toList());
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestBody List<String> testersToActivate) {
        logger.info("Activating testers: {}", testersToActivate);

        List<Userr> users = userRepo.findByLoginIn(testersToActivate);
        users.forEach(user -> user.setIsActive(true));
        userRepo.saveAll(users);

        return ResponseEntity.ok("Activated");
    }

    @PostMapping("/deactivate")
    public ResponseEntity<String> deactivate(@RequestBody List<String> testersToDeactivate) {
        logger.info("Deactivating testers: {}", testersToDeactivate);

        List<Userr> users = userRepo.findByLoginIn(testersToDeactivate);
        users.forEach(user -> user.setIsActive(false));
        userRepo.saveAll(users);

        return ResponseEntity.ok("Deactivated");
    }

    @PostMapping("/chroles")
    public ResponseEntity<String> chroles(@RequestBody Map<String, String> rolesMap) {
        logger.info("Changing roles: {}", rolesMap);

//        rolesMap.forEach((login, roleTitle) -> {
//            userRepo.findByLogin(login).ifPresent(user -> {
//                roleRepo.findByTitle(roleTitle).ifPresent(role -> {
//                    user.setRole(role);
//                    userRepo.save(user);
//                    logger.info("Assigned role '{}' to user '{}'", roleTitle, login);
//                });
//            });
//        });

        return ResponseEntity.ok("Roles updated successfully");
    }

    @PostMapping("/chdata")
    public ResponseEntity<String> chdata(@RequestBody List<NUser> usersToUpdate) {
        logger.info("Updating users: {}", usersToUpdate);

        userRepo.saveAll(convertToUserrList(usersToUpdate));

        return ResponseEntity.ok("Updated user data");
    }



    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody NUser userToCreate) {
        logger.info("Creating user: {}", userToCreate);

        userRepo.save(convertToUserr(userToCreate));

        return ResponseEntity.ok("User created successfully");
    }

    @PostMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody NUser userToUpdate) {
        logger.info("Updating user: {}", userToUpdate);

        userRepo.save(convertToUserr(userToUpdate));

        return ResponseEntity.ok("User updated successfully");
    }

    @PostMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestBody NUser userToDelete) {
        logger.info("Deleting user: {}", userToDelete);

        userRepo.delete(convertToUserr(userToDelete));

        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<Userr>> getAllUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }
    @PostMapping("/addRole")
    public ResponseEntity<String> addRole(@RequestBody NEntry entry) {
        logger.info("Adding role to user: {}", entry);

        userRepo.findByFullName(entry.getFullName()).ifPresent(user -> {
            roleRepo.findByTitle(entry.getRole()).ifPresent(role -> {
                user.setRole(role);
                userRepo.save(user);
                logger.info("Assigned role '{}' to user '{}'", entry.getRole(), entry.getFullName());
            });
        });

        return ResponseEntity.ok("Role added successfully");
    }

//
    public void updateUsers(List<Userr> newList, List<Userr> currentList) {
        Map<String, Userr> currentMap = currentList.stream()
                .collect(Collectors.toMap(Userr::getLogin, user -> user));

        List<Userr> usersToAdd = new ArrayList<>();

        for (Userr newUser : newList) {
            String userLogin = newUser.getLogin();

            if (currentMap.containsKey(userLogin)) {
                Userr currentUser = currentMap.get(userLogin);

                updateUser(currentUser, newUser);
            } else {
                usersToAdd.add(newUser);
            }
        }
        List<Userr> usersToRemove = new ArrayList<>(currentList.stream()
                .filter(user -> newList.stream().noneMatch(newUser -> newUser.getLogin().equals(user.getLogin())))
                .toList());
        // Удаляем пользователей из currentList, которых нет в newList
        currentList.removeIf(user -> newList.stream()
                .noneMatch(newUser -> newUser.getLogin().equals(user.getLogin())));

        // Добавляем новые пользователи в currentList
        currentList.addAll(usersToAdd);

        userRepo.deleteAll();

        userRepo.saveAll(currentList);
    }

    private void updateUser(Userr currentUser, Userr newUser) {
        currentUser.setName(newUser.getName());
        currentUser.setSurname(newUser.getSurname());
        currentUser.setPatronymic(newUser.getPatronymic());
        currentUser.setLogin(newUser.getLogin());
        currentUser.setPassword(newUser.getPassword());
        currentUser.setRole(newUser.getRole());
        currentUser.setIsActive(newUser.getIsActive());
    }

    @Scheduled(fixedRate = 20000)
    public void receive() {
        logger.info("Receiving data from external API");

        String url = "http://pms-api:8080/api/v1/integration/allUsers";
        List<NUser> nUserList = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<NUser>>() {}
        ).getBody();
        logger.info(nUserList);

        if (nUserList == null) {
            logger.warn("No data received from external API");
            return;
        }

        List<Userr> newList = convertToUserrList(nUserList);
        List<Userr> currentList = userRepo.findAll();

        System.out.println(newList);
        System.out.println(currentList);
        System.out.println("hello");

        userRepo.deleteAll();

        updateUsers(newList,currentList);

        System.out.println("Exit");
    }
}
