package com.example.demo.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.example.demo.entity.RDPayment;

public interface RDPaymentRepository extends JpaRepository<RDPayment, Long> {

    List<RDPayment> findByRd_RdId(Long rdId);
}
