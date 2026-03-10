package com.example.pms.model;

import com.example.pms.enums.GlobalRole;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")

public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GlobalRole role;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public User(){}

    public User(String name, String email, String password, GlobalRole role){
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public GlobalRole getRole(){
        return role;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
