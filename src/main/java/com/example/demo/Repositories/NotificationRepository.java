package com.example.demo.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Notification;

@Repository
public interface NotificationRepository
extends JpaRepository<Notification,Long>{

    List<Notification> findByCustomerCustomerId(Long customerId);

}