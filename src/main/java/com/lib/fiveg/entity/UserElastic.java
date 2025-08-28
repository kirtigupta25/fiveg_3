package com.lib.fiveg.entity;

import jakarta.persistence.*;
import org.springframework.data.elasticsearch.annotations.Document;

@Entity
@Document(indexName = "users")
public class UserElastic {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true,nullable=false)
    private String username;
    @Column(unique=true,nullable=false)
    private String email;
    @Column(nullable=false)
    private String password;
    private String fullname;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}
