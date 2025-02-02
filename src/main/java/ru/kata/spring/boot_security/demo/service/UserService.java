package ru.kata.spring.boot_security.demo.service;

import jakarta.validation.Valid;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    void createUser(User user);

    void updateUser(Long userId, User updatedUser, Long[] rolesIds);

    void deleteUser(long id);

    User getUserOrCreateIfNotExists(long id);

    List<User> getAllUsers();

    Optional<User> getByUsername(String username);

    boolean findByUsername(String username);

    void setRolesForUser(@Valid User user, List<Long> list);

    Set<Role> getRoles(User user);

}
