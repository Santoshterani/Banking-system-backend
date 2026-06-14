package com.example.demo.Services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Notification;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendMail(Customer customer,
                         String subject,
                         String body){

        try{

            SimpleMailMessage mail =
                    new SimpleMailMessage();

            mail.setTo(customer.getEmail());

            mail.setSubject(subject);

            mail.setText(body);

            mailSender.send(mail);

            Notification notification =
                    new Notification();

            notification.setCustomer(customer);

            notification.setEmail(customer.getEmail());

            notification.setSubject(subject);

            notification.setMessage(body);

            notification.setStatus("SUCCESS");

            notification.setSentTime(LocalDateTime.now());

            notificationRepository.save(notification);

        }
        catch(Exception e){

            Notification notification =
                    new Notification();

            notification.setCustomer(customer);

            notification.setEmail(customer.getEmail());

            notification.setSubject(subject);

            notification.setMessage(body);

            notification.setStatus("FAILED");

            notification.setSentTime(LocalDateTime.now());

            notificationRepository.save(notification);

        }

    }

}
