package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Services.FDService;

@RestController
@RequestMapping("/admin/fd")
public class FDController {

    @Autowired
    private FDService fdService;

    @PostMapping("/create")
    public String create(@RequestBody FD fd){

        try{

            fdService.createFD(fd);

            return "FD Created";

        }catch(Exception e){

            return e.getMessage();

        }

    }

    @GetMapping("/all")
    public List<FD> getAll(){

        return fdService.getAllFD();

    }

    @GetMapping("/customer/{id}")
    public List<FD> getCustomerFD(
            @PathVariable Long id){

        return fdService.getCustomerFD(id);

    }

    @PutMapping("/close/{id}")
    public String close(@PathVariable Long id){

        try{

            fdService.closeFD(id);

            return "FD Closed";

        }catch(Exception e){

            return e.getMessage();

        }

    }

}
