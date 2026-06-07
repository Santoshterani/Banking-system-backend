package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Services.AccountService;
import com.example.demo.entity.Account;

@RestController
@RequestMapping("/admin/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public Account create(
            @RequestBody Account account) {

        return accountService.createAccount(account);
    }

    @GetMapping("/all")
    public List<Account> all() {

        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public Account get(
            @PathVariable Long id) {

        return accountService.getAccount(id);
    }

    @PutMapping("/update/{id}")
    public Account update(
            @PathVariable Long id,
            @RequestBody Account account) {

        return accountService.updateAccount(
                id,
                account);
    }

    @DeleteMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id) {

        accountService.deleteAccount(id);

        return "Account Deleted";
    }
}