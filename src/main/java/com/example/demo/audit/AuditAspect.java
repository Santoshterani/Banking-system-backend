package com.example.demo.audit;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.demo.Repositories.AuditLogRepository;
import com.example.demo.entity.AuditLog;

/**
 * Cross-cutting audit trail: wraps every @Auditable service method, records who called it
 * and whether it succeeded or failed, without the service itself needing to know an audit
 * trail exists. Deliberately does NOT log method arguments verbatim (request DTOs can carry
 * passwords, Aadhaar/PAN numbers, etc) - just the action name, actor, and outcome.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {

        String[] actor = currentActor();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        AuditLog entry = new AuditLog();
        entry.setAction(auditable.value());
        entry.setPerformedBy(actor[0]);
        entry.setPerformedByRole(actor[1]);
        entry.setDetails(signature.getDeclaringType().getSimpleName() + "." + signature.getName() + "()");
        entry.setTimestamp(LocalDateTime.now());

        try {
            Object result = joinPoint.proceed();
            entry.setOutcome("SUCCESS");
            save(entry);
            return result;

        } catch (Throwable ex) {
            entry.setOutcome("FAILED");
            entry.setFailureReason(truncate(ex.getMessage()));
            save(entry);
            throw ex;
        }
    }

    private void save(AuditLog entry) {
        try {
            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Failed to persist audit log entry for action '{}': {}", entry.getAction(), e.getMessage());
        }
    }

    private String[] currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new String[]{"SYSTEM", "SYSTEM"};
        }
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("UNKNOWN");
        return new String[]{auth.getName(), role};
    }

    private String truncate(String message) {
        if (message == null) return null;
        return message.length() > 500 ? message.substring(0, 500) : message;
    }
}
