package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.KYCRepository;
import com.example.demo.entity.Customer;
import com.example.demo.entity.KYC;

@Service
public class KYCService {

    @Autowired
    private KYCRepository kycRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public void submitKYC(KYC kyc){

        Customer customer = customerRepository.findById(
                kyc.getCustomer().getCustomerId())
                .orElseThrow(() ->
                new RuntimeException("Customer Not Found"));

        kyc.setCustomer(customer);
        kyc.setStatus("PENDING");
        kyc.setSubmittedDate(LocalDateTime.now());

        kycRepository.save(kyc);

    }

    public List<KYC> getAllKYC(){

        return kycRepository.findAll();

    }

    public KYC getCustomerKYC(Long customerId){

        return kycRepository.findByCustomerCustomerId(customerId);

    }

    public void approve(Long id){

        KYC kyc = kycRepository.findById(id)
                .orElseThrow(() ->
                new RuntimeException("KYC Not Found"));

        kyc.setStatus("VERIFIED");

        kycRepository.save(kyc);

    }

    public void reject(Long id){

        KYC kyc = kycRepository.findById(id)
                .orElseThrow(() ->
                new RuntimeException("KYC Not Found"));

        kyc.setStatus("REJECTED");

        kycRepository.save(kyc);

    }

}