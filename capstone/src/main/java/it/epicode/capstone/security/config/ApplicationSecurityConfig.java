package it.epicode.capstone.security.config;

import it.epicode.capstone.security.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Properties;

@Configuration
//QUESTA ANNOTAZIONE SERVE A COMUNICARE A SPRING CHE QUESTA  CLASSE Ã¨ UTILIZZATA PER CONFIGURARE LA SECURITY
@EnableWebSecurity()
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {

    @Bean
    PasswordEncoder stdPasswordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    AuthTokenFilter authenticationJwtToken() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Utilizza la configurazione CORS
                .authorizeHttpRequests(authorize ->
                                authorize
                                        .requestMatchers("/api/users/login").permitAll()
                                        .requestMatchers("/api/users/register").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/movies").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/comments").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                                        .requestMatchers("/api/users/registerAdmin").hasAuthority("ADMIN")
                                        .requestMatchers(HttpMethod.PATCH, "/**").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/**").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/api/users/delete").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()

                                        .requestMatchers("/api/users/activate").permitAll()
                                        .requestMatchers(HttpMethod.GET, "/**").authenticated()
                                        .requestMatchers(HttpMethod.POST, "/**").authenticated()
                                        .requestMatchers(HttpMethod.PATCH, "/api/users/{id}").authenticated()
                                        .requestMatchers(HttpMethod.PUT, "/**").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/**").hasAuthority("ADMIN")
                                        .requestMatchers("/**").authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //COMUNICA ALLA FILTERCHAIN QUALE FILTRO UTILIZZARE, SENZA QUESTA RIGA DI CODICE IL FILTRO NON VIENE RICHIAMATO
                .addFilterBefore(authenticationJwtToken(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }




    @Bean
    public JavaMailSenderImpl getJavaMailSender(@Value("${gmail.mail.transport.protocol}") String protocol,
                                                @Value("${gmail.mail.smtp.auth}") String auth,
                                                @Value("${gmail.mail.smtp.starttls.enable}") String starttls,
                                                @Value("${gmail.mail.debug}") String debug,
                                                @Value("${gmail.mail.from}") String from,
                                                @Value("${gmail.mail.from.password}") String password,
                                                @Value("${gmail.smtp.host}") String host,
                                                @Value("${gmail.smtp.port}") String port) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(from);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.debug", debug);
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.ssl.trust", host);

        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

}

