package it.epicode.capstone.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//CLASSE CHE GENERA E CONTROLLA IL TOKEN (JWT)
@Component
public class JwtUtils {

    @Value("${jwt.key}")
    private String securityKey;
    @Value("${jwt.expirationMs}")
    private long expirationMs;

//    public String generateToken(Authentication auth) {
//        byte[] keyBytes = securityKey.getBytes();
//        Key key = Keys.hmacShaKeyFor(keyBytes);
//
//        var user = (SecurityUserDetails) auth.getPrincipal();
//        return Jwts.builder()
//                .subject(user.getUsername())
//                .issuedAt(new Date())
//                .issuer("MySpringApplication")
//                .expiration(new Date(new Date().getTime() + expirationMs))
//                .signWith(key)
//                //ESISTONO I CLAIM OVVERO SONO INFORMAZIONI AGGIUNTIVE CHE POSOSNO ESSERE AGGIUNTE AL TOKEN .claim("professore dell'aula", "Mauro")
//                .compact();
//    }

    public String generateToken(Authentication authentication) {
        byte[] keyBytes = securityKey.getBytes();
        Key key = Keys.hmacShaKeyFor(keyBytes);
        // Recupera l'utente autenticato dall'oggetto Authentication
        var userPrincipal = (SecurityUserDetails) authentication.getPrincipal();

        // Crea il payload del token JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userPrincipal.getUserId());
        claims.put("username", userPrincipal.getUsername());
        claims.put("email", userPrincipal.getEmail());
        claims.put("roles", userPrincipal.getRoles());

        // Genera il token JWT utilizzando il payload, il secret e la scadenza
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            byte[] keyBytes = securityKey.getBytes();
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            //PRENDIAMO LA DATA DI SCADENZA DAL TOKEN
            Date expirationDate = Jwts.parser()
                    .verifyWith(key).build()
                    .parseSignedClaims(token).getPayload().getExpiration();

            //token valido fino a 2024-04-01
            //token verificato il 2024-06-13

            //token valido fino 2024-06-13 10:01:00
            //token verifcato il 2024-06-13 10:02:00
            //VERIFICHIAMO SE LA DATA DI SCADENZA TROVATA E PRIMA O DOPO LA DATA DI OGGI
            if (expirationDate.before(new Date()))
                throw new JwtException("Token expired");
            Jwts.parser()
                    .verifyWith(key).requireIssuer("MySpringApplication");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        byte[] keyBytes = securityKey.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
}
