package com.example.demo.Services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.BranchRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.config.CacheConfig;
import com.example.demo.entity.Branch;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;

@Service
public class BranchService {

    private static final Logger log = LoggerFactory.getLogger(BranchService.class);

    @Autowired
    private BranchRepository branchRepository;

    @CacheEvict(value = CacheConfig.BRANCHES_CACHE, allEntries = true)
    @Auditable("CREATE_BRANCH")
    public Branch createBranch(Branch branch) {

        if (branchRepository.findByIfscCode(branch.getIfscCode()).isPresent()) {
            throw new DuplicateResourceException("A branch with IFSC code '" + branch.getIfscCode() + "' already exists");
        }

        Branch saved = branchRepository.save(branch);
        log.info("Branch created: id={}, name={}, ifsc={}", saved.getBranchId(), saved.getName(), saved.getIfscCode());
        return saved;
    }

    @Cacheable(CacheConfig.BRANCHES_CACHE)
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Branch getBranch(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch", id));
    }

    @CacheEvict(value = CacheConfig.BRANCHES_CACHE, allEntries = true)
    @Auditable("UPDATE_BRANCH")
    public Branch updateBranch(Long id, Branch updatedBranch) {

        Branch branch = getBranch(id);
        branch.setName(updatedBranch.getName());
        branch.setAddress(updatedBranch.getAddress());
        branch.setIfscCode(updatedBranch.getIfscCode());

        Branch saved = branchRepository.save(branch);
        log.info("Branch updated: id={}", id);
        return saved;
    }

    @CacheEvict(value = CacheConfig.BRANCHES_CACHE, allEntries = true)
    @Auditable("DELETE_BRANCH")
    public void deleteBranch(Long id) {
        Branch branch = getBranch(id);
        branchRepository.delete(branch);
        log.info("Branch deleted: id={}", id);
    }
}
