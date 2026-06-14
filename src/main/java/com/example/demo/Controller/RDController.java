package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Services.RDService;

@RestController
@RequestMapping("/admin/rd")
public class RDController {

    @Autowired
    private RDService rdService;

    @PostMapping("/create")
    public String create(@RequestBody RD rd){

        try{

            rdService.createRD(rd);

            return "RD Created";

        }catch(Exception e){

            return e.getMessage();

        }

    }

    @GetMapping("/all")
    public List<RD> getAll(){

        return rdService.getAll();

    }

    @GetMapping("/customer/{id}")
    public List<RD> getCustomer(
            @PathVariable Long id){

        return rdService.getCustomerRD(id);

    }

    @PutMapping("/close/{id}")
    public String close(
            @PathVariable Long id){

        try{

            rdService.close(id);

            return "RD Closed";

        }catch(Exception e){

            return e.getMessage();

        }

    }

}
