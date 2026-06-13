package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Loan;

public interface LoanRepository
extends JpaRepository<Loan,Long>{

    List<Loan> findByCustomerCustomerId(Long customerId);

}
