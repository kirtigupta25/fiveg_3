package com.lib.fiveg.service;
import java.util.List;

import com.lib.fiveg.elastic.UserSearchRepository;
import com.lib.fiveg.entity.UserElastic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lib.fiveg.entity.Role;
import com.lib.fiveg.entity.User;
import com.lib.fiveg.repository.RoleRepository;
import com.lib.fiveg.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserSearchRepository userSearchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public String registerUser(User user) {
        // Check for existing username or email
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username already exists!";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists!";
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Fetch role by name
        Role role = roleRepository.findByName(user.getRole().getName())
            .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);

        // Save user
        userRepository.save(user);
        User savedUser = userRepository.save(user);
        // Convert JPA -> ES Document
        UserElastic doc = new UserElastic();
        doc.setId(savedUser.getId());
        doc.setUsername(savedUser.getUsername());
        doc.setEmail(savedUser.getEmail());
        doc.setFullname(savedUser.getFullname());
       // doc.setRole(savedUser.getRole().getName());

        // Save to Elasticsearch
        userSearchRepository.save(doc);
        return "User registered successfully";
    }
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        System.out.println("Fetching from DB...");
        return userRepository.findAll();
    }
    // Search from Elasticsearch
    public List<UserElastic> searchByUsername(String username) {
        return userSearchRepository.findByUsername(username);
    }

    public List<UserElastic> searchByEmail(String email) {
        return userSearchRepository.findByEmail(email);
    }
}
