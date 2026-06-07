package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repositories.RoleRepository;
import com.example.demo.entity.Role;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleRepository repo;

    @PostMapping("/create")
    public Role create(
        @RequestBody Role role
    ){
        return repo.save(role);
    }
}
