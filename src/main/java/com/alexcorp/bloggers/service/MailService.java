package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.model.EmailUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

@Service
public class MailService {

    @Value("${server.domain.name}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private JavaMailSender mailSender;

    private Queue<SimpleMailMessage> messages = new ArrayDeque<>();

    public void send(List<? extends EmailUser> sendTo, String subject, String message){
        String[] emailsTo = new String[sendTo.size()];
        for (int i = 0; i < sendTo.size(); i++) emailsTo[i] = sendTo.get(i).getEmail();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(username);
        msg.setTo(emailsTo);
        msg.setSubject(subject);
        msg.setText(message);

        messages.add(msg);
    }

    public Queue<SimpleMailMessage> getMessages() {
        return messages;
    }

    public String getHost() {
        return host;
    }
}
