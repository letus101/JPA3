package org.example.jpa3.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.jpa3.entities.Role;
import org.example.jpa3.entities.User;
import org.example.jpa3.repositories.RoleRepository;
import org.example.jpa3.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Override
    public User addNewUser(User user) {
        user.setUserId(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    @Override
    public Role addNewRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public Role findRoleByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public void addRoleToUser(String userName, String roleName) {
        User user = userRepository.findByUserName(userName);
        Role role = roleRepository.findByRoleName(roleName);
        if (user.getRoles()!=null) {
            user.getRoles().add(role);
            role.getUsers().add(user);
        }
    }

    @Override
    public User AuthenticateUser(String userName, String password) {
        User user = userRepository.findByUserName(userName);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                return user;
            } else {
                throw new RuntimeException("Bad credentials");
            }
        } else {
            throw new RuntimeException("Bad credentials");
        }
    }
}
