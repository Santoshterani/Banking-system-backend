package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.CustomerRepository;

@Service
public class FDService {

    @Autowired
    private FDRepository fdRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public void createFD(FD fd){

        Customer customer = customerRepository
                .findById(fd.getCustomer().getCustomerId())
                .orElseThrow(() ->
                new RuntimeException("Customer Not Found"));

        fd.setCustomer(customer);

        fd.setStartDate(LocalDateTime.now());

        fd.setStatus("ACTIVE");

        double years = fd.getTenureMonths()/12.0;

        double maturity =
                fd.getPrincipalAmount() *
                (1 + fd.getInterestRate()/100 * years);

        fd.setMaturityAmount(maturity);

        fdRepository.save(fd);

    }

    public List<FD> getAllFD(){

        return fdRepository.findAll();

    }

    public List<FD> getCustomerFD(Long customerId){

        return fdRepository.findByCustomerCustomerId(customerId);

    }

    public void closeFD(Long id){

        FD fd = fdRepository.findById(id)
                .orElseThrow(() ->
                new RuntimeException("FD Not Found"));

        fd.setStatus("CLOSED");

        fdRepository.save(fd);

    }

}
