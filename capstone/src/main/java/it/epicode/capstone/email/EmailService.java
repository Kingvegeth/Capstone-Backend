package it.epicode.capstone.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendActivationEmail(String recipientEmail, String firstName, String activationToken) {
        log.info("Preparing to send email to: {}", recipientEmail);
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String activationLink = baseUrl + "/api/users/activate?token=" + activationToken;

            String htmlContent = "<html>" +
                    "<body style='font-family: Arial, sans-serif; text-align: center; color: #333;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                    "<img src='https://via.placeholder.com/100' alt='Logo' style='width: 100px; height: 100px; margin-bottom: 20px;'>" +
                    "<h1 style='color: #555;'>Benvenuto su Cult Cinema Hub, " + firstName + "!</h1>" +
                    "<p style='font-size: 16px; color: #666;'>Il tuo account è quasi pronto.</p>" +
                    "<a href='" + activationLink + "' style='display: inline-block; margin: 20px auto; padding: 10px 20px; color: #fff; background-color: #007bff; border-radius: 5px; text-decoration: none;'>Attiva il tuo account</a>" +
                    "<p style='font-size: 14px; color: #999;'>Puoi anche copiare e incollare il seguente link nel tuo browser:</p>" +
                    "<p style='font-size: 14px; color: #007bff;'>" + activationLink + "</p>" +
                    "<hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
                    "<p style='font-size: 12px; color: #bbb;'>Se non hai creato un account su Cult Cinema Hub, ignora questa email e nessun account verrà creato.</p>" +
                    "<div style='font-size: 12px; color: #bbb; margin-top: 20px;'>" +
                    "<a href='#' style='color: #007bff; text-decoration: none; margin: 0 10px;'>Facebook</a>" +
                    "<a href='#' style='color: #007bff; text-decoration: none; margin: 0 10px;'>Twitter</a>" +
                    "<a href='#' style='color: #007bff; text-decoration: none; margin: 0 10px;'>Website</a>" +
                    "<a href='#' style='color: #007bff; text-decoration: none; margin: 0 10px;'>Help</a>" +
                    "<a href='#' style='color: #007bff; text-decoration: none; margin: 0 10px;'>Community</a>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setTo(recipientEmail);
            helper.setSubject("Benvenuto in Cult Cinema Hub! Attiva il tuo account");
            helper.setText(htmlContent, true); // Secondo parametro 'true' per indicare che il contenuto è HTML

            log.info("Sending email to: {}", recipientEmail);
            emailSender.send(message);
            log.info("Email sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
