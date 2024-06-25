package it.epicode.capstone.user;


import it.epicode.capstone.security.RegisterUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Order(15)
public class UserRunner implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() == 0) {
            List<RegisterUserDTO> users = Arrays.asList(
                    RegisterUserDTO.builder()
                            .withFirstName("Mario")
                            .withLastName("Rossi")
                            .withUsername("mrossi")
                            .withEmail("mrossi@example.com")
                            .withPassword("password123")
                            .build(),
                    RegisterUserDTO.builder()
                            .withFirstName("Luigi")
                            .withLastName("Verdi")
                            .withUsername("lverdi")
                            .withEmail("lverdi@example.com")
                            .withPassword("password456")
                            .build(),
                    // Aggiungi altri utenti come necessario
                    // Esempio:
                    RegisterUserDTO.builder()
                            .withFirstName("Giovanna")
                            .withLastName("Bianchi")
                            .withUsername("gbianchi")
                            .withEmail("gbianchi@example.com")
                            .withPassword("password789")
                            .build()
            );

            users.forEach(userService::register);
            System.out.println("--- Utenti registrati ---");
        }
    }
}
