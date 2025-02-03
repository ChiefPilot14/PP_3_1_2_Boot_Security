package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Arrays;
import java.util.List;

@Controller
public class RegistrationController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public RegistrationController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        List<Role> allRoles = roleService.getAllRoles();
        model.addAttribute("allRoles", allRoles);
        return "registration";
    }

    @PostMapping("/registration")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               Model model, @RequestParam(value = "roles[]", required = false) Long[] rolesIds) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        if (userService.findByUsername(user.getUsername())) {
            model.addAttribute("errorMessage", "Пользователь с таким именем пользователя " +
                    "уже зарегистрирован.");
            return "registration";
        }

        userService.setRolesForUser(user, Arrays.asList(rolesIds));

        userService.createUser(user);
        return "redirect:/admin";
    }

}