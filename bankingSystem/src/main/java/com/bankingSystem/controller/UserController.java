package com.bankingSystem.controller;

import com.bankingSystem.entity.User;
import com.bankingSystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser(Authentication auth){
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> allUsers(Authentication auth){
        // Only admins can see all users, check role:
        User currentUser = (User) auth.getPrincipal();
        if(!"ADMIN".equals(currentUser.getRole())){
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
