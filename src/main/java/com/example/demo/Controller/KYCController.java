package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Services.KYCService;
import com.example.demo.entity.KYC;

@RestController
@RequestMapping("/admin/kyc")
public class KYCController {

    @Autowired
    private KYCService kycService;

    @PostMapping("/submit")
    public String submit(@RequestBody KYC kyc){

        try{
            kycService.submitKYC(kyc);
            return "KYC Submitted";
        }
        catch(Exception e){
            return e.getMessage();
        }

    }

    @GetMapping("/all")
    public List<KYC> getAll(){

        return kycService.getAllKYC();

    }

    @GetMapping("/customer/{id}")
    public KYC getCustomer(@PathVariable Long id){

        return kycService.getCustomerKYC(id);

    }

    @PutMapping("/approve/{id}")
    public String approve(@PathVariable Long id){

        try{
            kycService.approve(id);
            return "KYC Approved";
        }
        catch(Exception e){
            return e.getMessage();
        }

    }

    @PutMapping("/reject/{id}")
    public String reject(@PathVariable Long id){

        try{
            kycService.reject(id);
            return "KYC Rejected";
        }
        catch(Exception e){
            return e.getMessage();
        }

    }

}
