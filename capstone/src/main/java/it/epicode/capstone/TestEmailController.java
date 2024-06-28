package it.epicode.capstone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@RestController
public class TestEmailController {

    @Autowired
    private JavaMailSender testJavaMailSender;

    @GetMapping("/test-email")
    public String sendTestEmail(@RequestParam String to) {
        MimeMessage message = testJavaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Test Email from Spring Boot");
            helper.setText("This is a test email.", true);

            testJavaMailSender.send(message);
            return "Email sent successfully.";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error sending email: " + e.getMessage();
        }
    }
}

