package com.workschedule.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workschedule.app.dto.UserRegisterDTO;
import com.workschedule.app.entity.Role;
import com.workschedule.app.entity.User;
import com.workschedule.app.repository.UserRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> allUsers() {
        long count = userRepository.count();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createNewUser(@RequestBody UserRegisterDTO newUser) {
        User user = new User();
        user.setName(newUser.name());
        user.setSurname(newUser.surname());
        user.setRole(Role.ROLE_USER);
        user.setEmail(newUser.email());
        user.setUsername(newUser.username());

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

}
