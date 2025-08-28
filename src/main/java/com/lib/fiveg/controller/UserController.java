package com.lib.fiveg.controller;

import com.lib.fiveg.entity.User;
import com.lib.fiveg.entity.UserElastic;
import com.lib.fiveg.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String result = userService.registerUser(user);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
}

    // Search users by username (from Elasticsearch)
    @GetMapping("/search/username/{username}")
    public ResponseEntity<List<UserElastic>> searchByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.searchByUsername(username));
    }

    // Search users by email (from Elasticsearch)
    @GetMapping("/search/email/{email}")
    public ResponseEntity<List<UserElastic>> searchByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.searchByEmail(email));
    }
}
