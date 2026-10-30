package com.example.demo.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.CustomerRepository;
import com.example.demo.Repositories.KYCRepository;
import com.example.demo.audit.Auditable;
import com.example.demo.entity.Customer;
import com.example.demo.entity.KYC;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.SecurityUtils;

@Service
public class KYCService {

    private static final Logger log = LoggerFactory.getLogger(KYCService.class);

    @Autowired
    private KYCRepository kycRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public KYC submitKYC(KYC kyc) {

        Customer customer = customerRepository.findById(kyc.getCustomer().getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", kyc.getCustomer().getCustomerId()));

        securityUtils.assertBranchAccess(customer.getBranch().getBranchId());

        kyc.setCustomer(customer);
        kyc.setStatus("PENDING");
        kyc.setSubmittedDate(LocalDateTime.now());

        KYC saved = kycRepository.save(kyc);
        log.info("KYC submitted: id={}, customer={}", saved.getKycId(), customer.getCustomerId());
        return saved;
    }

    public List<KYC> getAllKYC() {
        Long branchFilter = securityUtils.getBranchFilter();
        if (branchFilter == null) {
            return kycRepository.findAll();
        }
        return kycRepository.findByCustomer_Branch_BranchId(branchFilter);
    }

    public KYC getCustomerKYC(Long customerId) {
        KYC kyc = kycRepository.findByCustomerCustomerId(customerId);
        if (kyc == null) {
            throw new ResourceNotFoundException("KYC record for customer", customerId);
        }
        securityUtils.assertBranchAccess(kyc.getCustomer().getBranch().getBranchId());
        return kyc;
    }

    @Auditable("APPROVE_KYC")
    public KYC approve(Long id) {

        KYC kyc = kycRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KYC", id));

        securityUtils.assertBranchAccess(kyc.getCustomer().getBranch().getBranchId());

        if (!"PENDING".equals(kyc.getStatus())) {
            throw new BadRequestException("Only pending KYC records can be approved");
        }

        kyc.setStatus("VERIFIED");
        KYC saved = kycRepository.save(kyc);
        log.info("KYC approved: id={}", id);
        return saved;
    }

    @Auditable("REJECT_KYC")
    public KYC reject(Long id) {

        KYC kyc = kycRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KYC", id));

        securityUtils.assertBranchAccess(kyc.getCustomer().getBranch().getBranchId());

        if (!"PENDING".equals(kyc.getStatus())) {
            throw new BadRequestException("Only pending KYC records can be rejected");
        }

        kyc.setStatus("REJECTED");
        KYC saved = kycRepository.save(kyc);
        log.info("KYC rejected: id={}", id);
        return saved;
    }
}
