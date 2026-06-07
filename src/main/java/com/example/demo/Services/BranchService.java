package com.example.demo.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.BranchRepository;
import com.example.demo.entity.Branch;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public Branch createBranch(Branch branch) {

        if(branchRepository
                .findByIfscCode(branch.getIfscCode())
                .isPresent()) {

            throw new RuntimeException(
                    "IFSC already exists");
        }

        return branchRepository.save(branch);
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Branch getBranch(Long id) {

        return branchRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Branch not found"));
    }

    public Branch updateBranch(
            Long id,
            Branch updatedBranch) {

        Branch branch =
                getBranch(id);

        branch.setName(
                updatedBranch.getName());

        branch.setAddress(
                updatedBranch.getAddress());

        branch.setIfscCode(
                updatedBranch.getIfscCode());

        return branchRepository.save(branch);
    }

    public void deleteBranch(Long id) {

        branchRepository.deleteById(id);
    }
}
