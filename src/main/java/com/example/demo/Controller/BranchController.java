package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Services.BranchService;
import com.example.demo.entity.Branch;

@RestController
@RequestMapping("/admin/branch")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @PostMapping("/create")
    public Branch create(
            @RequestBody Branch branch) {
    	System.out.println("entered branch controller post");

        return branchService
                .createBranch(branch);
    }

    @GetMapping("/all")
    public List<Branch> all() {

        return branchService.getAllBranches();
    }

    @GetMapping("/{id}")
    public Branch get(
            @PathVariable Long id) {

        return branchService
                .getBranch(id);
    }

    @PutMapping("/update/{id}")
    public Branch update(
            @PathVariable Long id,
            @RequestBody Branch branch) {

        return branchService
                .updateBranch(id, branch);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id) {

        branchService.deleteBranch(id);

        return "Branch Deleted";
    }
    
    @GetMapping("/test")
    public String test() {
    	System.out.println("entered branch controller test");
        return "Working";
    }
}