package com.tlc.group.seven.orderprocessingservice.authentication.model;


import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iD;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    public Role() {
    }
    public Role(ERole name) {
        this.name = name;
    }
}
