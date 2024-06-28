package it.epicode.capstone.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendActivationEmail(String recipientEmail, String activationToken) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            String activationLink = "http://localhost:8080/api/users/activate?token=" + activationToken;

            helper.setTo(recipientEmail);
            helper.setSubject("Benvenuto nella nostra applicazione! Attiva il tuo account");
            helper.setText("Grazie per esserti registrato. Per favore clicca sul link seguente per attivare il tuo account: " + activationLink);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
