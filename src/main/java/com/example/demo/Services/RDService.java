package com.example.demo.Services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.CustomerRepository;

@Service
public class RDService {

    @Autowired
    private RDRepository rdRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public void createRD(RD rd){

        Customer customer =
                customerRepository.findById(
                        rd.getCustomer().getCustomerId())
                        .orElseThrow(() ->
                        new RuntimeException("Customer Not Found"));

        rd.setCustomer(customer);

        rd.setCreatedDate(LocalDateTime.now());

        rd.setStatus("ACTIVE");

        double total =
                rd.getMonthlyDeposit() *
                rd.getTenureMonths();

        double maturity =
                total +
                (total *
                 rd.getInterestRate()/100 *
                 rd.getTenureMonths()/12);

        rd.setMaturityAmount(maturity);

        rdRepository.save(rd);

    }

    public List<RD> getAll(){

        return rdRepository.findAll();

    }

    public List<RD> getCustomerRD(Long customerId){

        return rdRepository
                .findByCustomerCustomerId(customerId);

    }

    public void close(Long id){

        RD rd =
                rdRepository.findById(id)
                .orElseThrow(() ->
                new RuntimeException("RD Not Found"));

        rd.setStatus("CLOSED");

        rdRepository.save(rd);

    }

}
