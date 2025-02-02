package ru.kata.spring.boot_security.demo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.kata.spring.boot_security.demo.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDao userDao;
    private final RoleService roleService;
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserDao userDao, RoleService roleService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userDao = userDao;
        this.roleService = roleService;
    }

    @Override
    @Transactional
    public void createUser(User user) {
        Set<Role> roles = user.getRoles();

        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Пользователь должен иметь хотя бы одну роль");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        Set<Role> managedRoles = new HashSet<>();

        for (Role role : roles) {
            Role managedRole = entityManager.merge(role);
            managedRoles.add(managedRole);
            System.out.println("Роль добавлена: " + managedRole.getName());
        }

        user.setRoles(managedRoles, false);

        try {
            userDao.save(user);
            System.out.println("Пользователь успешно создан: " + user.getUsername());
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении пользователя: " + e);
            throw e;
        }
    }
    @Override
    @Transactional
    public void updateUser(Long userId, User updatedUser, Long[] rolesIds) {
        User existingUser = userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с id=" + userId + " не найден"));

        if (!updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(bCryptPasswordEncoder.encode(updatedUser.getPassword()));
        } else {
            updatedUser.setPassword(existingUser.getPassword());
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setAge(updatedUser.getAge());

        Set<Role> updatedRoles = new HashSet<>();
        if (rolesIds != null) {
            for (Long roleId : rolesIds) {
                Optional<Role> optionalRole = roleService.findById(roleId);
                if (optionalRole.isPresent()) {
                    updatedRoles.add(optionalRole.get());
                } else {
                    throw new EntityNotFoundException("Роль с id=" + roleId + " не найдена");
                }
            }
        }
        existingUser.setRoles(updatedRoles, true);

        userDao.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserOrCreateIfNotExists(long id) {
        return userDao.findById(id)
                .orElse(new User(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String username) {
        return userDao.findByUsername(username);
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = getByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        User userEntity = optionalUser.get();

        if (userEntity.getPassword() == null || userEntity.getPassword().isBlank()) {
            throw new InternalAuthenticationServiceException("Password is missing for user: " + username);
        }

        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                authorities
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean findByUsername(String username) {
        return getByUsername(username).isPresent();
    }

    @Override
    @Transactional
    public void setRolesForUser(User user, List<Long> rolesIds) {
        List<Role> roles = roleService.findAllByIdIn(rolesIds);
        user.setRoles(new HashSet<>(roles), false);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRoles(User user) {
        return user.getRoles();
    }
}