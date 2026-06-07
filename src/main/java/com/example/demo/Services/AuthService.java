package com.example.demo.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    public String login(String email, String password) {

        Optional<User> user = repo.findByEmail(email);

        if(user.isPresent() &&
                encoder.matches(password,
                        user.get().getPassword())) {

        	return JwtUtil.generateToken(
        		    email,
        		    user.get()
        		        .getRole()
        		        .getRoleName()
        		);
        }

        throw new RuntimeException("Invalid credentials");
    }
}