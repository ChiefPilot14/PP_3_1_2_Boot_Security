package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUserForm(Model model, Principal principal) {
        String username = principal.getName();
        Optional<User> user = userService.getByUsername(username);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            Set<Role> allRoles = userService.getRoles(user.get());
            model.addAttribute("allRoles", allRoles);
        }
        return "user";
    }

}