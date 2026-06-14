package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.RD;

public interface RDRepository
extends JpaRepository<RD, Long>{

    List<RD> findByCustomerCustomerId(Long customerId);

}
