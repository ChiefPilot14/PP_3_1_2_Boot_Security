package ru.kata.spring.boot_security.demo.init;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class Init {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public Init(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //@EventListener(ContextRefreshedEvent.class)
    @PostConstruct
    public void init() {
        createTables();
        insertInitialData();
    }

    private void createTables() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS roles (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "name VARCHAR(255) NOT NULL UNIQUE)");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "password VARCHAR(255) NOT NULL, " +
                "name VARCHAR(255), " +
                "last_name VARCHAR(255), " +
                "age TINYINT, " +
                "email VARCHAR(255))");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users_roles (" +
                "user_id BIGINT NOT NULL, " +
                "role_id BIGINT NOT NULL, " +
                "PRIMARY KEY (user_id, role_id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (role_id) REFERENCES roles(id))");
    }

    private void insertInitialData() {
        jdbcTemplate.update("INSERT INTO roles (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER')");
        jdbcTemplate.update("INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN')");

        String userPassword = bCryptPasswordEncoder.encode("password");
        String adminPassword = bCryptPasswordEncoder.encode("admin");
        String admin1Password = bCryptPasswordEncoder.encode("123");

        jdbcTemplate.update("INSERT INTO users (username, password, name, last_name, age, email) " +
                "SELECT 'user', ?, 'Test User', 'Userovich', 25, 'user@example.com' " +
                "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'user')", userPassword);

        jdbcTemplate.update("INSERT INTO users (username, password, name, last_name, age, email) " +
                "SELECT 'admin', ?, 'Test Admin', 'Adminovich', 30, 'admin@example.com' " +
                "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')", adminPassword);

        jdbcTemplate.update("INSERT INTO users (username, password, name, last_name, age, email) " +
                "SELECT 'admin1', ?, 'Test Admin1', 'Adminovich1', 33, 'admin1@example.com' " +
                "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin1')", admin1Password);

        jdbcTemplate.update("INSERT INTO users_roles (user_id, role_id) " +
                "SELECT (SELECT id FROM users WHERE username = 'user'), (SELECT id FROM roles WHERE name = 'ROLE_USER') " +
                "WHERE NOT EXISTS (SELECT 1 FROM users_roles WHERE user_id = (SELECT id FROM users WHERE username = 'user') AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_USER'))");

        jdbcTemplate.update("INSERT INTO users_roles (user_id, role_id) " +
                "SELECT (SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN') " +
                "WHERE NOT EXISTS (SELECT 1 FROM users_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin') AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'))");

        jdbcTemplate.update("INSERT INTO users_roles (user_id, role_id) " +
                "SELECT (SELECT id FROM users WHERE username = 'admin1'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN') " +
                "WHERE NOT EXISTS (SELECT 1 FROM users_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin1') AND role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'))");
    }
}