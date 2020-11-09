package com.alexcorp.bloggers.worker;

import com.alexcorp.bloggers.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderWorker {

    @Autowired
    private MailService mailService;

    @Autowired
    private JavaMailSender mailSender;

    @Scheduled(cron = "0/1 * * * * ?")
    public void send() {
        try {
            long start = System.currentTimeMillis();
            while (mailService.getMessages().size() != 0) {
                SimpleMailMessage msg = mailService.getMessages().poll();
                mailSender.send(msg);
            }

            long passed = System.currentTimeMillis() - start;
            if(passed < 1000) Thread.sleep(1000 - passed);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
