package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repositories.TransactionRepository;
import com.example.demo.Services.AccountService;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.reporting.StatementService;

@RestController
@RequestMapping("/admin/account/{id}/statement")
public class StatementController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private StatementService statementService;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {

        Account account = accountService.getAccount(id);
        List<Transaction> transactions = transactionRepository.findByAccount_AccountIdOrderByTransactionDateDesc(id);
        byte[] pdf = statementService.generatePdfStatement(account, transactions);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statement-" + account.getAccountNumber() + ".pdf")
                .body(pdf);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> excel(@PathVariable Long id) {

        Account account = accountService.getAccount(id);
        List<Transaction> transactions = transactionRepository.findByAccount_AccountIdOrderByTransactionDateDesc(id);
        byte[] excel = statementService.generateExcelStatement(account, transactions);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statement-" + account.getAccountNumber() + ".xlsx")
                .body(excel);
    }
}
