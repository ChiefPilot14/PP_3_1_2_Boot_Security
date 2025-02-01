package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Set;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();

    Optional<Role> findById(Long id);

    List<Role> findAllByIdIn(List<Long> ids);

    Role findByName(String roleUser);

    Role save(Role newRole);
}
