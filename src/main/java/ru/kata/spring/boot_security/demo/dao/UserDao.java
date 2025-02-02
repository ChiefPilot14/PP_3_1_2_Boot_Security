package ru.kata.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

//    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
//    Optional<User> findUserWithRoles(@Param("username") String username);
//
    Optional<User> findByUsername(String username);

}