package ru.kata.spring.boot_security.demo.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import jakarta.annotation.PostConstruct;
import java.util.Collections;

@Component
@Transactional
public class Init {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public Init(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    @PostConstruct
    public void init() {
        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleUser = new Role("ROLE_USER");

        roleService.save(roleAdmin);
        roleService.save(roleUser);

        User admin = new User(
                "admin",
                "admin",
                Collections.singleton(roleAdmin),
                "Admin",
                "Adminov",
                (byte) 30,
                "admin@example.com"
        );

        User user = new User(
                "user",
                "user",
                Collections.singleton(roleUser),
                "User",
                "Userov",
                (byte) 25,
                "user@example.com"
        );

        userService.createUser(admin);
        userService.createUser(user);
    }

}
