package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.FD;

public interface FDRepository
extends JpaRepository<FD, Long>{

    List<FD> findByCustomerCustomerId(Long customerId);

}
