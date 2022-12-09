package com.tlc.group.seven.orderprocessingservice.authentication.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iD;
    @NotBlank(message = "Name required")
    @Size(max = 50)
    @Column
    private String name;
    @NotBlank(message = "Email required")
    @Email
    @Size(max = 50)
    private String email;
    @NotBlank(message = "Password required")
    @Size(max = 120, min = 8)
    private String password;
    @Column(scale = 2)
    private double balance;
    @ManyToMany
    @JoinTable(name = "users_role",joinColumns = @JoinColumn(name = "users_id"),inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User() {
    }
}
