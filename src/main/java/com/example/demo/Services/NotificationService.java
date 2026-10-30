package com.example.demo.Services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Notification;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendMail(Customer customer, String subject, String body) {

        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setEmail(customer.getEmail());
        notification.setSubject(subject);
        notification.setMessage(body);
        notification.setSentTime(LocalDateTime.now());

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(customer.getEmail());
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);

            notification.setStatus("SUCCESS");
            log.info("Notification email sent to {} - subject='{}'", customer.getEmail(), subject);

        } catch (Exception e) {
            notification.setStatus("FAILED");
            log.warn("Failed to send notification email to {}: {}", customer.getEmail(), e.getMessage());
        }

        notificationRepository.save(notification);
    }

    /**
     * For mail that isn't tied to a Customer record - OTP delivery, staff account
     * notifications. Not persisted to the Notification table since that table is modeled
     * around customer-facing notifications specifically.
     */
    public void sendRawMail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(toEmail);
            mail.setSubject(subject);
            mail.setText(body);
            mailSender.send(mail);
            log.info("Mail sent to {} - subject='{}'", toEmail, subject);
        } catch (Exception e) {
            log.warn("Failed to send mail to {}: {}", toEmail, e.getMessage());
        }
    }
}
