package com.example.demo.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.Repositories.FDRepository;
import com.example.demo.Repositories.RDRepository;
import com.example.demo.Services.FDService;
import com.example.demo.Services.NotificationService;
import com.example.demo.Services.RDService;
import com.example.demo.entity.FixedDeposit;
import com.example.demo.entity.RecurringDeposit;

/**
 * Nightly sweep that matures FDs/RDs which have reached their tenure, crediting the
 * maturity amount into each one's beneficiary account via FDService/RDService (same code
 * path a staff-initiated manual close uses - no logic duplicated here, this job only finds
 * the eligible rows and tells the service layer to process them).
 *
 * Runs at 1 AM server time - outside banking hours, low write contention. Computing
 * "is this matured yet" in Java after fetching ACTIVE rows (rather than a date-arithmetic
 * WHERE clause) is deliberate - date/interval arithmetic syntax differs across DB dialects,
 * so this keeps the logic portable. Fine at this data volume; if FD/RD tables grow large,
 * move the filtering into a native query.
 */
@Component
public class MaturityJob {

    private static final Logger log = LoggerFactory.getLogger(MaturityJob.class);

    @Autowired
    private FDRepository fdRepository;

    @Autowired
    private RDRepository rdRepository;

    @Autowired
    private FDService fdService;

    @Autowired
    private RDService rdService;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 0 1 * * *")
    public void processMaturities() {

        LocalDateTime now = LocalDateTime.now();
        int fdMatured = 0;
        int rdMatured = 0;

        List<FixedDeposit> activeFDs = fdRepository.findByStatus("ACTIVE");
        for (FixedDeposit fd : activeFDs) {
            if (fd.getStartDate() != null && fd.getStartDate().plusMonths(fd.getTenureMonths()).isBefore(now)) {
                try {
                    fdService.creditOnMaturity(fd);
                    fdMatured++;

                    notificationService.sendMail(fd.getCustomer(), "Fixed Deposit Matured",
                            "Your Fixed Deposit #" + fd.getFdId() + " has matured. " + fd.getMaturityAmount()
                                    + " has been credited to account " + fd.getBeneficiaryAccount().getAccountNumber() + ".");
                } catch (Exception e) {
                    // One bad row (e.g. missing beneficiary account on legacy data) must not
                    // stop the rest of tonight's batch from processing.
                    log.error("Failed to process maturity for FD {}: {}", fd.getFdId(), e.getMessage());
                }
            }
        }

        List<RecurringDeposit> activeRDs = rdRepository.findByStatus("ACTIVE");
        for (RecurringDeposit rd : activeRDs) {
            if (rd.getCreatedDate() != null && rd.getCreatedDate().plusMonths(rd.getTenureMonths()).isBefore(now)) {
                try {
                    rdService.creditOnMaturity(rd);
                    rdMatured++;

                    notificationService.sendMail(rd.getCustomer(), "Recurring Deposit Matured",
                            "Your Recurring Deposit #" + rd.getRdId() + " has matured. " + rd.getMaturityAmount()
                                    + " has been credited to account " + rd.getBeneficiaryAccount().getAccountNumber() + ".");
                } catch (Exception e) {
                    log.error("Failed to process maturity for RD {}: {}", rd.getRdId(), e.getMessage());
                }
            }
        }

        if (fdMatured > 0 || rdMatured > 0) {
            log.info("Maturity job completed: {} FD(s) and {} RD(s) matured and credited", fdMatured, rdMatured);
        }
    }
}
