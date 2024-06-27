package it.epicode.capstone;


import it.epicode.capstone.constans.Colors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CapstoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapstoneApplication.class, args);
		log.info(Colors.YELLOW + "Software Partito" + Colors.RESET);
	}


}
