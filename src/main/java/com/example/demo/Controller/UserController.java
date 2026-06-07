package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repositories.UserRepository;
import com.example.demo.entity.User;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository repo;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/create")
    public User create(
            @RequestBody User user
    ){
        user.setPassword(
            encoder.encode(
                user.getPassword()
            )
        );

        return repo.save(user);
    }
}
