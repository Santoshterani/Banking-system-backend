package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Repositories.NotificationRepository;
import com.example.demo.entity.Notification;

@RestController
@RequestMapping("/admin/notification")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/customer/{id}")
    public List<Notification> getCustomerNotifications(
            @PathVariable Long id){

        return notificationRepository
                .findByCustomerCustomerId(id);

    }

}
