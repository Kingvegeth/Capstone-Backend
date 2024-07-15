package it.epicode.capstone;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TestEmail {
    private static final Logger log = LoggerFactory.getLogger(TestEmail.class);

    public static void main(String[] args) {
        String host = "smtp.gmail.com";
        String port = "587";
        final String username = "cultcinemahubblog@gmail.com";  // Sostituisci con il tuo indirizzo email
        final String password = "jsfr rlmr lknb hsyy";  // Sostituisci con la tua password per le app

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("f9c7457717@emailcbox.pro"));
            message.setSubject("Test Email");
            message.setText("This is a test email.");

            Transport.send(message);

            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            log.info("test");
        }
    }
}

