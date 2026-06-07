package com.example.demo.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.entity.UPI;

public interface UPIRepository extends JpaRepository<UPI, String> {

    List<UPI> findByAccount_AccountId(Long accountId);
}
