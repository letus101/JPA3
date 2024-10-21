# Project Report

## Creator
Youssef Ketaj

## Project Structure

The project is organized as follows:

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

## Explanation of Each Code Part

### `application.properties`
This configuration file contains the Spring Boot application properties, such as the application name, server port, in-memory H2 database URL, and enabling the H2 console.

```ini
spring.application.name=JPA3
server.port=8085
spring.datasource.url=jdbc:h2:mem:usersdb
spring.h2.console.enabled=true
```
## Entities

### `User.java`
This class represents the `User` entity with JPA annotations for persistence. It uses Lombok to automatically generate getters, setters, constructors, and other utility methods.

```java
package org.example.jpa3.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String userId;
    @Column(name = "USER_NAME", unique = true, length = 20)
    private String userName;
    private String password;
    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();
}
```

### `Role.java`
This class represents the `Role` entity with JPA annotations for persistence. It also uses Lombok to generate utility methods. The `ManyToMany` relationship with the `User` entity is defined here.

```java
package org.example.jpa3.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "DESCRIPTION")
    private String desc;
    @Column(name = "ROLE_NAME", unique = true, length = 20)
    private String roleName;
    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<User> users = new ArrayList<>();
}
```
## Repositories

### `UserRepository.java`
This file defines the repository for the `User` entity, extending `JpaRepository`. It allows CRUD operations on users and includes a custom method to find a user by their username.

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
This file defines the repository for the `Role` entity, extending `JpaRepository`. It allows CRUD operations on roles and includes a custom method to find a role by its name.

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
This interface defines the service methods for managing users and roles, such as adding a new user, adding a new role, finding a user by their username, finding a role by its name, adding a role to a user, and authenticating a user.

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
This class implements the `UserService` interface and provides concrete implementations of the methods defined in the interface. It uses the `UserRepository` and `RoleRepository` to interact with the database.

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
## Main Application Class
### `Jpa3Application.java`
This class contains the main entry point of the Spring Boot application. It also defines a `CommandLineRunner` that initializes some users and roles and performs test operations such as authenticating a user and displaying their roles.

```java
package org.example.jpa3;

import org.example.jpa3.entities.Role;
import org.example.jpa3.entities.User;
import org.example.jpa3.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class Jpa3Application {

    public static void main(String[] args) {
        SpringApplication.run(Jpa3Application.class, args);
    }

    @Bean
    CommandLineRunner start(UserService userService) {
        return args -> {
            User user = new User();
            user.setUserName("user1");
            user.setPassword("1234");
            userService.addNewUser(user);

            User user2 = new User();
            user2.setUserName("admin");
            user2.setPassword("123456");
            userService.addNewUser(user2);

            Stream.of("ADMIN", "USER", "MANAGER").forEach(roleName -> {
                Role role = new Role();
                role.setRoleName(roleName);
                userService.addNewRole(role);
            });

            userService.addRoleToUser("user1", "USER");
            userService.addRoleToUser("admin", "ADMIN");
            userService.addRoleToUser("admin", "MANAGER");

            try {
                User user1 = userService.AuthenticateUser("user1", "1234");
                System.out.println(user1.getUserName() + " is authenticated");
                System.out.println("Roles of " + user1.getUserName());
                user1.getRoles().forEach(role -> {
                    System.out.println(role.getRoleName());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
```