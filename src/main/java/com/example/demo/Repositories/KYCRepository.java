package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.KYC;

public interface KYCRepository
extends JpaRepository<KYC, Long>{

    KYC findByCustomerCustomerId(Long customerId);

}
