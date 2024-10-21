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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "DESCRIPTION")
    private String desc;
    @Column(name = "ROLE_NAME",unique = true,length = 20)
    private String roleName;
    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "roles_users",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
    @ToString.Exclude
    private List<User> users=new ArrayList<>();
}
