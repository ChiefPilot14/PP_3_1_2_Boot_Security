package ru.kata.spring.boot_security.demo.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Collections;
import java.util.HashSet;

@Component
@Transactional
public class Init implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public Init(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) {
        Role roleUser = roleService.findByName("ROLE_USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_USER");
            Role savedRole = roleService.save(newRole);
            System.out.println("Saved ROLE_USER with ID: " + savedRole.getId()); // Логирование
            return savedRole;
        });

        roleUser = roleService.save(roleUser);

        Role roleAdmin = roleService.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_ADMIN");
            Role savedRole = roleService.save(newRole);
            System.out.println("Saved ROLE_ADMIN with ID: " + savedRole.getId()); // Логирование
            return savedRole;
        });

        roleAdmin = roleService.save(roleAdmin);


        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setRoles(new HashSet<>(Collections.singletonList(roleUser)), false);
        user.setName("Test User");
        user.setLastName("Userovich");
        user.setAge((byte) 25);
        user.setEmail("user@example.com");
        userService.createUser(user);


        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin");
        admin.setRoles(new HashSet<>(Collections.singletonList(roleAdmin)), false);
        admin.setName("Test Admin");
        admin.setLastName("Adminovich");
        admin.setAge((byte) 30);
        admin.setEmail("admin@example.com");
        userService.createUser(admin);

        User admin1 = new User();
        admin1.setUsername("admin1");
        admin1.setPassword("123");
        admin1.setRoles(new HashSet<>(Collections.singletonList(roleAdmin)), false);
        admin1.setName("Test Admin1");
        admin1.setLastName("Adminovich1");
        admin1.setAge((byte) 33);
        admin1.setEmail("admin1@example.com");
        userService.createUser(admin1);
    }
}