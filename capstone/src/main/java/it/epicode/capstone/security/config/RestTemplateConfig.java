package it.epicode.capstone.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Aggiungi l'interceptor per il token JWT
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new JwtInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    public static class JwtInterceptor implements ClientHttpRequestInterceptor {
        private static String token;

        public static void setToken(String token) {
            JwtInterceptor.token = token;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders headers = request.getHeaders();
            if (token != null) {
                headers.add("Authorization", "Bearer " + token);
            }
            return execution.execute(request, body);
        }
    }
}

