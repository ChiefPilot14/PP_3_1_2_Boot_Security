package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return roleDao.findById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllByIdIn(List<Long> ids) {
        return roleDao.findAllById(ids);
    }


    @Override
    @Transactional(readOnly = true)
    public Role findByName(String roleUser) {
        return roleDao.findByName(roleUser);
    }

    @Override
    @Transactional
    public Role save(Role newRole) {
        return roleDao.saveAndFlush(newRole);
    }
}
