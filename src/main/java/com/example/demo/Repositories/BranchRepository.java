package com.example.demo.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.demo.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByIfscCode(String ifscCode);
}
