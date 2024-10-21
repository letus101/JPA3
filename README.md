# Rapport du Projet

## Créateur
Youssef Ketaj

## Structure du Projet

Le projet est structuré de la manière suivante :

```
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── example/
│   │           └── jpa3/
│   │               ├── entities/
│   │               │   ├── Role.java
│   │               │   └── User.java
│   │               ├── repositories/
│   │               │   ├── RoleRepository.java
│   │               │   └── UserRepository.java
│   │               └── service/
│   │                   ├── UserService.java
│   │                   └── UserServiceImpl.java
│   └── resources/
│       └── application.properties
└── test/
```

## Configuration

Le projet utilise Spring Boot et Maven pour la gestion des dépendances et la configuration. Le fichier `application.properties` contient les configurations nécessaires pour la connexion à la base de données et d'autres paramètres de l'application.

## Entities

### `User.java`
```java
package org.example.jpa3.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Data
public class User {
    @Id
    private String userId;
    private String userName;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
}
```

### `Role.java`
```java
package org.example.jpa3.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String roleName;
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
```

## Repositories

### `UserRepository.java`
```java
package org.example.jpa3.repositories;

import org.example.jpa3.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUserName(String userName);
}
```

### `RoleRepository.java`
```java
package org.example.jpa3.repositories;

import org.example.jpa3.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
}
```

## Services

### `UserService.java`
```java
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
```

### `UserServiceImpl.java`
```java
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
        if (user.getRoles() != null) {
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
```