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
                System.out.println(user1.getUserName()+" is authenticated");
                System.out.println("Roles of "+user1.getUserName());
                user1.getRoles().forEach(role -> {
                    System.out.println(role.getRoleName());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    };

}
