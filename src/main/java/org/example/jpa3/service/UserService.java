package org.example.jpa3.service;

import org.example.jpa3.entities.Role;
import org.example.jpa3.entities.User;

public interface UserService {
    User addNewUser(User user);
    Role addNewRole(Role role);
    User findUserByUserName(String userName);
    Role findRoleByRoleName(String roleName);
    void addRoleToUser(String userName, String roleName);
    User AuthenticateUser(String userName, String password);
}
