package com.example.fakestore.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    String email;
    String password;
    @Enumerated(EnumType.STRING)
    Role role;
    String avatar;
    Boolean active;

    @OneToMany(mappedBy = "user")
    List<Order> orders;


}
