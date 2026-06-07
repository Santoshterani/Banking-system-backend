package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;
    
   @Column(unique = true)
    private String name;
   @Column(unique = true)
    private String address;
   @Column(unique = true)
    private String ifscCode;
   @Column
    private LocalDateTime createdAt;
   
   @PrePersist
   public void prePersist() {
       this.createdAt = LocalDateTime.now();
   }
   
// getters & setters

   public Long getBranchId() {
	return branchId;
   }

   public void setBranchId(Long branchId) {
	this.branchId = branchId;
   }

   public String getName() {
	return name;
   }

   public void setName(String name) {
	this.name = name;
   }

   public String getAddress() {
	return address;
   }

   public void setAddress(String address) {
	this.address = address;
   }

   public String getIfscCode() {
	return ifscCode;
   }

   public void setIfscCode(String ifscCode) {
	this.ifscCode = ifscCode;
   }

   public LocalDateTime getCreatedAt() {
	return createdAt;
   }

   public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
   }
      
}
