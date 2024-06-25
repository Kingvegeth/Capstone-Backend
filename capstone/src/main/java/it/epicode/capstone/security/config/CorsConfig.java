package it.epicode.capstone.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200"); // Aggiungi qui le origini consentite
        configuration.addAllowedMethod(CorsConfiguration.ALL); // Permette tutti i metodi comuni, puoi specificare: GET, POST, PUT, DELETE, ecc.
        configuration.addAllowedHeader(CorsConfiguration.ALL); // Permette tutti gli header
        configuration.setAllowCredentials(true); // Permette le credenziali

        // Setta i permessi per le richieste preflight (OPTIONS)
        configuration.addExposedHeader("Authorization"); // Espone l'header Authorization nelle risposte
        configuration.setMaxAge(3600L); // Cache delle preflight per 1 ora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Applica questa configurazione CORS a tutte le route

        return new CorsFilter(source);
    }
}
