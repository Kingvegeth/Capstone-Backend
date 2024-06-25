package it.epicode.capstone.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwt;

    @Autowired
    ApplicationUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //VERIFICA DEL TOKEN
            log.info("Processing AuthTokenFilter");

            var header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer")) {
                //IL TOKEN ESISTE ED E NELLA FORMA Bearer xxxxxxxxx
                //PER RECUPERARE LA PARTE XXXX SI ESCLUDE Bearer CON SUBSTRING
                var token = header.substring(7); //QUESTO Ã¨ IL TOKEN ESTRATTO A PARTIRE DAL SETTIMO CARATTERE
                log.info("Token: {}", token);

                //SI EFFETTUA LA VALIDAZIONE DEL TOKEN (IMPLEMENTARE ANCHE VERIFICA DEI MS)
                if (!jwt.isTokenValid(token))
                    throw new JwtException("token non valido");

                //SI ESTRAGGONO DAL TOKEN LE INFORMAZIONI RELATIVE ALLO USERNAME
                var username = jwt.getUsernameFromToken(token);
                log.info("Username: {}", username);
                //SI ESTRAE L'UTENTE DAL SUO USERNAME
                var details = userDetailsService.loadUserByUsername(username);
                log.info("Details: {}", details);

                //SI GENERA IL CONTESTO CONTENENTE I DATI DELL'UTENTE RECUPERATI DAL TOKEN IN MODO DA RENDERLI
                //DISPONIBILI SE NECESSARI ALL'INTERNO DI UN CONTROLLER
                var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());//VERIFICA LA CORRETTEZZA DELLE CREDENZIALI
                //SI COMPLETA L'OGGETTO AUTH
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //SI PASSA AL CONTESTO L'OGGETTO AUTH PER RENDERLO SEMPRE DISPONIBILE
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            else {
                System.out.println("---------------- No Token");
            }

        } catch (Exception e) {
            log.error("Exception in auth filter", e);
        }
        //CHIAMA A CASCATA TUTTI I FILTRI CREATI
        filterChain.doFilter(request, response);
    }

}

